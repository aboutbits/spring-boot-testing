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

public class FutureValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    static {
        TYPE_SOURCES.put(LocalDate.class, FutureValueSource::getLocalDateStream);
        TYPE_SOURCES.put(LocalDateTime.class, FutureValueSource::getLocalDatetimeStream);
        TYPE_SOURCES.put(OffsetDateTime.class, FutureValueSource::getOffsetDateTimeStream);
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
        var currentDate = LocalDate.now().plusDays(1);
        var largestDate = LocalDate.MAX;
        return Stream.concat(
                Stream.of(largestDate),
                Stream.iterate(currentDate, date -> date.plusDays(1))
                        .limit(4)
        );
    }

    private static Stream<LocalDateTime> getLocalDatetimeStream(Object[] args) {
        var currentDateTime = LocalDateTime.now().plusDays(1);
        var largestDateTime = LocalDateTime.MAX;
        return Stream.concat(
                Stream.of(largestDateTime, currentDateTime.plusSeconds(1)),
                Stream.iterate(currentDateTime, dateTime -> dateTime.plusHours(1))
                        .limit(4)
        );
    }

    private static Stream<OffsetDateTime> getOffsetDateTimeStream(Object[] args) {
        var currentOffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);
        var largestOffsetDateTime = OffsetDateTime.MAX;
        return Stream.concat(
                Stream.of(largestOffsetDateTime, currentOffsetDateTime.plusSeconds(1)),
                Stream.iterate(currentOffsetDateTime, offsetDateTime -> offsetDateTime.plusHours(1))
                        .limit(2)
        );
    }
}
