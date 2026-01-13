package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;

import java.util.stream.Stream;

@NullMarked
public class InertValueSource implements ValueSource {
    @Override
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        return Stream.empty();
    }
}
