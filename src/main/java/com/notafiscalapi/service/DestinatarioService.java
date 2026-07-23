package com.notafiscalapi.service;

import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.exception.BusinessRuleException;
import com.notafiscalapi.exception.DuplicateResourceException;
import com.notafiscalapi.exception.ResourceNotFoundException;
import com.notafiscalapi.repository.DestinatarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinatarioService {

    private final DestinatarioRepository destinatarioRepository;

    public DestinatarioService(DestinatarioRepository destinatarioRepository) {
        this.destinatarioRepository = destinatarioRepository;
    }

    @Transactional
    public Destinatario create(Destinatario destinatario) {

        if (destinatarioRepository.existsByDocumento(destinatario.getDocumento())) {
            throw new DuplicateResourceException("Destinatário já existe");
        }

        return destinatarioRepository.save(destinatario);
    }

    @Transactional
    public Destinatario findById(Long id) {

        Optional<Destinatario> destinatario = destinatarioRepository.findById(id);

        if (destinatario.isPresent()) {
            return destinatario.get();
        } else {
            throw new ResourceNotFoundException("Destinatário não encontrado");
        }
    }

    @Transactional
    public List<Destinatario> findAll() {
        return destinatarioRepository.findAll();
    }

    @Transactional
    public Destinatario update(Destinatario destinatarioComNovosDados, Long id) {

        Optional<Destinatario> destinatarioNoBanco = destinatarioRepository.findById(id);

        if (destinatarioNoBanco.isPresent()) {

            Destinatario destinatarioExistente = destinatarioNoBanco.get();

            if (!destinatarioExistente.getDocumento().equals(destinatarioComNovosDados.getDocumento())
                    && destinatarioRepository.existsByDocumento(destinatarioComNovosDados.getDocumento())) {

                throw new BusinessRuleException("Não é possível alterar o documento. Este documento já pertence a outro destinatário.");

            } else {

                destinatarioExistente.setDocumento(destinatarioComNovosDados.getDocumento());
                destinatarioExistente.setNome(destinatarioComNovosDados.getNome());
                destinatarioExistente.setEmail(destinatarioComNovosDados.getEmail());
                destinatarioExistente.setLogradouro(destinatarioComNovosDados.getLogradouro());
                destinatarioExistente.setNumero(destinatarioComNovosDados.getNumero());
                destinatarioExistente.setCep(destinatarioComNovosDados.getCep());

                return destinatarioRepository.save(destinatarioExistente);
            }

        } else {
            throw new ResourceNotFoundException("Destinatário não encontrado para atualização");
        }
    }

    @Transactional
    public void delete(Long id) {

        if (destinatarioRepository.existsById(id)) {
            destinatarioRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Destinatário não encontrado");
        }
    }
}