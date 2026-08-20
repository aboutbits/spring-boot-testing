package it.aboutbits.springboot.testing.testdata.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@NullMarked
class TestDataCreatorTest {
    @Test
    void sequentialByDefault() {
        var creator = new ThreadRecordingCreator(5, index -> "item-" + index);

        var result = creator.returnAll();

        assertThat(result).containsExactly("item-0", "item-1", "item-2", "item-3", "item-4");
        assertThat(creator.threads).hasSize(1);
    }

    @Test
    void parallelKeepsIndexOrder() {
        var creator = new ThreadRecordingCreator(5, index -> "item-" + index);

        var result = creator.parallel().returnAll();

        assertThat(result).containsExactly("item-0", "item-1", "item-2", "item-3", "item-4");
    }

    @Test
    void parallelRunsItemsConcurrently() {
        var allItemsStarted = new CountDownLatch(3);
        var creator = new ThreadRecordingCreator(3, index -> {
            allItemsStarted.countDown();
            try {
                // Only finishes if all items run at the same time.
                if (!allItemsStarted.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Items did not run concurrently");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "item-" + index;
        });

        var result = creator.parallel().returnAll();

        assertThat(result).hasSize(3);
        assertThat(creator.threads).hasSize(3);
    }

    @Test
    void parallelPropagatesItemFailure() {
        var creator = new ThreadRecordingCreator(3, index -> {
            if (index == 1) {
                throw new IllegalArgumentException("item 1 failed");
            }
            return "item-" + index;
        });

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> creator.parallel().returnAll())
                .withMessage("item 1 failed");
    }

    @Test
    void parallelWithSingleItemStaysSequential() {
        var creator = new ThreadRecordingCreator(1, index -> "item-" + index);

        var result = creator.parallel().returnAll();

        assertThat(result).containsExactly("item-0");
        assertThat(creator.threads).containsExactly(Thread.currentThread().getName());
    }

    @Test
    void parallelByDefaultProperty_flipsTheDefault() {
        System.setProperty(TestDataCreator.PARALLEL_BY_DEFAULT_PROPERTY, "true");
        try {
            var creator = new ThreadRecordingCreator(3, index -> "item-" + index);

            var result = creator.returnAll();

            assertThat(result).containsExactly("item-0", "item-1", "item-2");
            assertThat(creator.threads).doesNotContain(Thread.currentThread().getName());
        } finally {
            System.clearProperty(TestDataCreator.PARALLEL_BY_DEFAULT_PROPERTY);
        }
    }

    @Test
    void sequential_optsOutOfTheParallelDefault() {
        System.setProperty(TestDataCreator.PARALLEL_BY_DEFAULT_PROPERTY, "true");
        try {
            var creator = new ThreadRecordingCreator(3, index -> "item-" + index);

            var result = creator.sequential().returnAll();

            assertThat(result).containsExactly("item-0", "item-1", "item-2");
            assertThat(creator.threads).containsExactly(Thread.currentThread().getName());
        } finally {
            System.clearProperty(TestDataCreator.PARALLEL_BY_DEFAULT_PROPERTY);
        }
    }

    private static final class ThreadRecordingCreator extends TestDataCreator<String> {
        private final IntFunction<String> itemForIndex;
        private final Set<String> threads = ConcurrentHashMap.newKeySet();

        private ThreadRecordingCreator(int numberOfItems, IntFunction<String> itemForIndex) {
            super(numberOfItems);
            this.itemForIndex = itemForIndex;
        }

        @Override
        protected String create(int index) {
            threads.add(Thread.currentThread().getName());
            return itemForIndex.apply(index);
        }
    }
}
