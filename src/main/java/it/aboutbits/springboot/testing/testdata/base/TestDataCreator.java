package it.aboutbits.springboot.testing.testdata.base;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class TestDataCreator<ITEM> {
    protected final int numberOfItems;

    protected TestDataCreator(int numberIfItems) {
        this.numberOfItems = numberIfItems;
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

    public List<ITEM> returnSorted(@NonNull Comparator<ITEM> comparator) {
        return returnAll().stream().sorted(comparator).toList();
    }

    public <U extends Comparable<? super U>> List<ITEM> returnSorted(@NonNull Function<ITEM, U> comparator) {
        return returnAll().stream()
                .sorted(Comparator.comparing(comparator))
                .toList();
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
