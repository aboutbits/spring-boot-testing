package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import it.aboutbits.springboot.toolbox.type.ScaledBigDecimal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class ZeroValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    static {
        TYPE_SOURCES.put(Integer.class, (Object[] args) -> Stream.of(0));
        TYPE_SOURCES.put(int.class, (Object[] args) -> Stream.of(0));

        TYPE_SOURCES.put(Long.class, (Object[] args) -> Stream.of(0L));
        TYPE_SOURCES.put(long.class, (Object[] args) -> Stream.of(0L));

        TYPE_SOURCES.put(Float.class, (Object[] args) -> Stream.of(0F));
        TYPE_SOURCES.put(float.class, (Object[] args) -> Stream.of(0F));

        TYPE_SOURCES.put(Double.class, (Object[] args) -> Stream.of(0D));
        TYPE_SOURCES.put(double.class, (Object[] args) -> Stream.of(0D));

        TYPE_SOURCES.put(BigDecimal.class, (Object[] args) -> Stream.of(BigDecimal.valueOf(0)));
        TYPE_SOURCES.put(ScaledBigDecimal.class, (Object[] args) -> Stream.of(ScaledBigDecimal.valueOf(0)));
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
}
