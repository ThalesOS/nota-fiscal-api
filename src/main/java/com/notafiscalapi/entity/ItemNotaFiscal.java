package com.notafiscalapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemNotaFiscal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private NotaFiscal notaFiscal;
    @ManyToOne
    private Produto produto;
    @Column(nullable = false)
    private Integer quantidade;
    @Column(nullable = false)
    private BigDecimal valorUnitario;
    @Column(nullable = false)
    private BigDecimal valorTotal;
    @Column(nullable = false)
    private BigDecimal aliquotaIcms;
    @Column(nullable = false)
    private BigDecimal valorIcms;


}
