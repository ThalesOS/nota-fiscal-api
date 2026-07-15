package com.notafiscalapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
public class Emitente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private String razaoSocial;
    @Column(nullable = false)
    private String inscricaoEstadual;
    @Column(nullable = false)
    private String logradouro;
    @Column(nullable = false)
    private String numero;
    @Column(nullable = false)
    private String bairro;
    @Column(nullable = false, length = 8)
    private String cep;

}
