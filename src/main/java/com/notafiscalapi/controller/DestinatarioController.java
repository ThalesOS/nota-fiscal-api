package com.notafiscalapi.controller;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.service.DestinatarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/destinatarios")
@RequiredArgsConstructor
public class DestinatarioController {

    private final DestinatarioService destinatarioService;

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