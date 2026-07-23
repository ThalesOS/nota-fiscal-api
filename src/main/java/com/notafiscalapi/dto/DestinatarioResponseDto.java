package com.notafiscalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DestinatarioResponseDto {

    private Long id;
    private String documento;
    private String nome;
    private String email;
    private String logradouro;
    private String numero;
    private String cep;

}
