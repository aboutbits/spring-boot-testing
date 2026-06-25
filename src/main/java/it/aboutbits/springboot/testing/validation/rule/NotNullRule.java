package it.aboutbits.springboot.testing.validation.rule;

import com.google.errorprone.annotations.CheckReturnValue;
import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.NullValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NotNullRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    @CheckReturnValue
    default V notNull(String property) {
        addRule(
                new Rule(property, NullValueSource.class)
        );
        return (V) this;
    }
}
