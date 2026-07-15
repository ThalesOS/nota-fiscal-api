package com.notafiscalapi.controller;

import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.service.EmitenteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Emitente novoEmitente(@RequestBody Emitente emitente) {
        return emitenteService.create(emitente);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Emitente buscaEmitentePorId(@PathVariable Long id) {
        return emitenteService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Emitente> buscarTodosEmitentes() {
        return emitenteService.getAll();
    }

    @PutMapping ("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Emitente atualizarEmitente(@PathVariable Long id, @RequestBody Emitente emitente) {
        return emitenteService.update(emitente,id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarEmitente(@PathVariable Long id) {
        emitenteService.delete(id);
    }


}
