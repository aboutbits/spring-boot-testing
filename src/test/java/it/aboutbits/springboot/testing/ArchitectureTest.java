package it.aboutbits.springboot.testing;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.CacheMode;
import it.aboutbits.archunit.toolbox.ArchitectureTestBase;
import org.jspecify.annotations.NullMarked;

@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE,
        cacheMode = CacheMode.PER_CLASS
)
@NullMarked
class ArchitectureTest extends ArchitectureTestBase {
    static final String PACKAGE = "it.aboutbits.springboot.testing";

    static {
        // We declare the replacement in this library, so using the base class here is fine.
        ArchitectureTestBase.BLACKLISTED_CLASSES.remove("net.datafaker.Faker");
    }
}
