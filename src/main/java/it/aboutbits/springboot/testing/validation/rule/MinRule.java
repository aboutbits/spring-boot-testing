package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.LessThanValueSource;
import lombok.NonNull;

public interface MinRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V min(@NonNull String property, long min) {
        addRule(
                new Rule(property, LessThanValueSource.class, min)
        );
        return (V) this;
    }
}
