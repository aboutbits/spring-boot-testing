package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.InertValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NotValidatedRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V notValidated(String property) {
        addRule(
                new Rule(property, InertValueSource.class)
        );
        return (V) this;
    }
}
