package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
public class ShorterThanValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(String.class, ShorterThanValueSource::getStream);
    }

    @SuppressWarnings("unused")
    public static void registerType(Class<?> type, Function<Object[], Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        var sourceFunction = TYPE_SOURCES.get(propertyClass);
        if (sourceFunction != null) {
            return (Stream<T>) sourceFunction.apply(args);
        }

        throw new IllegalArgumentException("Property class not supported!");
    }

    private static Stream<String> getStream(Object[] args) {
        var length = Long.valueOf((long) args[0]).intValue();
        if (length <= 0) {
            return Stream.empty();
        }

        var minLength = 0;
        var maxLength = length - 1;

        if (minLength == maxLength) {
            return Stream.of("");
        }

        var randomLength = RANDOM.nextInt(minLength, maxLength);

        return Stream.of(
                generateRandomString(minLength),
                generateRandomString(maxLength),
                generateRandomString(randomLength)
        ).distinct();
    }

    private static String generateRandomString(int length) {
        return RANDOM.ints(length, 32, 127)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
