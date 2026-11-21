package com.notafiscalapi.service;

import com.notafiscalapi.dto.ItemNotaFiscalRequestDto;
import com.notafiscalapi.dto.NotaFiscalRequestDto;
import com.notafiscalapi.entity.Destinatario;
import com.notafiscalapi.entity.Emitente;
import com.notafiscalapi.entity.ItemNotaFiscal;
import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.entity.Produto;
import com.notafiscalapi.enums.StatusNotaFiscal;
import com.notafiscalapi.exception.BusinessRuleException;
import com.notafiscalapi.exception.DuplicateResourceException;
import com.notafiscalapi.exception.ResourceNotFoundException;
import com.notafiscalapi.integration.SefazClient;
import com.notafiscalapi.mapper.NotaFiscalMapper;
import com.notafiscalapi.repository.NotaFiscalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final EmitenteService emitenteService;
    private final DestinatarioService destinatarioService;
    private final ProdutoService produtoService;
    private final XmlNfeGeneratorService xmlNfeGeneratorService;
    private final SefazClient sefazClient;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository,
                             EmitenteService emitenteService,
                             DestinatarioService destinatarioService,
                             ProdutoService produtoService,
                             XmlNfeGeneratorService xmlNfeGeneratorService,
                             SefazClient sefazClient) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.emitenteService = emitenteService;
        this.destinatarioService = destinatarioService;
        this.produtoService = produtoService;
        this.xmlNfeGeneratorService = xmlNfeGeneratorService;
        this.sefazClient = sefazClient;
    }

    @Transactional
    public NotaFiscal emitir(NotaFiscalRequestDto dto) {
        if (notaFiscalRepository.existsByNumeroNotaFiscalAndNumeroDeSerie(dto.getNumeroNotaFiscal(), dto.getNumeroDeSerie())) {
            throw new DuplicateResourceException("Nota Fiscal já cadastrada com este número e série");
        }

        Emitente emitente = emitenteService.findById(dto.getEmitenteId());
        Destinatario destinatario = destinatarioService.findById(dto.getDestinatarioId());

        NotaFiscal notaFiscal = NotaFiscalMapper.toEntity(dto);
        notaFiscal.setEmitente(emitente);
        notaFiscal.setDestinatario(destinatario);
        // Status provisório; o definitivo vem do retorno da SEFAZ abaixo.
        notaFiscal.setStatus(StatusNotaFiscal.FALHA_ENVIO);
        notaFiscal.setDataEmissao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));

        BigDecimal totalProdutos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalIcms = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (ItemNotaFiscalRequestDto itemDto : dto.getItems()) {
            Produto produto = produtoService.findById(itemDto.getProdutoId());

            ItemNotaFiscal item = new ItemNotaFiscal();
            item.setNotaFiscal(notaFiscal);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setValorUnitario(produto.getValorUnitario().setScale(2, RoundingMode.HALF_UP));
            item.setAliquotaIcms(produto.getAliquotaIcms());

            BigDecimal valorTotalItem = produto.getValorUnitario()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantidade()))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setValorTotal(valorTotalItem);

            BigDecimal valorIcmsItem = valorTotalItem
                    .multiply(produto.getAliquotaIcms())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            item.setValorIcms(valorIcmsItem);

            totalProdutos = totalProdutos.add(valorTotalItem);
            totalIcms = totalIcms.add(valorIcmsItem);

            notaFiscal.getItens().add(item);
        }

        notaFiscal.setValorTotalProdutos(totalProdutos);
        notaFiscal.setValorTotalIcms(totalIcms);
        notaFiscal.setValorTotalNotaFiscal(totalProdutos);

        NotaFiscal notaSalva = notaFiscalRepository.save(notaFiscal);
        transmitirParaSefaz(notaSalva);
        return notaFiscalRepository.save(notaSalva);
    }

    private void transmitirParaSefaz(NotaFiscal notaFiscal) {
        String xml = xmlNfeGeneratorService.generateXmlNfe(notaFiscal);
        SefazClient.TransmissaoSefaz transmissao = sefazClient.enviarNfe(xml, notaFiscal.getId());
        SefazClient.RetornoSefaz retorno = transmissao.retorno();

        notaFiscal.setUrlSefaz(transmissao.url());
        notaFiscal.setDataEnvioSefaz(transmissao.dataEnvio());
        notaFiscal.setTempoRespostaMs(transmissao.tempoRespostaMs());
        notaFiscal.setHttpStatusSefaz(transmissao.httpStatus());
        notaFiscal.setRequisicaoSefaz(transmissao.requisicao());
        notaFiscal.setRespostaSefaz(transmissao.resposta());

        if (retorno.autorizada()) {
            notaFiscal.setStatus(StatusNotaFiscal.EMITIDA);
            notaFiscal.setProtocoloAutorizacao(retorno.protocolo());
        } else if (retorno.falhaEnvio()) {
            notaFiscal.setStatus(StatusNotaFiscal.FALHA_ENVIO);
        } else {
            notaFiscal.setStatus(StatusNotaFiscal.REJEITADA);
        }
        notaFiscal.setMensagemSefaz(retorno.mensagem());
    }

    public List<NotaFiscal> findAll() {
        return notaFiscalRepository.findAll();
    }

    public NotaFiscal findById(Long id) {
        return notaFiscalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota Fiscal não encontrada com ID: " + id));
    }

    @Transactional
    public NotaFiscal cancelar(Long id) {
        NotaFiscal notaFiscal = findById(id);
        if (StatusNotaFiscal.CANCELADA.equals(notaFiscal.getStatus())) {
            throw new BusinessRuleException("A Nota Fiscal ID " + id + " já se encontra cancelada");
        }
        if (!StatusNotaFiscal.EMITIDA.equals(notaFiscal.getStatus())) {
            throw new BusinessRuleException("Só é possível cancelar notas autorizadas pela SEFAZ");
        }
        notaFiscal.setStatus(StatusNotaFiscal.CANCELADA);
        return notaFiscalRepository.save(notaFiscal);
    }

    @Transactional
    public void delete(Long id) {
        if (!notaFiscalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota Fiscal não encontrada com ID: " + id);
        }
        notaFiscalRepository.deleteById(id);
    }
}
