package it.aboutbits.springboot.testing.validation.core;

import org.jspecify.annotations.NullMarked;

import java.util.stream.Stream;

@NullMarked
public interface ValueSource {
    <T> Stream<T> values(Class<T> propertyClass, Object... args);
}
