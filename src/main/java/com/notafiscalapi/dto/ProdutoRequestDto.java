package com.notafiscalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    private String descricao;
    @NotNull(message = "O valor unitário é obrigatório")
    @Positive(message = "O valor unitário deve ser maior que zero")
    private BigDecimal valorUnitario;
    @NotBlank(message = "O código NCM é obrigatório")
    private String codigoNcm;
    @NotNull(message = "A aliquotaICMS é obrigatória")
    @PositiveOrZero(message = "O valor da aliquota deve ser positiva")
    private BigDecimal aliquotaIcms;
}
