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
        var dto = new EmitenteRequestDto("12345678901234", "ab", "12345678", "ab", "1", "ab", "12345678");

        assertEquals(Set.of("razaoSocial", "logradouro", "bairro"), invalidFields(dto));
    }

    @Test
    void rejectsEmitenteInscricaoEstadualOutsideEightToFourteenDigits() {
        var tooShort = emitenteWithInscricao("1234567");
        var tooLong = emitenteWithInscricao("123456789012345");

        assertEquals(Set.of("inscricaoEstadual"), invalidFields(tooShort));
        assertEquals(Set.of("inscricaoEstadual"), invalidFields(tooLong));
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
    void rejectsNumbersOnlyInTextFields() {
        var emitente = new EmitenteRequestDto(
                "12345678901234", "123", "12345678", "123", "1", "123", "12345678"
        );
        var destinatario = new DestinatarioRequestDto(
                "12345678901", "123", "cliente@email.com", "123", "1", "12345678"
        );
        var produto = new ProdutoRequestDto("123", BigDecimal.ONE, "12345678", BigDecimal.ZERO);

        assertEquals(Set.of("razaoSocial", "logradouro", "bairro"), invalidFields(emitente));
        assertEquals(Set.of("nome", "logradouro"), invalidFields(destinatario));
        assertEquals(Set.of("descricao"), invalidFields(produto));
    }

    @Test
    void acceptsEightDigitNcmAndMinimumLengthNames() {
        var dto = new ProdutoRequestDto("abc", BigDecimal.ONE, "12345678", BigDecimal.ZERO);

        assertEquals(Set.of(), invalidFields(dto));
    }

    @Test
    void acceptsTextFieldsContainingLettersAndNumbers() {
        var emitente = new EmitenteRequestDto(
                "12345678901234", "Empresa 2", "12345678", "Rua 123", "1", "Setor 2", "12345678"
        );
        var destinatario = new DestinatarioRequestDto(
                "12345678901", "Cliente 2", "cliente@email.com", "Rua 123", "1", "12345678"
        );
        var produto = new ProdutoRequestDto("Produto 2", BigDecimal.ONE, "12345678", BigDecimal.ZERO);

        assertEquals(Set.of(), invalidFields(emitente));
        assertEquals(Set.of(), invalidFields(destinatario));
        assertEquals(Set.of(), invalidFields(produto));
    }

    private EmitenteRequestDto emitenteWithInscricao(String inscricaoEstadual) {
        return new EmitenteRequestDto(
                "12345678901234",
                "Empresa",
                inscricaoEstadual,
                "Rua válida",
                "1",
                "Centro",
                "12345678"
        );
    }

    private Set<String> invalidFields(Object dto) {
        return validator.validate(dto).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
