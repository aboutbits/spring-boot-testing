package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface MaxRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V max(String property, long max) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, max)
        );
        return (V) this;
    }
}
