package com.notafiscalapi.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.notafiscalapi.entity.ItemNotaFiscal;
import com.notafiscalapi.entity.NotaFiscal;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.time.format.DateTimeFormatter;

@Service
public class DanfePdfGeneratorService {

    public byte[] gerarDanfePdf(NotaFiscal notaFiscal) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font fontTextoNegrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font fontChave = FontFactory.getFont(FontFactory.COURIER_BOLD, 9, Color.DARK_GRAY);

            PdfPTable tableHeader = new PdfPTable(2);
            tableHeader.setWidthPercentage(100);
            tableHeader.setWidths(new float[]{4, 6});

            PdfPCell cellTitle = new PdfPCell();
            cellTitle.addElement(new Paragraph("DANFE", fontTitulo));
            cellTitle.addElement(new Paragraph("Documento Auxiliar da Nota Fiscal Eletrônica", fontTexto));
            cellTitle.addElement(new Paragraph("SÉRIE: " + notaFiscal.getNumeroDeSerie() + " | NÚMERO: " + notaFiscal.getNumeroNotaFiscal(), fontTextoNegrito));
            cellTitle.setPadding(8);
            tableHeader.addCell(cellTitle);

            PdfPCell cellChave = new PdfPCell();
            cellChave.addElement(new Paragraph("CHAVE DE ACESSO DA NF-e", fontSubtitulo));
            String chave = gerarChaveAcessoFicticia(notaFiscal);
            cellChave.addElement(new Paragraph(chave, fontChave));
            cellChave.addElement(new Paragraph("\nConsulta de autenticidade no portal nacional da NF-e", fontTexto));
            cellChave.setPadding(8);
            tableHeader.addCell(cellChave);

            document.add(tableHeader);
            document.add(new Paragraph(" "));

            PdfPTable tableParticipantes = new PdfPTable(2);
            tableParticipantes.setWidthPercentage(100);

            PdfPCell cellEmit = new PdfPCell();
            cellEmit.addElement(new Paragraph("EMITENTE", fontSubtitulo));
            if (notaFiscal.getEmitente() != null) {
                cellEmit.addElement(new Paragraph(notaFiscal.getEmitente().getRazaoSocial(), fontTextoNegrito));
                cellEmit.addElement(new Paragraph("CNPJ: " + notaFiscal.getEmitente().getCnpj() + " | IE: " + notaFiscal.getEmitente().getInscricaoEstadual(), fontTexto));
                cellEmit.addElement(new Paragraph(notaFiscal.getEmitente().getLogradouro() + ", " + notaFiscal.getEmitente().getNumero() + " - " + notaFiscal.getEmitente().getBairro(), fontTexto));
                cellEmit.addElement(new Paragraph("CEP: " + notaFiscal.getEmitente().getCep(), fontTexto));
            }
            cellEmit.setPadding(6);
            tableParticipantes.addCell(cellEmit);

            PdfPCell cellDest = new PdfPCell();
            cellDest.addElement(new Paragraph("DESTINATÁRIO / REMETENTE", fontSubtitulo));
            if (notaFiscal.getDestinatario() != null) {
                cellDest.addElement(new Paragraph(notaFiscal.getDestinatario().getNome(), fontTextoNegrito));
                cellDest.addElement(new Paragraph("CPF/CNPJ: " + notaFiscal.getDestinatario().getDocumento(), fontTexto));
                cellDest.addElement(new Paragraph("E-mail: " + notaFiscal.getDestinatario().getEmail(), fontTexto));
                cellDest.addElement(new Paragraph(notaFiscal.getDestinatario().getLogradouro() + ", " + notaFiscal.getDestinatario().getNumero() + " - CEP: " + notaFiscal.getDestinatario().getCep(), fontTexto));
            }
            cellDest.setPadding(6);
            tableParticipantes.addCell(cellDest);

            document.add(tableParticipantes);
            document.add(new Paragraph(" "));

            PdfPTable tableEmissao = new PdfPTable(3);
            tableEmissao.setWidthPercentage(100);

            PdfPCell cellData = new PdfPCell(new Phrase("DATA DE EMISSÃO: " + (notaFiscal.getDataEmissao() != null ? notaFiscal.getDataEmissao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "-"), fontTextoNegrito));
            cellData.setPadding(5);
            tableEmissao.addCell(cellData);

            PdfPCell cellStatus = new PdfPCell(new Phrase("STATUS DA NOTA: " + notaFiscal.getStatus(), fontTextoNegrito));
            cellStatus.setPadding(5);
            tableEmissao.addCell(cellStatus);

            PdfPCell cellNat = new PdfPCell(new Phrase("NATUREZA DA OPERAÇÃO: VENDA", fontTexto));
            cellNat.setPadding(5);
            tableEmissao.addCell(cellNat);

            document.add(tableEmissao);
            document.add(new Paragraph(" "));

            Paragraph pItensHeader = new Paragraph("DADOS DOS PRODUTOS / SERVIÇOS", fontSubtitulo);
            pItensHeader.setSpacingAfter(5);
            document.add(pItensHeader);

            PdfPTable tableItens = new PdfPTable(7);
            tableItens.setWidthPercentage(100);
            tableItens.setWidths(new float[]{1, 4, 2, 1.5f, 2, 2, 2});

            String[] headers = {"CÓD", "DESCRIÇÃO DO PRODUTO", "NCM", "QTD", "V. UNIT", "V. TOTAL", "V. ICMS"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontTextoNegrito));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tableItens.addCell(cell);
            }

            if (notaFiscal.getItens() != null) {
                for (ItemNotaFiscal item : notaFiscal.getItens()) {
                    tableItens.addCell(createCell(item.getProduto() != null ? item.getProduto().getId().toString() : "-", fontTexto, Element.ALIGN_CENTER));
                    tableItens.addCell(createCell(item.getProduto() != null ? item.getProduto().getDescricao() : "-", fontTexto, Element.ALIGN_LEFT));
                    tableItens.addCell(createCell(item.getProduto() != null ? item.getProduto().getCodigoNcm() : "-", fontTexto, Element.ALIGN_CENTER));
                    tableItens.addCell(createCell(String.valueOf(item.getQuantidade()), fontTexto, Element.ALIGN_CENTER));
                    tableItens.addCell(createCell("R$ " + item.getValorUnitario(), fontTexto, Element.ALIGN_RIGHT));
                    tableItens.addCell(createCell("R$ " + item.getValorTotal(), fontTexto, Element.ALIGN_RIGHT));
                    tableItens.addCell(createCell("R$ " + item.getValorIcms(), fontTexto, Element.ALIGN_RIGHT));
                }
            }

            document.add(tableItens);
            document.add(new Paragraph(" "));

            Paragraph pTotaisHeader = new Paragraph("CÁLCULO DO IMPOSTO E TOTAIS DA NOTA", fontSubtitulo);
            pTotaisHeader.setSpacingAfter(5);
            document.add(pTotaisHeader);

            PdfPTable tableTotais = new PdfPTable(3);
            tableTotais.setWidthPercentage(100);

            PdfPCell cellTotProd = new PdfPCell(new Phrase("VALOR TOTAL DOS PRODUTOS:\nR$ " + notaFiscal.getValorTotalProdutos(), fontTextoNegrito));
            cellTotProd.setPadding(8);
            tableTotais.addCell(cellTotProd);

            PdfPCell cellTotIcms = new PdfPCell(new Phrase("VALOR TOTAL DO ICMS:\nR$ " + notaFiscal.getValorTotalIcms(), fontTextoNegrito));
            cellTotIcms.setPadding(8);
            tableTotais.addCell(cellTotIcms);

            PdfPCell cellTotNota = new PdfPCell(new Phrase("VALOR TOTAL DA NOTA FISCAL:\nR$ " + notaFiscal.getValorTotalNotaFiscal(), fontTitulo));
            cellTotNota.setBackgroundColor(new Color(230, 240, 255));
            cellTotNota.setPadding(8);
            tableTotais.addCell(cellTotNota);

            document.add(tableTotais);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o PDF do DANFE: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        return cell;
    }

    private String gerarChaveAcessoFicticia(NotaFiscal notaFiscal) {
        String cnpj = (notaFiscal.getEmitente() != null && notaFiscal.getEmitente().getCnpj() != null)
                ? notaFiscal.getEmitente().getCnpj()
                : "00000000000000";
        String sufixo = String.format("%09d", notaFiscal.getId() != null ? notaFiscal.getId() : 1);
        return "3526 08" + cnpj.substring(0, Math.min(8, cnpj.length())) + " 55 001 " + sufixo + " 1000 0000 1";
    }
}
