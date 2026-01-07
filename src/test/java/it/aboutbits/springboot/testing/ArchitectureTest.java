package it.aboutbits.springboot.testing;

import com.tngtech.archunit.junit.AnalyzeClasses;
import it.aboutbits.springboot.testing.archunit.ArchitectureTestBase;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;

@Slf4j
@SuppressWarnings({"checkstyle:ConstantName"})
@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE
)
@NullMarked
class ArchitectureTest extends ArchitectureTestBase {
    static final String PACKAGE = "it.aboutbits.springboot.testing";

    static {
        // We declare the replacement in this library, so using the base class here is fine.
        ArchitectureTestBase.BLACKLISTED_CLASSES.remove("net.datafaker.Faker");
    }
}
