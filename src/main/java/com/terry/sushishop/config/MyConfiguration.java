package com.terry.sushishop.config;

import com.terry.sushishop.util.ChefExecutorPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author
 * @create 2022-01-23-11:33
 */
@Configuration
public class MyConfiguration {
    @Bean
    public ChefExecutorPool chefExecutorPool() {
        return new ChefExecutorPool(3);
    }
}
