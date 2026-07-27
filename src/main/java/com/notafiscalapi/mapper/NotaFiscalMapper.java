package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalResponseDto;
import com.notafiscalapi.entity.NotaFiscal;

public class NotaFiscalMapper {

    public static NotaFiscal toEntity(NotaFiscalRequestDto dto) {
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        notaFiscal.setNumeroDeSerie(dto.getNumeroDeSerie());
        return notaFiscal;
    }

    public static NotaFiscalResponseDto toDto(NotaFiscal entity) {
        NotaFiscalResponseDto dto = new NotaFiscalResponseDto();
        dto.setId(entity.getId());
        dto.setNumeroNotaFiscal(entity.getNumeroNotaFiscal());
        dto.setNumeroDeSerie(entity.getNumeroDeSerie());
        dto.setDataEmissao(entity.getDataEmissao());
        dto.setStatus(entity.getStatus());

        if (entity.getEmitente() != null) {
            dto.setEmitente(EmitenteMapper.toDto(entity.getEmitente()));
        }

        if (entity.getDestinatario() != null) {
            dto.setDestinatario(DestinatarioMapper.toDto(entity.getDestinatario()));
        }

        return dto;
    }
}
