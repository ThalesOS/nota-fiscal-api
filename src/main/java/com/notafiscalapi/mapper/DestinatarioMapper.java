package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.DestinatarioRequestDto;
import com.notafiscalapi.dto.DestinatarioResponseDto;
import com.notafiscalapi.entity.Destinatario;

public class DestinatarioMapper {

    public static Destinatario toEntity(DestinatarioRequestDto dto) {
        Destinatario destinatario = new Destinatario();
        destinatario.setDocumento(dto.getDocumento());
        destinatario.setNome(dto.getNome());
        destinatario.setEmail(dto.getEmail());
        destinatario.setLogradouro(dto.getLogradouro());
        destinatario.setNumero(dto.getNumero());
        destinatario.setCep(dto.getCep());
        return destinatario;
    }

    public static DestinatarioResponseDto toDto(Destinatario entity) {
        DestinatarioResponseDto  dto = new DestinatarioResponseDto();
        dto.setId(entity.getId());
        dto.setDocumento(entity.getDocumento());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setLogradouro(entity.getLogradouro());
        dto.setNumero(entity.getNumero());
        dto.setCep(entity.getCep());
        return dto;
    }
}
