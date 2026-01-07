package it.aboutbits.springboot.testing.validation.core;

import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public interface CustomValidationFunction extends Function<Object, CustomValidationFunction.Result> {
    record Result(boolean valid, String message) {
    }
}
