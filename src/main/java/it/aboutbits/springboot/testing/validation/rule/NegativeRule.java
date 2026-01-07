package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import it.aboutbits.springboot.testing.validation.source.ZeroValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NegativeRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V negative(String property) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, 0L)
        );
        addRule(
                new Rule(property, ZeroValueSource.class)
        );
        return (V) this;
    }
}
