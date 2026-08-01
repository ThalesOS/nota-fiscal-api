package com.notafiscalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ItemNotaFiscalResponseDto {

    private Long id;
    private Long produtoId;
    private String descricaoProduto;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal aliquotaIcms;
    private BigDecimal valorIcms;
}
