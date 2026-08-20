package it.aboutbits.springboot.testing.testdata.base;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/// A dependency of a test-data creator that each created item needs a value for.
/// Three modes, chosen while the creator is being configured:
///
///   - fixed:    [#set(Object)] — every item uses the explicitly provided value (`withXyz(id)`)
///   - shared:   [#share()] — the first resolution creates the value, every later one reuses it
///               (`sameXyz()`, or dependencies that are always shared across the items)
///   - per item: neither — every resolution creates a fresh value (the default)
///
/// [#resolve(Supplier)] is safe under parallel item creation: a shared value is created exactly
/// once (competing items wait for it), while per-item creation runs unsynchronized. The
/// configuration methods ([#set(Object)], [#share()]) belong to the builder phase and must not
/// be called once item creation has started.
@NullMarked
public final class TestDataDependency<T> {
    private boolean shared;
    private @Nullable T value;

    private TestDataDependency(boolean shared) {
        this.shared = shared;
    }

    public static <T> TestDataDependency<T> perItem() {
        return new TestDataDependency<>(false);
    }

    public static <T> TestDataDependency<T> shared() {
        return new TestDataDependency<>(true);
    }

    public synchronized void set(T value) {
        this.value = value;
        this.shared = true;
    }

    public synchronized void share() {
        this.shared = true;
    }

    /// True once a value is fixed or has been resolved — used by creators whose dependency is
    /// optional, to decide between "absent" and "resolve".
    public synchronized boolean isSet() {
        return value != null;
    }

    public T resolve(Supplier<T> creator) {
        if (!shared) {
            return creator.get();
        }

        synchronized (this) {
            if (value == null) {
                value = creator.get();
            }
            return value;
        }
    }
}
