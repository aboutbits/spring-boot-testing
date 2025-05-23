package it.aboutbits.springboot.testing.testdata.base;

import it.aboutbits.springboot.toolbox.type.ScaledBigDecimal;
import it.aboutbits.springboot.toolbox.type.identity.EntityId;
import lombok.NonNull;
import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;

import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.function.LongFunction;

public class FakerExtended extends Faker {
    public FakerExtended() {
        super();
    }

    public FakerExtended(Locale locale) {
        super(locale);
    }

    public FakerExtended(Random random) {
        super(random);
    }

    public FakerExtended(Locale locale, Random random) {
        super(locale, random);
    }

    public FakerExtended(Locale locale, RandomService randomService) {
        super(locale, randomService);
    }

    public FakerExtended(FakeValuesService fakeValuesService, FakerContext context) {
        super(fakeValuesService, context);
    }

    public <T extends Enum<?>> T randomEnumValue(@NonNull Class<T> enumClass) {
        var values = enumClass.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException("Enum class must have at least one value");
        }
        return values[this.random().nextInt(values.length)];
    }

    public <T extends EntityId<Long>> T randomEntityId(@NonNull LongFunction<T> constructor) {
        return constructor.apply(
                super.random().nextInt(9999999)
        );
    }

    public <T extends EntityId<String>> T randomEntityId(@NonNull Function<String, T> constructor) {
        return constructor.apply(
                super.internet().uuid()
        );
    }

    public String unique(@NonNull String value) {
        return value + "_" + super.random().nextInt(9999999);
    }

    public RandomNumericRange numericRange() {
        return new RandomNumericRange(
                super.getFaker()
        );
    }

    public static final class RandomNumericRange {
        private final Faker parent;

        private RandomNumericRange(Faker parent) {
            this.parent = parent;
        }

        public NumericRange random() {
            return random(-999999999, 999999999);
        }

        public NumericRange positive() {
            return random(1, 999999999);
        }

        public NumericRange positiveOrZero() {
            return random(0, 999999999);
        }

        public NumericRange negative() {
            return random(-999999999, -1);
        }

        public NumericRange negativeOrZero() {
            return random(-999999999, 0);
        }

        public NumericRange random(double min, double max) {
            var lower = parent.random().nextDouble(min, max - 1);
            var upper = parent.random().nextDouble(lower, max);

            return new NumericRange(
                    ScaledBigDecimal.valueOf(lower),
                    ScaledBigDecimal.valueOf(upper)
            );
        }

        public record NumericRange(ScaledBigDecimal lower, ScaledBigDecimal upper) {

        }
    }
}
