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

// For floating point values: The negative max value is the minimum, MIN_VALUE is the smallest positive
@NullMarked
public class LessThanValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(Byte.class, LessThanValueSource::getByteStream);
        TYPE_SOURCES.put(byte.class, LessThanValueSource::getByteStream);

        TYPE_SOURCES.put(Short.class, LessThanValueSource::getShortStream);
        TYPE_SOURCES.put(short.class, LessThanValueSource::getShortStream);

        TYPE_SOURCES.put(Integer.class, LessThanValueSource::getIntegerStream);
        TYPE_SOURCES.put(int.class, LessThanValueSource::getIntegerStream);

        TYPE_SOURCES.put(Long.class, LessThanValueSource::getLongStream);
        TYPE_SOURCES.put(long.class, LessThanValueSource::getLongStream);

        TYPE_SOURCES.put(Float.class, LessThanValueSource::getFloatStream);
        TYPE_SOURCES.put(float.class, LessThanValueSource::getFloatStream);

        TYPE_SOURCES.put(Double.class, LessThanValueSource::getDoubleStream);
        TYPE_SOURCES.put(double.class, LessThanValueSource::getDoubleStream);

        TYPE_SOURCES.put(BigInteger.class, LessThanValueSource::getBigIntegerStream);
        TYPE_SOURCES.put(BigDecimal.class, LessThanValueSource::getBigDecimalStream);
        TYPE_SOURCES.put(ScaledBigDecimal.class, LessThanValueSource::getScaledBigDecimalStream);
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
        var minValue = Byte.MIN_VALUE;
        var maxValue = (byte) (Long.valueOf((long) args[0]).byteValue() - 1);

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue)
                        .mapToObj(value -> Byte.valueOf((byte) value))
        );
    }


    private static Stream<Short> getShortStream(Object[] args) {
        var minValue = Short.MIN_VALUE;
        var maxValue = (short) (Long.valueOf((long) args[0]).shortValue() - 1);

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue)
                        .mapToObj(value -> Short.valueOf((short) value))
        );
    }


    private static Stream<Integer> getIntegerStream(Object[] args) {
        var minValue = Integer.MIN_VALUE;
        var maxValue = Long.valueOf((long) args[0]).intValue() - 1;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.ints(1, minValue, maxValue).boxed()
        );
    }


    private static Stream<Long> getLongStream(Object[] args) {
        var minValue = Long.MIN_VALUE;
        var maxValue = (long) args[0] - 1;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.longs(1, minValue, maxValue).boxed()
        );
    }


    private static Stream<Float> getFloatStream(Object[] args) {
        var minValue = Float.MAX_VALUE * -1;
        var maxValue = Long.valueOf((long) args[0]).floatValue() - 0.1f;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        Double::floatValue
                )
        );
    }


    private static Stream<Double> getDoubleStream(Object[] args) {
        var minValue = Double.MAX_VALUE * -1;
        var maxValue = Long.valueOf((long) args[0]).doubleValue() - 0.1d;

        return Stream.concat(
                Stream.of(minValue, maxValue),
                RANDOM.doubles(1, minValue, maxValue).boxed()
        );
    }


    private static Stream<BigInteger> getBigIntegerStream(Object[] args) {
        var minValue = Long.MIN_VALUE;
        var maxValue = (long) args[0] - 1;

        return Stream.concat(
                Stream.of(BigInteger.valueOf(minValue), BigInteger.valueOf(maxValue)),
                RANDOM.longs(1, minValue, maxValue).boxed().map(
                        BigInteger::valueOf
                )
        );
    }


    private static Stream<BigDecimal> getBigDecimalStream(Object[] args) {
        var minValue = Double.MAX_VALUE * -1;
        var maxValue = Long.valueOf((long) args[0]).doubleValue() - 0.1d;

        return Stream.concat(
                Stream.of(BigDecimal.valueOf(minValue), BigDecimal.valueOf(maxValue)),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        BigDecimal::valueOf
                )
        );
    }


    private static Stream<ScaledBigDecimal> getScaledBigDecimalStream(Object[] args) {
        var minValue = Double.MAX_VALUE * -1;
        var maxValue = Long.valueOf((long) args[0]).doubleValue() - 0.1d;

        return Stream.concat(
                Stream.of(ScaledBigDecimal.valueOf(minValue), ScaledBigDecimal.valueOf(maxValue)),
                RANDOM.doubles(1, minValue, maxValue).boxed().map(
                        ScaledBigDecimal::valueOf
                )
        );
    }
}
