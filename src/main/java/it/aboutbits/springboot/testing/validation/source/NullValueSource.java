package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class NullValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    public static void registerType(Class<?> type, Function<Object[], Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    public Stream<?> values(Class<?> propertyClass, Object... args) {
        var sourceFunction = TYPE_SOURCES.get(propertyClass);
        if (sourceFunction != null) {
            return sourceFunction.apply(args);
        }

        return Stream.of((Object) null);
    }
}
