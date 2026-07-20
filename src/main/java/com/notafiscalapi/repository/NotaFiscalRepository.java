package com.notafiscalapi.repository;

import com.notafiscalapi.entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {

    boolean existsByNumeroNotaFiscalAndNumeroDeSerie(String numeroNotaFiscal, String numeroDeSerie);
}
