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

import javax.sql.CommonDataSource;
import java.util.concurrent.ConcurrentHashMap;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.util.ClassUtils;

/**
 * {@link CommonDataSource} name resolver based on bean name.
 *
 * @author Arthur Gavlyukovskiy
 * @since 1.3.0
 */
public class PoolAwareDataSourceNameResolver implements DataSourceNameResolver {
    private final static boolean HIKARI_AVAILABLE = ClassUtils.isPresent("com.zaxxer.hikari.HikariDataSource", PoolAwareDataSourceNameResolver.class.getClassLoader());
    private final ConcurrentHashMap<CommonDataSource, String> cachedNames = new ConcurrentHashMap<>();

    @Override
    public void add(CommonDataSource dataSource, String fallbackName) {
        cachedNames.putIfAbsent(dataSource, getDataSourceName(dataSource, fallbackName));
    }

    @Override
    public String getName(CommonDataSource dataSource) {
        return cachedNames.getOrDefault(dataSource, "dataSource");
    }

    private String getDataSourceName(CommonDataSource dataSource, String fallbackName) {
        if (HIKARI_AVAILABLE && dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            if (hikariDataSource.getPoolName() != null && !hikariDataSource.getPoolName().startsWith("HikariPool-")) {
                return hikariDataSource.getPoolName();
            }
        }

        return fallbackName;
    }
}
