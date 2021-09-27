package io.github.lmm1990.mybatis.plugin.pagination.spring.boot.starter.core;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.ArrayList;

/**
 * 包装sql类
 *
 * @author liumingming
 * @since 2021-09-09 14:32
 */
public class BoundSqlSqlSource implements SqlSource {
    private BoundSql boundSql;

    /**
     * 包装sql类初始化
     *
     * @param baseMappedStatement: MappedStatement
     * @param sql: sql语句
     * @param baseBoundSql:  原包装sql类
     * @param isCustom:  是否是自定义sql
     * @since 刘明明/2021-09-09 16:30:13
     **/
    public BoundSqlSqlSource(MappedStatement baseMappedStatement, String sql, BoundSql baseBoundSql,boolean isCustom) {
        if(isCustom){
            this.boundSql = new BoundSql(baseMappedStatement.getConfiguration(), sql, new ArrayList<>(), new MapperMethod.ParamMap());
            return;
        }
        this.boundSql = new BoundSql(baseMappedStatement.getConfiguration(), sql,
                baseBoundSql.getParameterMappings(), baseBoundSql.getParameterObject());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}