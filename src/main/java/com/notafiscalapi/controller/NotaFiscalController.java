package com.notafiscalapi.controller;

import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalResponseDto;
import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.mapper.NotaFiscalMapper;
import com.notafiscalapi.service.DanfePdfGeneratorService;
import com.notafiscalapi.service.NotaFiscalService;
import com.notafiscalapi.service.XmlNfeGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;
    private final XmlNfeGeneratorService xmlNfeGeneratorService;
    private final DanfePdfGeneratorService danfePdfGeneratorService;

    public NotaFiscalController(NotaFiscalService notaFiscalService,
                                XmlNfeGeneratorService xmlNfeGeneratorService,
                                DanfePdfGeneratorService danfePdfGeneratorService) {
        this.notaFiscalService = notaFiscalService;
        this.xmlNfeGeneratorService = xmlNfeGeneratorService;
        this.danfePdfGeneratorService = danfePdfGeneratorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaFiscalResponseDto createNotaFiscal(@Valid @RequestBody NotaFiscalRequestDto notaFiscalRequestDto) {
        NotaFiscal notaSalva = notaFiscalService.emitir(notaFiscalRequestDto);
        return NotaFiscalMapper.toDto(notaSalva);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotaFiscalResponseDto> findAll() {
        List<NotaFiscal> notasFiscal = notaFiscalService.findAll();
        List<NotaFiscalResponseDto> notasFiscalResponseDto = new ArrayList<>();
        for (NotaFiscal notaFiscal : notasFiscal) {
            notasFiscalResponseDto.add(NotaFiscalMapper.toDto(notaFiscal));
        }
        return notasFiscalResponseDto;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotaFiscalResponseDto findById(@PathVariable Long id) {
        return NotaFiscalMapper.toDto(notaFiscalService.findById(id));
    }

    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String getXmlNfe(@PathVariable Long id) {
        NotaFiscal notaFiscal = notaFiscalService.findById(id);
        return xmlNfeGeneratorService.generateXmlNfe(notaFiscal);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getDanfePdf(@PathVariable Long id) {
        NotaFiscal notaFiscal = notaFiscalService.findById(id);
        return danfePdfGeneratorService.gerarDanfePdf(notaFiscal);
    }

    @RequestMapping(value = "/{id}/cancelar", method = {RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    public NotaFiscalResponseDto cancelarNotaFiscal(@PathVariable Long id) {
        NotaFiscal notaFiscalCancelada = notaFiscalService.cancelar(id);
        return NotaFiscalMapper.toDto(notaFiscalCancelada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotaFiscal(@PathVariable Long id) {
        notaFiscalService.delete(id);
    }
}
