package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.CustomValidationFunction;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.toolbox.validation.annotation.ValidDateRange;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@SuppressWarnings("unchecked")
@NullMarked
public interface ValidDateRangeRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V validDateRange(String fromDateField, String toDateField) {
        addValidationFunction(
                (Object o) -> {
                    var isValid = false;
                    var currentClass = o.getClass();

                    while (currentClass != null) {
                        var annotations = currentClass.getDeclaredAnnotationsByType(ValidDateRange.class);

                        isValid = Arrays.stream(annotations).anyMatch(
                                a -> a.fromDateField().equals(fromDateField) && a.toDateField()
                                        .equals(toDateField)
                        );

                        if (isValid) {
                            break;
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                    return new CustomValidationFunction.Result(
                            isValid,
                            "@ValidDateRange(fromDateField = \"%s\", toDateField = \"%s\") is missing.".formatted(
                                    fromDateField,
                                    toDateField
                            )
                    );
                }
        );

        return (V) this;
    }

    default V validDateRange(
            String fromDateField,
            String toDateField,
            boolean allowEmptyRange
    ) {
        addValidationFunction(
                (Object o) -> {
                    var isValid = false;
                    var currentClass = o.getClass();

                    while (currentClass != null) {
                        var annotations = currentClass.getDeclaredAnnotationsByType(ValidDateRange.class);

                        isValid = Arrays.stream(annotations).anyMatch(
                                a -> a.fromDateField().equals(fromDateField)
                                        && a.toDateField().equals(toDateField)
                                        && a.allowEmptyRange() == allowEmptyRange
                        );

                        if (isValid) {
                            break;
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                    return new CustomValidationFunction.Result(
                            isValid,
                            "@ValidDateRange(fromDateField = \"%s\", toDateField = \"%s\", allowEmptyRange = %s) is missing."
                                    .formatted(fromDateField, toDateField, allowEmptyRange)
                    );
                }
        );

        return (V) this;
    }
}
