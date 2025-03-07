package com.techlabs.admin.platform.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.techlabs.admin.base"})
public class JpaConfig {
}
