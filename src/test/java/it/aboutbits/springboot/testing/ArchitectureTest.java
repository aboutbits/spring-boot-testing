package it.aboutbits.springboot.testing;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.CacheMode;
import it.aboutbits.archunit.toolbox.BaseArchRuleCollection;
import it.aboutbits.archunit.toolbox.rule.base.BlacklistClassesArchRule;
import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import org.jspecify.annotations.NullMarked;

// BaseArchRuleCollection instead of CommonArchRuleCollection: this library has no Spring MVC controllers and no SortMappings, so the two rules of CommonArchRuleCollection have nothing to check.
@AnalyzeClasses(
        packagesOf = ArchitectureTest.class,
        cacheMode = CacheMode.PER_CLASS
)
@NullMarked
@ArchIgnoreNoProductionCounterpart
class ArchitectureTest implements BaseArchRuleCollection {
    static {
        // We declare the replacement in this library, so using the base class here is fine.
        BlacklistClassesArchRule.BLACKLISTED_CLASSES.remove("net.datafaker.Faker");
    }
}
