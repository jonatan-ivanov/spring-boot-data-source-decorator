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

import com.vladmihalcea.flexypool.config.PropertyLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DataSourceDecoratorAutoConfigurationTests {

    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Before
    public void init() {
        EnvironmentTestUtils.addEnvironment(context,
                "datasource.initialize:false",
                "datasource.url:jdbc:h2:mem:testdb-" + new Random().nextInt());
    }

    @After
    public void restore() {
        System.clearProperty(PropertyLoader.PROPERTIES_FILE_PATH);
        context.close();
    }

    @Test
    public void testDecoratingInDefaultOrder() throws Exception {
        context.register(DataSourceAutoConfiguration.class,
                DataSourceDecoratorAutoConfiguration.class,
                PropertyPlaceholderAutoConfiguration.class);
        context.refresh();

        DataSource dataSource = context.getBean(DataSource.class);

        assertThat(((DecoratedDataSource) dataSource).getDecoratingChain())
                .isEqualTo("p6SpyDataSourceDecorator -> proxyDataSourceDecorator -> flexyPoolDataSourceDecorator -> dataSource");
    }
}
