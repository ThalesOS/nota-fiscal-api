package com.notafiscalapi.service;

import com.notafiscalapi.entity.Produto;
import com.notafiscalapi.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
    @Transactional
    public Produto createProduto(Produto produto){
        if (produtoRepository.existsByDescricao(produto.getDescricao())) {
            throw new RuntimeException("Produto ja existe");

        }
        return produtoRepository.save(produto);
    }
    @Transactional
    public Produto findById(Long id){

        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isPresent()) {
            return produto.get();
        }else {
            throw new RuntimeException("Produto nao encontrado");

        }
    }
    @Transactional
    public List<Produto> findAll(){
        return produtoRepository.findAll();

    }
    @Transactional
    public Produto updateProduto(Long id, Produto produtoAtualizado){

        Optional<Produto> produtoBanco = produtoRepository.findById(id);

        if (produtoBanco.isPresent()) {
            Produto produto = produtoBanco.get();

            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setValorUnitario(produtoAtualizado.getValorUnitario());
            produto.setAliquotaIcms(produtoAtualizado.getAliquotaIcms());
            produto.setCodigoNcm(produtoAtualizado.getCodigoNcm());

            return produtoRepository.save(produto);
        }else {
            throw new RuntimeException("Produto nao encontrado");
        }


    }

    @Transactional
    public void deleteProduto(Long id){
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        }else {
            throw new RuntimeException("Produto nao encontrado");
        }
    }




}
