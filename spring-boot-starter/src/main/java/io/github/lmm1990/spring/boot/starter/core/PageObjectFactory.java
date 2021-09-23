package io.github.lmm1990.spring.boot.starter.core;

import io.github.lmm1990.spring.boot.starter.entity.Page;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Collection;
import java.util.List;

/**
 * 分页对象工厂
 *
 * @author liumingming
 * @since 2021-09-09 9:38
 */
public class PageObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        return super.create(type);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        if(type == Page.class){
            return true;
        }
        return Collection.class.isAssignableFrom(type);
    }
}
