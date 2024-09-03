package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;

import java.util.stream.Stream;

public class InertValueSource implements ValueSource {
    @Override
    public Stream<Object> values(Class<?> propertyClass, Object... args) {
        return Stream.empty();
    }
}
