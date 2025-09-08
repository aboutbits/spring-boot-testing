package it.aboutbits.springboot.testing.testdata.base;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class TestDataCreator<ITEM> {
    protected static final FakerExtended FAKER = new FakerExtended();

    protected final int numberOfItems;

    protected TestDataCreator(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void commit() {
        create();
    }

    public ITEM returnFirst() {
        return create().getFirst();
    }

    public List<ITEM> returnAll() {
        return create();
    }

    @SafeVarargs
    public final List<ITEM> returnSorted(@NonNull Comparator<ITEM>... comparators) {
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
    @SuppressWarnings("unchecked")
    public final <U extends Comparable<? super U>> List<ITEM> returnSorted(@NonNull Function<ITEM, ? extends Comparable<?>>... comparators) {
        if (comparators.length == 0) {
            throw new IllegalArgumentException("At least one comparator must be provided");
        }

        var combinedComparator = Comparator.comparing((Function<ITEM, U>) comparators[0]);
        for (var i = 1; i < comparators.length; i++) {
            combinedComparator = combinedComparator.thenComparing((Function<ITEM, U>) comparators[i]);
        }

        return returnAll().stream().sorted(combinedComparator).toList();
    }

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
