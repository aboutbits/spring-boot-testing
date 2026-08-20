package it.aboutbits.springboot.testing.testdata.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.aboutbits.springboot.testing.testdata.FakerExtended;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntFunction;

@SuppressWarnings("java:S119")
@NullMarked
public abstract class TestDataCreator<ITEM> {
    /// System property that flips the default creation mode of every creator to parallel;
    /// [#sequential()] then opts a single call site back out.
    public static final String PARALLEL_BY_DEFAULT_PROPERTY =
            "it.aboutbits.testing.testdata.parallel-by-default";

    @SuppressWarnings("unused")
    protected static final FakerExtended FAKER = new FakerExtended();

    protected final int numberOfItems;

    private boolean parallel = Boolean.getBoolean(PARALLEL_BY_DEFAULT_PROPERTY);

    protected TestDataCreator(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    /// Creates the items concurrently, one thread per item, instead of sequentially. The returned
    /// list keeps index order, but database side effects (e.g. sequence-assigned ids) interleave
    /// arbitrarily across items.
    ///
    /// Only safe for creators whose per-item creation is independent: a creator that shares lazily
    /// created state across items (e.g. `sameXyz()` memoization) must resolve that state before
    /// creation fans out.
    @SuppressWarnings("unused")
    @CanIgnoreReturnValue
    public TestDataCreator<ITEM> parallel() {
        this.parallel = true;
        return this;
    }

    /// Creates the items sequentially — the default, unless [#PARALLEL_BY_DEFAULT_PROPERTY]
    /// flipped it; then this is the per-call opt-out.
    @SuppressWarnings("unused")
    @CanIgnoreReturnValue
    public TestDataCreator<ITEM> sequential() {
        this.parallel = false;
        return this;
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
        return createItems(this::create);
    }

    /// Runs one full item creation per index and returns the items in index order — sequentially
    /// by default, concurrently after [#parallel()].
    protected final List<ITEM> createItems(IntFunction<ITEM> itemForIndex) {
        if (!parallel || numberOfItems <= 1) {
            var result = new ArrayList<ITEM>();

            for (var index = 0; index < numberOfItems; index++) {
                result.add(
                        itemForIndex.apply(index)
                );
            }

            return result;
        }

        try (var executor = Executors.newFixedThreadPool(numberOfItems)) {
            var futures = new ArrayList<Future<ITEM>>();
            for (var index = 0; index < numberOfItems; index++) {
                var itemIndex = index;
                futures.add(
                        executor.submit(() -> itemForIndex.apply(itemIndex))
                );
            }

            var result = new ArrayList<ITEM>();
            for (var future : futures) {
                result.add(awaitItem(future));
            }

            return result;
        }
    }

    private ITEM awaitItem(Future<ITEM> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Parallel test data creation was interrupted", e);
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case RuntimeException runtimeException -> throw runtimeException;
                case Error error -> throw error;
                case null, default -> throw new IllegalStateException("Parallel test data creation failed", e.getCause());
            }
        }
    }

    protected abstract ITEM create(int index);
}
