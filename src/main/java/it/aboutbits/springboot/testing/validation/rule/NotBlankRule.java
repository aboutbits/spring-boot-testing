package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BlankValueSource;
import it.aboutbits.springboot.testing.validation.source.NullValueSource;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public interface NotBlankRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V notBlank(@NonNull String property) {
        addRule(
                new Rule(property, BlankValueSource.class)
        );
        addRule(
                new Rule(property, NullValueSource.class)
        );
        return (V) this;
    }
}
