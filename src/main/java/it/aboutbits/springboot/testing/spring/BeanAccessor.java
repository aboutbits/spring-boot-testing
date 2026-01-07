package it.aboutbits.springboot.testing.spring;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class BeanAccessor implements ApplicationContextAware {
    @Nullable
    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not set. Did you @EnableBeanAccessor?");
        }
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        BeanAccessor.applicationContext = applicationContext;
    }
}
