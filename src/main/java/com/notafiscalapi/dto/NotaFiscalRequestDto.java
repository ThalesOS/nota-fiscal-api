package com.notafiscalapi.dto;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.entity.Emitente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotaFiscalRequestDto {

    @NotBlank(message = "O número da nota fiscal é obrigatório")
    private String numeroNotaFiscal;
    @NotBlank(message = "O número de serie é obrigatório")
    private String numeroDeSerie;
    @NotNull(message = "O ID do emitente é obrigatório")
    private Long emitenteId;
    @NotNull(message = "O ID de destinatário é obrigaatório")
    private Long destinatarioId;
}
