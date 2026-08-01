package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.ItemNotaFiscalResponseDto;
import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalResponseDto;
import com.notafiscalapi.entity.ItemNotaFiscal;
import com.notafiscalapi.entity.NotaFiscal;

import java.util.ArrayList;
import java.util.List;

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

        dto.setValorTotalProdutos(entity.getValorTotalProdutos());
        dto.setValorTotalIcms(entity.getValorTotalIcms());
        dto.setValorTotalNotaFiscal(entity.getValorTotalNotaFiscal());

        if (entity.getEmitente() != null) {
            dto.setEmitente(EmitenteMapper.toDto(entity.getEmitente()));
        }

        if (entity.getDestinatario() != null) {
            dto.setDestinatario(DestinatarioMapper.toDto(entity.getDestinatario()));
        }

        if (entity.getItens() != null && !entity.getItens().isEmpty()) {
            List<ItemNotaFiscalResponseDto> itensDto = new ArrayList<>();
            for (ItemNotaFiscal item : entity.getItens()) {
                itensDto.add(ItemNotaFiscalMapper.toDto(item));
            }
            dto.setItens(itensDto);
        }

        return dto;
    }
}
