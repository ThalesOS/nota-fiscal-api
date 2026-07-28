package com.notafiscalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class EmitenteRequestDto {

    @NotBlank(message = "O CNPJ é obrigatório")
    @Size(min = 14, max = 14, message = "O CNPJ deve conter exatamente 14 dígitos")
    private String cnpj;
    @NotBlank(message = "A razão social é obrigatória")
    private String razaoSocial;
    @NotBlank(message = "A inscrição Social é obrigatória")
    private String inscricaoEstadual;
    @NotBlank(message = "O logradouro é obrigatório")
    private String logradouro;
    @NotBlank(message = "O número é obrigatório")
    private String numero;
    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;
    @NotBlank(message = "O cep é obrigatório")
    @Size(min = 8, max = 8, message = "O cep deve conter exatos 8 dígitos")
    private String cep;
}
