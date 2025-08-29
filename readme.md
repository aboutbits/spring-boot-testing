# Spring Boot Testing

Testing library for Spring Boot projects.

## Setup

Add this library to the classpath by adding the following maven dependency. Versions can be found [here](../../packages)

```xml

<dependency>
    <groupId>it.aboutbits</groupId>
    <artifactId>spring-boot-testing</artifactId>
    <version>x.x.x</version>
</dependency>
```

## Usage

### Validation

The validation tester allows us to quickly test simple validation constraints. Most commonly we use bean validation for this.

#### Configuration

To use the validation tester in your project you need to extend both the [BaseValidationAssert.java](src/main/java/it/aboutbits/springboot/testing/validation/core/BaseValidationAssert.java) and the [BaseRuleBuilder.java](src/main/java/it/aboutbits/springboot/testing/validation/core/BaseRuleBuilder.java).

```java
public class ValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
    protected ValidationAssert() {
        super(new RuleBuilder());
    }

    public static ValidationAssert assertThatValidation() {
        return new ValidationAssert();
    }

    public static final class RuleBuilder extends BaseRuleBuilder<RuleBuilder> {

    }
}
```

By default, the validation tester will assume that all properties of type `Record` are substructures. Therefore, using the `@Valid` annotation is required to make sure that validation for those records is triggered.
You can add a class to a whitelist to disable this behavior:

```java

public class ValidationConfig {
    public static void configure() {
        ValidationAssert.registerNonBeanType(NotValidated.class);
    }
}

public class ValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
    static {
        ValidationConfig.configure();
    }

    // ...
}
```

#### Usage

Each property is required to have at least one rule defined. You can add multiple rules for the same property as needed to combine more complex rulesets.
The tester will fail if not all properties have rules. In case you have properties without any restrictions, use the `notValidated` rule.

The validation tester works by taking in a **valid** parameter. It will then mutate the parameter internally and test each property with an invalid value. Then a check is done if a validation violation is raised as expected.

In any case, the call to `isCompliant` is required at the end and then triggers the actual assertion.

You can use plain bean validation to verify a Record:

```java
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.lang.Nullable;

public record SomeParameter(
        @NotBlank
        String name,
        @Min(18)
        int age,
        @NotNull
        @Past
        LocalDate birthDay,
        @Nullable
        String something,
        String notValidatedAtAll
) {
}


@Test
void testValidation() {
    var validParameter = new SomeParameter("Sepp", 32);

    assertThatValidation().of(validParameter)
            .usingBeanValidation()
            .notBlank("name")
            .min("age", 18)
            .notNull("birthDay")
            .past("birthDay")
            .nullable("something")
            .notValidated("notValidatedAtAll")
            .isCompliant();
}
```

Alternatively you can use a method call to a service function to verify the validation. This is the preferred way as it makes sure that the bean validation is both triggered and also valid.

```java
import org.springframework.beans.factory.annotation.Autowired;

public record SomeParameter(
        @NotBlank
        String name,
        @Min(18)
        int age
) {
}

@Autowired
private MyService myService;


@Test
void testValidation() {
    var validParameter = new SomeParameter("Sepp", 32);

    assertThatValidation().of(validParameter)
            .calling(myService::create)
            .notBlank("name")
            .min("age", 18)
            .isCompliant();
}

@Test
void testValidationWithIdParameter() {
    var validParameter = new SomeParameter("Sepp", 32);

    assertThatValidation().of(validParameter)
            .calling(myService::update, new User.ID(3L))
            .notBlank("name")
            .min("age", 18)
            .isCompliant();
}
```

#### Adding Custom Validation Rules

You can add new rules by creating a new interface:

```java
public interface MyShinyNewRule<V extends BaseRuleBuilder<?>> extends ValidationRulesData {
    default V shiny(@NonNull String property) {
        this.addRule(new Rule(property, InertValueSource.class, new Object[0]));
        return (BaseRuleBuilder) this;
    }
}
```

To use the newly created rule, we can simply have our `RuleBuilder` implement the interface:

```java
public class ValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
    // ...

    public static final class RuleBuilder extends BaseRuleBuilder<RuleBuilder> implements MyShinyNewRule<RuleBuilder> {

    }
}
```

The `Rule` requires the property name, a value-source and an array of optional parameters. For example `min(property, minValue)` takes in the additional parameter for the value.
Note that the value-source must return **invalid** values. This is required because the tool is actively trying to violate the rules to check if an error is raised.

#### Adding Custom Value Sources

You can add custom values sources by implementing the `ValueSource` interface.
While the interface can not enforce the static function `registerType`, it is best practice to implement it in a way that keeps this extensible.
This way we can use the same logical value-source for multiple property types.

Here is an example:

```java
public class EmptyValueSource implements ValueSource {
    private static final Map<Class<?>, Function<Object[], Stream<?>>> TYPE_SOURCES = new HashMap<>();

    static {
        TYPE_SOURCES.put(
                String.class,
                (Object[] args) -> Stream.of("")
        );
        TYPE_SOURCES.put(
                Set.class,
                (Object[] args) -> Stream.of(new HashSet<>())
        );
        TYPE_SOURCES.put(
                List.class,
                (Object[] args) -> Stream.of(new ArrayList<>())
        );
    }

    public static void registerType(Class<?> type, Function<Object[], Stream<?>> source) {
        TYPE_SOURCES.put(type, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> values(Class<T> propertyClass, Object... args) {
        var sourceFunction = TYPE_SOURCES.get(propertyClass);
        if (sourceFunction != null) {
            return (Stream<T>) sourceFunction.apply(args);
        }

        throw new IllegalArgumentException("Property class not supported!");
    }
}
```

#### Adding Support for Custom Types

_Note: CustomType wrappers from the `toolbox` are currently not natively supported._

Adding custom types will require some extension to the existing value-sources. Those need to become aware of the new type in order to produce values of said type.

This can be done by extending the configuration:

```java
public record SurnameType(
        String value
) {
}

public class ValidationConfig {
    public static void configure() {
        EmptyValueSource.registerType(
                SurnameType.class,
                (args) -> {
                    return Stream.of(new SurnameType(""));
                }
        );

        // ...
    }
}

public class ValidationAssert extends BaseValidationAssert<BaseRuleBuilder<?>> {
    static {
        ValidationConfig.configure();
    }

    // ...
}
```

## Local Development

To use this library as a local development dependency, you can simply refer to the version `BUILD-SNAPSHOT`.

Check out this repository and run the maven goal `install`. This will build and install this library as version `BUILD-SNAPSHOT` into your local maven cache.

Note that you may have to tell your IDE to reload your main maven project each time you build the library.

## Build & Publish

To build and publish the chart, visit the GitHub Actions page of the repository and trigger the workflow "Release Package" manually.

## Information

About Bits is a company based in South Tyrol, Italy. You can find more information about us on [our website](https://aboutbits.it).

### Support

For support, please contact [info@aboutbits.it](mailto:info@aboutbits.it).

### Credits

- [All Contributors](../../contributors)

### License

The MIT License (MIT). Please see the [license file](license.md) for more information.
