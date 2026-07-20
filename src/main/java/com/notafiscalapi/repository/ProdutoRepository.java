package com.notafiscalapi.repository;

import com.notafiscalapi.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigoNcm(String codigoNcm);

    boolean existsByDescricao(String descricao);
}
