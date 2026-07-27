package com.notafiscalapi.dto;

import com.notafiscalapi.enums.StatusNotaFiscal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
}
