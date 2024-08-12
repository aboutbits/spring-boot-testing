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

## Local development:

To use this library as a local development dependency, you can simply refer to the version `BUILD-SNAPSHOT`.

Check out this repository and run the maven goal `install`. This will build and install this library as version `BUILD-SNAPSHOT` into your local maven cache.

Note that you may have to tell your IDE to reload your main maven project each time you build the library.

## Building and releasing a new version:

To create a new version of this package and push it to the maven registry, you will have to use the GitHub actions workflow and manually trigger it.

## Information

About Bits is a company based in South Tyrol, Italy. You can find more information about us on [our website](https://aboutbits.it).

### Support

For support, please contact [info@aboutbits.it](mailto:info@aboutbits.it).

### Credits

- [All Contributors](../../contributors)

### License

The MIT License (MIT). Please see the [license file](license.md) for more information.
