package com.notafiscalapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "destinatarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destinatario {
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private String documento; // CPF ou CNPJ

    private String nome;

    private String email;

    private String logradouro;

    private String numero;

    private String cep;
}
