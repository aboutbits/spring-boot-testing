package it.aboutbits.springboot.testing.testdata.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestDataDependencyTest {
    @Test
    void perItem_resolvesFreshValueEveryTime() {
        var creations = new AtomicInteger();
        var dependency = TestDataDependency.<Integer>perItem();

        var first = dependency.resolve(creations::incrementAndGet);
        var second = dependency.resolve(creations::incrementAndGet);

        assertThat(first).isEqualTo(1);
        assertThat(second).isEqualTo(2);
        assertThat(creations).hasValue(2);
        assertThat(dependency.isSet()).isFalse();
    }

    @Test
    void set_pinsTheValueAndNeverCreates() {
        var creations = new AtomicInteger();
        var dependency = TestDataDependency.<Integer>perItem();

        dependency.set(42);

        assertThat(dependency.isSet()).isTrue();
        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(42);
        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(42);
        assertThat(creations).hasValue(0);
    }

    @Test
    void share_createsOnceAndReuses() {
        var creations = new AtomicInteger();
        var dependency = TestDataDependency.<Integer>perItem();

        dependency.share();

        assertThat(dependency.isSet()).isFalse();
        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(1);
        assertThat(dependency.isSet()).isTrue();
        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(1);
        assertThat(creations).hasValue(1);
    }

    @Test
    void shared_startsInSharedMode() {
        var creations = new AtomicInteger();
        var dependency = TestDataDependency.<Integer>shared();

        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(1);
        assertThat(dependency.resolve(creations::incrementAndGet)).isEqualTo(1);
        assertThat(creations).hasValue(1);
    }

    @Test
    void shared_resolvesExactlyOnceUnderConcurrency() throws InterruptedException, ExecutionException {
        var creations = new AtomicInteger();
        var dependency = TestDataDependency.<Integer>shared();

        var results = new HashSet<Integer>();
        try (var executor = Executors.newFixedThreadPool(8)) {
            var futures = new ArrayList<Future<Integer>>();
            for (var i = 0; i < 8; i++) {
                futures.add(executor.submit(() -> dependency.resolve(creations::incrementAndGet)));
            }
            for (var future : futures) {
                results.add(future.get());
            }
        }

        assertThat(creations).hasValue(1);
        assertThat(results).containsExactly(1);
    }
}
