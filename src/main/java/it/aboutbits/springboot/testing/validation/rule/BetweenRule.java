package it.aboutbits.springboot.testing.validation.rule;

import com.google.errorprone.annotations.CheckReturnValue;
import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.BiggerThanValueSource;
import it.aboutbits.springboot.testing.validation.source.LessThanValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface BetweenRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    @CheckReturnValue
    default V between(String property, long min, long max) {
        addRule(
                new Rule(property, BiggerThanValueSource.class, max)
        );
        addRule(
                new Rule(property, LessThanValueSource.class, min)
        );
        return (V) this;
    }
}
