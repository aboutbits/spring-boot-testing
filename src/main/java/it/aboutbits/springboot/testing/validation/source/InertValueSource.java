package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;

import java.util.stream.Stream;

public class InertValueSource implements ValueSource {
    @Override
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        return Stream.empty();
    }
}
