package com.chy.agents.alibaba.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlibabaConfigValidationTest {

    private Validator validator;
    private AlibabaConfig config;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        config = new AlibabaConfig();
        config.setApiKey("test-api-key");
        config.setModel("qwen-max");
        config.setEndpoint("https://test-endpoint");
    }

    @Test
    void shouldValidateValidConfig() {
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWithBlankApiKey() {
        config.setApiKey("");
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("API key must not be blank");
    }

    @ParameterizedTest
    @ValueSource(floats = {-0.1f, 1.1f})
    void shouldFailValidationWithInvalidTemperature(float temperature) {
        config.setTemperature(temperature);
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Temperature must be between 0 and 1");
    }

    @Test
    void shouldFailValidationWithNegativeTimeout() {
        config.setTimeout(-1);
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Timeout must be positive");
    }

    @Test
    void shouldFailValidationWithNegativeMaxTokens() {
        config.setMaxTokens(-1);
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Max tokens must be positive");
    }

    @Test
    void shouldFailValidationWithNegativeMaxRetries() {
        config.setMaxRetries(-1);
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Max retries must not be negative");
    }

    @Test
    void shouldValidateWithValidTemperature() {
        config.setTemperature(0.5f);
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(config);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldThrowExceptionOnValidate() {
        config.setApiKey(null);
        assertThatThrownBy(() -> config.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("API key must not be blank");
    }

    @Test
    void shouldValidateWithDefaultValues() {
        AlibabaConfig defaultConfig = new AlibabaConfig();
        defaultConfig.setApiKey("test-api-key"); // Only set required field
        Set<ConstraintViolation<AlibabaConfig>> violations = validator.validate(defaultConfig);
        assertThat(violations).isEmpty();
    }
} 