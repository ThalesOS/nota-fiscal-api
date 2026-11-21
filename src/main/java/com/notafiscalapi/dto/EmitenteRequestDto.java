package com.notafiscalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 3, message = "A razão social deve conter ao menos 3 caracteres")
    @Pattern(regexp = ".*\\p{L}.*", message = "A razão social deve conter pelo menos uma letra")
    private String razaoSocial;
    @NotBlank(message = "A Inscrição Estadual é obrigatória")
    @Pattern(regexp = "\\d{8,14}", message = "A Inscrição Estadual deve conter entre 8 e 14 dígitos numéricos")
    private String inscricaoEstadual;
    @NotBlank(message = "O logradouro é obrigatório")
    @Size(min = 3, message = "O logradouro deve conter ao menos 3 caracteres")
    @Pattern(regexp = ".*\\p{L}.*", message = "O logradouro deve conter pelo menos uma letra")
    private String logradouro;
    @NotBlank(message = "O número é obrigatório")
    private String numero;
    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, message = "O bairro deve conter ao menos 3 caracteres")
    @Pattern(regexp = ".*\\p{L}.*", message = "O bairro deve conter pelo menos uma letra")
    private String bairro;
    @NotBlank(message = "O cep é obrigatório")
    @Size(min = 8, max = 8, message = "O cep deve conter exatos 8 dígitos")
    private String cep;
}
