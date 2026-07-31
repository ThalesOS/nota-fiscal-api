package com.notafiscalapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DestinatarioRequestDto {

    @NotBlank(message = "O documento é obrigatório")
    private String documento;
    @NotBlank(message = "O nome deve ser obrigatório")
    private String nome;
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email com formato inválido")
    private String email;
    @NotBlank(message = "O logradouro é obrigatório")
    private String logradouro;
    @NotBlank(message = "O número é obrigatório")
    private String numero;
    @NotBlank(message = "O cep é obrigatório")
    @Size(min = 8, max = 8, message = "O cep deve conter exatos 8 dígitos")
    private String cep;
}
