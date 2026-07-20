package com.notafiscalapi.service;

import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.repository.NotaFiscalRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
    }
    @Transactional
    public NotaFiscal create(NotaFiscal notaFiscal) {

        if (notaFiscalRepository.existsByNumeroNotaFiscalAndNumeroDeSerie(notaFiscal.getNumeroNotaFiscal(), notaFiscal.getNumeroDeSerie())) {
            throw new RuntimeException("Nota Fiscal ja existente");
        }else  {
            notaFiscal.setDataEmissao(LocalDateTime.now());
            return notaFiscalRepository.save(notaFiscal);
        }
    }

    public List<NotaFiscal> getAll() {
        return notaFiscalRepository.findAll();
    }

    public NotaFiscal findById(Long id) {
        Optional<NotaFiscal> notaFiscal = notaFiscalRepository.findById(id);
        if (notaFiscal.isPresent()) {
            return notaFiscal.get();
        }else  {
            throw new RuntimeException("Nota Fiscal nao encontrado");
        }
    }
    @Transactional
    public NotaFiscal update(Long id,NotaFiscal notaFiscal) {

        Optional<NotaFiscal> notaFiscal1 = notaFiscalRepository.findById(id);

        if (notaFiscal1.isPresent()) {

            NotaFiscal notaFiscalDoBanco = notaFiscal1.get();

            notaFiscalDoBanco.setStatus(notaFiscal.getStatus());
            notaFiscalDoBanco.setEmitente(notaFiscal.getEmitente());
            notaFiscalDoBanco.setDestinatario(notaFiscal.getDestinatario());

            return notaFiscalRepository.save(notaFiscalDoBanco);

        }else   {
            throw new RuntimeException("Nota Fiscal nao encontrado");
        }
    }
    @Transactional
    public void delete(Long id) {
        if (notaFiscalRepository.findById(id).isPresent()) {
            notaFiscalRepository.deleteById(id);
        }else   {
            throw new RuntimeException("Nota Fiscal nao encontrado");
        }
    }
}
