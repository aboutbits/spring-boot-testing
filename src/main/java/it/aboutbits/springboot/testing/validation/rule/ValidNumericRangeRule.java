package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.CustomValidationFunction;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.toolbox.validation.annotation.ValidNumericRange;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@SuppressWarnings("unchecked")
@NullMarked
public interface ValidNumericRangeRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V validNumericRange(String lowerBoundField, String upperBoundField) {
        addValidationFunction(
                (Object o) -> {
                    var isValid = false;
                    var currentClass = o.getClass();

                    while (currentClass != null) {
                        var annotations = currentClass.getDeclaredAnnotationsByType(ValidNumericRange.class);

                        isValid = Arrays.stream(annotations).anyMatch(
                                a -> a.lowerBoundField().equals(lowerBoundField) && a.upperBoundField()
                                        .equals(upperBoundField)
                        );

                        if (isValid) {
                            break;
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                    return new CustomValidationFunction.Result(
                            isValid,
                            "@ValidNumericRange(lowerBoundField = \"%s\", upperBoundField = \"%s\") is missing.".formatted(
                                    lowerBoundField,
                                    upperBoundField
                            )
                    );
                }
        );

        return (V) this;
    }

    default V validNumericRange(
            String lowerBoundField,
            String upperBoundField,
            boolean allowEqualValues
    ) {
        addValidationFunction(
                (Object o) -> {
                    var isValid = false;
                    var currentClass = o.getClass();

                    while (currentClass != null) {
                        var annotations = currentClass.getDeclaredAnnotationsByType(ValidNumericRange.class);

                        isValid = Arrays.stream(annotations).anyMatch(
                                a -> a.lowerBoundField().equals(lowerBoundField)
                                        && a.upperBoundField().equals(upperBoundField)
                                        && a.allowEqualValues() == allowEqualValues
                        );

                        if (isValid) {
                            break;
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                    return new CustomValidationFunction.Result(
                            isValid,
                            "@ValidNumericRange(lowerBoundField = \"%s\", upperBoundField = \"%s\", allowEqualValues = %s) is missing."
                                    .formatted(lowerBoundField, upperBoundField, allowEqualValues)
                    );
                }
        );

        return (V) this;
    }
}
