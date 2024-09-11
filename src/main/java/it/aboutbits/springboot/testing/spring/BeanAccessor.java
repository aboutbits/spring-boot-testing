package it.aboutbits.springboot.testing.spring;

import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanAccessor implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static <T> T getBean(@NonNull Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not set. Did you @EnableBeanAccessor?");
        }
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        BeanAccessor.applicationContext = applicationContext;
    }
}
