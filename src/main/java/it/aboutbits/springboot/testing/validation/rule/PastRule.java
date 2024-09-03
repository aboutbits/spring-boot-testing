package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.FutureValueSource;
import lombok.NonNull;

public interface PastRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V past(@NonNull String property) {
        addRule(
                new Rule(property, FutureValueSource.class)
        );
        return (V) this;
    }
}
