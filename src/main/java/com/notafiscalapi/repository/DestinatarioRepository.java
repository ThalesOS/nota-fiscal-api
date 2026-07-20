package com.notafiscalapi.repository;

import com.notafiscalapi.entity.Destinatario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {

    boolean existsByDocumento(String documento);

}