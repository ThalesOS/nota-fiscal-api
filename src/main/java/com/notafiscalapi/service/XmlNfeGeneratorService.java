package com.notafiscalapi.service;

import com.notafiscalapi.entity.ItemNotaFiscal;
import com.notafiscalapi.entity.NotaFiscal;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class XmlNfeGeneratorService {

    public String generateXmlNfe(NotaFiscal notaFiscal) {
        StringBuilder xml = new StringBuilder();

        String chaveAcesso = gerarChaveAcesso(notaFiscal);

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">\n");
        xml.append("  <infNFe Id=\"NFe").append(chaveAcesso).append("\" versao=\"4.00\">\n");
        xml.append("    <ide>\n");
        xml.append("      <cUF>35</cUF>\n");
        xml.append("      <cNF>").append(String.format("%08d", notaFiscal.getId())).append("</cNF>\n");
        xml.append("      <natOp>VENDA DE MERCADORIA</natOp>\n");
        xml.append("      <mod>55</mod>\n");
        xml.append("      <serie>").append(notaFiscal.getNumeroDeSerie()).append("</serie>\n");
        xml.append("      <nNF>").append(notaFiscal.getNumeroNotaFiscal()).append("</nNF>\n");
        xml.append("      <dhEmi>").append(notaFiscal.getDataEmissao() != null ? notaFiscal.getDataEmissao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "").append("</dhEmi>\n");
        xml.append("      <tpNF>1</tpNF>\n");
        xml.append("      <idDest>1</idDest>\n");
        xml.append("      <cMunFG>3550308</cMunFG>\n");
        xml.append("      <tpImp>1</tpImp>\n");
        xml.append("      <tpEmis>1</tpEmis>\n");
        xml.append("      <tpAmb>2</tpAmb>\n");
        xml.append("      <finNFe>1</finNFe>\n");
        xml.append("      <indFinal>1</indFinal>\n");
        xml.append("      <indPres>1</indPres>\n");
        xml.append("    </ide>\n");

        if (notaFiscal.getEmitente() != null) {
            xml.append("    <emit>\n");
            xml.append("      <CNPJ>").append(notaFiscal.getEmitente().getCnpj()).append("</CNPJ>\n");
            xml.append("      <xNome>").append(notaFiscal.getEmitente().getRazaoSocial()).append("</xNome>\n");
            xml.append("      <IE>").append(notaFiscal.getEmitente().getInscricaoEstadual()).append("</IE>\n");
            xml.append("      <enderEmit>\n");
            xml.append("        <xLgr>").append(notaFiscal.getEmitente().getLogradouro()).append("</xLgr>\n");
            xml.append("        <nro>").append(notaFiscal.getEmitente().getNumero()).append("</nro>\n");
            xml.append("        <xBairro>").append(notaFiscal.getEmitente().getBairro()).append("</xBairro>\n");
            xml.append("        <CEP>").append(notaFiscal.getEmitente().getCep()).append("</CEP>\n");
            xml.append("      </enderEmit>\n");
            xml.append("    </emit>\n");
        }


        if (notaFiscal.getDestinatario() != null) {
            xml.append("    <dest>\n");
            String doc = notaFiscal.getDestinatario().getDocumento();
            if (doc != null && doc.length() > 11) {
                xml.append("      <CNPJ>").append(doc).append("</CNPJ>\n");
            } else {
                xml.append("      <CPF>").append(doc != null ? doc : "").append("</CPF>\n");
            }
            xml.append("      <xNome>").append(notaFiscal.getDestinatario().getNome()).append("</xNome>\n");
            xml.append("      <enderDest>\n");
            xml.append("        <xLgr>").append(notaFiscal.getDestinatario().getLogradouro()).append("</xLgr>\n");
            xml.append("        <nro>").append(notaFiscal.getDestinatario().getNumero()).append("</nro>\n");
            xml.append("        <CEP>").append(notaFiscal.getDestinatario().getCep()).append("</CEP>\n");
            xml.append("      </enderDest>\n");
            xml.append("    </dest>\n");
        }

        if (notaFiscal.getItens() != null) {
            int nItem = 1;
            for (ItemNotaFiscal item : notaFiscal.getItens()) {
                xml.append("    <det nItem=\"").append(nItem++).append("\">\n");
                xml.append("      <prod>\n");
                xml.append("        <cProd>").append(item.getProduto() != null ? item.getProduto().getId() : "").append("</cProd>\n");
                xml.append("        <xProd>").append(item.getProduto() != null ? item.getProduto().getDescricao() : "").append("</xProd>\n");
                xml.append("        <NCM>").append(item.getProduto() != null ? item.getProduto().getCodigoNcm() : "").append("</NCM>\n");
                xml.append("        <qCom>").append(item.getQuantidade()).append("</qCom>\n");
                xml.append("        <vUnCom>").append(item.getValorUnitario()).append("</vUnCom>\n");
                xml.append("        <vProd>").append(item.getValorTotal()).append("</vProd>\n");
                xml.append("      </prod>\n");
                xml.append("      <imposto>\n");
                xml.append("        <ICMS>\n");
                xml.append("          <pICMS>").append(item.getAliquotaIcms()).append("</pICMS>\n");
                xml.append("          <vICMS>").append(item.getValorIcms()).append("</vICMS>\n");
                xml.append("        </ICMS>\n");
                xml.append("      </imposto>\n");
                xml.append("    </det>\n");
            }
        }

        xml.append("    <total>\n");
        xml.append("      <ICMSTot>\n");
        xml.append("        <vProd>").append(notaFiscal.getValorTotalProdutos()).append("</vProd>\n");
        xml.append("        <vICMS>").append(notaFiscal.getValorTotalIcms()).append("</vICMS>\n");
        xml.append("        <vNF>").append(notaFiscal.getValorTotalNotaFiscal()).append("</vNF>\n");
        xml.append("      </ICMSTot>\n");
        xml.append("    </total>\n");

        xml.append("  </infNFe>\n");
        xml.append("</NFe>");

        return xml.toString();
    }

    private String gerarChaveAcesso(NotaFiscal notaFiscal) {
        String cnpj = (notaFiscal.getEmitente() != null && notaFiscal.getEmitente().getCnpj() != null)
                ? notaFiscal.getEmitente().getCnpj()
                : "00000000000000";
        String sufixo = String.format("%09d", notaFiscal.getId() != null ? notaFiscal.getId() : 1);
        return "352608" + cnpj + "55001" + sufixo + "100000001";
    }
}
