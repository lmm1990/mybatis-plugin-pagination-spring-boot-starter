package io.github.lmm1990.mybatis.plugin.pagination.spring.boot.starter.entity;

import io.github.lmm1990.mybatis.plugin.pagination.spring.boot.starter.utils.PaginationHelper;
import lombok.Data;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息
 *
 * @author liumingming
 * @since 2021-09-06 17:08
 */
@Data
public class Page<T> implements ObjectWrapper {

    /**
     * 当前页码
     */
    private int p;

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 数据总条数
     */
    private long totalCount;

    /**
     * 总页数
     */
    private int totalPage = -1;

    /**
     * 数据列表
     */
    private List<T> records = new ArrayList<>();

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        totalPage = (int) ((totalCount + pageSize - 1) / (float) pageSize);
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGetterNames() {
        return null;
    }

    @Override
    public String[] getSetterNames() {
        return null;
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        this.getRecords().addAll((List<T>) element);
        PaginationInfo basePageInfo = PaginationHelper.getPaginationInfo();
        setP(basePageInfo.getP());
        setPageSize(basePageInfo.getPageSize());
        setTotalCount(basePageInfo.getTotalCount());
        PaginationHelper.clear();
    }
}
