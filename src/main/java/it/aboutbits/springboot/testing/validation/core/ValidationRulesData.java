package it.aboutbits.springboot.testing.validation.core;

import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@NullMarked
public interface ValidationRulesData {
    void addRule(Rule rule);

    void addValidationFunction(CustomValidationFunction function);
}
