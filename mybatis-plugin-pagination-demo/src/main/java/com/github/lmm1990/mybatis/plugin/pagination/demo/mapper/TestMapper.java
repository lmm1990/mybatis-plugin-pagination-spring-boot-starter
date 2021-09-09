package com.github.lmm1990.mybatis.plugin.pagination.demo.mapper;

import com.github.lmm1990.mybatis.plugin.pagination.demo.entity.TestInfo;
import io.github.lmm1990.spring.boot.starter.entity.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * TestMapper
 *
 * @author liumingming
 * @since 2021-09-09 16:44:08
 */
@Mapper
public interface TestMapper {

    /**
     * 列表
     *
     * @return java.util.List<com.github.lmm1990.mybatis.plugin.pagination.demo.entity.TestInfo>
     * @since 刘明明/2021-09-09 16:41:56
     **/
    @Select("SELECT id, name, tenantId FROM test")
    List<TestInfo> list();

    /**
     * 列表
     *
     * @param status: 状态
     * @return io.github.lmm1990.spring.boot.starter.entity.Page<com.github.lmm1990.mybatis.plugin.pagination.demo.entity.TestInfo>
     * @since 刘明明/2021-09-09 16:42:07
     **/
    @Select("SELECT id, name, tenantId FROM test where status = #{status}")
    Page<TestInfo> listByStatus(int status);
}