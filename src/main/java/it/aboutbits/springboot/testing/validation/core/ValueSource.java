package it.aboutbits.springboot.testing.validation.core;

import java.util.stream.Stream;

public interface ValueSource {
    <T> Stream<T> values(Class<T> propertyClass, Object... args);
}
