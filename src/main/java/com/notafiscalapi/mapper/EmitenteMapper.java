package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.EmitenteRequestDto;
import com.notafiscalapi.dto.EmitenteResponseDto;
import com.notafiscalapi.entity.Emitente;

public class EmitenteMapper {

    public static Emitente toEntity(EmitenteRequestDto dto) {
        Emitente emitente = new Emitente();
        emitente.setCnpj(dto.getCnpj());
        emitente.setRazaoSocial(dto.getRazaoSocial());
        emitente.setInscricaoEstadual(dto.getInscricaoEstadual());
        emitente.setLogradouro(dto.getLogradouro());
        emitente.setNumero(dto.getNumero());
        emitente.setBairro(dto.getBairro());
        emitente.setCep(dto.getCep());
        return emitente;
    }

    public static EmitenteResponseDto toDto(Emitente entity) {
        EmitenteResponseDto dto = new EmitenteResponseDto();
        dto.setId(entity.getId());
        dto.setCnpj(entity.getCnpj());
        dto.setRazaoSocial(entity.getRazaoSocial());
        dto.setInscricaoEstadual(entity.getInscricaoEstadual());
        dto.setLogradouro(entity.getLogradouro());
        dto.setNumero(entity.getNumero());
        dto.setBairro(entity.getBairro());
        dto.setCep(entity.getCep());
        return dto;
    }
}
