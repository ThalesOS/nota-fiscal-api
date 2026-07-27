package com.notafiscalapi.dto;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.entity.Emitente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotaFiscalRequestDto {

    private String numeroNotaFiscal;
    private String numeroDeSerie;
    private Long emitenteId;
    private Long destinatarioId;
}
