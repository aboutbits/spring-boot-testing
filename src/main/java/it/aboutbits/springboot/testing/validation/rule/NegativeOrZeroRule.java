package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import lombok.NonNull;

public interface NegativeOrZeroRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V negativeOrZero(@NonNull String property) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, 0L)
        );
        return (V) this;
    }
}
