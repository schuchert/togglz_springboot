package org.shoe.togglz;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestApplicationContext {
    private static ConfigurableApplicationContext context;
    private static String baseUrl;

    static {
        context = SpringApplication.run(Main.class);
        String serverPort = context.getEnvironment().getProperty("local.server.port");
        baseUrl = String.format("http://localhost:%s/", serverPort);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String urlFor(String path) {
        return baseUrl + path;
    }
}
