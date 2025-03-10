package com.techlabs.common.config.http;

import com.techlabs.common.base.handler.interceptor.RestTemplateLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig
{

    @Bean
    public RestTemplate restTemplate()
    {
        try
        {
            RestTemplate template = new RestTemplate();
            template.getInterceptors().add(restTemplateLoggingInterceptor());
            return template;
        } catch (Exception ex)
        {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Bean
    public RestTemplateLoggingInterceptor restTemplateLoggingInterceptor()
    {
        return new RestTemplateLoggingInterceptor();
    }
}
