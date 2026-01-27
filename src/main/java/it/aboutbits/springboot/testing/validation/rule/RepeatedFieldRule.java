package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.CustomValidationFunction;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.toolbox.validation.annotation.RepeatedField;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@SuppressWarnings("unchecked")
@NullMarked
public interface RepeatedFieldRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V repeatedField(String originalField, String repeatedField) {
        addValidationFunction(
                (Object o) -> {
                    var isValid = false;
                    var currentClass = o.getClass();

                    while (currentClass != null) {
                        var annotations = currentClass.getDeclaredAnnotationsByType(RepeatedField.class);

                        isValid = Arrays.stream(annotations).anyMatch(
                                a -> a.originalField().equals(originalField) && a.repeatedField()
                                        .equals(repeatedField)
                        );

                        if (isValid) {
                            break;
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                    return new CustomValidationFunction.Result(
                            isValid,
                            "@RepeatedField(originalField = \"%s\", repeatedField = \"%s\") is missing.".formatted(
                                    originalField,
                                    repeatedField
                            )
                    );
                }
        );

        return (V) this;
    }
}
