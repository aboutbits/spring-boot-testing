package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
public class FutureValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    // https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/future
    static {
        TYPE_SOURCES.put(Instant.class, FutureValueSource::getInstantStream);
        TYPE_SOURCES.put(LocalTime.class, FutureValueSource::getLocalTimeStream);
        TYPE_SOURCES.put(LocalDate.class, FutureValueSource::getLocalDateStream);
        TYPE_SOURCES.put(LocalDateTime.class, FutureValueSource::getLocalDateTimeStream);
        TYPE_SOURCES.put(OffsetTime.class, FutureValueSource::getOffsetTimeStream);
        TYPE_SOURCES.put(OffsetDateTime.class, FutureValueSource::getOffsetDateTimeStream);
        TYPE_SOURCES.put(Year.class, FutureValueSource::getYearStream);
        TYPE_SOURCES.put(YearMonth.class, FutureValueSource::getYearMonthStream);
        TYPE_SOURCES.put(ZonedDateTime.class, FutureValueSource::getZonedDateTimeStream);
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

    private static Stream<Instant> getInstantStream(Object[] args) {
        var currentInstant = Instant.now();
        var largestInstant = Instant.MAX;

        return Stream.of(currentInstant.plusSeconds(1L), largestInstant);
    }

    private static Stream<LocalTime> getLocalTimeStream(Object[] args) {
        var currentLocalTime = LocalTime.now();
        var largestLocalTime = LocalTime.MAX;

        return Stream.of(currentLocalTime.plusSeconds(1L), largestLocalTime);
    }

    private static Stream<LocalDate> getLocalDateStream(Object[] args) {
        var currentDate = LocalDate.now();
        var largestDate = LocalDate.MAX;

        return Stream.of(currentDate.plusDays(1L), largestDate);
    }

    private static Stream<LocalDateTime> getLocalDateTimeStream(Object[] args) {
        var currentDateTime = LocalDateTime.now();
        var largestDateTime = LocalDateTime.MAX;

        return Stream.of(currentDateTime.plusSeconds(1L), largestDateTime);
    }

    private static Stream<OffsetTime> getOffsetTimeStream(Object[] args) {
        var currentOffsetTime = OffsetTime.now(ZoneOffset.UTC);
        var largestOffsetTime = OffsetTime.MAX;

        return Stream.of(currentOffsetTime.plusSeconds(1L), largestOffsetTime);
    }

    private static Stream<OffsetDateTime> getOffsetDateTimeStream(Object[] args) {
        var currentOffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        var largestOffsetDateTime = OffsetDateTime.MAX;

        return Stream.of(currentOffsetDateTime.plusSeconds(1L), largestOffsetDateTime);
    }

    private static Stream<Year> getYearStream(Object[] args) {
        var currentYear = Year.now();
        var largestYear = Year.of(Year.MAX_VALUE);

        return Stream.of(currentYear.plusYears(1L), largestYear);
    }

    private static Stream<YearMonth> getYearMonthStream(Object[] args) {
        var currentYearMonth = YearMonth.now();
        var largestYearMonth = YearMonth.of(Year.MAX_VALUE, Month.DECEMBER);

        return Stream.of(currentYearMonth.plusMonths(1L), largestYearMonth);
    }

    private static Stream<ZonedDateTime> getZonedDateTimeStream(Object[] args) {
        var currentZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        var largestZonedDateTime = LocalDateTime.MAX.atZone(ZoneOffset.MIN);

        return Stream.of(currentZonedDateTime.plusSeconds(1L), largestZonedDateTime);
    }
}
