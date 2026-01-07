package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface NullableRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V nullable(String property) {
        addRule(
                Rule.nullableAnnotated(property)
        );
        return (V) this;
    }
}
