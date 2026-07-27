package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.ProdutoRequestDto;
import com.notafiscalapi.dto.ProdutoResponseDto;
import com.notafiscalapi.entity.Produto;

public class ProdutoMapper {

    public static Produto toEntity(ProdutoRequestDto dto) {
        Produto produto = new Produto();
        produto.setDescricao(dto.getDescricao());
        produto.setValorUnitario(dto.getValorUnitario());
        produto.setCodigoNcm(dto.getCodigoNcm());
        produto.setAliquotaIcms(dto.getAliquotaIcms());
        return produto;
    }

    public static ProdutoResponseDto toDto(Produto entity) {
        ProdutoResponseDto dto = new ProdutoResponseDto();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setCodigoNcm(entity.getCodigoNcm());
        dto.setAliquotaIcms(entity.getAliquotaIcms());
        return dto;
    }
}
