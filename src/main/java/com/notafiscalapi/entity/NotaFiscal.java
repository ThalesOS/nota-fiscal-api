package com.notafiscalapi.entity;

import com.notafiscalapi.enums.StatusNotaFiscal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor@AllArgsConstructor
public class NotaFiscal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String numeroNotaFiscal;
    @Column(nullable = false)
    private String numeroDeSerie;
    @Column(nullable = false)
    private LocalDateTime dataEmissao;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusNotaFiscal status;
    @ManyToOne
    private Emitente emitente;
    @ManyToOne
    private Destinatario destinatario;

}
