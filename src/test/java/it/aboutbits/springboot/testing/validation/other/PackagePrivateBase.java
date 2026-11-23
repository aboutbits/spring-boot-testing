package it.aboutbits.springboot.testing.validation.other;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;

/// A superclass in another package than the test. A method with the same signature in a subclass in another package does not override its package-private method.
@ArchIgnoreNoProductionCounterpart
@NullMarked
@SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
public class PackagePrivateBase {
    void save(@Valid Item last) {
    }

    public record Item() {
    }
}
