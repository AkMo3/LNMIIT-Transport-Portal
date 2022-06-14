package com.example.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import java.util.Objects;

@Configuration
public class ApplicationInitializer {

    public void onStartup() {

        String port = System.getenv("PORT");
        if (port == null) port = "8080";
        System.out.println("Port set to: " + port);
        System.setProperty("server.port", port);
    }
}
