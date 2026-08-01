package com.notafiscalapi.mapper;

import com.notafiscalapi.dto.ItemNotaFiscalResponseDto;
import com.notafiscalapi.entity.ItemNotaFiscal;

public class ItemNotaFiscalMapper {

    public static ItemNotaFiscalResponseDto toDto(ItemNotaFiscal itemNotaFiscal) {
        ItemNotaFiscalResponseDto dto = new ItemNotaFiscalResponseDto();
        dto.setId(itemNotaFiscal.getId());
        dto.setAliquotaIcms(itemNotaFiscal.getAliquotaIcms());
        dto.setQuantidade(itemNotaFiscal.getQuantidade());
        dto.setValorUnitario(itemNotaFiscal.getValorUnitario());
        dto.setValorTotal(itemNotaFiscal.getValorTotal());
        dto.setValorIcms(itemNotaFiscal.getValorIcms());

        if (itemNotaFiscal.getProduto() != null) {
            dto.setProdutoId(itemNotaFiscal.getProduto().getId());
            dto.setDescricaoProduto(itemNotaFiscal.getProduto().getDescricao());
        }

        return dto;
    }
}
