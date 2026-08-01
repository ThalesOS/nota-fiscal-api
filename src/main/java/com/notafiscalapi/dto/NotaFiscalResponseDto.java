package com.notafiscalapi.dto;

import com.notafiscalapi.enums.StatusNotaFiscal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotaFiscalResponseDto {
    private Long id;
    private String numeroDeSerie;
    private String numeroNotaFiscal;
    private LocalDateTime dataEmissao;
    private StatusNotaFiscal  status;
    private EmitenteResponseDto emitente;
    private DestinatarioResponseDto  destinatario;
    private List<ItemNotaFiscalResponseDto> itens;
    private BigDecimal valorTotalProdutos;
    private BigDecimal valorTotalIcms;
    private BigDecimal valorTotalNotaFiscal;
}
