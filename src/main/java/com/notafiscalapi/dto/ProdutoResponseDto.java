package com.notafiscalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoResponseDto {

    private Long id;
    private String descricao;
    private BigDecimal valorUnitario;
    private String codigoNcm;
    private BigDecimal aliquotaIcms;
}
