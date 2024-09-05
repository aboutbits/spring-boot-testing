package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.LessThanValueSource;
import it.aboutbits.springboot.testing.validation.source.ZeroValueSource;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface PositiveRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V positive(@NonNull String property) {
        addRule(
                new Rule(property, LessThanValueSource.class, 0L)
        );
        addRule(
                new Rule(property, ZeroValueSource.class)
        );
        return (V) this;
    }
}
