import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { notaFiscalService } from '../services/notaFiscalService';
import { NotaFiscal } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { 
  FileText, 
  DollarSign, 
  CheckCircle2, 
  XCircle, 
  PlusCircle, 
  ArrowUpRight,
  Download,
  Eye
} from 'lucide-react';

const Dashboard: React.FC = () => {
  const [notas, setNotas] = useState<NotaFiscal[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarNotas();
  }, []);

  const carregarNotas = async () => {
    try {
      setLoading(true);
      const data = await notaFiscalService.getAll();
      setNotas(data);
    } catch (error) {
      console.error('Erro ao buscar notas fiscais:', error);
    } finally {
      setLoading(false);
    }
  };

  const totalNotas = notas.length;
  const emitidas = notas.filter(n => n.status === 'EMITIDA');
  const canceladas = notas.filter(n => n.status === 'CANCELADA');
  const faturamentoTotal = emitidas.reduce((acc, curr) => acc + (curr.valorTotalNotaFiscal || 0), 0);

  return (
    <div className="space-y-6">
      {/* Banner de Boas-Vindas & Ações Rápidas */}
      <div className="bg-gradient-to-r from-blue-700 to-blue-600 rounded-xl p-6 text-white shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold">Painel de Controle Fiscal</h2>
          <p className="text-blue-100 text-sm mt-1">
            Visão consolidada das suas notas fiscais eletrônicas, faturamento e integrações.
          </p>
        </div>
        <Link
          to="/emitir"
          className="inline-flex items-center gap-2 bg-white text-blue-700 font-semibold px-4 py-2.5 rounded-lg text-sm hover:bg-blue-50 transition-colors shadow-sm"
        >
          <PlusCircle className="w-4 h-4" />
          Emitir Nova Nota
        </Link>
      </div>

      {/* Cards de Métricas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center font-bold">
            <FileText className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">Total de Notas</p>
            <p className="text-2xl font-bold text-slate-800">{totalNotas}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center font-bold">
            <DollarSign className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">Faturamento Emitido</p>
            <p className="text-2xl font-bold text-slate-800">{formatCurrency(faturamentoTotal)}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center font-bold">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">Notas Emitidas</p>
            <p className="text-2xl font-bold text-slate-800">{emitidas.length}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center font-bold">
            <XCircle className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">Notas Canceladas</p>
            <p className="text-2xl font-bold text-slate-800">{canceladas.length}</p>
          </div>
        </div>
      </div>

      {/* Tabela das Últimas Notas Emitidas */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-slate-800 text-base">Últimas Notas Fiscais</h3>
            <p className="text-xs text-slate-500 mt-0.5">Histórico recente de emissões cadastradas no sistema</p>
          </div>
          <Link
            to="/notas"
            className="text-sm font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
          >
            Ver todas <ArrowUpRight className="w-4 h-4" />
          </Link>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Carregando dados...</div>
        ) : notas.length === 0 ? (
          <div className="p-10 text-center text-slate-400">
            <FileText className="w-10 h-10 mx-auto text-slate-300 mb-2" />
            <p className="text-sm font-medium">Nenhuma nota fiscal emitida ainda.</p>
            <Link to="/emitir" className="text-sm text-blue-600 hover:underline mt-1 inline-block">
              Emitir a primeira nota fiscal agora
            </Link>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-500 border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Número / Série</th>
                  <th className="py-3 px-4">Data Emissão</th>
                  <th className="py-3 px-4">Destinatário</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Valor Total</th>
                  <th className="py-3 px-4 text-center">Ações Rápidas</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {notas.slice(0, 5).map((nota) => (
                  <tr key={nota.id} className="hover:bg-slate-50 transition-colors">
                    <td className="py-3.5 px-4 font-semibold text-slate-800">
                      NF {nota.numeroNotaFiscal} <span className="text-xs font-normal text-slate-400">s.{nota.numeroDeSerie}</span>
                    </td>
                    <td className="py-3.5 px-4">{formatDate(nota.dataEmissao)}</td>
                    <td className="py-3.5 px-4">{nota.destinatario ? nota.destinatario.nome : '-'}</td>
                    <td className="py-3.5 px-4">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          nota.status === 'EMITIDA'
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                            : 'bg-rose-50 text-rose-700 border border-rose-200'
                        }`}
                      >
                        {nota.status}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right font-semibold text-slate-800">
                      {formatCurrency(nota.valorTotalNotaFiscal)}
                    </td>
                    <td className="py-3.5 px-4 text-center">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => notaFiscalService.visualizarPdf(nota.id)}
                          title="Visualizar DANFE PDF"
                          className="p-1.5 rounded-md hover:bg-blue-50 text-slate-600 hover:text-blue-600 transition-colors"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => notaFiscalService.downloadPdf(nota.id, nota.numeroNotaFiscal)}
                          title="Baixar PDF"
                          className="p-1.5 rounded-md hover:bg-blue-50 text-slate-600 hover:text-blue-600 transition-colors"
                        >
                          <Download className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
