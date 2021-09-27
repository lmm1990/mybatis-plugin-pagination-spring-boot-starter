package io.github.lmm1990.mybatis.plugin.pagination.spring.boot.starter.core;

import org.apache.ibatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页插件全局变量
 *
 * @author liumingming
 * @since 2021-09-08 16:45
 */
public class PaginationDataHandler {

    /**
     * 分页方法返回值类型列表
     */
    public static final Map<String, Class<?>> PAGINATION_METHOD_RETURN_TYPES = new HashMap<>();

    /**
     * 数据总条数MappedStatement列表
     */
    public static final Map<String, MappedStatement> COUNT_MAPPED_STATEMENTS = new HashMap<>();

    /**
     * 分页sql语句列表
     */
    public static final Map<String,String> PAGINATION_SQLS = new HashMap<>();
}
