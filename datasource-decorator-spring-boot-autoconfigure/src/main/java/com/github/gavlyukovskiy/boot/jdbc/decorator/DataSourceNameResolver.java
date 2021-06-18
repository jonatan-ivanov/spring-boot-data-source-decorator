package com.github.gavlyukovskiy.boot.jdbc.decorator;

import javax.sql.CommonDataSource;

public interface DataSourceNameResolver {
	void add(CommonDataSource dataSource, String fallbackName);
	String getName(CommonDataSource dataSource);
}
