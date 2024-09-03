package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.LessThanValueSource;
import lombok.NonNull;

public interface PositiveOrZeroRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V positiveOrZero(@NonNull String property) {
        addRule(
                new Rule(property, LessThanValueSource.class, 0L)
        );
        return (V) this;
    }
}
