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
    private final DataSource originalDataSource;

    DecoratedDataSource(DataSource originalDataSource, DataSource decoratedDataSource) {
        super(decoratedDataSource);
        this.originalDataSource = originalDataSource;
    }

    /**
     * Returns original data source, before applying any decorator.
     *
     * @return original data source
     */
    public DataSource getOriginalDataSource() {
        return originalDataSource;
    }

    /**
     * Returns wrapped data source with the decorator applied.
     *
     * @return decorated data source
     */
    public DataSource getDecoratedDataSource() {
        return getTargetDataSource();
    }
}
