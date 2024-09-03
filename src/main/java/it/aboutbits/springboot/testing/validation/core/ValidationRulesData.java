package it.aboutbits.springboot.testing.validation.core;

import lombok.NonNull;

public interface ValidationRulesData {
    void addRule(@NonNull Rule rule);
}
