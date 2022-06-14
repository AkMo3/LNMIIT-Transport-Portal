package com.example.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Configuration
public class DataSourceConfig {

    @Autowired private Environment environment;

    @Bean
    public DataSource getDataSource() {
        String currentProfile = Arrays.toString(this.environment.getActiveProfiles());
        System.out.println("Current Env: " + currentProfile);
        if (currentProfile.equals("[dev]")) {
            return getDataSource2();
        }
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

    public DataSource getDataSource2() {
        System.out.println("Current Env: " + Arrays.toString(this.environment.getActiveProfiles()));
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/LNMIIT_TRANSPORT");
        dataSourceBuilder.username("lnmuser");
        dataSourceBuilder.password("Password@123");
        return dataSourceBuilder.build();
    }
}
