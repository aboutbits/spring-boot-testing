package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class PastValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    static {
        TYPE_SOURCES.put(LocalDate.class, PastValueSource::getLocalDateStream);
        TYPE_SOURCES.put(LocalDateTime.class, PastValueSource::getLocalDatetimeStream);
        TYPE_SOURCES.put(OffsetDateTime.class, PastValueSource::getOffsetDateTimeStream);
    }

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


    private static Stream<LocalDate> getLocalDateStream(Object[] args) {
        var currentDate = LocalDate.now();
        var smallestDate = LocalDate.MIN;

        return Stream.of(smallestDate, currentDate.minusDays(1));
    }

    private static Stream<LocalDateTime> getLocalDatetimeStream(Object[] args) {
        var currentDateTime = LocalDateTime.now();
        var smallestDateTime = LocalDateTime.MIN;

        return Stream.of(smallestDateTime, currentDateTime.minusSeconds(1));
    }

    private static Stream<OffsetDateTime> getOffsetDateTimeStream(Object[] args) {
        var currentOffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        var smallestOffsetDateTime = OffsetDateTime.MIN;

        return Stream.of(smallestOffsetDateTime, currentOffsetDateTime.minusSeconds(1));
    }
}
