package it.aboutbits.springboot.testing.validation.core;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The main idea of the RuleValidator is to take in a valid parameter and then mutate it.
 * Each mutation changes exactly one property`s value.
 * Then we check if an exception is raised.
 * We repeat this for each defined rule.
 * <p>
 * Additionally, we also check if @Valid or @Nullable is present where required according to the rules.
 * Also, we enforce that all properties must have at least one rule (with a rule existing that says "no-rule").
 * </p>
 *
 * @parameterUnderTest A valid parameter that we can use as the basis for our mutations. Validation for the unmodified parameter MUST succeed.
 * @functionToCallWithParameter Optional. Instead of directly using bean validation, we can also validate a real function call. This makes sure the parameter is actually annotated with @Valid as well and that the class is using @Validated.
 * @rules The list of rules to validate.
 * @nonBeanTypes This is a whitelist that holds classes that don't implicitly require @Valid. We assume that @Valid is required
 * for all substructures.
 */
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
            List<CustomValidationFunction> functions,
            @NonNull
            Set<Class<?>> nonBeanTypes
    ) {
    }

    void assertValidation(AssertionParameter<P> assertionParameter) {
        var parameterUnderTest = assertionParameter.parameterUnderTest();
        @SuppressWarnings("unchecked")
        var functionToCallWithParameter = (Consumer<P>) assertionParameter.functionToCallWithParameter();
        var rules = assertionParameter.rules();
        var functions = assertionParameter.functions();
        var nonBeanTypes = assertionParameter.nonBeanTypes();

        var rulesAndFunctions = rules.size() + functions.size();

        assertThat(rulesAndFunctions)
                .withFailMessage("Validation failed: no rules were defined.")
                .isNotZero();

        for (var function : functions) {
            var result = function.apply(parameterUnderTest);
            assertThat(result.valid())
                    .withFailMessage(
                            "Validation failed: %s", result.message())
                    .isTrue();
        }

        var validator = VALIDATOR_FACTORY.getValidator();

        var propertiesWithRules = getPropertyNamesThatHaveRules(rules);

        assertThatSuppliedParameterIsValid(
                parameterUnderTest,
                functionToCallWithParameter,
                validator
        );

        assertThatValidationIsCompliantForEachProperty(
                rules,
                parameterUnderTest,
                functionToCallWithParameter,
                validator
        );

        assertThatValidAnnotationsArePresent(rules, propertiesWithRules, parameterUnderTest);

        assertThatNullableAnnotationsArePresent(rules, propertiesWithRules, parameterUnderTest);

        assertThatAllPropertiesHaveRules(parameterUnderTest, propertiesWithRules);

        checkIfNestedValidationIsEnabledForNestedRecords(parameterUnderTest, nonBeanTypes);
    }

    private static HashSet<String> getPropertyNamesThatHaveRules(List<Rule> rules) {
        // Create a set to keep track of properties that have validation rules
        var propertiesWithRules = new HashSet<String>();
        for (var rule : rules) {
            propertiesWithRules.add(rule.getProperty());
        }
        return propertiesWithRules;
    }

    private static <P> void assertThatSuppliedParameterIsValid(
            P parameterUnderTest,
            Consumer<P> functionToCallWithParameter,
            Validator validator
    ) {
        if (functionToCallWithParameter != null) {
            try {
                functionToCallWithParameter.accept(parameterUnderTest);
            } catch (ConstraintViolationException e) {
                var violatingFieldMessages = getViolatingFieldMessages(e.getConstraintViolations());

                assertThat(true)
                        .withFailMessage(
                                "The supplied parameter violates the validation rules. The supplied parameter is not valid: %s",
                                violatingFieldMessages.collect(Collectors.joining(" | "))
                        )
                        .isFalse();
            } catch (Exception ignored) {
                // ignore any other exceptions
            }
        } else {
            // Use Bean Validation to validate
            var violations = new HashSet<ConstraintViolation<?>>(validator.validate(parameterUnderTest));

            var violatingFieldMessages = getViolatingFieldMessages(violations);

            assertThat(violations)
                    .withFailMessage(
                            "The supplied parameter possibly contains invalid values: %s",
                            violatingFieldMessages.collect(Collectors.joining(" | "))
                    )
                    .isEmpty();
        }
    }

    private static Stream<String> getViolatingFieldMessages(Set<ConstraintViolation<?>> violations) {
        return violations
                .stream()
                .map(violation ->
                             "%s => %s".formatted(
                                     violation.getPropertyPath().toString(),
                                     violation.getMessage()
                             )
                );
    }

    private static <P> void assertThatValidationIsCompliantForEachProperty(
            List<Rule> rules,
            P parameterUnderTest,
            Consumer<P> functionToCallWithParameter,
            Validator validator
    ) {
        // Iterate through the rules and validate each property
        for (var rule : rules) {
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

                    var violatingProperties = violations.stream().map(
                            f -> f.getPropertyPath().toString()
                    ).collect(Collectors.toSet());

                    assertThat(violatingProperties)
                            .withFailMessage("Validation failed for property: " + rule.getProperty() + " [" + alteredValue + "]")
                            .contains(rule.getProperty());
                }
            });
        }
    }

    private static <P> void assertThatValidAnnotationsArePresent(
            List<Rule> rules,
            HashSet<String> propertiesWithRules,
            P parameterUnderTest
    ) {
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
    }

    private static <P> void assertThatNullableAnnotationsArePresent(
            List<Rule> rules,
            HashSet<String> propertiesWithRules,
            P parameterUnderTest
    ) {
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
    }

    private static <P> void assertThatAllPropertiesHaveRules(
            P parameterUnderTest,
            HashSet<String> propertiesWithRules
    ) {
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

    @NonNull
    @SneakyThrows(ReflectiveOperationException.class)
    private static <P> Stream<?> getValues(Rule rule, P parameterUnderTest) {
        var source = (ValueSource) rule.getValueSource().getDeclaredConstructors()[0].newInstance();

        return source.values(getPropertyType(rule.getProperty(), parameterUnderTest), rule.getArgs());
    }

    @SuppressWarnings("unchecked")
    private static <T> T createCopyWithAlteredProperty(T original, String property, Object alteredValue) {
        try {
            // Get the class of the original object
            var clazz = (Class<T>) original.getClass();

            // Get all the declared fields of the class
            var fields = getAllFields(clazz);

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

            return createCopyWithAlteredValues(clazz, parameterTypes, newPropertyValues, fields);
        } catch (NoSuchMethodException e) {
            throw new RuleValidationException(
                    "Error creating copy with altered property. Maybe there is no eligible-constructor?",
                    e
            );
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuleValidationException("Error creating copy with altered property: " + property, e);
        }
    }

    private static <T> T createCopyWithAlteredValues(
            Class<T> clazz,
            Class<?>[] parameterTypes,
            Object[] newPropertyValues,
            Field[] fields
    ) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        T instance = null;
        try {
            // Get the constructor that accepts all properties as arguments
            var constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);

            // Create a copy with the altered property value
            instance = constructor.newInstance(newPropertyValues);
        } catch (NoSuchMethodException e) {
            // Get the no args constructor
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            // Create a copy with the altered property value
            instance = constructor.newInstance();
            for (var i = 0; i < newPropertyValues.length; i++) {
                fields[i].setAccessible(true);
                fields[i].set(instance, newPropertyValues[i]);
            }
        }
        return instance;
    }

    private static <T> Set<String> getAllPropertiesOf(T object) {
        var clazz = object.getClass();

        var properties = new HashSet<String>();
        for (var field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

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

    @NonNull
    private static Field getFieldOrFail(String propertyName, Object object) {
        var clazz = object.getClass();

        var field = Arrays.stream(getAllFields(clazz))
                .filter(
                        f -> f.getName().equals(propertyName)
                )
                .findFirst();

        return field.orElseThrow(() -> new RuleValidationException("Property does not exist: " + propertyName));
    }

    private static Field[] getAllFields(Class<?> initialClazz) {
        Class<?> clazz = initialClazz;

        var fields = new ArrayList<Field>();
        while (clazz != null) {
            var newFields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .toList();

            fields.addAll(newFields);
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}
