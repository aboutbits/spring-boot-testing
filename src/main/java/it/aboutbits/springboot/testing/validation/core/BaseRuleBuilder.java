package it.aboutbits.springboot.testing.validation.core;

import it.aboutbits.springboot.testing.validation.rule.BetweenRule;
import it.aboutbits.springboot.testing.validation.rule.FutureRule;
import it.aboutbits.springboot.testing.validation.rule.MaxRule;
import it.aboutbits.springboot.testing.validation.rule.MinRule;
import it.aboutbits.springboot.testing.validation.rule.NegativeOrZeroRule;
import it.aboutbits.springboot.testing.validation.rule.NegativeRule;
import it.aboutbits.springboot.testing.validation.rule.NotBlankRule;
import it.aboutbits.springboot.testing.validation.rule.NotEmptyRule;
import it.aboutbits.springboot.testing.validation.rule.NotNullRule;
import it.aboutbits.springboot.testing.validation.rule.NotValidatedRule;
import it.aboutbits.springboot.testing.validation.rule.NullableRule;
import it.aboutbits.springboot.testing.validation.rule.PastRule;
import it.aboutbits.springboot.testing.validation.rule.PositiveOrZeroRule;
import it.aboutbits.springboot.testing.validation.rule.PositiveRule;
import it.aboutbits.springboot.testing.validation.rule.ValidBeanRule;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class BaseRuleBuilder<R extends BaseRuleBuilder<R>> implements
        ValidationRulesData,
        BetweenRule<R>,
        FutureRule<R>,
        MaxRule<R>,
        MinRule<R>,
        NegativeOrZeroRule<R>,
        NegativeRule<R>,
        NotBlankRule<R>,
        NotEmptyRule<R>,
        NotNullRule<R>,
        NullableRule<R>,
        PastRule<R>,
        PositiveOrZeroRule<R>,
        PositiveRule<R>,
        NotValidatedRule<R>,
        ValidBeanRule<R> {
    @Getter(AccessLevel.PACKAGE)
    private final List<Rule> rules = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private final List<CustomValidationFunction> validationFunctions = new ArrayList<>();

    @Setter(AccessLevel.PACKAGE)
    private Runnable triggerValidation;

    @Override
    public void addRule(@NonNull Rule rule) {
        rules.add(rule);
    }

    @Override
    public void addValidationFunction(@NonNull CustomValidationFunction function) {
        validationFunctions.add(function);
    }

    public <T extends BaseRuleBuilder<T>> T withAdditionalRules(Consumer<T> registrar) {
        var self = (T) this;
        registrar.accept(self);
        return self;
    }

    public void isCompliant() {
        triggerValidation.run();
    }
}
