package it.aboutbits.springboot.testing.validation.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public abstract class BaseValidationAssert<R extends BaseRuleBuilder<?>> implements AutoCloseable {
    @Getter(AccessLevel.PROTECTED)
    private final R ruleBuilder;

    protected static final Set<Class<?>> NON_BEAN_TYPES = new HashSet<>();

    private Object parameterUnderTest;

    @Setter(AccessLevel.PRIVATE)
    private Consumer<Object> functionToCallWithParameter = null;

    protected boolean hasBeenCalled = false;

    public static void registerNonBeanType(Class<?> type) {
        NON_BEAN_TYPES.add(type);
    }

    public <P> CallBuilder<R, P> that(@NonNull P parameterUnderTest) {
        this.parameterUnderTest = parameterUnderTest;
        ruleBuilder.setTriggerValidation(this::assertValidation);
        return new CallBuilder<>(this, parameterUnderTest);
    }

    @Override
    public void close() throws RuntimeException {
        assertThat(hasBeenCalled)
                .withFailMessage("Validation was never invoked by calling 'isCompliant()'.")
                .isTrue();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CallBuilder<R extends BaseRuleBuilder<?>, P> {
        private final BaseValidationAssert<R> parent;
        private final P parameterUnderTest;

        public R calling(@NonNull Consumer<P> functionToCallWithParameter) {
            parent.setFunctionToCallWithParameter((Consumer<Object>) functionToCallWithParameter);
            return parent.ruleBuilder;
        }

        public R usingBeanValidation() {
            return parent.ruleBuilder;
        }

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
        hasBeenCalled = true;
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
