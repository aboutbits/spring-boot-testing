package it.aboutbits.springboot.testing.valuesource;

import org.junit.jupiter.params.provider.NullSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NullSource
@BlankSource
public @interface NullAndBlankSource {
}
