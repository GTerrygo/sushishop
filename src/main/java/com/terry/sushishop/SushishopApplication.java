package com.terry.sushishop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SushishopApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SushishopApplication.class, args);
    }

}
