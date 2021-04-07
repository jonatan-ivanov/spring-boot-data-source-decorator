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

import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface that implicitly added to the CGLIB proxy of {@link DataSource}.
 *
 * Returns link of both real {@link DataSource}, decorated {@link DataSource}
 * and all decorating chain including decorator bean name, instance and result of decorating.
 *
 * @author Arthur Gavlyukovskiy
 * @since 1.2.2
 */
public class DecoratedDataSource extends DelegatingDataSource {

    private final String beanName;
    private final DataSource realDataSource;
    private final DataSource decoratedDataSource;
    private final List<DataSourceDecorationStage> decoratingChain;

    DecoratedDataSource(String beanName, DataSource realDataSource, DataSource decoratedDataSource, List<DataSourceDecorationStage> decoratingChain) {
        super(decoratedDataSource);
        this.beanName = beanName;
        this.realDataSource = realDataSource;
        this.decoratedDataSource = decoratedDataSource;
        this.decoratingChain = decoratingChain;
    }

    public String getBeanName() {
        return beanName;
    }

    public DataSource getRealDataSource() {
        return realDataSource;
    }

    public DataSource getDecoratedDataSource() {
        return decoratedDataSource;
    }

    public List<DataSourceDecorationStage> getDecoratingChain() {
        return decoratingChain;
    }

    @Override
    public String toString() {
        return decoratingChain.stream()
                .map(entry -> entry.getBeanName() + " [" + entry.getDataSource().getClass().getName() + "]")
                .collect(Collectors.joining(" -> ")) + " -> " + beanName + " [" + realDataSource.getClass().getName() + "]";
    }
}
