package com.techlabs.app.platform.framework.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.techlabs.app.base"})
public class AppJpaConfig {
}
