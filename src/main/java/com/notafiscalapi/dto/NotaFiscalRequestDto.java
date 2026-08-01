package com.notafiscalapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @NotEmpty(message = "A nota fiscal deve conter ao menos um item")
    @Valid
    private List<ItemNotaFiscalRequestDto> items;

}
