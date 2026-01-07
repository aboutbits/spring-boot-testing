package it.aboutbits.springboot.testing.validation.core;

import it.aboutbits.springboot.testing.validation.source.InertValueSource;
import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Rule {
    @Getter(AccessLevel.PACKAGE)
    private final String property;

    @Getter(AccessLevel.PACKAGE)
    private final Class<? extends ValueSource> valueSource;

    @Getter(AccessLevel.PACKAGE)
    private final Object[] args;

    @Getter(AccessLevel.PACKAGE)
    private boolean requireValid = false;

    @Getter(AccessLevel.PACKAGE)
    private boolean requireNullable = false;

    public Rule(String property, Class<? extends ValueSource> valueSource, Object... args) {
        checkPropertyName(property);

        this.property = property;
        this.valueSource = valueSource;
        this.args = args;
    }

    public static Rule validAnnotated(String property) {
        checkPropertyName(property);

        var rule = new Rule(property, InertValueSource.class);
        rule.requireValid = true;
        return rule;
    }

    public static Rule nullableAnnotated(String property) {
        checkPropertyName(property);

        var rule = new Rule(property, InertValueSource.class);
        rule.requireNullable = true;
        return rule;
    }

    private static void checkPropertyName(String property) {
        if (property.contains(".")) {
            throw new IllegalArgumentException("Referencing sub-objects using dot notation is not supported.");
        }
    }
}
