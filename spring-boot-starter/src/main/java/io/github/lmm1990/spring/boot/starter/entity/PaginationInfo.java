package io.github.lmm1990.spring.boot.starter.entity;

import lombok.Data;

/**
 * 分页信息
 *
 * @author liumingming
 * @since 2021-09-09 15:53
 */
@Data
public class PaginationInfo {

    public PaginationInfo(int p, int pageSize) {
        this.p = p;
        this.pageSize = pageSize;
    }

    public PaginationInfo(int p, int pageSize, String countSql) {
        this.p = p;
        this.pageSize = pageSize;
        this.countSql = countSql;
    }

    /**
     * 当前页码
     */
    private int p;

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 查询数量sql
     */
    private String countSql;

    /**
     * 数据总条数
     */
    private long totalCount;
}
