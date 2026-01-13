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
public class PastValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    // https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/past
    static {
        TYPE_SOURCES.put(Instant.class, PastValueSource::getInstantStream);
        TYPE_SOURCES.put(LocalTime.class, PastValueSource::getLocalTimeStream);
        TYPE_SOURCES.put(LocalDate.class, PastValueSource::getLocalDateStream);
        TYPE_SOURCES.put(LocalDateTime.class, PastValueSource::getLocalDateTimeStream);
        TYPE_SOURCES.put(OffsetTime.class, PastValueSource::getOffsetTimeStream);
        TYPE_SOURCES.put(OffsetDateTime.class, PastValueSource::getOffsetDateTimeStream);
        TYPE_SOURCES.put(Year.class, PastValueSource::getYearStream);
        TYPE_SOURCES.put(YearMonth.class, PastValueSource::getYearMonthStream);
        TYPE_SOURCES.put(ZonedDateTime.class, PastValueSource::getZonedDateTimeStream);
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
        var smallestInstant = Instant.MIN;

        return Stream.of(smallestInstant, currentInstant.minusSeconds(1L));
    }

    private static Stream<LocalTime> getLocalTimeStream(Object[] args) {
        var currentLocalTime = LocalTime.now();
        var smallestLocalTime = LocalTime.MIN;

        return Stream.of(smallestLocalTime, currentLocalTime.minusSeconds(1L));
    }

    private static Stream<LocalDate> getLocalDateStream(Object[] args) {
        var currentDate = LocalDate.now();
        var smallestDate = LocalDate.MIN;

        return Stream.of(smallestDate, currentDate.minusDays(1L));
    }

    private static Stream<LocalDateTime> getLocalDateTimeStream(Object[] args) {
        var currentDateTime = LocalDateTime.now();
        var smallestDateTime = LocalDateTime.MIN;

        return Stream.of(smallestDateTime, currentDateTime.minusSeconds(1L));
    }

    private static Stream<OffsetTime> getOffsetTimeStream(Object[] args) {
        var currentOffsetTime = OffsetTime.now(ZoneOffset.UTC);
        var smallestOffsetTime = OffsetTime.MIN;

        return Stream.of(smallestOffsetTime, currentOffsetTime.minusSeconds(1L));
    }

    private static Stream<OffsetDateTime> getOffsetDateTimeStream(Object[] args) {
        var currentOffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        var smallestOffsetDateTime = OffsetDateTime.MIN;

        return Stream.of(smallestOffsetDateTime, currentOffsetDateTime.minusSeconds(1L));
    }

    private static Stream<Year> getYearStream(Object[] args) {
        var currentYear = Year.now();
        var smallestYear = Year.of(Year.MIN_VALUE);

        return Stream.of(smallestYear, currentYear.minusYears(1L));
    }

    private static Stream<YearMonth> getYearMonthStream(Object[] args) {
        var currentYearMonth = YearMonth.now();
        var smallestYearMonth = YearMonth.of(Year.MIN_VALUE, Month.JANUARY);

        return Stream.of(smallestYearMonth, currentYearMonth.minusMonths(1L));
    }

    private static Stream<ZonedDateTime> getZonedDateTimeStream(Object[] args) {
        var currentZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        var smallestZonedDateTime = LocalDateTime.MIN.atZone(ZoneOffset.MAX);

        return Stream.of(smallestZonedDateTime, currentZonedDateTime.minusSeconds(1L));
    }
}
