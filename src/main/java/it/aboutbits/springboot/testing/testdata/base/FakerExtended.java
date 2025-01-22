package it.aboutbits.springboot.testing.testdata.base;

import it.aboutbits.springboot.toolbox.type.identity.EntityId;
import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;

import java.util.Locale;
import java.util.Random;
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

    public <T extends Enum<?>> T randomEnumValue(Class<T> enumClass) {
        var values = enumClass.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException("Enum class must have at least one value");
        }
        return values[this.random().nextInt(values.length)];
    }

    public <T extends EntityId<Long>> T randomEntityId(LongFunction<T> constructor) {
        return constructor.apply(
                super.random().nextInt(99999)
        );
    }
}
