package com.techlabs.common.base.utill;

import com.techlabs.platform.core.handler.provider.ApplicationContextProvider;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;

/**
 * {@link ApplicationContext}의 Bean관련 유틸리티
 */
@UtilityClass
public class BeanUtil {
    public static <T> T getBean(Class<T> classType) {
        return ApplicationContextProvider.getApplicationContext().getBean(classType);
    }
    
    public static Object getBean(String name) {
        return ApplicationContextProvider.getApplicationContext().getBean(name);
    }
}