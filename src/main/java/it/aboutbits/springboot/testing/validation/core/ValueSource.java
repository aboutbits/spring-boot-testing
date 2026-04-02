package it.aboutbits.springboot.testing.validation.core;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

@NullMarked
public interface ValueSource {
    <T> Stream<@Nullable T> values(Class<T> propertyClass, Object... args);
}
