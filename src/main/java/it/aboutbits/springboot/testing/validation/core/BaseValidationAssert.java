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
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
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

        /// Fails unless calls of the method are validated: the class needs [Validated], and the last parameter needs `@Valid`, on the parameter itself or on a type argument of a JDK container such as `List<@Valid Item>`.
        public void isEnabled() {
            assertThat(AnnotatedElementUtils.hasAnnotation(classUnderTest, Validated.class))
                    .withFailMessage("Missing @Validated annotation [class=%s]", classUnderTest.getCanonicalName())
                    .isTrue();

            var method = getMethodOrFail();
            assertNoHiddenMethodInSuperclass(method);

            var lastParameterIndex = method.getParameterCount() - 1;
            var methodDescriptor = RuleValidator.getBeanDescriptor(
                    classUnderTest,
                    "class=%s, method=%s".formatted(classUnderTest.getCanonicalName(), methodName)
            ).getConstraintsForMethod(methodName, methodParameterTypes);
            var parameterDescriptor = methodDescriptor != null
                    ? methodDescriptor.getParameterDescriptors().get(lastParameterIndex)
                    : null;

            assertThat(RuleValidator.isCascaded(parameterDescriptor))
                    .withFailMessage(
                            "Missing @Valid annotation [class=%s, method=%s, parameter=%s]",
                            classUnderTest.getCanonicalName(),
                            methodName,
                            method.getParameters()[lastParameterIndex].getName()
                    )
                    .isTrue();
        }

        /// Not redundant to the metadata check in [#isEnabled]: when a superclass declares a private or not-visible method with the same signature, the result of Hibernate Validator depends on which of the two methods it reads first.
        private void assertNoHiddenMethodInSuperclass(Method method) {
            for (var type = classUnderTest.getSuperclass(); type != null && !Object.class.equals(type); type = type.getSuperclass()) {
                for (var candidate : type.getDeclaredMethods()) {
                    if (candidate.equals(method)
                            // Hibernate Validator ignores static and synthetic methods, so they never collide with the checked method
                            || Modifier.isStatic(candidate.getModifiers())
                            || candidate.isSynthetic()
                            || !candidate.getName().equals(method.getName())
                            || !Arrays.equals(candidate.getParameterTypes(), method.getParameterTypes())) {
                        continue;
                    }

                    var modifiers = candidate.getModifiers();
                    var isPrivate = Modifier.isPrivate(modifiers);
                    var isPackagePrivateInOtherPackage = !isPrivate
                            && !Modifier.isPublic(modifiers)
                            && !Modifier.isProtected(modifiers)
                            && !candidate.getDeclaringClass().getPackageName().equals(method.getDeclaringClass().getPackageName());

                    assertThat(isPrivate || isPackagePrivateInOtherPackage)
                            .withFailMessage(
                                    "Hibernate Validator has no reliable result for this method, because a superclass declares a private or package-private method with the same signature. Rename one of the two methods [class=%s, method=%s, superclass=%s]",
                                    classUnderTest.getCanonicalName(),
                                    methodName,
                                    type.getCanonicalName()
                            )
                            .isFalse();
                }
            }
        }

        private Method getMethodOrFail() {
            try {
                return classUnderTest.getMethod(methodName, methodParameterTypes);
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
