package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class BiggerThanValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(Integer.class, BiggerThanValueSource::getIntegerStream);
        TYPE_SOURCES.put(int.class, BiggerThanValueSource::getIntegerStream);

        TYPE_SOURCES.put(Float.class, BiggerThanValueSource::getFloatStream);
        TYPE_SOURCES.put(float.class, BiggerThanValueSource::getFloatStream);

        TYPE_SOURCES.put(Long.class, BiggerThanValueSource::getLongStream);
        TYPE_SOURCES.put(long.class, BiggerThanValueSource::getLongStream);

        TYPE_SOURCES.put(Double.class, BiggerThanValueSource::getDoubleStream);
        TYPE_SOURCES.put(double.class, BiggerThanValueSource::getDoubleStream);

        TYPE_SOURCES.put(BigDecimal.class, BiggerThanValueSource::getBigDecimalStream);
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

    @NotNull
    private static Stream<BigDecimal> getBigDecimalStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).doubleValue() + 0.1d;
        var maxValue = Double.MAX_VALUE;

        return Stream.concat(
                Stream.of(BigDecimal.valueOf(minValue), BigDecimal.valueOf(maxValue)),
                RANDOM.doubles(minValue, maxValue).limit(5).boxed().map(BigDecimal::valueOf)
        );
    }

    @NotNull
    private static Stream<Double> getDoubleStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).doubleValue() + 0.1d;
        var maxValue = Double.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(minValue, maxValue).limit(5).boxed()
        );
    }

    @NotNull
    private static Stream<Long> getLongStream(Object[] args) {
        var minValue = (long) args[0] + 1;
        var maxValue = Long.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.longs(minValue, maxValue).limit(5).boxed()
        );
    }

    @NotNull
    private static Stream<Float> getFloatStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).floatValue() + 0.1f;
        var maxValue = Float.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(minValue, maxValue).limit(5).boxed().map(
                        Double::floatValue
                )
        );
    }

    @NotNull
    private static Stream<Integer> getIntegerStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).intValue() + 1;
        var maxValue = Integer.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(minValue, maxValue).limit(5).boxed()
        );
    }
}
