package it.aboutbits.springboot.testing.validation;

import it.aboutbits.springboot.testing.validation.core.BaseRuleBuilder;
import it.aboutbits.springboot.testing.validation.core.BaseValidationAssert;
import it.aboutbits.springboot.toolbox.type.ScaledBigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.With;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static it.aboutbits.springboot.testing.validation.ValidationAssertTest.TestValidationAssert.assertThatValidation;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ValidationAssertTest {
    @With
    public record SomeValidParameter(
            // NotNull
            @NotNull String notNullable,

            // NotBlank
            @NotBlank String notBlank,

            // Min
            @Min(5) byte biggerThanByte,
            @Min(5) short biggerThanShort,
            @Min(5) int biggerThanInt,
            @Min(5) long biggerThanLong,
            @Min(5) float biggerThanFloat,
            @Min(5) double biggerThanDouble,
            @Min(5) BigInteger biggerThanBigInteger,
            @Min(5) BigDecimal biggerThanBigDecimal,
            @Min(5) ScaledBigDecimal biggerThanScaledBigDecimal,

            // Max
            @Max(5) byte lessThanByte,
            @Max(5) short lessThanShort,
            @Max(5) int lessThanInt,
            @Max(5) long lessThanLong,
            @Max(5) float lessThanFloat,
            @Max(5) double lessThanDouble,
            @Max(5) BigInteger lessThanBigInteger,
            @Max(5) BigDecimal lessThanBigDecimal,
            @Max(5) ScaledBigDecimal lessThanScaledBigDecimal,

            // Between
            @Min(-3) @Max(5) byte betweenByte,
            @Min(-3) @Max(5) short betweenShort,
            @Min(-3) @Max(5) int betweenInt,
            @Min(-3) @Max(5) long betweenLong,
            @Min(-3) @Max(5) float betweenFloat,
            @Min(-3) @Max(5) double betweenDouble,
            @Min(-3) @Max(5) BigInteger betweenBigInteger,
            @Min(-3) @Max(5) BigDecimal betweenBigDecimal,
            @Min(-3) @Max(5) ScaledBigDecimal betweenScaledBigDecimal,

            // Positive
            @Positive byte positiveByte,
            @Positive short positiveShort,
            @Positive int positiveInt,
            @Positive long positiveLong,
            @Positive float positiveFloat,
            @Positive double positiveDouble,
            @Positive BigInteger positiveBigInteger,
            @Positive BigDecimal positiveBigDecimal,
            @Positive ScaledBigDecimal positiveScaledBigDecimal,

            // Negative
            @Negative byte negativeByte,
            @Negative short negativeShort,
            @Negative int negativeInt,
            @Negative long negativeLong,
            @Negative float negativeFloat,
            @Negative double negativeDouble,
            @Negative BigInteger negativeBigInteger,
            @Negative BigDecimal negativeBigDecimal,
            @Negative ScaledBigDecimal negativeScaledBigDecimal,

            // PositiveOrZero
            @PositiveOrZero byte positiveOrZeroByte,
            @PositiveOrZero short positiveOrZeroShort,
            @PositiveOrZero int positiveOrZeroInt,
            @PositiveOrZero long positiveOrZeroLong,
            @PositiveOrZero float positiveOrZeroFloat,
            @PositiveOrZero double positiveOrZeroDouble,
            @PositiveOrZero BigInteger positiveOrZeroBigInteger,
            @PositiveOrZero BigDecimal positiveOrZeroBigDecimal,
            @PositiveOrZero ScaledBigDecimal positiveOrZeroScaledBigDecimal,

            // NegativeOrZero
            @NegativeOrZero byte negativeOrZeroByte,
            @NegativeOrZero short negativeOrZeroShort,
            @NegativeOrZero int negativeOrZeroInt,
            @NegativeOrZero long negativeOrZeroLong,
            @NegativeOrZero float negativeOrZeroFloat,
            @NegativeOrZero double negativeOrZeroDouble,
            @NegativeOrZero BigInteger negativeOrZeroBigInteger,
            @NegativeOrZero BigDecimal negativeOrZeroBigDecimal,
            @NegativeOrZero ScaledBigDecimal negativeOrZeroScaledBigDecimal,

            // Future
            @Future Instant futureInstant,
            @Future LocalTime futureTime,
            @Future LocalDate futureDate,
            @Future LocalDateTime futureDateTime,
            @Future OffsetTime futureOffsetTime,
            @Future OffsetDateTime futureOffsetDateTime,
            @Future Year futureYear,
            @Future YearMonth futureYearMonth,
            @Future ZonedDateTime futureZonedDateTime,

            // Past
            @Past Instant pastInstant,
            @Past LocalTime pastTime,
            @Past LocalDate pastDate,
            @Past LocalDateTime pastDateTime,
            @Past OffsetTime pastOffsetTime,
            @Past OffsetDateTime pastOffsetDateTime,
            @Past Year pastYear,
            @Past YearMonth pastYearMonth,
            @Past ZonedDateTime pastZonedDateTime,

            // Valid
            @Valid Object validObject,

            // Size - Min
            @Size(min = 3) String minSizeString,
            @Size(min = 3) Set<UUID> minSizeCollection,

            // Size - Max
            @Size(max = 10) String maxSizeString,
            @Size(max = 10) Collection<String> maxSizeCollection,

            // Size - Min/Max
            @Size(min = 2, max = 8) String minMaxSizeString,
            @Size(min = 2, max = 8) Collection<String> minMaxSizeCollection,

            // Nullable
            @Nullable Object nullable,

            // Not validated
            Object notValidated
    ) {

    }

    @Test
    @SuppressWarnings("checkstyle:MethodLength")
    void testWithBeanValidation() {
        var validParameter = getSomeValidParameter();

        assertThatValidation().of(validParameter)
                .usingBeanValidation()
                // NotNull
                .notNull("notNullable")

                // NotBlank
                .notBlank("notBlank")

                // Min
                .min("biggerThanByte", 5)
                .min("biggerThanShort", 5)
                .min("biggerThanInt", 5)
                .min("biggerThanLong", 5)
                .min("biggerThanFloat", 5)
                .min("biggerThanDouble", 5)
                .min("biggerThanBigInteger", 5)
                .min("biggerThanBigDecimal", 5)
                .min("biggerThanScaledBigDecimal", 5)

                // Max
                .max("lessThanByte", 5)
                .max("lessThanShort", 5)
                .max("lessThanInt", 5)
                .max("lessThanLong", 5)
                .max("lessThanFloat", 5)
                .max("lessThanDouble", 5)
                .max("lessThanBigInteger", 5)
                .max("lessThanBigDecimal", 5)
                .max("lessThanScaledBigDecimal", 5)

                // Positive
                .positive("positiveByte")
                .positive("positiveShort")
                .positive("positiveInt")
                .positive("positiveLong")
                .positive("positiveFloat")
                .positive("positiveDouble")
                .positive("positiveBigInteger")
                .positive("positiveBigDecimal")
                .positive("positiveScaledBigDecimal")

                // Negative
                .negative("negativeByte")
                .negative("negativeShort")
                .negative("negativeInt")
                .negative("negativeLong")
                .negative("negativeFloat")
                .negative("negativeDouble")
                .negative("negativeBigInteger")
                .negative("negativeBigDecimal")
                .negative("negativeScaledBigDecimal")

                // Between
                .between("betweenByte", -3, 5)
                .between("betweenShort", -3, 5)
                .between("betweenInt", -3, 5)
                .between("betweenLong", -3, 5)
                .between("betweenFloat", -3, 5)
                .between("betweenDouble", -3, 5)
                .between("betweenBigInteger", -3, 5)
                .between("betweenBigDecimal", -3, 5)
                .between("betweenScaledBigDecimal", -3, 5)

                // PositiveOrZero
                .positiveOrZero("positiveOrZeroByte")
                .positiveOrZero("positiveOrZeroShort")
                .positiveOrZero("positiveOrZeroInt")
                .positiveOrZero("positiveOrZeroLong")
                .positiveOrZero("positiveOrZeroFloat")
                .positiveOrZero("positiveOrZeroDouble")
                .positiveOrZero("positiveOrZeroBigInteger")
                .positiveOrZero("positiveOrZeroBigDecimal")
                .positiveOrZero("positiveOrZeroScaledBigDecimal")

                // NegativeOrZero
                .negativeOrZero("negativeOrZeroByte")
                .negativeOrZero("negativeOrZeroShort")
                .negativeOrZero("negativeOrZeroInt")
                .negativeOrZero("negativeOrZeroLong")
                .negativeOrZero("negativeOrZeroFloat")
                .negativeOrZero("negativeOrZeroDouble")
                .negativeOrZero("negativeOrZeroBigInteger")
                .negativeOrZero("negativeOrZeroBigDecimal")
                .negativeOrZero("negativeOrZeroScaledBigDecimal")

                // Future
                .future("futureInstant")
                .future("futureTime")
                .future("futureDate")
                .future("futureDateTime")
                .future("futureOffsetTime")
                .future("futureOffsetDateTime")
                .future("futureYear")
                .future("futureYearMonth")
                .future("futureZonedDateTime")

                // Past
                .past("pastInstant")
                .past("pastTime")
                .past("pastDate")
                .past("pastDateTime")
                .past("pastOffsetTime")
                .past("pastOffsetDateTime")
                .past("pastYear")
                .past("pastYearMonth")
                .past("pastZonedDateTime")

                // Valid
                .validBean("validObject")

                // Size - Min
                .size("minSizeString").min(3)
                .size("minSizeCollection").min(3)

                // Size - Max
                .size("maxSizeString").max(10)
                .size("maxSizeCollection").max(10)

                // Size - Min/Max
                .size("minMaxSizeString").minMax(2, 8)
                .size("minMaxSizeCollection").minMax(2, 8)

                // Nullable
                .nullable("nullable")

                // Not validated
                .notValidated("notValidated")
                .isCompliant();
    }

    @Test
    void invalidParameter_shouldFail() {
        var validParameter = getSomeValidParameter();

        var invalidParameter = validParameter.withFutureDate(LocalDate.EPOCH);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> assertThatValidation().of(invalidParameter)
                        .usingBeanValidation()
                        // NotNull
                        .notNull("notNullable")

                        // NotBlank
                        .notBlank("notBlank")

                        // Min
                        .min("biggerThanByte", 5)
                        .min("biggerThanShort", 5)
                        .min("biggerThanInt", 5)
                        .min("biggerThanLong", 5)
                        .min("biggerThanFloat", 5)
                        .min("biggerThanDouble", 5)
                        .min("biggerThanBigInteger", 5)
                        .min("biggerThanBigDecimal", 5)
                        .min("biggerThanScaledBigDecimal", 5)

                        // Max
                        .max("lessThanByte", 5)
                        .max("lessThanShort", 5)
                        .max("lessThanInt", 5)
                        .max("lessThanLong", 5)
                        .max("lessThanFloat", 5)
                        .max("lessThanDouble", 5)
                        .max("lessThanBigInteger", 5)
                        .max("lessThanBigDecimal", 5)
                        .max("lessThanScaledBigDecimal", 5)

                        // Positive
                        .positive("positiveByte")
                        .positive("positiveShort")
                        .positive("positiveInt")
                        .positive("positiveLong")
                        .positive("positiveFloat")
                        .positive("positiveDouble")
                        .positive("positiveBigInteger")
                        .positive("positiveBigDecimal")
                        .positive("positiveScaledBigDecimal")

                        // Negative
                        .negative("negativeByte")
                        .negative("negativeShort")
                        .negative("negativeInt")
                        .negative("negativeLong")
                        .negative("negativeFloat")
                        .negative("negativeDouble")
                        .negative("negativeBigInteger")
                        .negative("negativeBigDecimal")
                        .negative("negativeScaledBigDecimal")

                        // Between
                        .between("betweenByte", -3, 5)
                        .between("betweenShort", -3, 5)
                        .between("betweenInt", -3, 5)
                        .between("betweenLong", -3, 5)
                        .between("betweenFloat", -3, 5)
                        .between("betweenDouble", -3, 5)
                        .between("betweenBigInteger", -3, 5)
                        .between("betweenBigDecimal", -3, 5)
                        .between("betweenScaledBigDecimal", -3, 5)

                        // PositiveOrZero
                        .positiveOrZero("positiveOrZeroByte")
                        .positiveOrZero("positiveOrZeroShort")
                        .positiveOrZero("positiveOrZeroInt")
                        .positiveOrZero("positiveOrZeroLong")
                        .positiveOrZero("positiveOrZeroFloat")
                        .positiveOrZero("positiveOrZeroDouble")
                        .positiveOrZero("positiveOrZeroBigInteger")
                        .positiveOrZero("positiveOrZeroBigDecimal")
                        .positiveOrZero("positiveOrZeroScaledBigDecimal")

                        // NegativeOrZero
                        .negativeOrZero("negativeOrZeroByte")
                        .negativeOrZero("negativeOrZeroShort")
                        .negativeOrZero("negativeOrZeroInt")
                        .negativeOrZero("negativeOrZeroLong")
                        .negativeOrZero("negativeOrZeroFloat")
                        .negativeOrZero("negativeOrZeroDouble")
                        .negativeOrZero("negativeOrZeroBigInteger")
                        .negativeOrZero("negativeOrZeroBigDecimal")
                        .negativeOrZero("negativeOrZeroScaledBigDecimal")

                        // Future
                        .future("futureInstant")
                        .future("futureTime")
                        .future("futureDate")
                        .future("futureDateTime")
                        .future("futureOffsetTime")
                        .future("futureOffsetDateTime")
                        .future("futureYear")
                        .future("futureYearMonth")
                        .future("futureZonedDateTime")

                        // Past
                        .past("pastInstant")
                        .past("pastTime")
                        .past("pastDate")
                        .past("pastDateTime")
                        .past("pastOffsetTime")
                        .past("pastOffsetDateTime")
                        .past("pastYear")
                        .past("pastYearMonth")
                        .past("pastZonedDateTime")

                        // Valid
                        .validBean("validObject")

                        // Size - Min
                        .size("minSizeString").min(3)
                        .size("minSizeCollection").min(3)

                        // Size - Max
                        .size("maxSizeString").max(10)
                        .size("maxSizeCollection").max(10)

                        // Size - Min/Max
                        .size("minMaxSizeString").minMax(2, 8)
                        .size("minMaxSizeCollection").minMax(2, 8)

                        // Nullable
                        .nullable("nullable")

                        // Not validated
                        .notValidated("notValidated")
                        .isCompliant());
    }

    @Test
    void propertyMissingRule_shouldFail() {
        var validParameter = getSomeValidParameter();

        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> assertThatValidation().of(validParameter)
                        .usingBeanValidation()
                        // NotNull
                        .notNull("notNullable")

                        // NotBlank
                        // this is now missing: .notBlank("notBlank")

                        // Min
                        .min("biggerThanByte", 5)
                        .min("biggerThanShort", 5)
                        .min("biggerThanInt", 5)
                        .min("biggerThanLong", 5)
                        .min("biggerThanFloat", 5)
                        .min("biggerThanDouble", 5)
                        .min("biggerThanBigInteger", 5)
                        .min("biggerThanBigDecimal", 5)
                        .min("biggerThanScaledBigDecimal", 5)

                        // Max
                        .max("lessThanByte", 5)
                        .max("lessThanShort", 5)
                        .max("lessThanInt", 5)
                        .max("lessThanLong", 5)
                        .max("lessThanFloat", 5)
                        .max("lessThanDouble", 5)
                        .max("lessThanBigInteger", 5)
                        .max("lessThanBigDecimal", 5)
                        .max("lessThanScaledBigDecimal", 5)

                        // Positive
                        .positive("positiveByte")
                        .positive("positiveShort")
                        .positive("positiveInt")
                        .positive("positiveLong")
                        .positive("positiveFloat")
                        .positive("positiveDouble")
                        .positive("positiveBigInteger")
                        .positive("positiveBigDecimal")
                        .positive("positiveScaledBigDecimal")

                        // Negative
                        .negative("negativeByte")
                        .negative("negativeShort")
                        .negative("negativeInt")
                        .negative("negativeLong")
                        .negative("negativeFloat")
                        .negative("negativeDouble")
                        .negative("negativeBigInteger")
                        .negative("negativeBigDecimal")
                        .negative("negativeScaledBigDecimal")

                        // Between
                        .between("betweenByte", -3, 5)
                        .between("betweenShort", -3, 5)
                        .between("betweenInt", -3, 5)
                        .between("betweenLong", -3, 5)
                        .between("betweenFloat", -3, 5)
                        .between("betweenDouble", -3, 5)
                        .between("betweenBigInteger", -3, 5)
                        .between("betweenBigDecimal", -3, 5)
                        .between("betweenScaledBigDecimal", -3, 5)

                        // PositiveOrZero
                        .positiveOrZero("positiveOrZeroByte")
                        .positiveOrZero("positiveOrZeroShort")
                        .positiveOrZero("positiveOrZeroInt")
                        .positiveOrZero("positiveOrZeroLong")
                        .positiveOrZero("positiveOrZeroFloat")
                        .positiveOrZero("positiveOrZeroDouble")
                        .positiveOrZero("positiveOrZeroBigInteger")
                        .positiveOrZero("positiveOrZeroBigDecimal")
                        .positiveOrZero("positiveOrZeroScaledBigDecimal")

                        // NegativeOrZero
                        .negativeOrZero("negativeOrZeroByte")
                        .negativeOrZero("negativeOrZeroShort")
                        .negativeOrZero("negativeOrZeroInt")
                        .negativeOrZero("negativeOrZeroLong")
                        .negativeOrZero("negativeOrZeroFloat")
                        .negativeOrZero("negativeOrZeroDouble")
                        .negativeOrZero("negativeOrZeroBigInteger")
                        .negativeOrZero("negativeOrZeroBigDecimal")
                        .negativeOrZero("negativeOrZeroScaledBigDecimal")

                        // Future
                        .future("futureInstant")
                        .future("futureTime")
                        .future("futureDate")
                        .future("futureDateTime")
                        .future("futureOffsetTime")
                        .future("futureOffsetDateTime")
                        .future("futureYear")
                        .future("futureYearMonth")
                        .future("futureZonedDateTime")

                        // Past
                        .past("pastInstant")
                        .past("pastTime")
                        .past("pastDate")
                        .past("pastDateTime")
                        .past("pastOffsetTime")
                        .past("pastOffsetDateTime")
                        .past("pastYear")
                        .past("pastYearMonth")
                        .past("pastZonedDateTime")

                        // Valid
                        .validBean("validObject")

                        // Size - Min
                        .size("minSizeString").min(3)
                        .size("minSizeCollection").min(3)

                        // Size - Max
                        .size("maxSizeString").max(10)
                        .size("maxSizeCollection").max(10)

                        // Size - Min/Max
                        .size("minMaxSizeString").minMax(2, 8)
                        .size("minMaxSizeCollection").minMax(2, 8)

                        // Nullable
                        .nullable("nullable")

                        // Not validated
                        .notValidated("notValidated")
                        .isCompliant());
    }

    @Nested
    @SuppressWarnings("java:S5778") // Suppress the warning for lambdas with more than one exception cause
    class AnnotationChecking {
        @Test
        void givenValidatedClass_andValidParameter_shouldSucceed() {
            assertThatCode(
                    () -> assertThatValidation().calling(
                            ClassWithValidated.class,
                            "someMethodWithValidParameter",
                            String.class
                    ).isEnabled()
            ).doesNotThrowAnyException();

            assertThatCode(
                    () -> assertThatValidation().calling(
                            ClassWithValidated.class,
                            "someMethodWithValidParameter",
                            Long.class,
                            String.class
                    ).isEnabled()
            ).doesNotThrowAnyException();

            assertThatCode(
                    () -> assertThatValidation().calling(
                            ClassWithValidated.class,
                            "someMethodWithValidParameter",
                            Long.class,
                            Integer.class,
                            String.class
                    ).isEnabled()
            ).doesNotThrowAnyException();
        }

        @Test
        void givenNotValidatedClass_shouldAlwaysFail() {
            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithValidParameter",
                            String.class
                    ).isEnabled()
            );

            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithValidParameter",
                            Long.class,
                            String.class
                    ).isEnabled()
            );

            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithValidParameter",
                            Long.class,
                            Integer.class,
                            String.class
                    ).isEnabled()
            );

            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithoutValidParameter",
                            String.class
                    ).isEnabled()
            );

            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithoutValidParameter",
                            Long.class,
                            String.class
                    ).isEnabled()
            );

            assertThatExceptionOfType(AssertionError.class).isThrownBy(
                    () -> assertThatValidation().calling(
                            ClassWithoutValidated.class,
                            "someMethodWithoutValidParameter",
                            Long.class,
                            Integer.class,
                            String.class
                    ).isEnabled()
            );
        }
    }

    @Test
    void shouldAlsoWorkForExtendedClassesEvenWithoutAllArgsConstructors() {
        var item = new SomeExtendingClass();
        item.notNull = "notNull";
        item.notNullPositiveOrZero = ScaledBigDecimal.ONE;

        assertThatValidation().of(item)
                .usingBeanValidation()
                .notNull("notNull")
                .notNull("notNull")
                .positiveOrZero("notNullPositiveOrZero")
                .isCompliant();

        var invalidItem = new SomeExtendingClass();

        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> assertThatValidation().of(invalidItem)
                        .usingBeanValidation()
                        .notNull("notNull")
                        .notNull("notNull")
                        .positiveOrZero("notNullPositiveOrZero")
                        .isCompliant()
        );
    }

    @Test
    void usingRuleRegistrarShouldWork() {
        var item = new SomeExtendingClass();
        item.notNull = "notNull";
        item.notNullPositiveOrZero = ScaledBigDecimal.ONE;

        var registrar = (Consumer<TestValidationAssert.TestRuleBuilder>) ruleBuilder -> ruleBuilder
                .notNull("notNull")
                .notNull("notNull")
                .positiveOrZero("notNullPositiveOrZero");

        assertThatValidation().of(item)
                .usingBeanValidation()
                .withAdditionalRules(registrar)
                .isCompliant();
    }


    private static SomeValidParameter getSomeValidParameter() {
        return new SomeValidParameter(
                // NotNull
                "",

                // NotBlank
                "something",

                // Min
                (byte) 6,
                (short) 6,
                6,
                6L,
                6.0f,
                6.0d,
                BigInteger.valueOf(6L),
                BigDecimal.valueOf(6.0d),
                ScaledBigDecimal.valueOf(6.0d),

                // Max
                (byte) 4,
                (short) 4,
                4,
                4L,
                4.0f,
                4.0d,
                BigInteger.valueOf(4L),
                BigDecimal.valueOf(4.0d),
                ScaledBigDecimal.valueOf(4.0d),

                // Positive
                (byte) 4,
                (short) 4,
                4,
                4L,
                4.0f,
                4.0d,
                BigInteger.valueOf(4L),
                BigDecimal.valueOf(4.0d),
                ScaledBigDecimal.valueOf(4.0d),

                // Negative
                (byte) 1,
                (short) 1,
                1,
                1L,
                1.0f,
                1.0d,
                BigInteger.valueOf(1L),
                BigDecimal.valueOf(1.0d),
                ScaledBigDecimal.valueOf(1.0d),

                // Between
                (byte) -1,
                (short) -1,
                -1,
                -1L,
                -1.0f,
                -1.0d,
                BigInteger.valueOf(-1L),
                BigDecimal.valueOf(-1.0d),
                ScaledBigDecimal.valueOf(-1.0d),

                // PositiveOrZero
                (byte) 0,
                (short) 0,
                0,
                0L,
                0.0f,
                0.0d,
                BigInteger.valueOf(0L),
                BigDecimal.valueOf(0.0d),
                ScaledBigDecimal.valueOf(0.0d),

                // NegativeOrZero
                (byte) 0,
                (short) 0,
                0,
                0L,
                0.0f,
                0.0d,
                BigInteger.valueOf(0L),
                BigDecimal.valueOf(0.0d),
                ScaledBigDecimal.valueOf(0.0d),

                // Future
                Instant.now().plus(1L, ChronoUnit.DAYS),
                LocalTime.now().plusMinutes(1L),
                LocalDate.now().plusDays(1L),
                LocalDateTime.now().plusDays(1L),
                OffsetTime.now().plusMinutes(1L),
                OffsetDateTime.now().plusDays(1L),
                Year.now().plusYears(1L),
                YearMonth.now().plusMonths(1L),
                ZonedDateTime.now().plusDays(1L),

                // Past
                Instant.now().minus(1L, ChronoUnit.DAYS),
                LocalTime.now().minusMinutes(1L),
                LocalDate.now().minusDays(1L),
                LocalDateTime.now().minusDays(1L),
                OffsetTime.now().minusMinutes(1L),
                OffsetDateTime.now().minusDays(1L),
                Year.now().minusYears(1L),
                YearMonth.now().minusMonths(1L),
                ZonedDateTime.now().minusDays(1L),

                // Valid
                null,

                // Size - Min
                "validstring", // 11 characters, > 3 min
                Set.of(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                ), // 4 elements, > 3 min

                // Size - Max
                "small", // 5 characters, < 10 max
                List.of("a", "b", "c"), // 3 elements, < 10 max

                // Size - Min/Max
                "medium", // 6 characters, between 2-8
                List.of("a", "b", "c", "d", "e"), // 5 elements, between 2-8

                // Nullable
                null,

                // Not validated
                null
        );
    }

    public static final class TestValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
        private TestValidationAssert() {
            super(new TestRuleBuilder());
        }

        public static TestValidationAssert assertThatValidation() {
            return new TestValidationAssert();
        }

        public static final class TestRuleBuilder extends BaseRuleBuilder<TestRuleBuilder> {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    @Validated
    public static class ClassWithValidated {
        public void someMethodWithValidParameter(@Valid String last) {
        }

        public void someMethodWithValidParameter(Long first, @Valid String last) {
        }

        public void someMethodWithValidParameter(Long first, Integer second, @Valid String last) {
        }

        public void someMethodWithoutValidParameter(String last) {
        }

        public void someMethodWithoutValidParameter(Long first, String last) {
        }

        public void someMethodWithoutValidParameter(Long first, Integer second, String last) {
        }
    }

    @SuppressWarnings({"java:S1186", "unused"}) // Suppress the "empty method body" warning
    public static class ClassWithoutValidated {
        public void someMethodWithValidParameter(@Valid String last) {
        }

        public void someMethodWithValidParameter(Long first, @Valid String last) {
        }

        public void someMethodWithValidParameter(Long first, Integer second, @Valid String last) {
        }

        public void someMethodWithoutValidParameter(String last) {
        }

        public void someMethodWithoutValidParameter(Long first, String last) {
        }

        public void someMethodWithoutValidParameter(Long first, Integer second, String last) {
        }
    }

    public abstract static class SomeBaseClass {
        @NotNull
        protected String notNull;
    }

    public static class SomeExtendingClass extends SomeBaseClass {
        @NotNull
        @PositiveOrZero
        private ScaledBigDecimal notNullPositiveOrZero;
    }
}
