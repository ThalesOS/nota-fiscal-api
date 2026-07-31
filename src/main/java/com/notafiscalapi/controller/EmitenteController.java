package com.notafiscalapi.controller;

import com.notafiscalapi.dto.EmitenteRequestDto;
import com.notafiscalapi.dto.EmitenteResponseDto;
import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.mapper.EmitenteMapper;
import com.notafiscalapi.service.EmitenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/emitentes")
public class EmitenteController {

    private final EmitenteService emitenteService;

    public EmitenteController(EmitenteService emitenteService) {
        this.emitenteService = emitenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmitenteResponseDto novoEmitente( @Valid @RequestBody EmitenteRequestDto emitenteRequestDto) {
        Emitente entity = EmitenteMapper.toEntity(emitenteRequestDto);
        Emitente emitenteSalvo = emitenteService.create(entity);
        EmitenteResponseDto resposta = EmitenteMapper.toDto(emitenteSalvo);
        return  resposta;

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmitenteResponseDto buscaEmitentePorId(@PathVariable Long id) {
        return EmitenteMapper.toDto(emitenteService.findById(id));


    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmitenteResponseDto> buscarTodosEmitentes() {
       List<Emitente> emitentes = emitenteService.findAll();
       List<EmitenteResponseDto> resposta = new ArrayList<>();
       for (Emitente emitente : emitentes) {
           resposta.add(EmitenteMapper.toDto(emitente));

       }
        return resposta;
    }

    @PutMapping ("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmitenteResponseDto atualizarEmitente(@PathVariable Long id,@Valid @RequestBody EmitenteRequestDto emitenteRequestDto) {
        Emitente entity = EmitenteMapper.toEntity(emitenteRequestDto);
        Emitente emitenteAtualizado = emitenteService.update(entity, id);
        return EmitenteMapper.toDto(emitenteAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarEmitente(@PathVariable Long id) {
        emitenteService.delete(id);
    }


}
