package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import it.aboutbits.springboot.toolbox.type.ScaledBigDecimal;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
public class BiggerThanValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(Byte.class, BiggerThanValueSource::getByteStream);
        TYPE_SOURCES.put(byte.class, BiggerThanValueSource::getByteStream);

        TYPE_SOURCES.put(Short.class, BiggerThanValueSource::getShortStream);
        TYPE_SOURCES.put(short.class, BiggerThanValueSource::getShortStream);

        TYPE_SOURCES.put(Integer.class, BiggerThanValueSource::getIntegerStream);
        TYPE_SOURCES.put(int.class, BiggerThanValueSource::getIntegerStream);

        TYPE_SOURCES.put(Long.class, BiggerThanValueSource::getLongStream);
        TYPE_SOURCES.put(long.class, BiggerThanValueSource::getLongStream);

        TYPE_SOURCES.put(Float.class, BiggerThanValueSource::getFloatStream);
        TYPE_SOURCES.put(float.class, BiggerThanValueSource::getFloatStream);

        TYPE_SOURCES.put(Double.class, BiggerThanValueSource::getDoubleStream);
        TYPE_SOURCES.put(double.class, BiggerThanValueSource::getDoubleStream);

        TYPE_SOURCES.put(BigInteger.class, BiggerThanValueSource::getBigIntegerStream);
        TYPE_SOURCES.put(BigDecimal.class, BiggerThanValueSource::getBigDecimalStream);
        TYPE_SOURCES.put(ScaledBigDecimal.class, BiggerThanValueSource::getScaledBigDecimalStream);
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

    private static Stream<Byte> getByteStream(Object[] args) {
        var minValue = (byte) (Long.valueOf((long) args[0]).byteValue() + 1);
        var maxValue = Byte.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue)
                        .mapToObj(value -> Byte.valueOf((byte) value))
        );
    }

    private static Stream<Short> getShortStream(Object[] args) {
        var minValue = (short) (Long.valueOf((long) args[0]).shortValue() + 1);
        var maxValue = Short.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue)
                        .mapToObj(value -> Short.valueOf((short) value))
        );
    }

    private static Stream<Integer> getIntegerStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).intValue() + 1;
        var maxValue = Integer.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue).boxed()
        );
    }

    private static Stream<Long> getLongStream(Object[] args) {
        var minValue = (long) args[0] + 1;
        var maxValue = Long.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.longs(1, minValue, maxValue).boxed()
        );
    }

    private static Stream<Float> getFloatStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).floatValue() + 0.1f;
        var maxValue = Float.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        Double::floatValue
                )
        );
    }

    private static Stream<Double> getDoubleStream(Object[] args) {
        var minValue = Long.valueOf((long) args[0]).doubleValue() + 0.1d;
        var maxValue = Double.MAX_VALUE;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(1, minValue, maxValue).boxed()
        );
    }

    private static Stream<BigInteger> getBigIntegerStream(Object[] args) {
        var minValue = (long) args[0] + 1;
        var maxValue = Long.MAX_VALUE;

        return Stream.concat(
                Stream.of(BigInteger.valueOf(minValue), BigInteger.valueOf(maxValue)),
                RANDOM.longs(1, minValue, maxValue).boxed().map(
                        BigInteger::valueOf
                )
        );
    }

    private static Stream<BigDecimal> getBigDecimalStream(Object[] args) {
        var minValue = (long) args[0] + 0.1d;
        var maxValue = Double.MAX_VALUE;

        return Stream.concat(
                Stream.of(BigDecimal.valueOf(minValue), BigDecimal.valueOf(maxValue)),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        BigDecimal::valueOf
                )
        );
    }

    private static Stream<ScaledBigDecimal> getScaledBigDecimalStream(Object[] args) {
        var minValue = (long) args[0] + 0.1d;
        var maxValue = Double.MAX_VALUE;

        return Stream.concat(
                Stream.of(ScaledBigDecimal.valueOf(minValue), ScaledBigDecimal.valueOf(maxValue)),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        ScaledBigDecimal::valueOf
                )
        );
    }
}
