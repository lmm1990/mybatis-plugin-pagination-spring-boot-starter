package com.github.lmm1990.mybatis.plugin.pagination.demo.entity;

/**
 * 描述
 *
 * @author liumingming
 * @since 2021-09-06 17:49
 */
public class TestInfo {

    /**
     * 测试信息id
     */
    private int id;

    /**
     * 测试名称
     */
    private String name;

    /**
     * 租户id
     */
    private int tenantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}
