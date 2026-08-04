package com.notafiscalapi.controller;

import com.notafiscalapi.dto.ItemNotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalResponseDto;
import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.entity.ItemNotaFiscal;
import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.entity.Produto;
import com.notafiscalapi.enums.StatusNotaFiscal;
import com.notafiscalapi.mapper.NotaFiscalMapper;
import com.notafiscalapi.service.DanfePdfGeneratorService;
import com.notafiscalapi.service.DestinatarioService;
import com.notafiscalapi.service.EmitenteService;
import com.notafiscalapi.service.NotaFiscalService;
import com.notafiscalapi.service.ProdutoService;
import com.notafiscalapi.service.XmlNfeGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;
    private final EmitenteService emitenteService;
    private final DestinatarioService destinatarioService;
    private final ProdutoService produtoService;
    private final XmlNfeGeneratorService xmlNfeGeneratorService;
    private final DanfePdfGeneratorService danfePdfGeneratorService;

    public NotaFiscalController(NotaFiscalService notaFiscalService,
                                EmitenteService emitenteService,
                                DestinatarioService destinatarioService,
                                ProdutoService produtoService,
                                XmlNfeGeneratorService xmlNfeGeneratorService,
                                DanfePdfGeneratorService danfePdfGeneratorService) {
        this.notaFiscalService = notaFiscalService;
        this.emitenteService = emitenteService;
        this.destinatarioService = destinatarioService;
        this.produtoService = produtoService;
        this.xmlNfeGeneratorService = xmlNfeGeneratorService;
        this.danfePdfGeneratorService = danfePdfGeneratorService;
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

        BigDecimal totalProdutos = BigDecimal.ZERO;
        BigDecimal totalIcms = BigDecimal.ZERO;

        for (ItemNotaFiscalRequestDto itemDto : notaFiscalRequestDto.getItems()) {
            Produto produto = produtoService.findById(itemDto.getProdutoId());

            ItemNotaFiscal item = new ItemNotaFiscal();
            item.setNotaFiscal(notaFiscal);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setValorUnitario(produto.getValorUnitario());
            item.setAliquotaIcms(produto.getAliquotaIcms());

            BigDecimal valorTotalItem = produto.getValorUnitario().multiply(new BigDecimal(itemDto.getQuantidade()));
            item.setValorTotal(valorTotalItem);

            BigDecimal valorIcmsItem = valorTotalItem.multiply(produto.getAliquotaIcms()).divide(new BigDecimal(100));
            item.setValorIcms(valorIcmsItem);

            totalProdutos = totalProdutos.add(valorTotalItem);
            totalIcms = totalIcms.add(valorIcmsItem);

            notaFiscal.getItens().add(item);
        }

        notaFiscal.setValorTotalProdutos(totalProdutos);
        notaFiscal.setValorTotalIcms(totalIcms);
        notaFiscal.setValorTotalNotaFiscal(totalProdutos);

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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotaFiscalResponseDto updateNotaFiscal(@PathVariable Long id, @Valid @RequestBody NotaFiscalRequestDto notaFiscalRequestDto){
        NotaFiscal notaFiscal = NotaFiscalMapper.toEntity(notaFiscalRequestDto);
        Emitente emitente = emitenteService.findById(notaFiscalRequestDto.getEmitenteId());
        Destinatario destinatario = destinatarioService.findById(notaFiscalRequestDto.getDestinatarioId());
        notaFiscal.setEmitente(emitente);
        notaFiscal.setDestinatario(destinatario);
        NotaFiscal notaFiscal1 = notaFiscalService.update(id, notaFiscal);
        return NotaFiscalMapper.toDto(notaFiscal1);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotaFiscal(@PathVariable Long id){
        notaFiscalService.delete(id);
    }
}
