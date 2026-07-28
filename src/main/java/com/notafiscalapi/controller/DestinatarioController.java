package com.notafiscalapi.controller;

import com.notafiscalapi.dto.DestinatarioRequestDto;
import com.notafiscalapi.dto.DestinatarioResponseDto;
import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.mapper.DestinatarioMapper;
import com.notafiscalapi.service.DestinatarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public DestinatarioResponseDto novoDestinatario(@Valid @RequestBody DestinatarioRequestDto destinatarioRequestDto) {
        Destinatario destinatario = DestinatarioMapper.toEntity(destinatarioRequestDto);
        Destinatario destinatario1 = destinatarioService.create(destinatario);
        return DestinatarioMapper.toDto(destinatario1);


    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DestinatarioResponseDto buscaDestinatarioPorId(@Valid @PathVariable Long id) {
        return DestinatarioMapper.toDto(destinatarioService.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DestinatarioResponseDto> buscarTodosDestinatarios() {
        List<Destinatario> destinatario = destinatarioService.findAll();
        List<DestinatarioResponseDto> resposta = new ArrayList<DestinatarioResponseDto>();
        for (Destinatario d : destinatario) {
            resposta.add(DestinatarioMapper.toDto(d));
        }
        return resposta;

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DestinatarioResponseDto atualizarDestinatario( @PathVariable Long id, @Valid @RequestBody DestinatarioRequestDto destinatarioRequestDto) {
        Destinatario destinatario = DestinatarioMapper.toEntity(destinatarioRequestDto);
        Destinatario destinatario1 = destinatarioService.update(destinatario, id);
        return DestinatarioMapper.toDto(destinatario1);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarDestinatario(@PathVariable Long id) {
        destinatarioService.delete(id);
    }

}