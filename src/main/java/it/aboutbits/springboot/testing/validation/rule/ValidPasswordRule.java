package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.LongerThanValueSource;
import it.aboutbits.springboot.testing.validation.source.ShorterThanValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface ValidPasswordRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V validPassword(String property, long minLength, long maxLength) {
        addRule(
                new Rule(property, LongerThanValueSource.class, maxLength)
        );
        addRule(
                new Rule(property, ShorterThanValueSource.class, minLength)
        );
        return (V) this;
    }
}
