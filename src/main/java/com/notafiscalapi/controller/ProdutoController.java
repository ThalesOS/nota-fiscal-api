package com.notafiscalapi.controller;

import com.notafiscalapi.dto.ProdutoRequestDto;
import com.notafiscalapi.dto.ProdutoResponseDto;
import com.notafiscalapi.entity.Produto;
import com.notafiscalapi.mapper.ProdutoMapper;
import com.notafiscalapi.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;


    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProdutoResponseDto> getProdutos(){
        List<Produto> produtos = produtoService.findAll();
        List<ProdutoResponseDto> produtosResponseDto = new ArrayList<>();
        for (Produto produto : produtos) {
            ProdutoResponseDto produtoResponseDto = ProdutoMapper.toDto(produto);
            produtosResponseDto.add(produtoResponseDto);
        }
        return produtosResponseDto;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponseDto createProduto(@Valid @RequestBody ProdutoRequestDto produtoRequestDto){
        Produto produto = ProdutoMapper.toEntity(produtoRequestDto);
        Produto produto1 = produtoService.create(produto);
        return ProdutoMapper.toDto(produto1);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProdutoResponseDto findById(@PathVariable Long id){
        Produto produto = produtoService.findById(id);
        return ProdutoMapper.toDto(produto);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProdutoResponseDto updateProduto(@PathVariable Long id,@Valid @RequestBody ProdutoRequestDto produtoRequestDto){
        Produto produto = ProdutoMapper.toEntity(produtoRequestDto);
        Produto produto1 = produtoService.update(id, produto);
        return ProdutoMapper.toDto(produto1);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduto(@PathVariable Long id){
        produtoService.delete(id);
    }
}
