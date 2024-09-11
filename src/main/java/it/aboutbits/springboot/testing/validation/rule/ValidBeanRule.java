package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface ValidBeanRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V validBean(@NonNull String property) {
        addRule(
                Rule.validAnnotated(property)
        );
        return (V) this;
    }
}
