package it.aboutbits.springboot.testing.testdata.base;

import lombok.NonNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AllTestDataReader<ITEM> {
    public ITEM returnFirst() {
        return (ITEM) this.returnAll().getFirst();
    }

    public List<ITEM> returnAll() {
        return this.fetch();
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

    public <U extends Comparable<? super U>> AllAndFiltered<ITEM> returnFiltered(@NonNull Predicate<ITEM> predicate) {
        var all = this.returnAll();
        return new AllAndFiltered<ITEM>(
                all,
                all.stream().filter(predicate).toList(),
                all.stream().filter(item -> !predicate.test(item)).toList()
        );
    }

    public Set<ITEM> returnSet() {
        return new HashSet<>(this.returnAll());
    }

    protected abstract List<ITEM> fetch();

    public record AllAndFiltered<T>(@NonNull List<T> all, @NonNull List<T> filtered, @NonNull List<T> other) {

    }
}
