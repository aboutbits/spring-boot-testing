package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.InertValueSource;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface NotValidatedRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V notValidated(@NonNull String property) {
        addRule(
                new Rule(property, InertValueSource.class)
        );
        return (V) this;
    }
}
