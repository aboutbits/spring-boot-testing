package it.aboutbits.springboot.testing.validation;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import it.aboutbits.springboot.testing.validation.other.PackagePrivateBase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static it.aboutbits.springboot.testing.validation.ValidationAssertTest.TestValidationAssert.assertThatValidation;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ArchIgnoreNoProductionCounterpart
@NullMarked
class ValidAnnotationTest {
    @Nested
    class ValidBean {
        @Test
        @DisplayName("When @Valid is on type arguments of JDK containers, then validBean passes")
        void when_valid_is_on_type_arguments_of_jdk_containers_then_valid_bean_passes() {
            // given
            var parameter = new ParameterWithValidTypeArguments(
                    List.of(new Item()),
                    Set.of(new Item()),
                    Map.of("key", new Item()),
                    Map.of(new Item(), "value"),
                    Optional.of(new Item()),
                    List.of(new Outer.Inner()),
                    Map.of("key", List.of(new Item()))
            );
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("list")
                            .validBean("set")
                            .validBean("mapValue")
                            .validBean("mapKey")
                            .validBean("optional")
                            .validBean("qualifiedList")
                            .validBean("nestedContainer")
                            .isCompliant() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on the list property, then validBean passes")
        void when_valid_is_on_the_list_property_then_valid_bean_passes() {
            // given
            var parameter = new ParameterWithValidList(List.of(new Item()));
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("validList")
                            .isCompliant() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on a record container property, then validBean passes")
        void when_valid_is_on_a_record_container_property_then_valid_bean_passes() {
            // given
            var parameter = new ParameterWithValidRecordContainer(new Bag<>(List.of(new Item())));
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("bag")
                            .isCompliant() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the record has a static constant of a record type, then validBean passes")
        void when_record_has_a_static_record_constant_then_valid_bean_passes() {
            // given
            var parameter = new ParameterWithStaticRecordConstant(new Item());
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("item")
                            .isCompliant() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the list property has no @Valid, then validBean fails")
        void when_list_property_has_no_valid_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithoutValid(List.of(new Item()));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("plainList")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: plainList"
            );
        }

        @Test
        @DisplayName("When @Valid is on the own type of the property, then validBean fails")
        void when_valid_is_on_the_own_type_of_the_property_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnOwnType(new Outer.Inner());
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("own")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: own"
            );
        }

        @Test
        @DisplayName("When @Valid is on a wildcard bound, then validBean fails")
        void when_valid_is_on_a_wildcard_bound_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnWildcardBound(List.of(new Item()));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("wildcardBound")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: wildcardBound"
            );
        }

        @Test
        @DisplayName("When @Valid is on an array component, then validBean fails")
        void when_valid_is_on_an_array_component_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnArrayComponent(new Outer.Inner[]{new Outer.Inner()});
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("array")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: array"
            );
        }

        @Test
        @DisplayName("When @Valid is on a type argument of a record container, then validBean fails")
        void when_valid_is_on_a_type_argument_of_a_record_container_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnRecordContainerTypeArgument(new Bag<>(List.of(new Item())));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("bag")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: bag"
            );
        }

        @Test
        @DisplayName("When @Valid is on a type argument of a class container, then validBean fails")
        void when_valid_is_on_a_type_argument_of_a_class_container_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnClassContainerTypeArgument(new Box<>(List.of(new Item())));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("box")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: box"
            );
        }

        @Test
        @DisplayName("When @Valid is on a type argument of a record container in a list, then validBean fails")
        void when_valid_is_on_a_type_argument_of_a_record_container_in_a_list_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidInRecordContainerInList(List.of(new Bag<>(List.of(new Item()))));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("listOfBags")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: listOfBags"
            );
        }

        @Test
        @DisplayName("When the type argument with @Valid has no value extractor, then validBean fails")
        void when_type_argument_has_no_value_extractor_then_valid_bean_fails() {
            // given
            var parameter = new ParameterWithValidOnAtomicReference(new AtomicReference<>(new Item()));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .validBean("atomic")
                            .isCompliant() // when
            ).withMessageStartingWith(
                    "Hibernate Validator rejects the configuration [class=%s, reason=HV000197",
                    ParameterWithValidOnAtomicReference.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When a nested record has no @Valid, then the validation fails")
        void when_nested_record_has_no_valid_then_validation_fails() {
            // given
            var parameter = new ParameterWithNestedRecordWithoutValid(new Item());
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .notNull("item")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: %s. Note: This is implicitly assumed for nested records. You can configure `BaseValidationAssert.registerNonBeanType` to add exceptions.",
                    "item"
            );
        }

        @Test
        @DisplayName("When a nested record container has @Valid only on its type argument, then the validation fails")
        void when_nested_record_container_has_valid_only_on_its_type_argument_then_validation_fails() {
            // given
            var parameter = new ParameterWithNestedRecordContainerWithValidTypeArgument(new Bag<>(List.of(new Item())));
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.of(parameter)
                            .usingBeanValidation()
                            .notNull("bag")
                            .isCompliant() // when
            ).withMessage(
                    "Missing @Valid annotation for property: %s. Note: This is implicitly assumed for nested records. You can configure `BaseValidationAssert.registerNonBeanType` to add exceptions.",
                    "bag"
            );
        }
    }

    @Nested
    class IsEnabled {
        @Test
        @DisplayName("When @Valid is on the parameter, then isEnabled passes")
        void when_valid_is_on_the_parameter_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithValid",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on a type argument of the parameter, then isEnabled passes")
        void when_valid_is_on_a_type_argument_of_the_parameter_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithValidTypeArgument",
                            List.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on a record container parameter, then isEnabled passes")
        void when_valid_is_on_a_record_container_parameter_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithValidRecordContainer",
                            Bag.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Validated is on the superclass, then isEnabled passes")
        void when_validated_is_on_the_superclass_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceExtendingValidatedService.class,
                            "saveWithValid",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Validated is a meta-annotation, then isEnabled passes")
        void when_validated_is_a_meta_annotation_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceWithMetaValidated.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Validated and @Valid are on the interface, then isEnabled passes")
        void when_validated_and_valid_are_on_the_interface_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceImplementingValidatedInterface.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on the parameter of an overridden abstract method, then isEnabled passes")
        void when_valid_is_on_the_parameter_of_an_overridden_abstract_method_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceExtendingBaseWithValid.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on the parameter of a generic interface method, then isEnabled passes")
        void when_valid_is_on_the_parameter_of_a_generic_interface_method_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceImplementingGenericInterface.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When @Valid is on the parameter of a superinterface of a redeclaring interface, then isEnabled passes")
        void when_valid_is_on_the_parameter_of_a_superinterface_of_a_redeclaring_interface_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceImplementingRedeclaringInterface.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the method overrides a package-private method in the same package, then isEnabled passes")
        void when_method_overrides_a_package_private_method_in_the_same_package_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceOverridingPackagePrivateMethod.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the method overrides a protected method, then isEnabled passes")
        void when_method_overrides_a_protected_method_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceOverridingProtectedMethod.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the superclass has the same private static method, then isEnabled passes")
        void when_superclass_has_the_same_private_static_method_then_is_enabled_passes() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatCode(
                    () -> sut.calling(
                            ServiceExtendingPrivateStaticBase.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the parameter has no @Valid, then isEnabled fails")
        void when_parameter_has_no_valid_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithoutValid",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=saveWithoutValid, parameter=last]",
                    ValidatedService.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the list parameter has no @Valid, then isEnabled fails")
        void when_list_parameter_has_no_valid_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithoutValidTypeArgument",
                            List.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=saveWithoutValidTypeArgument, parameter=last]",
                    ValidatedService.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When @Valid is on a type argument of a record container parameter, then isEnabled fails")
        void when_valid_is_on_a_type_argument_of_a_record_container_parameter_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithValidOnTypeArgumentOfRecordContainer",
                            Bag.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=saveWithValidOnTypeArgumentOfRecordContainer, parameter=last]",
                    ValidatedService.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the parameter has @Validated instead of @Valid, then isEnabled fails")
        void when_parameter_has_validated_instead_of_valid_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithValidated",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=saveWithValidated, parameter=last]",
                    ValidatedService.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the parameter has an annotation meta-annotated with @Valid, then isEnabled fails")
        void when_parameter_has_an_annotation_meta_annotated_with_valid_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ValidatedService.class,
                            "saveWithMetaValid",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=saveWithMetaValid, parameter=last]",
                    ValidatedService.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When @Valid is only on an overload of the method, then isEnabled fails")
        void when_valid_is_only_on_an_overload_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceImplementingValidatedInterface.class,
                            "save",
                            String.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=save, parameter=last]",
                    ServiceImplementingValidatedInterface.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When @Valid is on a static interface method, then isEnabled fails")
        void when_valid_is_on_a_static_interface_method_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceImplementingInterfaceWithStaticMethod.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Valid annotation [class=%s, method=save, parameter=last]",
                    ServiceImplementingInterfaceWithStaticMethod.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the superclass has the same private method, then isEnabled fails")
        void when_superclass_has_the_same_private_method_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceExtendingPrivateBase.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Hibernate Validator has no reliable result for this method, because a superclass declares a private or package-private method with the same signature. Rename one of the two methods [class=%s, method=save, superclass=%s]",
                    ServiceExtendingPrivateBase.class.getCanonicalName(),
                    PrivateBase.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When a superclass in another package has the same package-private method, then isEnabled fails")
        void when_superclass_in_another_package_has_the_same_package_private_method_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceExtendingPackagePrivateBase.class,
                            "save",
                            PackagePrivateBase.Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Hibernate Validator has no reliable result for this method, because a superclass declares a private or package-private method with the same signature. Rename one of the two methods [class=%s, method=save, superclass=%s]",
                    ServiceExtendingPackagePrivateBase.class.getCanonicalName(),
                    PackagePrivateBase.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When @Valid is on a method of a parallel type, then isEnabled fails")
        void when_valid_is_on_a_method_of_a_parallel_type_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceWithParallelMethods.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).withMessageStartingWith(
                    "Hibernate Validator rejects the configuration [class=%s, method=save, reason=HV000152",
                    ServiceWithParallelMethods.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the overriding method adds @Valid, then isEnabled fails")
        void when_overriding_method_adds_valid_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceAddingValidToOverriddenMethod.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).withMessageStartingWith(
                    "Hibernate Validator rejects the configuration [class=%s, method=save, reason=HV000151",
                    ServiceAddingValidToOverriddenMethod.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the type argument with @Valid has no value extractor, then isEnabled fails")
        void when_type_argument_of_the_parameter_has_no_value_extractor_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceWithValidOnAtomicReference.class,
                            "save",
                            AtomicReference.class
                    ).isEnabled() // when
            ).withMessageStartingWith(
                    "Hibernate Validator rejects the configuration [class=%s, method=save, reason=HV000197",
                    ServiceWithValidOnAtomicReference.class.getCanonicalName()
            );
        }

        @Test
        @DisplayName("When the class has no @Validated, then isEnabled fails")
        void when_class_has_no_validated_then_is_enabled_fails() {
            // given
            var sut = assertThatValidation();

            // then
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> sut.calling(
                            ServiceWithoutValidated.class,
                            "save",
                            Item.class
                    ).isEnabled() // when
            ).withMessage(
                    "Missing @Validated annotation [class=%s]",
                    ServiceWithoutValidated.class.getCanonicalName()
            );
        }
    }

    public record Item() {
    }

    public record ParameterWithValidTypeArguments(
            List<@Valid Item> list,
            Set<@Valid Item> set,
            Map<String, @Valid Item> mapValue,
            Map<@Valid Item, String> mapKey,
            Optional<@Valid Item> optional,
            List<Outer.@Valid Inner> qualifiedList,
            Map<String, List<@Valid Item>> nestedContainer
    ) {
    }

    public record ParameterWithValidList(
            @Valid List<Item> validList
    ) {
    }

    public record ParameterWithValidRecordContainer(
            @Valid Bag<Item> bag
    ) {
    }

    public record ParameterWithStaticRecordConstant(
            @Valid Item item
    ) {
        public static final Item DEFAULT = new Item();
    }

    public record ParameterWithoutValid(
            List<Item> plainList
    ) {
    }

    public record ParameterWithValidOnOwnType(
            Outer.@Valid Inner own
    ) {
    }

    public record ParameterWithValidOnWildcardBound(
            List<? extends @Valid Item> wildcardBound
    ) {
    }

    public record ParameterWithValidOnArrayComponent(
            Outer.@Valid Inner[] array
    ) {
    }

    public record ParameterWithValidOnRecordContainerTypeArgument(
            Bag<@Valid Item> bag
    ) {
    }

    public record ParameterWithValidOnClassContainerTypeArgument(
            Box<@Valid Item> box
    ) {
    }

    public record ParameterWithValidInRecordContainerInList(
            List<Bag<@Valid Item>> listOfBags
    ) {
    }

    public record ParameterWithValidOnAtomicReference(
            AtomicReference<@Valid Item> atomic
    ) {
    }

    public record ParameterWithNestedRecordWithoutValid(
            @NotNull Item item
    ) {
    }

    public record ParameterWithNestedRecordContainerWithValidTypeArgument(
            @NotNull Bag<@Valid Item> bag
    ) {
    }

    public static final class Outer {
        public record Inner() {
        }
    }

    public record Bag<T>(List<T> items) implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return items.iterator();
        }
    }

    public static final class Box<T> implements Iterable<T> {
        private final List<T> items;

        Box(List<T> items) {
            this.items = items;
        }

        @Override
        public Iterator<T> iterator() {
            return items.iterator();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Validated
    public @interface ValidatedStereotype {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Valid
    public @interface ValidCascade {
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ValidatedService {
        public void saveWithValid(@Valid Item last) {
        }

        public void saveWithoutValid(Item last) {
        }

        public void saveWithValidTypeArgument(List<@Valid Item> last) {
        }

        public void saveWithoutValidTypeArgument(List<Item> last) {
        }

        public void saveWithValidRecordContainer(@Valid Bag<Item> last) {
        }

        public void saveWithValidOnTypeArgumentOfRecordContainer(Bag<@Valid Item> last) {
        }

        public void saveWithValidated(@Validated Item last) {
        }

        public void saveWithMetaValid(@ValidCascade Item last) {
        }
    }

    public static class ServiceExtendingValidatedService extends ValidatedService {
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class ServiceWithoutValidated {
        public void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @ValidatedStereotype
    public static class ServiceWithMetaValidated {
        public void save(@Valid Item last) {
        }
    }

    @Validated
    public interface ValidatedInterface {
        void save(@Valid Item last);
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class ServiceImplementingValidatedInterface implements ValidatedInterface {
        @Override
        public void save(Item last) {
        }

        public void save(String last) {
        }
    }

    public abstract static class BaseWithValid {
        public abstract void save(@Valid Item last);
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceExtendingBaseWithValid extends BaseWithValid {
        @Override
        public void save(Item last) {
        }
    }

    public interface GenericInterface<T> {
        void save(@Valid T last);
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceImplementingGenericInterface implements GenericInterface<Item> {
        @Override
        public void save(Item last) {
        }
    }

    public interface ParentInterface {
        void save(@Valid Item last);
    }

    public interface RedeclaringInterface extends ParentInterface {
        @Override
        void save(Item last);
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceImplementingRedeclaringInterface implements RedeclaringInterface {
        @Override
        public void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class SamePackageBase {
        void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceOverridingPackagePrivateMethod extends SamePackageBase {
        @Override
        public void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class ProtectedBase {
        protected void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceOverridingProtectedMethod extends ProtectedBase {
        @Override
        public void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class PrivateStaticBase {
        private static void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceExtendingPrivateStaticBase extends PrivateStaticBase {
        public void save(@Valid Item last) {
        }
    }

    public interface InterfaceWithStaticMethod {
        @SuppressWarnings("java:S1186") // Suppress the "empty method body" warning
        static void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceImplementingInterfaceWithStaticMethod implements InterfaceWithStaticMethod {
        public void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class PrivateBase {
        private void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceExtendingPrivateBase extends PrivateBase {
        public void save(Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceExtendingPackagePrivateBase extends PackagePrivateBase {
        public void save(PackagePrivateBase.Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class ParallelBase {
        public void save(Item last) {
        }
    }

    public interface ParallelInterface {
        void save(@Valid Item last);
    }

    @Validated
    public static class ServiceWithParallelMethods extends ParallelBase implements ParallelInterface {
    }

    public interface InterfaceWithoutValid {
        void save(Item last);
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceAddingValidToOverriddenMethod implements InterfaceWithoutValid {
        @Override
        public void save(@Valid Item last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ServiceWithValidOnAtomicReference {
        public void save(AtomicReference<@Valid Item> last) {
        }
    }
}
