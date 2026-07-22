package com.notafiscalapi.service;

import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.repository.EmitenteRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmitenteService {

    private final EmitenteRepository emitenteRepository;

    public EmitenteService(EmitenteRepository emitenteRepository) {
        this.emitenteRepository = emitenteRepository;
    }

    @Transactional
    public Emitente create(Emitente emitente){
        if(emitenteRepository.existsByCnpj(emitente.getCnpj())){
            throw new RuntimeException("Emitente já existe");
        }
        return emitenteRepository.save(emitente);
    }

    @Transactional
    public Emitente findById(Long id){

        Optional<Emitente> emitente = emitenteRepository.findById(id);

        if(emitente.isPresent()){
            return emitente.get();
        } else {
            throw new RuntimeException("Emitente nao encontrado");
        }
    }
    @Transactional
    public List<Emitente> findAll(){
        return emitenteRepository.findAll();
    }

    @Transactional
    public Emitente update(Emitente emitenteComNovosDados, Long id) {
        Optional<Emitente> emitenteNoBanco = emitenteRepository.findById(id);

        if (emitenteNoBanco.isPresent()) {
            Emitente emitenteExistente = emitenteNoBanco.get();

            if (!emitenteExistente.getCnpj().equals(emitenteComNovosDados.getCnpj()) &&
                    emitenteRepository.existsByCnpj(emitenteComNovosDados.getCnpj())) {

                throw new RuntimeException("Não é possível alterar o CNPJ. Este novo CNPJ já pertence a outra empresa.");

            } else {

                emitenteExistente.setCnpj(emitenteComNovosDados.getCnpj());
                emitenteExistente.setRazaoSocial(emitenteComNovosDados.getRazaoSocial());
                emitenteExistente.setInscricaoEstadual(emitenteComNovosDados.getInscricaoEstadual());
                emitenteExistente.setLogradouro(emitenteComNovosDados.getLogradouro());
                emitenteExistente.setNumero(emitenteComNovosDados.getNumero());
                emitenteExistente.setBairro(emitenteComNovosDados.getBairro());
                emitenteExistente.setCep(emitenteComNovosDados.getCep());

                return emitenteRepository.save(emitenteExistente);
            }
        } else {
            throw new RuntimeException("Emitente não encontrado para atualização");
        }
    }

    @Transactional
    public void delete(Long id){
        if(emitenteRepository.existsById(id)){
            emitenteRepository.deleteById(id);
        }else {
            throw new RuntimeException("Emitente nao encontrado");
        }
    }






}
