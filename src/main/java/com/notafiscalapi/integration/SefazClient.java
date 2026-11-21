package com.notafiscalapi.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transmite a NF-e para o webservice NFeAutorizacao4 da SEFAZ (padrão: SVRS, ambiente de homologação).
 *
 * A SEFAZ exige autenticação mútua (TLS com certificado digital ICP-Brasil A1).
 * Sem certificado configurado, o servidor da SEFAZ recusa o acesso (HTTP 403) e esse retorno é registrado na nota.
 * Para usar o certificado da empresa, basta configurar SEFAZ_CERTIFICADO_CAMINHO e SEFAZ_CERTIFICADO_SENHA.
 */
@Component
public class SefazClient {

    private static final String NS_WSDL = "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4";
    // Os servidores da SEFAZ usam certificados da cadeia ICP-Brasil, que não vem no truststore padrão do Java.
    private static final String[] RAIZES_ICP_BRASIL = {"certs/icp-brasil-raiz-v10.crt", "certs/icp-brasil-raiz-v5.crt"};

    @Value("${sefaz.autorizacao.url}")
    private String urlAutorizacao;

    @Value("${sefaz.certificado.caminho:}")
    private String caminhoCertificado;

    @Value("${sefaz.certificado.senha:}")
    private String senhaCertificado;

    public TransmissaoSefaz enviarNfe(String xmlNfe, long idLote) {
        String envelope = montarEnvelopeSoap(xmlNfe, idLote);
        LocalDateTime dataEnvio = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        long inicio = System.currentTimeMillis();

        try {
            HttpClient client = criarHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(urlAutorizacao))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/soap+xml; charset=utf-8; action=\"" + NS_WSDL + "/nfeAutorizacaoLote\"")
                    .POST(HttpRequest.BodyPublishers.ofString(envelope, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            long tempoMs = System.currentTimeMillis() - inicio;

            // Resposta bruta, exatamente como veio do servidor: linha de status, cabeçalhos e corpo.
            StringBuilder respostaBruta = new StringBuilder("HTTP " + response.statusCode() + "\n");
            response.headers().map().forEach((nome, valores) -> respostaBruta.append(nome).append(": ").append(String.join(", ", valores)).append("\n"));
            respostaBruta.append("\n").append(response.body());

            return new TransmissaoSefaz(urlAutorizacao, dataEnvio, tempoMs, response.statusCode(), envelope,
                    respostaBruta.toString(), interpretarResposta(response.statusCode(), response.body()));
        } catch (Exception e) {
            long tempoMs = System.currentTimeMillis() - inicio;
            String detalhe = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return new TransmissaoSefaz(urlAutorizacao, dataEnvio, tempoMs, null, envelope, e.getClass().getName() + ": " + detalhe,
                    RetornoSefaz.falhaEnvio("Falha de comunicação com a SEFAZ: " + detalhe));
        }
    }

    private HttpClient criarHttpClient() throws Exception {
        HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15));
        javax.net.ssl.KeyManager[] keyManagers = null;

        if (caminhoCertificado != null && !caminhoCertificado.isBlank() && Files.exists(Path.of(caminhoCertificado))) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream in = new FileInputStream(caminhoCertificado)) {
                keyStore.load(in, senhaCertificado.toCharArray());
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, senhaCertificado.toCharArray());
            keyManagers = kmf.getKeyManagers();
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, criarTrustManagers(), null);
        return builder.sslContext(sslContext).build();
    }

    private javax.net.ssl.TrustManager[] criarTrustManagers() throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        Path cacertsJava = Path.of(System.getProperty("java.home"), "lib", "security", "cacerts");
        try (InputStream in = Files.newInputStream(cacertsJava)) {
            trustStore.load(in, "changeit".toCharArray());
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        for (String recurso : RAIZES_ICP_BRASIL) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(recurso)) {
                if (in != null) {
                    trustStore.setCertificateEntry(recurso, (X509Certificate) cf.generateCertificate(in));
                }
            }
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }

    private String montarEnvelopeSoap(String xmlNfe, long idLote) {
        String nfeSemDeclaracao = xmlNfe.replaceFirst("^<\\?xml[^>]*\\?>\\s*", "");
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
                + "<soap12:Body>"
                + "<nfeDadosMsg xmlns=\"" + NS_WSDL + "\">"
                + "<enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">"
                + "<idLote>" + idLote + "</idLote>"
                + "<indSinc>1</indSinc>"
                + nfeSemDeclaracao
                + "</enviNFe>"
                + "</nfeDadosMsg>"
                + "</soap12:Body>"
                + "</soap12:Envelope>";
    }

    private RetornoSefaz interpretarResposta(int httpStatus, String corpo) {
        String cStat = extrairTag(corpo, "cStat");
        String xMotivo = extrairTag(corpo, "xMotivo");
        String nProt = extrairTag(corpo, "nProt");

        // Resposta fiscal da SEFAZ (XML com cStat). 100 = Autorizado o uso da NF-e.
        if (cStat != null) {
            boolean autorizada = "100".equals(cStat) && nProt != null;
            return new RetornoSefaz(autorizada, false, httpStatus, cStat,
                    "SEFAZ: " + cStat + " - " + (xMotivo != null ? xMotivo : "sem motivo informado"),
                    autorizada ? nProt : null);
        }

        // Recusa no nível HTTP, antes de a SEFAZ processar o XML (ex.: 403 sem certificado digital).
        String mensagem = "SEFAZ recusou o acesso: HTTP " + httpStatus;
        if (httpStatus == 403) {
            mensagem += " - Forbidden: Access is denied. Certificado digital (ICP-Brasil A1) não apresentado ou não aceito";
        } else {
            String titulo = extrairTag(corpo, "title");
            if (titulo != null) mensagem += " - " + titulo;
        }
        return new RetornoSefaz(false, false, httpStatus, null, mensagem, null);
    }

    private String extrairTag(String xml, String tag) {
        if (xml == null) return null;
        Matcher m = Pattern.compile("<(?:\\w+:)?" + tag + "[^>]*>([^<]*)</(?:\\w+:)?" + tag + ">").matcher(xml);
        return m.find() ? m.group(1).trim() : null;
    }

    /** Registro do que foi enviado e do que voltou da SEFAZ (log da transmissão). */
    public record TransmissaoSefaz(String url, LocalDateTime dataEnvio, long tempoRespostaMs, Integer httpStatus,
                                   String requisicao, String resposta, RetornoSefaz retorno) {
    }

    public record RetornoSefaz(boolean autorizada, boolean falhaEnvio, Integer httpStatus,
                               String cStat, String mensagem, String protocolo) {
        static RetornoSefaz falhaEnvio(String mensagem) {
            return new RetornoSefaz(false, true, null, null, mensagem, null);
        }
    }
}
