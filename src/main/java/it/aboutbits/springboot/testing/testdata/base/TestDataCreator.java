package it.aboutbits.springboot.testing.testdata.base;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("java:S119")
@NullMarked
public abstract class TestDataCreator<ITEM> {
    @SuppressWarnings("unused")
    protected static final FakerExtended FAKER = new FakerExtended();

    protected final int numberOfItems;

    protected TestDataCreator(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    @SuppressWarnings("unused")
    public void commit() {
        create();
    }

    @SuppressWarnings("unused")
    public ITEM returnFirst() {
        return create().getFirst();
    }

    @SuppressWarnings("unused")
    public List<ITEM> returnAll() {
        return create();
    }

    @SafeVarargs
    @SuppressWarnings("unused")
    public final List<ITEM> returnSorted(Comparator<ITEM>... comparators) {
        if (comparators.length == 0) {
            throw new IllegalArgumentException("At least one comparator must be provided");
        }

        var combinedComparator = comparators[0];
        for (var i = 1; i < comparators.length; i++) {
            combinedComparator = combinedComparator.thenComparing(comparators[i]);
        }

        return returnAll().stream().sorted(combinedComparator).toList();
    }

    @SafeVarargs
    @SuppressWarnings({"unchecked", "unused"})
    public final <U extends Comparable<? super U>> List<ITEM> returnSorted(Function<ITEM, ? extends Comparable<?>>... comparators) {
        if (comparators.length == 0) {
            throw new IllegalArgumentException("At least one comparator must be provided");
        }

        var combinedComparator = Comparator.comparing((Function<ITEM, U>) comparators[0]);
        for (var i = 1; i < comparators.length; i++) {
            combinedComparator = combinedComparator.thenComparing((Function<ITEM, U>) comparators[i]);
        }

        return returnAll().stream().sorted(combinedComparator).toList();
    }

    @SuppressWarnings("unused")
    public Set<ITEM> returnSet() {
        return new HashSet<>(create());
    }

    protected List<ITEM> create() {
        var result = new ArrayList<ITEM>();

        for (var index = 0; index < numberOfItems; index++) {
            result.add(
                    create(index)
            );
        }

        return result;
    }

    protected abstract ITEM create(int index);
}
