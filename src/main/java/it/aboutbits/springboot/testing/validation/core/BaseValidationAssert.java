package it.aboutbits.springboot.testing.validation.core;

import it.aboutbits.springboot.toolbox.type.CustomType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseValidationAssert<R extends BaseRuleBuilder<?>> {
    @Getter(AccessLevel.PROTECTED)
    private final R ruleBuilder;

    // This keeps track of classes that are not required to have a @Valid annotation.
    protected static final Set<Class<?>> NON_BEAN_TYPES = new HashSet<>(
            Set.of(
                    CustomType.class
            )
    );

    private Object parameterUnderTest;

    @Setter(AccessLevel.PRIVATE)
    private Consumer<?> functionToCallWithParameter = null;

    /**
     * Configure a class that is not required to have a @Valid annotation. Sub-structures are assumed to always require @Valid.
     *
     * @param type The class to whitelist.
     */
    public static void registerNonBeanType(Class<?> type) {
        NON_BEAN_TYPES.add(type);
    }

    public <P> CallBuilder<R, P> of(@NonNull P parameterUnderTest) {
        this.parameterUnderTest = parameterUnderTest;
        ruleBuilder.setTriggerValidation(this::assertValidation);
        return new CallBuilder<>(this);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CallBuilder<R extends BaseRuleBuilder<?>, P> {
        private final BaseValidationAssert<R> parent;

        public R calling(@NonNull Consumer<P> functionToCallWithParameter) {
            parent.setFunctionToCallWithParameter(functionToCallWithParameter);
            return parent.ruleBuilder;
        }

        public R usingBeanValidation() {
            return parent.ruleBuilder;
        }

        @SuppressWarnings("unchecked")
        public <ID> R calling(
                @NonNull BiConsumer<ID, P> functionToCallWithParameter,
                @NonNull ID id
        ) {
            parent.setFunctionToCallWithParameter(
                    p -> functionToCallWithParameter.accept(id, (P) p)
            );
            return parent.ruleBuilder;
        }
    }

    private void assertValidation() {
        new RuleValidator<>().assertValidation(
                new RuleValidator.AssertionParameter<>(
                        parameterUnderTest,
                        functionToCallWithParameter,
                        ruleBuilder.getRules(),
                        NON_BEAN_TYPES
                )
        );
    }
}
