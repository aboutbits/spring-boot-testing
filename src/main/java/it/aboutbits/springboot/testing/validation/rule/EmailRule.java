package it.aboutbits.springboot.testing.validation.rule;

import com.google.errorprone.annotations.CheckReturnValue;
import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.InvalidEmailValueSource;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface EmailRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    /// Bean validation considers `null` and blank values to be valid e-mail addresses.
    /// Combine this rule with `notNull` or `notBlank` if the property must be present.
    @CheckReturnValue
    default V email(String property) {
        addRule(
                new Rule(property, InvalidEmailValueSource.class)
        );
        return (V) this;
    }
}
