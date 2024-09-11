package it.aboutbits.springboot.testing.validation.core;

class RuleValidationException extends RuntimeException {
    RuleValidationException(String message) {
        super(message);
    }

    RuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
