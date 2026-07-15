package com.notafiscalapi.service;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.repository.DestinatarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DestinatarioService {

    private final DestinatarioRepository repository;

    public DestinatarioService(DestinatarioRepository repository) {
        this.repository = repository;
    }

    public Destinatario salvar(Destinatario destinatario) {

        if (destinatario.getEmail() == null || !destinatario.getEmail().contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }

        if (destinatario.getDocumento() == null || destinatario.getDocumento().isBlank()) {
            throw new IllegalArgumentException("Documento obrigatório.");
        }

        return repository.save(destinatario);
    }

    public Optional<Destinatario> buscarPorId(Long id) {
        return repository.findById(id);
    }
}