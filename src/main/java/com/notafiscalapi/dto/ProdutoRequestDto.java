package com.notafiscalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoRequestDto {
    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 3, message = "A descrição deve conter ao menos 3 caracteres")
    private String descricao;
    @NotNull(message = "O valor unitário é obrigatório")
    @Positive(message = "O valor unitário deve ser maior que zero")
    private BigDecimal valorUnitario;
    @NotBlank(message = "O código NCM é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "O código NCM deve conter exatamente 8 dígitos numéricos")
    private String codigoNcm;
    @NotNull(message = "A aliquotaICMS é obrigatória")
    @PositiveOrZero(message = "O valor da aliquota deve ser positiva")
    private BigDecimal aliquotaIcms;
}
