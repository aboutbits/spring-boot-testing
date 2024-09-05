package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface MaxRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V max(@NonNull String property, long max) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, max)
        );
        return (V) this;
    }
}
