package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import it.aboutbits.springboot.testing.validation.source.LessThanValueSource;
import lombok.NonNull;

public interface BetweenRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V between(@NonNull String property, long min, long max) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, max)
        );
        addRule(
                new Rule(property, LessThanValueSource.class, min)
        );
        return (V) this;
    }
}
