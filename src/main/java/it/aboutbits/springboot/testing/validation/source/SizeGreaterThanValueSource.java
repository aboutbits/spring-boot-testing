package it.aboutbits.springboot.testing.validation.source;

import it.aboutbits.springboot.testing.validation.core.ValueSource;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.LongFunction;
import java.util.stream.Stream;

public class SizeGreaterThanValueSource implements ValueSource {
    private static final Map<Class<?>, LongFunction<Stream<?>>> TYPE_SOURCES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        TYPE_SOURCES.put(String.class, SizeGreaterThanValueSource::getStringStream);
        TYPE_SOURCES.put(CharSequence.class, SizeGreaterThanValueSource::getStringStream);
        TYPE_SOURCES.put(Collection.class, SizeGreaterThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(List.class, SizeGreaterThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(ArrayList.class, SizeGreaterThanValueSource::getArrayListStream);
        TYPE_SOURCES.put(LinkedList.class, SizeGreaterThanValueSource::getLinkedListStream);
        TYPE_SOURCES.put(Set.class, SizeGreaterThanValueSource::getHashSetStream);
        TYPE_SOURCES.put(HashSet.class, SizeGreaterThanValueSource::getHashSetStream);
        TYPE_SOURCES.put(TreeSet.class, SizeGreaterThanValueSource::getTreeSetStream);
        TYPE_SOURCES.put(Map.class, SizeGreaterThanValueSource::getHashMapStream);
        TYPE_SOURCES.put(HashMap.class, SizeGreaterThanValueSource::getHashMapStream);
        TYPE_SOURCES.put(TreeMap.class, SizeGreaterThanValueSource::getTreeMapStream);
    }

    @SuppressWarnings("unused")
    public static void registerType(Class<?> type, LongFunction<Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        var value = (Long.valueOf((long) args[0]));

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

        return getTestSizes(value)
                .stream()
                .map(size -> generateArray(
                        Math.toIntExact(size),
                        arrayClass
                ));

    }

    @NonNull
    private static Stream<String> getStringStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateRandomString(Math.toIntExact(size)));
    }

    @NonNull
    private static Stream<Collection<?>> getHashSetStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateCollection(
                        Math.toIntExact(size),
                        new HashSet<>()
                ));
    }

    @NonNull
    private static Stream<Collection<?>> getTreeSetStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateCollection(
                        Math.toIntExact(size),
                        new TreeSet<>()
                ));
    }

    @NonNull
    private static Stream<Collection<?>> getArrayListStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateCollection(
                        Math.toIntExact(size),
                        new ArrayList<>()
                ));
    }

    @NonNull
    private static Stream<Collection<?>> getLinkedListStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateCollection(
                        Math.toIntExact(size),
                        new LinkedList<>()
                ));
    }

    @NonNull
    private static Stream<Map<?, ?>> getHashMapStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateMap(
                        Math.toIntExact(size),
                        new HashMap<>()
                ));
    }

    @NonNull
    private static Stream<Map<?, ?>> getTreeMapStream(long value) {
        return getTestSizes(value)
                .stream()
                .map(size -> generateMap(
                        Math.toIntExact(size),
                        new TreeMap<>()
                ));
    }

    private static List<Long> getTestSizes(long value) {
        var sizes = new ArrayList<Long>();

        sizes.add(value + 1);
        sizes.add(value + 2);
        sizes.add(value + 6);

        return sizes;
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
