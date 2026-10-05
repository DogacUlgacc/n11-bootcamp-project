package com.dogac.logging.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.dogac.logging.aspect.LogExecutionTimeAspect;

@AutoConfiguration
public class LoggingAutoConfiguration {

    @Bean
    public LogExecutionTimeAspect logExecutionTimeAspect() {
        return new LogExecutionTimeAspect();
    }
}