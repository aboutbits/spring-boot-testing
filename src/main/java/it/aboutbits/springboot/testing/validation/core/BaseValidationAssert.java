package it.aboutbits.springboot.testing.validation.core;

import com.google.errorprone.annotations.CheckReturnValue;
import it.aboutbits.springboot.toolbox.type.CustomType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@NullMarked
public abstract class BaseValidationAssert<R extends BaseRuleBuilder<?>> {
    @Getter(AccessLevel.PROTECTED)
    private final R ruleBuilder;

    // This keeps track of classes that are not required to have a @Valid annotation.
    protected static final Set<Class<?>> NON_BEAN_TYPES = new HashSet<>(
            Set.of(
                    CustomType.class
            )
    );

    @Nullable
    private Object parameterUnderTest;

    @Setter(AccessLevel.PRIVATE)
    @Nullable
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

    @CheckReturnValue
    public <P> CallBuilder<R, P> of(P parameterUnderTest) {
        this.parameterUnderTest = parameterUnderTest;
        ruleBuilder.setTriggerValidation(this::assertValidation);
        return new CallBuilder<>(this);
    }

    @SuppressWarnings("unused")
    @CheckReturnValue
    public AnnotationChecker calling(
            Class<?> classUnderTest,
            String methodName,
            Class<?>... methodParameterTypes
    ) {
        return new AnnotationChecker(classUnderTest, methodName, methodParameterTypes);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AnnotationChecker {
        private final Class<?> classUnderTest;
        private final String methodName;
        private final Class<?>[] methodParameterTypes;

        /// Checks that Spring validates calls of the method through its `MethodValidationPostProcessor` and that Hibernate Validator cascades into the last parameter.
        ///
        /// `@Validated` counts on the class, on a superclass, on an interface or as a meta-annotation, because the pointcut of the `MethodValidationPostProcessor` finds it there. `@Valid` counts on the last parameter of the method or of a method that it overrides, because Hibernate Validator reads the parameter from the whole type hierarchy.
        public void isEnabled() {
            assertThat(AnnotatedElementUtils.hasAnnotation(classUnderTest, Validated.class))
                    .withFailMessage("Missing @Validated annotation on class \"%s\"", classUnderTest.getCanonicalName())
                    .isTrue();

            try {
                var method = classUnderTest.getMethod(methodName, methodParameterTypes);
                var parameter = method.getParameters()[method.getParameterCount() - 1];
                assertThat(hasValidLastParameter(method))
                        .withFailMessage(
                                "Missing @Valid annotation for parameter \"%s\" of method \"%s\" in class \"%s\"",
                                parameter.getName(),
                                methodName,
                                classUnderTest.getCanonicalName()
                        )
                        .isTrue();
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

        private boolean hasValidLastParameter(Method method) {
            return methodsInHierarchy(method).stream()
                    .map(candidate -> candidate.getParameters()[candidate.getParameterCount() - 1])
                    .anyMatch(candidate -> RuleValidator.hasValidAnnotation(candidate, candidate.getAnnotatedType()));
        }

        /// Returns the method and the methods that it overrides in the superclasses and interfaces of the class under test. The check for an override is the same as in Spring's `AnnotatedMethod`.
        private List<Method> methodsInHierarchy(Method method) {
            var methods = new ArrayList<Method>();
            methods.add(method);

            Class<?> type = classUnderTest;
            while (type != null && !Object.class.equals(type)) {
                for (var candidate : type.getDeclaredMethods()) {
                    if (isOverriddenBy(candidate, method)) {
                        methods.add(candidate);
                    }
                }
                for (var anInterface : type.getInterfaces()) {
                    for (var candidate : anInterface.getMethods()) {
                        if (isOverriddenBy(candidate, method)) {
                            methods.add(candidate);
                        }
                    }
                }
                type = type.getSuperclass();
            }

            return methods;
        }

        private boolean isOverriddenBy(Method candidate, Method method) {
            if (candidate.equals(method)
                    || Modifier.isPrivate(candidate.getModifiers())
                    || !candidate.getName().equals(method.getName())
                    || candidate.getParameterCount() != method.getParameterCount()) {
                return false;
            }

            var parameterTypes = method.getParameterTypes();
            for (var i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].equals(ResolvableType.forMethodParameter(candidate, i, classUnderTest).toClass())) {
                    return false;
                }
            }

            return true;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CallBuilder<R extends BaseRuleBuilder<?>, P> {
        private final BaseValidationAssert<R> parent;

        @SuppressWarnings("unused")
        @CheckReturnValue
        public R calling(Consumer<P> functionToCallWithParameter) {
            parent.setFunctionToCallWithParameter(functionToCallWithParameter);
            return parent.ruleBuilder;
        }

        @SuppressWarnings("unused")
        @CheckReturnValue
        public R usingBeanValidation() {
            return parent.ruleBuilder;
        }

        @SuppressWarnings({"unused", "unchecked"})
        @CheckReturnValue
        public <ID> R calling(
                BiConsumer<ID, P> functionToCallWithParameter,
                ID id
        ) {
            parent.setFunctionToCallWithParameter(
                    p -> functionToCallWithParameter.accept(id, (P) p)
            );
            return parent.ruleBuilder;
        }

        @SuppressWarnings({"unused", "unchecked"})
        @CheckReturnValue
        public <A, B> R calling(
                TriConsumer<A, B, P> functionToCallWithParameter,
                A a,
                B b
        ) {
            parent.setFunctionToCallWithParameter(
                    p -> functionToCallWithParameter.accept(a, b, (P) p)
            );
            return parent.ruleBuilder;
        }
    }

    private void assertValidation() {
        if (parameterUnderTest == null) {
            throw new IllegalStateException("Parameter under test not set.");
        }
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
