package it.aboutbits.springboot.testing.validation.rule;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.Rule;
import it.aboutbits.springboot.testing.validation.core.ValidationRulesData;
import it.aboutbits.springboot.testing.validation.source.SizeGreaterThanValueSource;
import it.aboutbits.springboot.testing.validation.source.SizeLessThanValueSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unchecked")
@NullMarked
public interface SizeRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default Builder<V> size(String property) {
        return new Builder<>((V) this, property);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class Builder<V extends BaseRuleBuilder<?>> {
        private final V parent;
        private final String property;

        public V min(long minSize) {
            parent.addRule(
                    new Rule(property, SizeLessThanValueSource.class, minSize)
            );
            return parent;
        }

        public V max(long maxSize) {
            parent.addRule(
                    new Rule(property, SizeGreaterThanValueSource.class, maxSize)
            );
            return parent;
        }

        public V minMax(long minSize, long maxSize) {
            parent.addRule(
                    new Rule(property, SizeLessThanValueSource.class, minSize)
            );
            parent.addRule(
                    new Rule(property, SizeGreaterThanValueSource.class, maxSize)
            );
            return parent;
        }
    }
}
