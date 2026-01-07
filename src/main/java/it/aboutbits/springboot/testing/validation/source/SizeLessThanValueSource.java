package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.LongFunction;
import java.util.stream.Stream;

@NullMarked
public class SizeLessThanValueSource implements ValueSource {
    private static final Map<Class<?>, LongFunction<Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(String.class, SizeLessThanValueSource::getStringStream);
        TYPE_SOURCES.put(CharSequence.class, SizeLessThanValueSource::getStringStream);
        TYPE_SOURCES.put(Collection.class, SizeLessThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(List.class, SizeLessThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(ArrayList.class, SizeLessThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(LinkedList.class, SizeLessThanValueSource::getLinkedListStream);
        TYPE_SOURCES.put(Set.class, SizeLessThanValueSource::getHashSetStream);
        TYPE_SOURCES.put(HashSet.class, SizeLessThanValueSource::getHashSetStream);
        TYPE_SOURCES.put(LinkedHashSet.class, SizeLessThanValueSource::getLinkedHashSetStream);
        TYPE_SOURCES.put(TreeSet.class, SizeLessThanValueSource::getTreeSetStream);
        TYPE_SOURCES.put(Map.class, SizeLessThanValueSource::getHashMapStream);
        TYPE_SOURCES.put(HashMap.class, SizeLessThanValueSource::getHashMapStream);
        TYPE_SOURCES.put(LinkedHashMap.class, SizeLessThanValueSource::getLinkedHashMapStream);
        TYPE_SOURCES.put(TreeMap.class, SizeLessThanValueSource::getTreeMapStream);
        TYPE_SOURCES.put(ConcurrentMap.class, SizeLessThanValueSource::getConcurrentHashMapStream);
        TYPE_SOURCES.put(ConcurrentHashMap.class, SizeLessThanValueSource::getConcurrentHashMapStream);
    }

    @SuppressWarnings("unused")
    public static void registerType(Class<?> type, LongFunction<Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        var value = (Long.valueOf((long) args[0]));

        if (value == 0) {
            return Stream.empty();
        }

        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive or zero. A size cannot be less than empty.");
        }

        if (propertyClass.isArray()) {
            return (Stream<T>) arrayFunction(propertyClass, value);
        }

        var sourceFunction = TYPE_SOURCES.get(propertyClass);
        if (sourceFunction != null) {
            return (Stream<T>) sourceFunction.apply(value);
        }

        throw new IllegalArgumentException("Property class not supported!");
    }

    private static Stream<?> arrayFunction(Class<?> arrayClass, long value) {
        return Stream.concat(
                Stream.of(generateArray(0, arrayClass)),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateArray(
                                Math.toIntExact(size),
                                arrayClass
                        ))
        );
    }


    private static Stream<String> getStringStream(long value) {
        return Stream.concat(
                Stream.of(""),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(length -> generateRandomString(Math.toIntExact(length)))
        );
    }


    private static Stream<Collection<?>> getArrayListStream(long value) {
        return Stream.concat(
                Stream.of(new ArrayList<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateCollection(
                                Math.toIntExact(size),
                                new ArrayList<>()
                        ))
        );
    }


    private static Stream<Collection<?>> getLinkedListStream(long value) {
        return Stream.concat(
                Stream.of(new LinkedList<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateCollection(
                                Math.toIntExact(size),
                                new LinkedList<>()
                        ))
        );
    }


    private static Stream<Collection<?>> getHashSetStream(long value) {
        return Stream.concat(
                Stream.of(new HashSet<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateCollection(
                                Math.toIntExact(size),
                                new HashSet<>()
                        ))
        );
    }


    private static Stream<Collection<?>> getLinkedHashSetStream(long value) {
        return Stream.concat(
                Stream.of(new LinkedHashSet<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateCollection(
                                Math.toIntExact(size),
                                new LinkedHashSet<>()
                        ))
        );
    }


    private static Stream<Collection<?>> getTreeSetStream(long value) {
        return Stream.concat(
                Stream.of(new TreeSet<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateCollection(
                                Math.toIntExact(size),
                                new TreeSet<>()
                        ))
        );
    }


    private static Stream<Map<?, ?>> getHashMapStream(long value) {
        return Stream.concat(
                Stream.of(new HashMap<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateMap(
                                Math.toIntExact(size),
                                new HashMap<>()
                        ))
        );
    }


    private static Stream<Map<?, ?>> getLinkedHashMapStream(long value) {
        return Stream.concat(
                Stream.of(new LinkedHashMap<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateMap(
                                Math.toIntExact(size),
                                new LinkedHashMap<>()
                        ))
        );
    }


    private static Stream<Map<?, ?>> getTreeMapStream(long value) {
        return Stream.concat(
                Stream.of(new TreeMap<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateMap(
                                Math.toIntExact(size),
                                new TreeMap<>()
                        ))
        );
    }


    private static Stream<Map<?, ?>> getConcurrentHashMapStream(long value) {
        return Stream.concat(
                Stream.of(new ConcurrentHashMap<>()),
                Stream.iterate(1L, i -> i < value, i -> i + 1)
                        .map(size -> generateMap(
                                Math.toIntExact(size),
                                new ConcurrentHashMap<>()
                        ))
        );
    }

    private static String generateRandomString(int length) {
        // Include printable ASCII characters (32-126) which includes space and common characters
        return RANDOM.ints(length, 32, 127)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static Collection<Object> generateCollection(int size, Collection<Object> collection) {
        for (int i = 0; i < size; i++) {
            collection.add("dummy_" + i); // Add dummy elements, content doesn't matter
        }
        return collection;
    }

    private static Map<Object, Object> generateMap(int size, Map<Object, Object> map) {
        for (int i = 0; i < size; i++) {
            map.put(i, "dummy_" + i); // Add dummy elements, content doesn't matter
        }
        return map;
    }

    private static Object generateArray(int size, Class<?> arrayClass) {
        var componentType = arrayClass.getComponentType();
        return Array.newInstance(componentType, size);
    }
}
