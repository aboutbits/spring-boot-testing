package it.aboutbits.springboot.testing.validation.core;

import java.util.function.Function;

public interface CustomValidationFunction extends Function<Object, CustomValidationFunction.Result> {
    record Result(boolean valid, String message) {
    }
}
