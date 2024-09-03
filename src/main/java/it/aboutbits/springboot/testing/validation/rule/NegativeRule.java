package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import it.aboutbits.springboot.testing.validation.source.ZeroValueSource;
import lombok.NonNull;

public interface NegativeRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V negative(@NonNull String property) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, 0L)
        );
        addRule(
                new Rule(property, ZeroValueSource.class)
        );
        return (V) this;
    }
}
