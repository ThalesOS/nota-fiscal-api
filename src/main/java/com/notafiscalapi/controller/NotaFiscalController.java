package com.notafiscalapi.controller;

import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalResponseDto;
import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.enums.StatusNotaFiscal;
import com.notafiscalapi.mapper.EmitenteMapper;
import com.notafiscalapi.mapper.NotaFiscalMapper;
import com.notafiscalapi.service.DestinatarioService;
import com.notafiscalapi.service.EmitenteService;
import com.notafiscalapi.service.NotaFiscalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;
    private final EmitenteService emitenteService;
    private final DestinatarioService destinatarioService;

    public NotaFiscalController(NotaFiscalService notaFiscalService, EmitenteService emitenteService, DestinatarioService destinatarioService) {
        this.notaFiscalService = notaFiscalService;
        this.emitenteService = emitenteService;
        this.destinatarioService = destinatarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaFiscalResponseDto createNotaFiscal(@Valid @RequestBody NotaFiscalRequestDto notaFiscalRequestDto){
        NotaFiscal notaFiscal = NotaFiscalMapper.toEntity(notaFiscalRequestDto);
        Emitente emitente = emitenteService.findById(notaFiscalRequestDto.getEmitenteId());
        Destinatario destinatario = destinatarioService.findById(notaFiscalRequestDto.getDestinatarioId());
        notaFiscal.setEmitente(emitente);
        notaFiscal.setDestinatario(destinatario);
        notaFiscal.setStatus(StatusNotaFiscal.EMITIDA);
        NotaFiscal notaSalva = notaFiscalService.create(notaFiscal);
        return NotaFiscalMapper.toDto(notaSalva);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotaFiscalResponseDto> findAll(){
        List<NotaFiscal> notasFiscal = notaFiscalService.findAll();
        List<NotaFiscalResponseDto> notasFiscalResponseDto = new ArrayList<>();
        for (NotaFiscal notaFiscal : notasFiscal) {
            NotaFiscalResponseDto dto = NotaFiscalMapper.toDto(notaFiscal);
            notasFiscalResponseDto.add(dto);

        }
        return notasFiscalResponseDto;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotaFiscalResponseDto findById(@PathVariable Long id){
        return NotaFiscalMapper.toDto(notaFiscalService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotaFiscalResponseDto updateNotaFiscal(@PathVariable Long id,@Valid @RequestBody NotaFiscalRequestDto notaFiscalRequestDto){
        NotaFiscal notaFiscal = NotaFiscalMapper.toEntity(notaFiscalRequestDto);
        Emitente emitente = emitenteService.findById(notaFiscalRequestDto.getEmitenteId());
        Destinatario destinatario = destinatarioService.findById(notaFiscalRequestDto.getDestinatarioId());
        notaFiscal.setEmitente(emitente);
        notaFiscal.setDestinatario(destinatario);
        NotaFiscal notaFiscal1 = notaFiscalService.update(id,notaFiscal);
        return NotaFiscalMapper.toDto(notaFiscal1);



    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotaFiscal(@PathVariable Long id){
        notaFiscalService.delete(id);
    }

}
