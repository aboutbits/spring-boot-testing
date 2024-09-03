package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.PastValueSource;
import lombok.NonNull;

public interface FutureRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V future(@NonNull String property) {
        addRule(
                new Rule(property, PastValueSource.class)
        );
        return (V) this;
    }
}
