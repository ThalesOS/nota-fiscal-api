import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { notaFiscalService } from '../services/notaFiscalService';
import { NotaFiscal } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { 
  FileText, 
  PlusCircle, 
  Search, 
  FileCode, 
  Download, 
  Ban, 
  Eye,
  AlertCircle
} from 'lucide-react';

const NotasFiscais: React.FC = () => {
  const [notas, setNotas] = useState<NotaFiscal[]>([]);
  const [loading, setLoading] = useState(true);
  const [termoBusca, setTermoBusca] = useState('');
  const [filtroStatus, setFiltroStatus] = useState<'TODOS' | 'EMITIDA' | 'CANCELADA'>('TODOS');
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  useEffect(() => {
    carregarNotas();
  }, []);

  const carregarNotas = async () => {
    try {
      setLoading(true);
      setErro(null);
      const data = await notaFiscalService.getAll();
      setNotas(data);
    } catch (err: any) {
      setErro('Não foi possível carregar a lista de notas fiscais. Verifique se o servidor está ativo.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelarNota = async (id: number, numeroNota: string) => {
    const confirmar = window.confirm(`Tem certeza que deseja cancelar a Nota Fiscal ${numeroNota}?`);
    if (!confirmar) return;

    try {
      setErro(null);
      await notaFiscalService.cancelar(id);
      setSucesso(`Nota Fiscal ${numeroNota} cancelada com sucesso!`);
      setTimeout(() => setSucesso(null), 4000);
      carregarNotas();
    } catch (err: any) {
      console.error('Erro detalhado ao cancelar:', err);
      const msg = err.response?.data?.message || err.message || 'Erro ao cancelar nota fiscal.';
      setErro(msg);
      setTimeout(() => setErro(null), 6000);
    }
  };

  const notasFiltradas = notas.filter((n) => {
    const matchesBusca = 
      n.numeroNotaFiscal.toLowerCase().includes(termoBusca.toLowerCase()) ||
      (n.destinatario && n.destinatario.nome.toLowerCase().includes(termoBusca.toLowerCase())) ||
      (n.emitente && n.emitente.razaoSocial.toLowerCase().includes(termoBusca.toLowerCase()));

    const matchesStatus = filtroStatus === 'TODOS' || n.status === filtroStatus;

    return matchesBusca && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Topo com Título e Botão de Ação */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Gerenciamento de Notas Fiscais</h2>
          <p className="text-xs text-slate-500 mt-0.5">Consulte, baixe o XML e DANFE PDF ou cancele notas emitidas</p>
        </div>
        <Link
          to="/emitir"
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2.5 rounded-lg text-sm shadow-sm transition-colors"
        >
          <PlusCircle className="w-4 h-4" />
          Emitir Nova Nota
        </Link>
      </div>

      {/* Alertas de Notificação */}
      {sucesso && (
        <div className="p-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
          {sucesso}
        </div>
      )}
      {erro && (
        <div className="p-4 rounded-lg bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-600" />
          {erro}
        </div>
      )}

      {/* Barra de Filtros e Busca */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-col md:flex-row gap-3 justify-between items-center">
        <div className="relative w-full md:w-96">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Buscar por número, destinatário ou emitente..."
            value={termoBusca}
            onChange={(e) => setTermoBusca(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all"
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto">
          <span className="text-xs font-medium text-slate-500">Status:</span>
          <div className="inline-flex rounded-lg border border-slate-200 bg-slate-50 p-1 text-xs">
            <button
              onClick={() => setFiltroStatus('TODOS')}
              className={`px-3 py-1 rounded-md font-medium transition-all ${
                filtroStatus === 'TODOS' ? 'bg-white text-blue-700 shadow-sm' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Todos
            </button>
            <button
              onClick={() => setFiltroStatus('EMITIDA')}
              className={`px-3 py-1 rounded-md font-medium transition-all ${
                filtroStatus === 'EMITIDA' ? 'bg-white text-emerald-700 shadow-sm' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Emitidas
            </button>
            <button
              onClick={() => setFiltroStatus('CANCELADA')}
              className={`px-3 py-1 rounded-md font-medium transition-all ${
                filtroStatus === 'CANCELADA' ? 'bg-white text-rose-700 shadow-sm' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Canceladas
            </button>
          </div>
        </div>
      </div>

      {/* Tabela de Notas */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-500 text-sm">Carregando notas fiscais...</div>
        ) : notasFiltradas.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <FileText className="w-12 h-12 mx-auto text-slate-300 mb-3" />
            <p className="text-base font-semibold text-slate-700">Nenhuma nota encontrada</p>
            <p className="text-xs text-slate-500 mt-1">Ajuste os filtros de pesquisa ou emita uma nova nota fiscal.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-500 border-b border-slate-200">
                <tr>
                  <th className="py-3.5 px-4">NF / Série</th>
                  <th className="py-3.5 px-4">Emissão</th>
                  <th className="py-3.5 px-4">Emitente</th>
                  <th className="py-3.5 px-4">Destinatário</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">ICMS (R$)</th>
                  <th className="py-3.5 px-4 text-right">Total (R$)</th>
                  <th className="py-3.5 px-4 text-center">Exportar & Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {notasFiltradas.map((nota) => (
                  <tr key={nota.id} className="hover:bg-slate-50 transition-colors">
                    <td className="py-4 px-4 font-bold text-slate-800">
                      #{nota.numeroNotaFiscal}
                      <span className="block text-xs font-normal text-slate-400">Série {nota.numeroDeSerie}</span>
                    </td>
                    <td className="py-4 px-4 text-xs font-medium text-slate-600">
                      {formatDate(nota.dataEmissao)}
                    </td>
                    <td className="py-4 px-4">
                      <span className="font-medium text-slate-800 block text-xs">
                        {nota.emitente ? nota.emitente.razaoSocial : '-'}
                      </span>
                      <span className="text-[11px] text-slate-400">
                        {nota.emitente ? `CNPJ: ${nota.emitente.cnpj}` : ''}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span className="font-medium text-slate-800 block text-xs">
                        {nota.destinatario ? nota.destinatario.nome : '-'}
                      </span>
                      <span className="text-[11px] text-slate-400">
                        {nota.destinatario ? `Doc: ${nota.destinatario.documento}` : ''}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span
                        className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${
                          nota.status === 'EMITIDA'
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                            : 'bg-rose-50 text-rose-700 border border-rose-200'
                        }`}
                      >
                        {nota.status}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-right text-xs text-slate-600 font-medium">
                      {formatCurrency(nota.valorTotalIcms)}
                    </td>
                    <td className="py-4 px-4 text-right font-bold text-slate-900">
                      {formatCurrency(nota.valorTotalNotaFiscal)}
                    </td>
                    <td className="py-4 px-4 text-center">
                      <div className="flex items-center justify-center gap-1.5">
                        {/* Visualizar DANFE PDF */}
                        <button
                          onClick={() => notaFiscalService.visualizarPdf(nota.id)}
                          title="Visualizar DANFE PDF"
                          className="p-1.5 rounded-lg border border-slate-200 hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200 text-slate-600 transition-colors"
                        >
                          <Eye className="w-4 h-4" />
                        </button>

                        {/* Baixar DANFE PDF */}
                        <button
                          onClick={() => notaFiscalService.downloadPdf(nota.id, nota.numeroNotaFiscal)}
                          title="Baixar DANFE (PDF)"
                          className="p-1.5 rounded-lg border border-slate-200 hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200 text-slate-600 transition-colors"
                        >
                          <Download className="w-4 h-4" />
                        </button>

                        {/* Baixar XML NF-e */}
                        <button
                          onClick={() => notaFiscalService.downloadXml(nota.id, nota.numeroNotaFiscal)}
                          title="Baixar XML NF-e 4.00"
                          className="p-1.5 rounded-lg border border-slate-200 hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200 text-slate-600 transition-colors"
                        >
                          <FileCode className="w-4 h-4" />
                        </button>

                        {/* Cancelar Nota (Apenas se estiver EMITIDA) */}
                        {nota.status === 'EMITIDA' && (
                          <button
                            onClick={() => handleCancelarNota(nota.id, nota.numeroNotaFiscal)}
                            title="Cancelar esta Nota Fiscal"
                            className="p-1.5 rounded-lg border border-rose-200 hover:bg-rose-50 text-rose-600 transition-colors"
                          >
                            <Ban className="w-4 h-4" />
                          </button>
                        )}
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

export default NotasFiscais;
