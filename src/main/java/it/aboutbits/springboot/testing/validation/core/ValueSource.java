package it.aboutbits.springboot.testing.validation.core;

import java.util.stream.Stream;

public interface ValueSource {
    Stream<?> values(Class<?> propertyClass, Object... args);
}
