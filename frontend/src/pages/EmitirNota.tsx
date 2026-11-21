import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { emitenteService } from '../services/emitenteService';
import { destinatarioService } from '../services/destinatarioService';
import { produtoService } from '../services/produtoService';
import { notaFiscalService } from '../services/notaFiscalService';
import { Emitente, Destinatario, Produto, NotaFiscal, NotaFiscalRequest } from '../types';
import TransmissaoSefazModal from '../components/TransmissaoSefazModal';
import { formatCurrency } from '../utils/formatters';
import { getFormValidationError } from '../utils/formValidation';
import { 
  Plus, 
  Trash2, 
  FileCheck2, 
  Building2, 
  User, 
  Package, 
  AlertCircle
} from 'lucide-react';

interface ItemFormState {
  produtoId: number;
  quantidade: number;
}

const EmitirNota: React.FC = () => {
  const navigate = useNavigate();
  const [notaTransmitida, setNotaTransmitida] = useState<NotaFiscal | null>(null);

  const [emitentes, setEmitentes] = useState<Emitente[]>([]);
  const [destinatarios, setDestinatarios] = useState<Destinatario[]>([]);
  const [produtos, setProdutos] = useState<Produto[]>([]);

  const [numeroNotaFiscal, setNumeroNotaFiscal] = useState('');
  const [numeroDeSerie, setNumeroDeSerie] = useState('1');
  const [emitenteId, setEmitenteId] = useState<number | ''>('');
  const [destinatarioId, setDestinatarioId] = useState<number | ''>('');
  const [items, setItems] = useState<ItemFormState[]>([
    { produtoId: 0, quantidade: 1 }
  ]);

  const [loading, setLoading] = useState(false);
  const [carregandoDados, setCarregandoDados] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    carregarDependencias();
    // Gera um número sequencial aleatório para conveniência
    const numeroAleatorio = Math.floor(100000 + Math.random() * 900000).toString();
    setNumeroNotaFiscal(numeroAleatorio);
  }, []);

  const carregarDependencias = async () => {
    try {
      setCarregandoDados(true);
      const [emits, dests, prods] = await Promise.all([
        emitenteService.getAll(),
        destinatarioService.getAll(),
        produtoService.getAll(),
      ]);
      setEmitentes(emits);
      setDestinatarios(dests);
      setProdutos(prods);

      if (emits.length > 0) setEmitenteId(emits[0].id);
      if (dests.length > 0) setDestinatarioId(dests[0].id);
      if (prods.length > 0) setItems([{ produtoId: prods[0].id, quantidade: 1 }]);
    } catch (err) {
      setErro('Erro ao carregar dados de emitentes, clientes ou produtos.');
    } finally {
      setCarregandoDados(false);
    }
  };

  const handleAddItem = () => {
    const defaultProdId = produtos.length > 0 ? produtos[0].id : 0;
    setItems([...items, { produtoId: defaultProdId, quantidade: 1 }]);
  };

  const handleRemoveItem = (index: number) => {
    if (items.length === 1) {
      setErro('A nota fiscal deve conter ao menos um item.');
      return;
    }
    setItems(items.filter((_, i) => i !== index));
  };

  const handleItemChange = (index: number, field: keyof ItemFormState, value: number) => {
    const updated = [...items];
    updated[index] = { ...updated[index], [field]: value };
    setItems(updated);
  };

  // Cálculos dinâmicos em tempo de tela
  let subtotalCalculado = 0;
  let icmsCalculado = 0;

  items.forEach((item) => {
    const prod = produtos.find((p) => p.id === Number(item.produtoId));
    if (prod && item.quantidade > 0) {
      const valorItem = prod.valorUnitario * item.quantidade;
      const valorIcms = (valorItem * prod.aliquotaIcms) / 100;
      subtotalCalculado += valorItem;
      icmsCalculado += valorIcms;
    }
  });

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setErro(null);

    if (!numeroNotaFiscal.trim()) {
      setErro('O número da nota fiscal é obrigatório.');
      return;
    }
    if (!numeroDeSerie.trim()) {
      setErro('O número de série é obrigatório.');
      return;
    }
    if (!emitenteId) {
      setErro('Selecione uma empresa emitente.');
      return;
    }
    if (!destinatarioId) {
      setErro('Selecione um cliente destinatário.');
      return;
    }
    if (items.some((i) => !i.produtoId || i.quantidade <= 0)) {
      setErro('Verifique os produtos e garanta que todas as quantidades sejam maiores que zero.');
      return;
    }
    const validationError = getFormValidationError(e.currentTarget);
    if (validationError) {
      setErro(validationError);
      return;
    }

    const payload: NotaFiscalRequest = {
      numeroNotaFiscal,
      numeroDeSerie,
      emitenteId: Number(emitenteId),
      destinatarioId: Number(destinatarioId),
      items: items.map((i) => ({
        produtoId: Number(i.produtoId),
        quantidade: Number(i.quantidade),
      })),
    };

    try {
      setLoading(true);
      const nota = await notaFiscalService.emitir(payload);
      setNotaTransmitida(nota); // mostra o resultado da SEFAZ antes de voltar para a lista
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao emitir nota fiscal.';
      setErro(msg);
    } finally {
      setLoading(false);
    }
  };

  if (carregandoDados) {
    return <div className="p-12 text-center text-slate-500 text-sm">Carregando cadastros...</div>;
  }

  return (
    <form noValidate onSubmit={handleSubmit} className="max-w-5xl space-y-6">
      {notaTransmitida && <TransmissaoSefazModal nota={notaTransmitida} onClose={() => navigate('/notas')} />}
      {/* Topo */}
      <div>
        <h2 className="text-xl font-bold text-slate-800">Emissão de Nota Fiscal Eletrônica</h2>
        <p className="text-xs text-slate-500 mt-0.5">Preencha os dados e produtos para emissão imediata da NF-e</p>
      </div>

      {erro && (
        <div className="p-4 rounded-lg bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-600" />
          {erro}
        </div>
      )}

      {/* Bloco 1: Dados da Nota (Número e Série) */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
        <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2 border-b border-slate-100 pb-2">
          <FileCheck2 className="w-4 h-4 text-blue-600" />
          Identificação da Nota Fiscal
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Número da Nota Fiscal *
            </label>
            <input
              type="text"
              required
              value={numeroNotaFiscal}
              onChange={(e) => setNumeroNotaFiscal(e.target.value)}
              className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
              placeholder="Ex: 000123"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Número de Série *
            </label>
            <input
              type="text"
              required
              value={numeroDeSerie}
              onChange={(e) => setNumeroDeSerie(e.target.value)}
              className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
              placeholder="Ex: 1"
            />
          </div>
        </div>
      </div>

      {/* Bloco 2: Participantes (Emitente e Destinatário) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Emitente */}
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-3">
          <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2 border-b border-slate-100 pb-2">
            <Building2 className="w-4 h-4 text-blue-600" />
            Empresa Emitente *
          </h3>
          {emitentes.length === 0 ? (
            <p className="text-xs text-rose-500">Nenhum emitente cadastrado. Cadastre um na aba Emitentes.</p>
          ) : (
            <div>
              <label className="block text-xs text-slate-500 mb-1">Selecione o Emitente</label>
              <select
                value={emitenteId}
                onChange={(e) => setEmitenteId(Number(e.target.value))}
                className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              >
                {emitentes.map((e) => (
                  <option key={e.id} value={e.id}>
                    {e.razaoSocial} (CNPJ: {e.cnpj})
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>

        {/* Destinatário */}
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-3">
          <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2 border-b border-slate-100 pb-2">
            <User className="w-4 h-4 text-blue-600" />
            Cliente Destinatário *
          </h3>
          {destinatarios.length === 0 ? (
            <p className="text-xs text-rose-500">Nenhum cliente cadastrado. Cadastre um na aba Destinatários.</p>
          ) : (
            <div>
              <label className="block text-xs text-slate-500 mb-1">Selecione o Destinatário</label>
              <select
                value={destinatarioId}
                onChange={(e) => setDestinatarioId(Number(e.target.value))}
                className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              >
                {destinatarios.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.nome} (Doc: {d.documento})
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
      </div>

      {/* Bloco 3: Itens da Nota Fiscal */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2">
            <Package className="w-4 h-4 text-blue-600" />
            Itens da Nota Fiscal
          </h3>
          <button
            type="button"
            onClick={handleAddItem}
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-blue-600 hover:text-blue-800 bg-blue-50 px-3 py-1.5 rounded-lg transition-colors"
          >
            <Plus className="w-3.5 h-3.5" /> Adicionar Produto
          </button>
        </div>

        {produtos.length === 0 ? (
          <p className="text-xs text-rose-500">Nenhum produto cadastrado no estoque.</p>
        ) : (
          <div className="space-y-3">
            {items.map((item, index) => {
              const prod = produtos.find((p) => p.id === Number(item.produtoId));
              const precoUnitario = prod ? prod.valorUnitario : 0;
              const subtotalItem = precoUnitario * item.quantidade;

              return (
                <div
                  key={index}
                  className="p-3 bg-slate-50 rounded-lg border border-slate-200 flex flex-col md:flex-row items-center gap-3"
                >
                  <div className="flex-1 w-full">
                    <label className="block text-[11px] font-semibold text-slate-500 mb-1">
                      Produto
                    </label>
                    <select
                      value={item.produtoId}
                      onChange={(e) => handleItemChange(index, 'produtoId', Number(e.target.value))}
                      className="w-full px-3 py-1.5 bg-white border border-slate-200 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    >
                      {produtos.map((p) => (
                        <option key={p.id} value={p.id}>
                          {p.descricao} (NCM: {p.codigoNcm}) - {formatCurrency(p.valorUnitario)}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="w-full md:w-28">
                    <label className="block text-[11px] font-semibold text-slate-500 mb-1">
                      Quantidade
                    </label>
                    <input
                      type="number"
                      min="1"
                      value={item.quantidade}
                      onChange={(e) => handleItemChange(index, 'quantidade', Math.max(1, Number(e.target.value)))}
                      className="w-full px-3 py-1.5 bg-white border border-slate-200 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    />
                  </div>

                  <div className="w-full md:w-36 text-right">
                    <span className="block text-[11px] font-semibold text-slate-400">Total Item</span>
                    <span className="text-sm font-bold text-slate-800">
                      {formatCurrency(subtotalItem)}
                    </span>
                  </div>

                  <div className="w-full md:w-auto flex justify-end">
                    <button
                      type="button"
                      onClick={() => handleRemoveItem(index)}
                      className="p-1.5 text-slate-400 hover:text-rose-600 transition-colors"
                      title="Remover item"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Resumo Financeiro da Nota */}
        <div className="mt-4 pt-4 border-t border-slate-100 flex flex-col sm:flex-row justify-end items-end gap-6 text-sm">
          <div className="text-right">
            <span className="text-xs text-slate-500 block">Total em Produtos:</span>
            <span className="font-semibold text-slate-700">{formatCurrency(subtotalCalculado)}</span>
          </div>
          <div className="text-right">
            <span className="text-xs text-slate-500 block">ICMS Estimado:</span>
            <span className="font-semibold text-blue-600">{formatCurrency(icmsCalculado)}</span>
          </div>
          <div className="text-right border-l pl-6 border-slate-200">
            <span className="text-xs font-bold text-slate-500 block uppercase">Valor Total da NF-e:</span>
            <span className="text-xl font-bold text-slate-900">{formatCurrency(subtotalCalculado)}</span>
          </div>
        </div>
      </div>

      {/* Botões de Ação */}
      <div className="flex justify-end gap-3">
        <button
          type="button"
          onClick={() => navigate('/notas')}
          className="px-5 py-2.5 rounded-lg border border-slate-300 text-slate-700 font-medium text-sm hover:bg-slate-50 transition-colors"
        >
          Cancelar
        </button>
        <button
          type="submit"
          disabled={loading}
          className="px-6 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm shadow-sm transition-colors flex items-center gap-2 disabled:opacity-50"
        >
          {loading ? 'Emitindo Nota...' : 'Emitir Nota Fiscal'}
        </button>
      </div>
    </form>
  );
};

export default EmitirNota;
