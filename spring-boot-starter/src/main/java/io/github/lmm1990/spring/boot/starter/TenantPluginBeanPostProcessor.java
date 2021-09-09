package io.github.lmm1990.spring.boot.starter;

import io.github.lmm1990.spring.boot.starter.core.PageObjectFactory;
import io.github.lmm1990.spring.boot.starter.core.PaginationDataHandler;
import io.github.lmm1990.spring.boot.starter.entity.Page;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 后置处理器，解析mapper方法自定义注解
 *
 * @author liumingming
 * @since 2021-09-03 12:02
 */
@Component
public class TenantPluginBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MapperFactoryBean) {
            MapperFactoryBean mapperFactoryBean = (MapperFactoryBean) bean;

            Configuration configuration = mapperFactoryBean.getSqlSession().getConfiguration();
            configuration.setObjectFactory(new PageObjectFactory());

            final String mapperName = mapperFactoryBean.getObjectType().getName();
            Method[] methods = mapperFactoryBean.getObjectType().getMethods();
            for (Method method : methods) {
                if (method.getReturnType().isAssignableFrom(Page.class)) {
                    Class<?> returnType = (Class<?>) ((((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]));
                    PaginationDataHandler.PAGINATION_METHOD_RETURN_TYPES.put(String.format("%s.%s", mapperName, method.getName()), returnType);
                }
            }
            return bean;
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
