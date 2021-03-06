package io.github.lmm1990.spring.boot.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis分页插件自动注册
 *
 * @author liumingming
 * @since 刘明明/2021-09-06 17:18:15
 */
@Configuration
public class PaginationPluginAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PaginationPlugin.class)
    public PaginationPlugin transportClient() {
        return new PaginationPlugin();
    }

    @Bean
    @ConditionalOnMissingBean(PaginationPluginBeanPostProcessor.class)
    public PaginationPluginBeanPostProcessor tenantPluginBeanPostProcessor() {
        return new PaginationPluginBeanPostProcessor();
    }
}
