package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.FutureValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface PastRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V past(String property) {
        addRule(
                new Rule(property, FutureValueSource.class)
        );
        return (V) this;
    }
}
