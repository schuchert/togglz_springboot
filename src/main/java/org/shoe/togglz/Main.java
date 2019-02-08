package org.shoe.togglz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
