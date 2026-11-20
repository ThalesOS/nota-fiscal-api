package com.notafiscalapi.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void rejectsShortEmitenteNameAndAddress() {
        var dto = new EmitenteRequestDto("12345678901234", "ab", "123", "ab", "1", "ab", "12345678");

        assertEquals(Set.of("razaoSocial", "logradouro", "bairro"), invalidFields(dto));
    }

    @Test
    void rejectsShortDestinatarioNameAndAddress() {
        var dto = new DestinatarioRequestDto("12345678901", "ab", "cliente@email.com", "ab", "1", "12345678");

        assertEquals(Set.of("nome", "logradouro"), invalidFields(dto));
    }

    @Test
    void rejectsShortProductDescriptionAndInvalidNcm() {
        var dto = new ProdutoRequestDto("ab", BigDecimal.ONE, "1", BigDecimal.ZERO);

        assertEquals(Set.of("descricao", "codigoNcm"), invalidFields(dto));
    }

    @Test
    void acceptsEightDigitNcmAndMinimumLengthNames() {
        var dto = new ProdutoRequestDto("abc", BigDecimal.ONE, "12345678", BigDecimal.ZERO);

        assertEquals(Set.of(), invalidFields(dto));
    }

    private Set<String> invalidFields(Object dto) {
        return validator.validate(dto).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
