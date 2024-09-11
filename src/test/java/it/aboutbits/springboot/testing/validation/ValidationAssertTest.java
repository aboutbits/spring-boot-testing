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
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static it.aboutbits.springboot.testing.validation.ValidationAssertTest.TestValidationAssert.assertThatValidation;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ValidationAssertTest {
    @With
    public record SomeValidParameter(
            @NotNull
            String notNullable,
            @NotBlank
            String notBlank,
            @Min(5)
            int biggerThanInt,
            @Min(5)
            long biggerThanLong,
            @Min(5)
            float biggerThanFloat,
            @Min(5)
            double biggerThanDouble,
            @Min(5)
            BigDecimal biggerThanBigDecimal,
            @Min(5)
            ScaledBigDecimal biggerThanScaledBigDecimal,
            @Max(5)
            int lessThanInt,
            @Max(5)
            long lessThanLong,
            @Max(5)
            float lessThanFloat,
            @Max(5)
            double lessThanDouble,
            @Max(5)
            BigDecimal lessThanBigDecimal,
            @Max(5)
            ScaledBigDecimal lessThanScaledBigDecimal,
            @Min(-3)
            @Max(5)
            int betweenInt,
            @Min(-3)
            @Max(5)
            long betweenLong,
            @Min(-3)
            @Max(5)
            float betweenFloat,
            @Min(-3)
            @Max(5)
            double betweenDouble,
            @Min(-3)
            @Max(5)
            BigDecimal betweenBigDecimal,
            @Min(-3)
            @Max(5)
            ScaledBigDecimal betweenScaledBigDecimal,
            @Positive
            int positiveInt,
            @Positive
            long positiveLong,
            @Positive
            float positiveFloat,
            @Positive
            double positiveDouble,
            @Positive
            BigDecimal positiveBigDecimal,
            @Positive
            ScaledBigDecimal positiveScaledBigDecimal,
            @Negative
            int negativeInt,
            @Negative
            long negativeLong,
            @Negative
            float negativeFloat,
            @Negative
            double negativeDouble,
            @Negative
            BigDecimal negativeBigDecimal,
            @Negative
            ScaledBigDecimal negativeScaledBigDecimal,
            @PositiveOrZero
            int positiveOrZeroInt,
            @PositiveOrZero
            long positiveOrZeroLong,
            @PositiveOrZero
            float positiveOrZeroFloat,
            @PositiveOrZero
            double positiveOrZeroDouble,
            @PositiveOrZero
            BigDecimal positiveOrZeroBigDecimal,
            @PositiveOrZero
            ScaledBigDecimal positiveOrZeroScaledBigDecimal,
            @NegativeOrZero
            int negativeOrZeroInt,
            @NegativeOrZero
            long negativeOrZeroLong,
            @NegativeOrZero
            float negativeOrZeroFloat,
            @NegativeOrZero
            double negativeOrZeroDouble,
            @NegativeOrZero
            BigDecimal negativeOrZeroBigDecimal,
            @NegativeOrZero
            ScaledBigDecimal negativeOrZeroScaledBigDecimal,
            @Future
            LocalDate futureDate,
            @Future
            LocalDateTime futureDateTime,
            @Future
            OffsetDateTime futureOffsetDateTime,
            @Past
            LocalDate pastDate,
            @Past
            LocalDateTime pastDateTime,
            @Past
            OffsetDateTime pastOffsetDateTime,
            @Valid
            Object validObject,
            @Nullable
            Object nullable,
            Object notValidated
    ) {

    }

    @Test
    @SuppressWarnings("checkstyle:MethodLength")
    void testWithBeanValidation() {
        var validParameter = getSomeValidParameter();

        assertThatValidation().of(validParameter)
                .usingBeanValidation()
                .notNull("notNullable")
                .notBlank("notBlank")
                .min("biggerThanInt", 5)
                .min("biggerThanLong", 5)
                .min("biggerThanFloat", 5)
                .min("biggerThanDouble", 5)
                .min("biggerThanBigDecimal", 5)
                .min("biggerThanScaledBigDecimal", 5)
                .max("lessThanInt", 5)
                .max("lessThanLong", 5)
                .max("lessThanFloat", 5)
                .max("lessThanDouble", 5)
                .max("lessThanBigDecimal", 5)
                .max("lessThanScaledBigDecimal", 5)
                .positive("positiveInt")
                .positive("positiveLong")
                .positive("positiveFloat")
                .positive("positiveDouble")
                .positive("positiveBigDecimal")
                .positive("positiveScaledBigDecimal")
                .negative("negativeInt")
                .negative("negativeLong")
                .negative("negativeFloat")
                .negative("negativeDouble")
                .negative("negativeBigDecimal")
                .negative("negativeScaledBigDecimal")
                .between("betweenInt", -3, 5)
                .between("betweenLong", -3, 5)
                .between("betweenFloat", -3, 5)
                .between("betweenDouble", -3, 5)
                .between("betweenBigDecimal", -3, 5)
                .between("betweenScaledBigDecimal", -3, 5)
                .positiveOrZero("positiveOrZeroInt")
                .positiveOrZero("positiveOrZeroLong")
                .positiveOrZero("positiveOrZeroFloat")
                .positiveOrZero("positiveOrZeroDouble")
                .positiveOrZero("positiveOrZeroBigDecimal")
                .positiveOrZero("positiveOrZeroScaledBigDecimal")
                .negativeOrZero("negativeOrZeroInt")
                .negativeOrZero("negativeOrZeroLong")
                .negativeOrZero("negativeOrZeroFloat")
                .negativeOrZero("negativeOrZeroDouble")
                .negativeOrZero("negativeOrZeroBigDecimal")
                .negativeOrZero("negativeOrZeroScaledBigDecimal")
                .future("futureDate")
                .future("futureDateTime")
                .future("futureOffsetDateTime")
                .past("pastDate")
                .past("pastDateTime")
                .past("pastOffsetDateTime")
                .validBean("validObject")
                .nullable("nullable")
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
                        .notNull("notNullable")
                        .notBlank("notBlank")
                        .min("biggerThanInt", 5)
                        .min("biggerThanLong", 5)
                        .min("biggerThanFloat", 5)
                        .min("biggerThanDouble", 5)
                        .min("biggerThanBigDecimal", 5)
                        .min("biggerThanScaledBigDecimal", 5)
                        .max("lessThanInt", 5)
                        .max("lessThanLong", 5)
                        .max("lessThanFloat", 5)
                        .max("lessThanDouble", 5)
                        .max("lessThanBigDecimal", 5)
                        .max("lessThanScaledBigDecimal", 5)
                        .positive("positiveInt")
                        .positive("positiveLong")
                        .positive("positiveFloat")
                        .positive("positiveDouble")
                        .positive("positiveBigDecimal")
                        .positive("positiveScaledBigDecimal")
                        .negative("negativeInt")
                        .negative("negativeLong")
                        .negative("negativeFloat")
                        .negative("negativeDouble")
                        .negative("negativeBigDecimal")
                        .negative("negativeScaledBigDecimal")
                        .between("betweenInt", -3, 5)
                        .between("betweenLong", -3, 5)
                        .between("betweenFloat", -3, 5)
                        .between("betweenDouble", -3, 5)
                        .between("betweenBigDecimal", -3, 5)
                        .between("betweenScaledBigDecimal", -3, 5)
                        .positiveOrZero("positiveOrZeroInt")
                        .positiveOrZero("positiveOrZeroLong")
                        .positiveOrZero("positiveOrZeroFloat")
                        .positiveOrZero("positiveOrZeroDouble")
                        .positiveOrZero("positiveOrZeroBigDecimal")
                        .positiveOrZero("positiveOrZeroScaledBigDecimal")
                        .negativeOrZero("negativeOrZeroInt")
                        .negativeOrZero("negativeOrZeroLong")
                        .negativeOrZero("negativeOrZeroFloat")
                        .negativeOrZero("negativeOrZeroDouble")
                        .negativeOrZero("negativeOrZeroBigDecimal")
                        .negativeOrZero("negativeOrZeroScaledBigDecimal")
                        .future("futureDate")
                        .future("futureDateTime")
                        .future("futureOffsetDateTime")
                        .past("pastDate")
                        .past("pastDateTime")
                        .past("pastOffsetDateTime")
                        .validBean("validObject")
                        .nullable("nullable")
                        .notValidated("notValidated")
                        .isCompliant());
    }

    @Test
    void propertyMissingRule_shouldFail() {
        var validParameter = getSomeValidParameter();

        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> assertThatValidation().of(validParameter)
                        .usingBeanValidation()
                        .notNull("notNullable")
                        // this is now missing: .notBlank("notBlank")
                        .min("biggerThanInt", 5)
                        .min("biggerThanLong", 5)
                        .min("biggerThanFloat", 5)
                        .min("biggerThanDouble", 5)
                        .min("biggerThanBigDecimal", 5)
                        .min("biggerThanScaledBigDecimal", 5)
                        .max("lessThanInt", 5)
                        .max("lessThanLong", 5)
                        .max("lessThanFloat", 5)
                        .max("lessThanDouble", 5)
                        .max("lessThanBigDecimal", 5)
                        .max("lessThanScaledBigDecimal", 5)
                        .positive("positiveInt")
                        .positive("positiveLong")
                        .positive("positiveFloat")
                        .positive("positiveDouble")
                        .positive("positiveBigDecimal")
                        .positive("positiveScaledBigDecimal")
                        .negative("negativeInt")
                        .negative("negativeLong")
                        .negative("negativeFloat")
                        .negative("negativeDouble")
                        .negative("negativeBigDecimal")
                        .negative("negativeScaledBigDecimal")
                        .between("betweenInt", -3, 5)
                        .between("betweenLong", -3, 5)
                        .between("betweenFloat", -3, 5)
                        .between("betweenDouble", -3, 5)
                        .between("betweenBigDecimal", -3, 5)
                        .between("betweenScaledBigDecimal", -3, 5)
                        .positiveOrZero("positiveOrZeroInt")
                        .positiveOrZero("positiveOrZeroLong")
                        .positiveOrZero("positiveOrZeroFloat")
                        .positiveOrZero("positiveOrZeroDouble")
                        .positiveOrZero("positiveOrZeroBigDecimal")
                        .positiveOrZero("positiveOrZeroScaledBigDecimal")
                        .negativeOrZero("negativeOrZeroInt")
                        .negativeOrZero("negativeOrZeroLong")
                        .negativeOrZero("negativeOrZeroFloat")
                        .negativeOrZero("negativeOrZeroDouble")
                        .negativeOrZero("negativeOrZeroBigDecimal")
                        .negativeOrZero("negativeOrZeroScaledBigDecimal")
                        .future("futureDate")
                        .future("futureDateTime")
                        .future("futureOffsetDateTime")
                        .past("pastDate")
                        .past("pastDateTime")
                        .past("pastOffsetDateTime")
                        .validBean("validObject")
                        .nullable("nullable")
                        .notValidated("notValidated")
                        .isCompliant());
    }

    private static SomeValidParameter getSomeValidParameter() {
        return new SomeValidParameter(
                // notNull
                "",

                // notBlank
                "something",

                // min
                6,
                6,
                6,
                6,
                BigDecimal.valueOf(6),
                ScaledBigDecimal.valueOf(6),

                // max
                4,
                4,
                4,
                4,
                BigDecimal.valueOf(4),
                ScaledBigDecimal.valueOf(4),

                // positive
                4,
                4,
                4,
                4,
                BigDecimal.valueOf(4),
                ScaledBigDecimal.valueOf(4),

                // negative
                1,
                1,
                1,
                1,
                BigDecimal.valueOf(1),
                ScaledBigDecimal.valueOf(1),

                // between
                -1,
                -1,
                -1,
                -1,
                BigDecimal.valueOf(-1),
                ScaledBigDecimal.valueOf(-1),

                // positiveOrZero
                0,
                0,
                0,
                0,
                BigDecimal.valueOf(0),
                ScaledBigDecimal.valueOf(0),

                // negativeOrZero
                0,
                0,
                0,
                0,
                BigDecimal.valueOf(0),
                ScaledBigDecimal.valueOf(0),

                // future
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1),

                // past
                LocalDate.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                OffsetDateTime.now().minusDays(1),

                // valid
                null,

                // nullable
                null,

                // not validated
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
}
