package com.notafiscalapi.controller;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.service.DestinatarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinatarios")
public class DestinatarioController {

    private final DestinatarioService destinatarioService;

    public DestinatarioController(DestinatarioService destinatarioService) {
        this.destinatarioService = destinatarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Destinatario novoDestinatario(@RequestBody Destinatario destinatario) {
        return destinatarioService.create(destinatario);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Destinatario buscaDestinatarioPorId(@PathVariable Long id) {
        return destinatarioService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Destinatario> buscarTodosDestinatarios() {
        return destinatarioService.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Destinatario atualizarDestinatario(@PathVariable Long id,
                                              @RequestBody Destinatario destinatario) {
        return destinatarioService.update(destinatario, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarDestinatario(@PathVariable Long id) {
        destinatarioService.delete(id);
    }
    @PostMapping
    public ResponseEntity<Destinatario> criarDestinatario(@RequestBody Destinatario destinatario) {
        try {
            Destinatario novoDestinatario = destinatarioService.salvar(destinatario);
            return ResponseEntity.ok(novoDestinatario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destinatario> buscarDestinatarioPorId(@PathVariable Long id) {
        return destinatarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}