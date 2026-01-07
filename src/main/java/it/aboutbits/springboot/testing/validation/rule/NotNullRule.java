package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.NullValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NotNullRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V notNull(String property) {
        addRule(
                new Rule(property, NullValueSource.class)
        );
        return (V) this;
    }
}
