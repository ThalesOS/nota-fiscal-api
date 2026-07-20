package com.notafiscalapi.controller;

import com.notafiscalapi.entity.Produto;
import com.notafiscalapi.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;


    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Produto> getProdutos(){
        return produtoService.findAll();

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto createProduto(@RequestBody Produto produto){
        return produtoService.createProduto(produto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Produto findById(@PathVariable Long id){
        return produtoService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Produto updateProduto(@PathVariable Long id, @RequestBody Produto produto){
        return produtoService.updateProduto(id, produto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduto(@PathVariable Long id){
        produtoService.deleteProduto(id);
    }
}
