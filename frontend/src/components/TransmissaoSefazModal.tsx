import React from 'react';
import { createPortal } from 'react-dom';
import { CheckCircle2, XCircle, AlertTriangle, Eye } from 'lucide-react';
import { NotaFiscal } from '../types';
import { notaFiscalService } from '../services/notaFiscalService';

interface Props {
  nota: NotaFiscal;
  onClose: () => void;
}

// Resultado da transmissão para a SEFAZ: mensagem amigável + log do que foi enviado e do que voltou.
const TransmissaoSefazModal: React.FC<Props> = ({ nota, onClose }) => {
  const resultado = {
    EMITIDA: { Icone: CheckCircle2, cor: 'text-emerald-600', titulo: 'Nota autorizada pela SEFAZ' },
    REJEITADA: { Icone: XCircle, cor: 'text-amber-600', titulo: 'A SEFAZ recusou a nota' },
    FALHA_ENVIO: { Icone: AlertTriangle, cor: 'text-rose-600', titulo: 'Não foi possível comunicar com a SEFAZ' },
    CANCELADA: { Icone: XCircle, cor: 'text-rose-600', titulo: 'Nota cancelada' },
  }[nota.status];

  const dados: [string, string][] = [
    ['Nota / Série', `${nota.numeroNotaFiscal} / ${nota.numeroDeSerie}`],
    ['Webservice', nota.urlSefaz ?? '-'],
    ['Enviado em', nota.dataEnvioSefaz ? new Date(nota.dataEnvioSefaz).toLocaleString('pt-BR') : '-'],
    ['Tempo de resposta', nota.tempoRespostaMs != null ? `${nota.tempoRespostaMs} ms` : '-'],
    ['Código HTTP', nota.httpStatusSefaz != null ? String(nota.httpStatusSefaz) : 'sem resposta'],
    ['Protocolo', nota.protocoloAutorizacao ?? 'NÃO AUTORIZADA'],
  ];

  return createPortal(
    <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl shadow-xl border border-slate-200 max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6 space-y-4">
        <div className="flex items-start gap-3">
          <resultado.Icone className={`w-8 h-8 shrink-0 ${resultado.cor}`} />
          <div>
            <h3 className="text-base font-bold text-slate-800">{resultado.titulo}</h3>
            {nota.status === 'REJEITADA' && (
              <p className="text-sm text-slate-600 mt-1">
                A nota foi gerada e transmitida, mas não foi autorizada. Ela continua disponível em PDF, sem valor fiscal.
              </p>
            )}
            {nota.mensagemSefaz && <p className="text-sm font-medium text-slate-800 mt-2">Motivo: {nota.mensagemSefaz}</p>}
          </div>
        </div>

        <div>
          <h4 className="text-xs font-bold text-slate-500 uppercase mb-2">Log da transmissão</h4>
          <dl className="grid grid-cols-[140px_1fr] gap-x-3 gap-y-1 text-xs">
            {dados.map(([rotulo, valor]) => (
              <React.Fragment key={rotulo}>
                <dt className="font-semibold text-slate-500">{rotulo}</dt>
                <dd className="text-slate-800 break-all">{valor}</dd>
              </React.Fragment>
            ))}
          </dl>
        </div>

        <details className="text-xs">
          <summary className="cursor-pointer font-semibold text-slate-700">Requisição enviada à SEFAZ (SOAP)</summary>
          <pre className="mt-2 p-3 bg-slate-900 text-slate-100 rounded-lg overflow-x-auto whitespace-pre-wrap break-all max-h-60">{nota.requisicaoSefaz}</pre>
        </details>
        <details className="text-xs">
          <summary className="cursor-pointer font-semibold text-slate-700">Resposta recebida da SEFAZ</summary>
          <pre className="mt-2 p-3 bg-slate-900 text-slate-100 rounded-lg overflow-x-auto whitespace-pre-wrap break-all max-h-60">{nota.respostaSefaz}</pre>
        </details>

        <div className="flex justify-end gap-2 pt-2">
          <button
            type="button"
            onClick={() => notaFiscalService.visualizarPdf(nota.id)}
            className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg border border-slate-200 text-sm font-medium text-slate-700 hover:bg-slate-50"
          >
            <Eye className="w-4 h-4" /> Ver DANFE (PDF)
          </button>
          <button type="button" onClick={onClose} className="px-4 py-2 rounded-lg bg-blue-600 text-white text-sm font-medium hover:bg-blue-700">
            Fechar
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
};

export default TransmissaoSefazModal;
