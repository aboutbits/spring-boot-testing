package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.EmptyValueSource;
import it.aboutbits.springboot.testing.validation.source.NullValueSource;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface NotEmptyRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V notEmpty(@NonNull String property) {
        addRule(
                new Rule(property, EmptyValueSource.class)
        );
        addRule(
                new Rule(property, NullValueSource.class)
        );
        return (V) this;
    }
}
