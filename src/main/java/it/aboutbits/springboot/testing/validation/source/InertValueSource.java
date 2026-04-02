package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

@NullMarked
public class InertValueSource implements ValueSource {
    @Override
    public <T> Stream<@Nullable T> values(Class<T> propertyClass, Object... args) {
        return Stream.empty();
    }
}
