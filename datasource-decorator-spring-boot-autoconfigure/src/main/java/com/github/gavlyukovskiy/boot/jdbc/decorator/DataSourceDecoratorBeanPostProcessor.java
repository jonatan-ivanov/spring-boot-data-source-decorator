/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gavlyukovskiy.boot.jdbc.decorator;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.sql.DataSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link BeanPostProcessor} that wraps all data source beans in {@link DataSource}
 * proxies specified in property 'spring.datasource.type'.
 *
 * @author Arthur Gavlyukovskiy
 */
public class DataSourceDecoratorBeanPostProcessor implements BeanPostProcessor, Ordered, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private DataSourceDecoratorProperties dataSourceDecoratorProperties;
    private DataSourceNameResolver dataSourceNameResolver;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && !ScopedProxyUtils.isScopedTarget(beanName) && !getDataSourceDecoratorProperties().getExcludeBeans().contains(beanName)) {
            Map<String, DataSourceDecorator> decorators = applicationContext.getBeansOfType(DataSourceDecorator.class).entrySet().stream()
                    .sorted(Entry.comparingByValue(AnnotationAwareOrderComparator.INSTANCE))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));

            return decorate((DataSource) bean, beanName, decorators);
        }
        else {
            return bean;
        }
    }

    private DataSource decorate(DataSource dataSource, String name, Map<String, DataSourceDecorator> decorators) {
        getDataSourceNameResolver().addDataSource(name, dataSource);
        DataSource decoratedDataSource = dataSource;
        for (Entry<String, DataSourceDecorator> decoratorEntry : decorators.entrySet()) {
            String decoratorBeanName = decoratorEntry.getKey();
            DataSourceDecorator decorator = decoratorEntry.getValue();

            DataSource dataSourceBeforeDecorating = decoratedDataSource;
            decoratedDataSource = Objects.requireNonNull(decorator.decorate(name, decoratedDataSource), "DataSourceDecorator (" + decoratorBeanName + ", " + decorator + ") should not return null");

            if (dataSourceBeforeDecorating != decoratedDataSource) {
                getDataSourceNameResolver().addDataSource(name, decoratedDataSource);
            }
        }

        return decoratedDataSource;
    }

    private DataSourceDecoratorProperties getDataSourceDecoratorProperties() {
        if (dataSourceDecoratorProperties == null) {
            dataSourceDecoratorProperties = applicationContext.getBean(DataSourceDecoratorProperties.class);
        }

        return dataSourceDecoratorProperties;
    }

    private DataSourceNameResolver getDataSourceNameResolver() {
        if (this.dataSourceNameResolver == null) {
            this.dataSourceNameResolver = applicationContext.getBean(DataSourceNameResolver.class);
        }

        return this.dataSourceNameResolver;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
