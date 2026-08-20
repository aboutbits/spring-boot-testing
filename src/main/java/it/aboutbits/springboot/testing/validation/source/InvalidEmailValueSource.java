package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
public class InvalidEmailValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    static {
        TYPE_SOURCES.put(
                String.class,
                (Object[] args) -> Stream.of(
                        "plainaddress",
                        "@example.com",
                        "someone@",
                        "someone@@example.com",
                        "some one@example.com",
                        "someone@exam ple.com",
                        "someone@example..com",
                        "someone@.example.com"
                )
        );
    }

    @SuppressWarnings("unused")
    public static void registerType(Class<?> type, Function<Object[], Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<@Nullable T> values(Class<T> propertyClass, Object... args) {
        var sourceFunction = TYPE_SOURCES.get(propertyClass);
        if (sourceFunction != null) {
            return (Stream<@Nullable T>) sourceFunction.apply(args);
        }

        throw new IllegalArgumentException("Property class not supported!");
    }
}
