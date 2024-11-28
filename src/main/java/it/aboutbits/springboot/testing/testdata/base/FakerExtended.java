package it.aboutbits.springboot.testing.testdata.base;

import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;

import java.util.Locale;
import java.util.Random;

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
        return values[this.random().nextInt(values.length)];
    }
}
