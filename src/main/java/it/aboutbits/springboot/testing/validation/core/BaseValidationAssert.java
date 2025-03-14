package it.aboutbits.springboot.testing.validation.core;

import it.aboutbits.springboot.toolbox.type.CustomType;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

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
    @SuppressWarnings("unused")
    public static void registerNonBeanType(Class<?> type) {
        NON_BEAN_TYPES.add(type);
    }

    public <P> CallBuilder<R, P> of(@NonNull P parameterUnderTest) {
        this.parameterUnderTest = parameterUnderTest;
        ruleBuilder.setTriggerValidation(this::assertValidation);
        return new CallBuilder<>(this);
    }

    @SuppressWarnings("unused")
    public AnnotationChecker calling(
            @NonNull Class<?> classUnderTest,
            @NonNull String methodName,
            @NonNull Class<?>... methodParameterTypes
    ) {
        return new AnnotationChecker(classUnderTest, methodName, methodParameterTypes);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AnnotationChecker {
        private final Class<?> classUnderTest;
        private final String methodName;
        private final Class<?>[] methodParameterTypes;

        public void isEnabled() {
            assertThat(classUnderTest.isAnnotationPresent(Validated.class)).isTrue();

            try {
                var method = classUnderTest.getMethod(methodName, methodParameterTypes);
                var parameter = method.getParameters()[method.getParameterCount() - 1];
                assertThat(parameter.isAnnotationPresent(Valid.class)).isTrue();
            } catch (NoSuchMethodException e) {
                throw new AssertionError(
                        "Method \"%s(%s)\" not found in class \"%s\"".formatted(
                                methodName,
                                String.join(
                                        ", ",
                                        Arrays.stream(methodParameterTypes).map(Class::getCanonicalName).toList()
                                ),
                                classUnderTest.getCanonicalName()
                        ), e
                );
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CallBuilder<R extends BaseRuleBuilder<?>, P> {
        private final BaseValidationAssert<R> parent;

        @SuppressWarnings("unused")
        public R calling(@NonNull Consumer<P> functionToCallWithParameter) {
            parent.setFunctionToCallWithParameter(functionToCallWithParameter);
            return parent.ruleBuilder;
        }

        @SuppressWarnings("unused")
        public R usingBeanValidation() {
            return parent.ruleBuilder;
        }

        @SuppressWarnings({"unused", "unchecked"})
        public <ID> R calling(
                @NonNull BiConsumer<ID, P> functionToCallWithParameter,
                @NonNull ID id
        ) {
            parent.setFunctionToCallWithParameter(
                    p -> functionToCallWithParameter.accept(id, (P) p)
            );
            return parent.ruleBuilder;
        }

        @SuppressWarnings({"unused", "unchecked"})
        public <A, B> R calling(
                @NonNull TriConsumer<A, B, P> functionToCallWithParameter,
                @NonNull A a,
                @NonNull B b
        ) {
            parent.setFunctionToCallWithParameter(
                    p -> functionToCallWithParameter.accept(a, b, (P) p)
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
                        ruleBuilder.getValidationFunctions(),
                        NON_BEAN_TYPES
                )
        );
    }
}
