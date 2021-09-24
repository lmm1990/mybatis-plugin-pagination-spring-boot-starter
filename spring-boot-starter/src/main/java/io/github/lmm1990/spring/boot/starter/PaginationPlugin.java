package io.github.lmm1990.spring.boot.starter;

import io.github.lmm1990.spring.boot.starter.core.BoundSqlSqlSource;
import io.github.lmm1990.spring.boot.starter.core.PaginationDataHandler;
import io.github.lmm1990.spring.boot.starter.entity.Page;
import io.github.lmm1990.spring.boot.starter.entity.PaginationInfo;
import io.github.lmm1990.spring.boot.starter.utils.PaginationHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * mybatis分页插件
 *
 * @author liumingming
 * @since 刘明明/2021-09-09 14:44:28
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
//        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class PaginationPlugin implements Interceptor {

    /**
     * 数据总数量sql删除关键字
     */
    private static final HashSet<String> COUNT_SQL_REMOVE_KEYWORDS = new HashSet<String>() {{
        add(" order by ");
        add(" limit ");
        add(" group by ");
    }};

    /**
     * 数据总数量sql方法id后缀
     */
    private static final String COUNT_SQL_METHOD_ID_SUFFIX = "$$Count";

    /**
     * 拦截器
     *
     * @param invocation: 调用对象
     * @return java.lang.Object
     * @since 刘明明/2021-09-09 14:44:36
     **/
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        if(invocation.getTarget() instanceof  Executor){
//            Executor executor = (Executor)invocation.getTarget();
//            MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
//            System.out.println("**************a");
//        }
        if (invocation.getTarget() instanceof StatementHandler) {
            return rewriteStatementHandler(invocation);
        }
        if (invocation.getTarget() instanceof ResultSetHandler) {
            DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
            MetaObject metaObject = MetaObject.forObject(resultSetHandler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
            MappedStatement mappedStatement = ((MappedStatement) metaObject.getValue("mappedStatement"));
            // 获取结果映射
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            if (resultMaps.isEmpty()) {
                return invocation.proceed();
            }
            Configuration configuration = (Configuration) metaObject.getValue("configuration");
            ResultMap baseResultMap = resultMaps.get(0);
            if (!baseResultMap.getType().isAssignableFrom(Page.class)) {
                return invocation.proceed();
            }


            Statement statement = (Statement) invocation.getArgs()[0];
            ResultSet resultSet = statement.getResultSet();
            if (resultSet == null) {
                return invocation.proceed();
            }
            DefaultResultHandler resultHandler = new DefaultResultHandler();
            metaObject.setValue("resultHandler", resultHandler);
            if (mappedStatement.getId().endsWith(COUNT_SQL_METHOD_ID_SUFFIX)) {
                ResultSetWrapper rsw = new ResultSetWrapper(resultSet, configuration);
                ResultMap resultMap = new ResultMap.Builder(configuration, baseResultMap.getId(), Long.TYPE, baseResultMap.getResultMappings())
                        .discriminator(baseResultMap.getDiscriminator())
                        .build();

                resultSetHandler.handleRowValues(rsw, resultMap, resultHandler, RowBounds.DEFAULT, null);
                return resultHandler.getResultList();
            }

            //查询数据总条数
            executeCount(((CachingExecutor) metaObject.getValue("executor")), mappedStatement, metaObject);

            ResultSetWrapper rsw = new ResultSetWrapper(resultSet, configuration);
            ResultMap resultMap = new ResultMap.Builder(configuration, baseResultMap.getId(), PaginationDataHandler.PAGINATION_METHOD_RETURN_TYPES.get(mappedStatement.getId()), baseResultMap.getResultMappings())
                    .discriminator(baseResultMap.getDiscriminator())
                    .build();

            resultSetHandler.handleRowValues(rsw, resultMap, resultHandler, RowBounds.DEFAULT, null);


            return resultHandler.getResultList();
        }
        return invocation.proceed();
    }

    /**
     * 重写StatementHandler
     *
     * @param invocation: 调用对象
     * @return java.lang.Object
     * @since 刘明明/2021-09-09 14:45:35
     **/
    private Object rewriteStatementHandler(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String methodId = ((MappedStatement) SystemMetaObject.forObject(statementHandler.getParameterHandler()).getValue("mappedStatement")).getId();
        if (!PaginationDataHandler.PAGINATION_METHOD_RETURN_TYPES.containsKey(methodId)) {
            return invocation.proceed();
        }
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        rewriteSql(methodId, metaObject);
        return invocation.proceed();
    }

    /**
     * 重写sql
     *
     * @param metaObject: 反射工具类
     * @since 刘明明/2021-09-07 16:35:32
     **/
    private void rewriteSql(String methodId, MetaObject metaObject) {
//        if (!PaginationDataHandler.PAGINATION_SQLS.containsKey(methodId)) {
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            String sql = boundSql.getSql();

            PaginationInfo pageInfo = PaginationHelper.getPaginationInfo();
            StringBuilder pageSql = new StringBuilder();
            pageSql.append(sql);
            pageSql.append(" limit ");
            pageSql.append((pageInfo.getP() - 1) * pageInfo.getPageSize());
            pageSql.append(",");
            pageSql.append(pageInfo.getPageSize());
            PaginationDataHandler.PAGINATION_SQLS.put(methodId, pageSql.toString());
//        }
        metaObject.setValue("delegate.boundSql.sql", PaginationDataHandler.PAGINATION_SQLS.get(methodId));
    }

    /**
     * 获得查询数据总条数sql
     *
     * @param cachingExecutor:     执行器
     * @param baseMappedStatement: MappedStatement
     * @param metaObject:          反射工具类
     * @since 刘明明/2021-09-09 14:33:00
     **/
    private void executeCount(CachingExecutor cachingExecutor, MappedStatement baseMappedStatement, MetaObject metaObject) throws SQLException {
        String methodId = String.format("%s%s", baseMappedStatement.getId(), COUNT_SQL_METHOD_ID_SUFFIX);
        BoundSql baseBoundSql = (BoundSql) metaObject.getValue("boundSql");
        RowBounds rowBounds = (RowBounds) metaObject.getValue("rowBounds");
        Object value;
        //自定义查询数据总数量
        if (PaginationHelper.getPaginationInfo().getCountSql() != null) {
            methodId = String.format("%s%s", PaginationHelper.getPaginationInfo().getCountSql(), methodId);
            MappedStatement mappedStatement = newMappedStatement(methodId, baseMappedStatement, PaginationHelper.getPaginationInfo().getCountSql(), baseBoundSql, true);
            ResultHandler resultHandler = (ResultHandler) metaObject.getValue("resultHandler");

            value = cachingExecutor.query(mappedStatement, new MapperMethod.ParamMap(), rowBounds, resultHandler);
        } else {
//            if (!PaginationDataHandler.COUNT_MAPPED_STATEMENTS.containsKey(methodId)) {
                String countSql = getCountSql(baseBoundSql);
                PaginationDataHandler.COUNT_MAPPED_STATEMENTS.put(methodId, newMappedStatement(methodId, baseMappedStatement, countSql, baseBoundSql, false));
//            }
            MappedStatement mappedStatement = PaginationDataHandler.COUNT_MAPPED_STATEMENTS.get(methodId);
            Object parameter = ((DefaultParameterHandler) metaObject.getValue("parameterHandler")).getParameterObject();
            ResultHandler resultHandler = (ResultHandler) metaObject.getValue("resultHandler");

            BoundSql boundSql = mappedStatement.getBoundSql(parameter);

            //创建 count 查询的缓存 key
            CacheKey countKey = cachingExecutor.createCacheKey(mappedStatement, parameter, RowBounds.DEFAULT, boundSql);

            value = cachingExecutor.query(mappedStatement, parameter, rowBounds, resultHandler, countKey, boundSql);
        }
        long totalCount = (Long) ((ArrayList) value).get(0);
        PaginationHelper.getPaginationInfo().setTotalCount(totalCount);
    }

    /**
     * 获得查询数据总条数sql
     *
     * @param boundSql: sql对象
     * @return java.lang.String
     * @since 刘明明/2021-09-09 16:03:26
     **/
    private String getCountSql(BoundSql boundSql) {
        String sql = boundSql.getSql().replaceAll(" +", " ");
        StringBuilder countSql = new StringBuilder("select count(1)");
        int fromIndex = sql.toLowerCase(Locale.ROOT).indexOf(" from ");
        countSql.append(sql.substring(fromIndex));
        StringBuilder lowerCaseSql = new StringBuilder(countSql.toString().toLowerCase(Locale.ROOT));

        COUNT_SQL_REMOVE_KEYWORDS.forEach((item) -> {
            int index = lowerCaseSql.indexOf(item);
            if (index > -1) {
                countSql.delete(index, countSql.length());
                lowerCaseSql.delete(index, lowerCaseSql.length());
            }
        });
        return countSql.toString();
    }

    /**
     * 构造新的MappedStatement
     *
     * @param methodId:    方法id
     * @param ms:          旧的MappedStatement
     * @param countSql:    查询数据总条数sql
     * @param boundSql:    sql包装类
     * @param isCustomSql: 是否是自定义sql
     * @return org.apache.ibatis.mapping.MappedStatement
     * @since 刘明明/2021-09-09 16:33:52
     **/
    private MappedStatement newMappedStatement(String methodId, MappedStatement ms, String countSql, BoundSql boundSql, boolean isCustomSql) {
        SqlSource newSqlSource = new BoundSqlSqlSource(ms, countSql, boundSql, isCustomSql);

        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), methodId, newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        if (!isCustomSql) {
            builder.parameterMap(ms.getParameterMap());
            builder.cache(ms.getCache());
            builder.useCache(ms.isUseCache());
        }
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        builder.flushCacheRequired(ms.isFlushCacheRequired());

        return builder.build();
    }
}