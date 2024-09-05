package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface NullableRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V nullable(@NonNull String property) {
        addRule(
                Rule.nullableAnnotated(property)
        );
        return (V) this;
    }
}
