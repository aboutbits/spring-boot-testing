package it.aboutbits.springboot.testing.validation;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.BaseValidationAssert;

public class TestValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
    public TestValidationAssert() {
        super(new TestRuleBuilder());
    }

    public static final class TestRuleBuilder extends BaseRuleBuilder<TestValidationAssert.TestRuleBuilder> {

    }
}
