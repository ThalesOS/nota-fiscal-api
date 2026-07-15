package com.notafiscalapi.repository;

import com.notafiscalapi.entity.Emitente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmitenteRepository extends JpaRepository<Emitente, Long> {
    boolean existsByCnpj(String cnpj);
}
