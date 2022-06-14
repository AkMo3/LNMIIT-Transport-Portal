package com.example.application.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("production")
    public DataSource getDataSource() {
        try {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("org.postgresql.Driver");
            dataSourceBuilder.url(dbUrl);
            dataSourceBuilder.username(username);
            dataSourceBuilder.password(password);
            return dataSourceBuilder.build();
        }
        catch (URISyntaxException exception){
            return null;
        }
    }

    @Bean
    @Profile("!production")
    public DataSource getDataSource2() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/LNMIIT_TRANSPORT");
        dataSourceBuilder.username("lnmuser");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
    }
}
