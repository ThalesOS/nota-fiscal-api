package com.notafiscalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmitenteResponseDto {

    private Long id;
    private String cnpj;
    private String razaoSocial;
    private String inscricaoEstadual;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;

}
