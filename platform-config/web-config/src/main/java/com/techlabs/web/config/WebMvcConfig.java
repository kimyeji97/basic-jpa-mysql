package com.techlabs.web.config;

import com.techlabs.common.base.CommonConst;
import com.techlabs.platform.core.util.ResourceScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * 기본 Mvc Configuration
 * 
 * @author yjkim
 *
 */
@Slf4j
// configuration check :
// https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = WebMvcConfig.class)
public class WebMvcConfig implements WebMvcConfigurer
{
    // private final AuthenticationInterceptor autthenticationInterceptor;
    //
    // public WebMvcConfig(AuthenticationInterceptor autthenticationInterceptor)
    // {
    // this.autthenticationInterceptor = autthenticationInterceptor;
    // }

//     @Autowired
//     private MappingJackson2HttpMessageConverter jsonConvertor;
//     @Override
//     public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
//     {
//     converters.add(0, jsonConvertor);
//     }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
    {
        configurer.favorParameter(true).defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry)
    {
        Set<BeanDefinition> beanDefinitions = new HashSet<BeanDefinition>();
        beanDefinitions
            .addAll(ResourceScanner.scanByAssignableFilter(CommonConst.JACKSON_HANDLER_PKG , Converter.class));

        for (BeanDefinition bd : beanDefinitions)
        {
            String beanClassName = bd.getBeanClassName();
            try
            {
                Class<?> clazz = Class.forName(beanClassName);
                if (Converter.class.isAssignableFrom(clazz))
                {
                    registry.addConverter((Converter) clazz.getConstructor().newInstance());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
            {
                log.warn("Unknown Error for registering Spring converter > className:{}", beanClassName);
            }
        }
    }

    // @Override
    // public void addInterceptors(InterceptorRegistry registry)
    // {
    // registry.addInterceptor(autthenticationInterceptor);
    // }
}
