package com.ing.study.loan_api.repository;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

@Component
public class DatabaseManager {
    private DataSource dataSource;

    public DatabaseManager() {
        dataSource = createDataSource();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        //TODO: Get jdbc url from application properties
        ds.setJdbcUrl("jdbc:h2:mem:loan-api;USER=SA;");

        return ds;
    }
}
