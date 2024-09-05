package it.aboutbits.springboot.testing.validation.core;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

final class RuleValidator<P> {
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    record AssertionParameter<P>(
            @NonNull
            P parameterUnderTest,
            @Nullable
            Consumer<?> functionToCallWithParameter,
            @NonNull
            List<Rule> rules,
            @NonNull
            Set<Class<?>> nonBeanTypes
    ) {
    }

    void assertValidation(AssertionParameter<P> assertionParameter) {
        var parameterUnderTest = assertionParameter.parameterUnderTest();
        @SuppressWarnings("unchecked")
        var functionToCallWithParameter = (Consumer<P>) assertionParameter.functionToCallWithParameter();
        var rules = assertionParameter.rules();
        var nonBeanTypes = assertionParameter.nonBeanTypes();

        assertThat(rules)
                .withFailMessage("Validation failed: no rules were defined.")
                .isNotEmpty();

        var validator = VALIDATOR_FACTORY.getValidator();

        // Create a set to keep track of properties that have validation rules
        var propertiesWithRules = new HashSet<String>();

        // Iterate through the rules and validate each property
        for (var rule : rules) {
            propertiesWithRules.add(rule.getProperty());

            var values = getValues(rule, parameterUnderTest);
            values.forEach(alteredValue -> {
                // Create a copy of the original object
                var copy = createCopyWithAlteredProperty(parameterUnderTest, rule.getProperty(), alteredValue);

                if (functionToCallWithParameter != null) {
                    assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(
                            () -> functionToCallWithParameter.accept(copy)
                    ).withMessageContaining(rule.getProperty());
                } else {

                    // Use Bean Validation to validate the copy
                    var violations = validator.validate(copy);

                    // Check if there are any violations
                    assertThat(violations)
                            .withFailMessage(
                                    "More than one property failed to validate during mutation. The supplied parameter is possibly contains invalid values.")
                            .hasSizeLessThan(2);

                    assertThat(violations)
                            .withFailMessage("Validation failed for property: " + rule.getProperty() + " [" + alteredValue + "]")
                            .hasSize(1);
                }
            });
        }

        var propertiesWithValid = rules.stream()
                .filter(Rule::isRequireValid)
                .map(Rule::getProperty)
                .collect(Collectors.toSet());

        for (var property : propertiesWithValid) {
            propertiesWithRules.add(property);

            assertThat(hasValidAnnotation(property, parameterUnderTest))
                    .withFailMessage("Missing @Valid annotation for property: " + property)
                    .isTrue();
        }

        var propertiesWithNullable = rules.stream()
                .filter(Rule::isRequireNullable)
                .map(Rule::getProperty)
                .collect(Collectors.toSet());

        for (var property : propertiesWithNullable) {
            propertiesWithRules.add(property);

            assertThat(hasNullableAnnotation(property, parameterUnderTest))
                    .withFailMessage("Missing @Nullable annotation for property: " + property + ". Note: This does not work with `org.jetbrains.annotations.Nullable` because of their retention policy. Use `org.springframework.lang.Nullable` or `jakarta.annotation.Nullable` instead.")
                    .isTrue();
        }

        // Check if all properties have rules (you can also handle this differently based on your needs)
        var allProperties = getAllPropertiesOf(parameterUnderTest);

        var missingProperties = new HashSet<>(allProperties);
        missingProperties.removeAll(propertiesWithRules);

        assertThat(missingProperties)
                .withFailMessage("Not all properties have validation rules: " + "[" + String.join(
                        ", ",
                        missingProperties
                ) + "]")
                .isEmpty();

        checkIfNestedValidationIsEnabledForNestedRecords(parameterUnderTest, nonBeanTypes);
    }

    private static <P> void checkIfNestedValidationIsEnabledForNestedRecords(
            P parameterUnderTest,
            Set<Class<?>> nonBeanTypes
    ) {
        var clazz = parameterUnderTest.getClass();

        for (var field : clazz.getDeclaredFields()) {
            var isRecord = Record.class.isAssignableFrom(field.getType());
            if (isRecord) {
                var isBeanType = nonBeanTypes.stream().noneMatch(type -> type.isAssignableFrom(field.getType()));

                if (isBeanType) {
                    var maybeAnnotation = field.getAnnotation(Valid.class);
                    assertThat(maybeAnnotation).withFailMessage("Missing @Valid annotation for property: " + field.getName() + ". Note: This is implicitly assumed for nested records. You can configure `BaseValidationAssert.registerNonBeanType` to add exceptions.")
                            .isNotNull();
                }
            }
        }
    }

    @NotNull
    @SneakyThrows(ReflectiveOperationException.class)
    private static <P> Stream<?> getValues(Rule rule, P parameterUnderTest) {
        var source = (ValueSource) rule.getValueSource().getDeclaredConstructors()[0].newInstance();

        return source.values(getPropertyType(rule.getProperty(), parameterUnderTest), rule.getArgs());
    }

    @SuppressWarnings("unchecked")
    private static <T> T createCopyWithAlteredProperty(T original, String property, Object alteredValue) {
        try {
            // Get the class of the original object
            var clazz = original.getClass();

            // Get all the declared fields of the class
            var fields = clazz.getDeclaredFields();

            // Create an array to hold the values of the original object's properties
            var propertyValues = new Object[fields.length];

            // Get the types of the fields
            var parameterTypes = new Class<?>[fields.length];

            // Populate the array with the current property values
            for (var i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                propertyValues[i] = fields[i].get(original);
                parameterTypes[i] = fields[i].getType();
            }

            // Get the constructor that accepts all properties as arguments
            var constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);

            // Create an array to hold the new property values
            var newPropertyValues = new Object[propertyValues.length];

            // Find the index of the property to be altered
            var propertyIndex = -1;
            for (var i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals(property)) {
                    propertyIndex = i;
                    break;
                }
            }

            // Replace the value of the altered property
            newPropertyValues[propertyIndex] = alteredValue;

            // Copy the other property values
            for (var i = 0; i < propertyValues.length; i++) {
                if (i != propertyIndex) {
                    newPropertyValues[i] = propertyValues[i];
                }
            }

            // Create a copy with the altered property value
            return (T) constructor.newInstance(newPropertyValues);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Error creating copy with altered property. Maybe there is no all-args-constructor?",
                    e
            );
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Error creating copy with altered property: " + property, e);
        }
    }

    private static <T> Set<String> getAllPropertiesOf(T object) {
        var clazz = object.getClass();

        var properties = new HashSet<String>();
        for (var field : clazz.getDeclaredFields()) {
            properties.add(field.getName());
        }

        return properties;
    }

    private static Class<?> getPropertyType(String propertyName, Object object) {
        var field = getFieldOrFail(propertyName, object);

        return field.getType();
    }

    private static <T> boolean hasValidAnnotation(String propertyName, T object) {
        var field = getFieldOrFail(propertyName, object);

        return field.getAnnotation(Valid.class) != null;
    }

    private static boolean hasNullableAnnotation(String propertyName, Object object) {
        var field = getFieldOrFail(propertyName, object);

        return field.getAnnotation(org.springframework.lang.Nullable.class) != null
                || field.getAnnotation(jakarta.annotation.Nullable.class) != null;
    }

    private static @NotNull Field getFieldOrFail(String propertyName, Object object) {
        var clazz = object.getClass();

        Field field = null;
        try {
            field = clazz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Property does not exist: " + propertyName, e);
        }
        return field;
    }
}
