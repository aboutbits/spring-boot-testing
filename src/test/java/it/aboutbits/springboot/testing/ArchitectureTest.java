package it.aboutbits.springboot.testing;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import it.aboutbits.springboot.testing.support.archunit.ArchIgnoreGroupName;
import it.aboutbits.springboot.testing.support.archunit.ArchIgnoreNoProductionCounterpart;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@Slf4j
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "checkstyle:ConstantName"})
@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE
)
@NullMarked
class ArchitectureTest {
    static final String PACKAGE = "it.aboutbits.springboot.testing";

    /*
     * This is needed because JUnit does not recognize the @ArchTest when run via mvnw.
     *
     * So JUnit basically tests this function where we then conditionally run the individual ArchUnit tests based if it is a Maven environment or not.
     */
    @Test
    void runArchitectureTests() {
        // Only run the manual execution when running via Maven
        // This prevents the tests from running twice when executed normally
        if (isRunningWithMaven()) {
            // Manually execute all @ArchTest annotated fields
            var classes = new ClassFileImporter().importPackages(PACKAGE);

            // Get all fields in this class
            for (var field : ArchitectureTest.class.getDeclaredFields()) {
                // Check if the field is annotated with @ArchTest
                if (field.isAnnotationPresent(ArchTest.class)) {
                    try {
                        field.setAccessible(true);

                        // Get the field value (should be an ArchRule)
                        var value = field.get(null);

                        if (value instanceof ArchRule rule) {
                            rule.check(classes);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access @ArchTest field: " + field.getName(), e);
                    } catch (Exception e) {
                        // If a rule is violated, it will throw an exception
                        // We want to fail the test in this case
                        throw new AssertionError("Architecture rule violated: " + field.getName(), e);
                    }
                }
            }
        }
    }

    private static final Set<String> BLACKLISTED_METHODS = Set.of(
            // assertThat (allowed is only org.assertj.core.api.Assertions.assertThat)
            "org.assertj.core.api.AssertionsForClassTypes.assertThat",
            "org.assertj.core.api.AssertionsForInterfaceTypes.assertThat",
            "org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat",
            "org.assertj.core.api.ClassBasedNavigableListAssert.assertThat",
            "org.assertj.core.api.FactoryBasedNavigableIterableAssert.assertThat",
            "org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat",
            "org.assertj.core.api.Java6Assertions.assertThat",
            "org.hamcrest.MatcherAssert.assertThat",
            "org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat",
            "org.junit.Assert.assertTrue",
            "org.junit.Assert.assertFalse",
            "org.junit.Assert.fail",
            "org.junit.Assert.assertEquals",
            "org.junit.Assert.assertNotEquals",
            "org.junit.Assert.assertArrayEquals",
            "org.junit.Assert.assertSame",
            "org.junit.Assert.assertNotSame",
            "org.junit.Assert.assertThat",
            "org.junit.Assert.assertThrows"
    );

    private static final Set<String> BLACKLISTED_CLASSES = Set.of(
    );

    private static final Set<String> BLACKLISTED_ANNOTATIONS = Set.of(
            "org.junit.After",
            "org.junit.AfterClass",
            "org.junit.Before",
            "org.junit.BeforeClass",
            "org.junit.ClassRule",
            "org.junit.FixMethodOrder",
            "org.junit.Ignore",
            "org.junit.Rule",
            "org.junit.Test",
            //  (allowed is only org.jspecify.annotations.NonNull)
            "lombok.NonNull",
            "edu.umd.cs.findbugs.annotations.NonNull",
            "io.micrometer.common.lang.NonNull",
            "io.micrometer.core.lang.NonNull",
            "org.springframework.lang.NonNull",
            "org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.NonNull",
            // @NotNull (allowed is only jakarta.validation.constraints.NotNull)
            "com.drew.lang.annotations.NotNull",
            "com.sun.istack.NotNull",
            "org.antlr.v4.runtime.misc.NotNull",
            "org.jetbrains.annotations.NotNull",
            "software.amazon.awssdk.annotations.NotNull",
            // @Nullable (allowed is only org.jspecify.annotations.Nullable)
            "org.springframework.lang.Nullable",
            "com.drew.lang.annotations.Nullable",
            "com.sun.istack.Nullable",
            "edu.umd.cs.findbugs.annotations.Nullable",
            "io.micrometer.common.lang.Nullable",
            "io.micrometer.core.lang.Nullable",
            "jakarta.annotation.Nullable",
            "javax.annotation.Nullable",
            "org.jetbrains.annotations.Nullable",
            "org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable",
            // @Transactional (allowed is only org.springframework.transaction.annotation.Transactional)
            "jakarta.transaction.Transactional"
    );

    // tests
    @ArchTest
    static final ArchRule test_classes_must_be_package_private = classes()
            .that()
            .haveSimpleNameEndingWith("Test")
            .and()
            .resideOutsideOfPackages(".._support..", ".._config..")
            .should()
            .bePackagePrivate();

    @ArchTest
    static final ArchRule nested_test_classes_must_be_package_private = classes()
            .that()
            .areAnnotatedWith(org.junit.jupiter.api.Nested.class)
            .should()
            .bePackagePrivate();

    @ArchTest
    static final ArchRule test_methods_must_be_package_private = methods()
            .that()
            .areAnnotatedWith(Test.class)
            .or()
            .areAnnotatedWith(org.junit.jupiter.api.RepeatedTest.class)
            .or()
            .areAnnotatedWith(org.junit.jupiter.params.ParameterizedTest.class)
            .or()
            .areAnnotatedWith(ArchTest.class)
            .should()
            .bePackagePrivate();

    @ArchTest
    static final ArchRule test_classes_should_be_in_the_same_package_as_their_production_code = classes()
            .that()
            .haveSimpleNameEndingWith("Test")
            .and()
            .doNotHaveSimpleName("ArchitectureTest")
            .and()
            .areNotAnnotatedWith(org.junit.jupiter.api.Disabled.class)
            .and()
            .areNotAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
            .and()
            .areNotAnnotatedWith(ArchIgnoreNoProductionCounterpart.class)
            .and()
            .resideOutsideOfPackages(".._support..", ".._config..")
            .should(beInTheSamePackageAsProductionClass(new ClassFileImporter().importPackages(PACKAGE)))
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule nested_test_classes_have_matching_production_method_name = classes()
            .that()
            .haveSimpleNameEndingWith("Test")
            .and()
            .areNotAnnotatedWith(org.junit.jupiter.api.Disabled.class)
            .and()
            .areNotAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
            .should(nestedClassesMatchProdMethodName(new ClassFileImporter().importPackages(PACKAGE)));

    @ArchTest
    static final ArchRule no_blacklisted_methods_are_used = classes()
            .should(new ArchCondition<>("not use blacklisted methods or statically import them") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    // Check all method calls from this class
                    for (var method : javaClass.getMethods()) {
                        for (var methodCall : method.getMethodCallsFromSelf()) {
                            var fullMethodName = "%s.%s".formatted(
                                    methodCall.getTargetOwner().getFullName(),
                                    methodCall.getTarget().getName()
                            );

                            if (BLACKLISTED_METHODS.contains(fullMethodName)) {
                                var message = String.format(
                                        "Method %s calls blacklisted method %s (%s.java:%d)",
                                        method.getFullName(),
                                        fullMethodName,
                                        javaClass.getSimpleName(),
                                        methodCall.getSourceCodeLocation().getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(method, message));
                            }
                        }
                    }

                    // Check static initializers for method calls
                    javaClass.getStaticInitializer().ifPresent(staticInitializer -> {
                        for (var methodCall : staticInitializer.getMethodCallsFromSelf()) {
                            var fullMethodName = "%s.%s".formatted(
                                    methodCall.getTargetOwner().getFullName(),
                                    methodCall.getTarget().getName()
                            );

                            if (BLACKLISTED_METHODS.contains(fullMethodName)) {
                                var message = String.format(
                                        "Static initializer in %s calls blacklisted method %s (%s.java:%d)",
                                        javaClass.getFullName(),
                                        fullMethodName,
                                        javaClass.getSimpleName(),
                                        methodCall.getSourceCodeLocation().getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(staticInitializer, message));
                            }
                        }
                    });
                }
            });

    @ArchTest
    static final ArchRule no_blacklisted_classes_are_used = noClasses()
            .should()
            .dependOnClassesThat(
                    new DescribedPredicate<JavaClass>("not use blacklisted classes") {
                        @Override
                        public boolean test(JavaClass javaClass) {
                            return BLACKLISTED_CLASSES.contains(javaClass.getFullName());
                        }
                    }
            );

    @ArchTest
    static final ArchRule no_blacklisted_annotations_are_used = classes()
            .should(new ArchCondition<>(
                    "not use blacklisted annotations on classes, methods, method parameters, or fields"
            ) {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    // Check annotations on the class itself
                    for (var annotation : javaClass.getAnnotations()) {
                        if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                            var message = String.format(
                                    "Class %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                    javaClass.getFullName(),
                                    annotation.getRawType().getFullName(),
                                    javaClass.getSimpleName(),
                                    javaClass.getSourceCodeLocation().getLineNumber()
                            );
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                        }
                    }

                    // Check annotations on methods and their parameters
                    for (var method : javaClass.getMethods()) {
                        // Check method annotations
                        for (var annotation : method.getAnnotations()) {
                            if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                                var message = String.format(
                                        "Method %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                        method.getFullName(),
                                        annotation.getRawType().getFullName(),
                                        javaClass.getSimpleName(),
                                        method.getSourceCodeLocation().getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(method, message));
                            }
                        }
                        // Check method parameter annotations
                        for (var parameter : method.getParameters()) {
                            for (var annotation : parameter.getAnnotations()) {
                                if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                                    var message = String.format(
                                            "Parameter %s of method %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                            parameter.getIndex(),
                                            method.getFullName(),
                                            annotation.getRawType().getFullName(),
                                            javaClass.getSimpleName(),
                                            method.getSourceCodeLocation().getLineNumber()
                                    ); // Parameter doesn't have its own SLOC, use method's
                                    events.add(SimpleConditionEvent.violated(parameter, message));
                                }
                            }
                        }
                    }

                    // Check annotations on fields (ArchUnit includes record components as fields)
                    for (var field : javaClass.getFields()) {
                        for (var annotation : field.getAnnotations()) {
                            if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                                var message = String.format(
                                        "Field %s in class %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                        field.getName(),
                                        javaClass.getFullName(),
                                        annotation.getRawType().getFullName(),
                                        javaClass.getSimpleName(),
                                        field.getSourceCodeLocation().getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(field, message));
                            }
                        }
                    }
                }
            });

    @ArchTest
    static final ArchRule top_level_classes_must_be_annotated_with_jspecify = classes()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotAnnotations()
            .should()
            .beAnnotatedWith(NullMarked.class)
            .orShould()
            .beAnnotatedWith(org.jspecify.annotations.NullUnmarked.class);

    /*
     * Advanced ArchUnit should ArchCondition rules
     */
    // Infrastructure layer

    // Web layer
    private static ArchCondition<JavaClass> beInTheSamePackageAsProductionClass(JavaClasses allClasses) {
        return new ArchCondition<>("be in the same package as their production class") {
            @Override
            public void check(JavaClass testClass, ConditionEvents events) {
                var testClassName = testClass.getSimpleName();
                if (!testClassName.endsWith("Test")) {
                    return;
                }

                // Derive the production class name
                var productionClassSimpleName = testClassName.replaceAll("(Test|SecurityTest)$", "");
                var productionClassFullName = testClass.getPackageName() + "." + productionClassSimpleName;

                // Check if the production class exists in the same package
                var productionClass = allClasses.stream()
                        .filter(clazz -> clazz.getFullName().equals(productionClassFullName))
                        .findFirst();

                if (productionClass.isEmpty()) {
                    var message = "Test class <%s> does not have a matching production class <%s> in the same package (%s.java:0)".formatted(
                            testClass.getFullName(),
                            productionClassFullName,
                            productionClassSimpleName
                    );
                    events.add(SimpleConditionEvent.violated(testClass, message));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> nestedClassesMatchProdMethodName(JavaClasses allClasses) {
        return new ArchCondition<>("have a @Nested class that matches the method name in the production code class") {
            @Override
            public void check(JavaClass testClass, ConditionEvents events) {
                // Find all @Nested classes that are nested in the test class, except the $Validation classes
                var nestedClasses = testClass.getPackage()
                        .getClasses()
                        .stream()
                        .filter(clazz -> clazz.getName().startsWith(testClass.getName() + "$")
                                && clazz.isAnnotatedWith(org.junit.jupiter.api.Nested.class)
                                && !clazz.isAnnotatedWith(ArchIgnoreGroupName.class)
                                && !clazz.getName().endsWith("$Validation")
                        )
                        .collect(Collectors.toSet());

                if (nestedClasses.isEmpty()) {
                    return;
                }

                for (var nestedClass : nestedClasses) {
                    /*
                     * We want to skip classes that are a @Nested Group
                     * (for example, $ExportAction of class RwValueListGroupTest)
                     * as they are not directly related to a method in the production class,
                     * but the @Nested classes within this @Nested Group class are.
                     *
                     * Example:
                     * We want to check if method deleteAll
                     * of @Nested test class
                     * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction$DeleteAll
                     * exists in production class
                     * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroup
                     * but this code skips @Nested class
                     * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction
                     */
                    if (nestedClass.getPackage()
                            .getClasses()
                            .stream()
                            .anyMatch(clazz -> clazz.getName().startsWith(nestedClass.getName() + "$")
                                    && clazz.isAnnotatedWith(org.junit.jupiter.api.Nested.class)
                                    && !clazz.isAnnotatedWith(ArchIgnoreGroupName.class)
                                    && !clazz.getName().endsWith("$Validation")
                            )
                    ) {
                        continue;
                    }

                    var nestedClassName = nestedClass.getSimpleName();
                    var expectedMethodName = Character.toLowerCase(nestedClassName.charAt(0))
                            + nestedClassName.substring(1);

                    var nestedClassBaseClassSimpleName = nestedClass.getName()
                            .replace(nestedClass.getPackageName() + ".", "")
                            .replaceAll("\\$.+", "");
                    var nestedClassLineNumber = nestedClass.getConstructors()
                            .iterator()
                            .next()
                            .getSourceCodeLocation()
                            .getLineNumber();

                    /*
                     * This is only true for inner @Nested group classes like for example
                     * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction$DeleteAll
                     * where the enclosing class is
                     * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction
                     *
                     * If an enclosing class is found, this will produce a suffix like "$DeleteAction"
                     */
                    var enclosingClassSuffix = nestedClass.getEnclosingClass()
                            .map(enclosingClass -> {
                                if (!enclosingClass.getName().contains("$")) {
                                    return null;
                                }

                                return "$%s".formatted(enclosingClass.getSimpleName());
                            });

                    var productionClassName = "%s.%s%s".formatted(
                            testClass.getPackageName(),
                            testClass.getSimpleName().replaceAll("(Test|SecurityTest)$", ""),
                            enclosingClassSuffix.orElse("")
                    );

                    var productionClassOptional = allClasses.stream()
                            .filter(clazz -> clazz.getFullName().equals(productionClassName))
                            .findFirst();

                    if (productionClassOptional.isEmpty() && enclosingClassSuffix.isPresent()) {
                        var message = "The @Nested test class <%s> (%s.java:%s)%ndoes not have a matching production class <%s>".formatted(
                                nestedClass.getName(),
                                nestedClassBaseClassSimpleName,
                                nestedClassLineNumber,
                                productionClassName
                        );
                        events.add(SimpleConditionEvent.violated(nestedClass, message));
                    }

                    if (productionClassOptional.isPresent()) {
                        var productionClass = productionClassOptional.get();

                        var methodExists = productionClass.getMethods()
                                .stream()
                                .map(JavaMethod::getName)
                                .anyMatch(methodName -> methodName.equals(expectedMethodName));

                        if (!methodExists) {
                            int productionClassLineNumber = -1;

                            try {
                                productionClassLineNumber = productionClass.getConstructors()
                                        .iterator()
                                        .next()
                                        .getSourceCodeLocation()
                                        .getLineNumber();
                            } catch (Exception e) {
                                log.error(
                                        "Failed to resolve productionClassLineNumber. [nestedClass.getName()=%s, nestedClassBaseClassSimpleName=%s, nestedClassLineNumber=%s, expectedMethodName=%s, productionClass.getName()=%s]".formatted(
                                                nestedClass.getName(),
                                                nestedClassBaseClassSimpleName,
                                                nestedClassLineNumber,
                                                expectedMethodName,
                                                productionClass.getName()
                                        ));
                            }

                            var message = "The @Nested test class <%s> (%s.java:%s)%ndoes not match any expected method name <%s> in production class <%s> (%s.java:%s)".formatted(
                                    nestedClass.getName(),
                                    nestedClassBaseClassSimpleName,
                                    nestedClassLineNumber,
                                    expectedMethodName,
                                    productionClass.getName(),
                                    productionClass.getName()
                                            .replace(nestedClass.getPackageName() + ".", "")
                                            .replaceAll("\\$.+", ""),
                                    productionClassLineNumber

                            );
                            events.add(SimpleConditionEvent.violated(nestedClass, message));
                        }
                    }
                }
            }
        };
    }

    /**
     * Determines if the tests are being run via Maven.
     * Maven sets several system properties when running tests.
     *
     * @return true if running with Maven, false otherwise
     */
    private boolean isRunningWithMaven() {
        // Check for Maven-specific system properties
        return System.getProperty("maven.home") != null
                || System.getProperty("maven.test.skip") != null
                || System.getProperty("maven.test.failure.ignore") != null
                || System.getProperty("surefire.failIfNoSpecifiedTests") != null;
    }

    private record LineNumberType(
            int lineNumber,
            String type
    ) {
        public static LineNumberType of(int lineNumber, String type) {
            return new LineNumberType(lineNumber, type);
        }
    }
}
