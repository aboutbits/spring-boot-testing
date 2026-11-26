package it.aboutbits.springboot.testing.validation.other;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;

/// In another package than the test on purpose: a subclass in the test's package does not override the package-private method.
@ArchIgnoreNoProductionCounterpart
@SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
@NullMarked
public class PackagePrivateBase {
    void save(@Valid Item last) {
    }

    public record Item() {
    }
}
