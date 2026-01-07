package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NegativeOrZeroRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V negativeOrZero(String property) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, 0L)
        );
        return (V) this;
    }
}
