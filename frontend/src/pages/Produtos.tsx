import React, { useEffect, useState } from 'react';
import { produtoService } from '../services/produtoService';
import { Produto, ProdutoRequest } from '../types';
import { formatCurrency } from '../utils/formatters';
import { Package, Plus, Trash2, AlertCircle } from 'lucide-react';

const Produtos: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  const [formData, setFormData] = useState<ProdutoRequest>({
    descricao: '',
    valorUnitario: 0,
    codigoNcm: '',
    aliquotaIcms: 18.0,
  });

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      setLoading(true);
      const data = await produtoService.getAll();
      setProdutos(data);
    } catch (err) {
      setErro('Erro ao listar catálogo de produtos.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Deseja realmente excluir este produto?')) return;
    try {
      setErro(null);
      await produtoService.delete(id);
      setSucesso('Produto excluído com sucesso.');
      setTimeout(() => setSucesso(null), 3000);
      carregarProdutos();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao excluir produto.';
      setErro(msg);
      setTimeout(() => setErro(null), 5000);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    if (formData.valorUnitario <= 0) {
      setErro('O valor unitário deve ser maior que zero.');
      return;
    }
    if (!formData.codigoNcm.trim()) {
      setErro('O código NCM é obrigatório.');
      return;
    }

    try {
      await produtoService.create({
        ...formData,
        valorUnitario: Number(formData.valorUnitario),
        aliquotaIcms: Number(formData.aliquotaIcms),
      });
      setModalAberto(false);
      setFormData({
        descricao: '',
        valorUnitario: 0,
        codigoNcm: '',
        aliquotaIcms: 18.0,
      });
      setSucesso('Produto cadastrado com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      carregarProdutos();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao cadastrar produto.';
      setErro(msg);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Catálogo de Produtos</h2>
          <p className="text-xs text-slate-500 mt-0.5">Produtos disponíveis para emissão de notas fiscais com tributos</p>
        </div>
        <button
          onClick={() => setModalAberto(true)}
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2.5 rounded-lg text-sm shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" /> Novo Produto
        </button>
      </div>

      {sucesso && (
        <div className="p-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm">
          {sucesso}
        </div>
      )}
      {erro && (
        <div className="p-4 rounded-lg bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-600" />
          {erro}
        </div>
      )}

      {/* Tabela de Produtos */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Carregando catálogo...</div>
        ) : produtos.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <Package className="w-12 h-12 mx-auto text-slate-300 mb-2" />
            <p className="text-sm font-semibold text-slate-700">Nenhum produto cadastrado.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-500 border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Descrição do Produto</th>
                  <th className="py-3 px-4">Código NCM</th>
                  <th className="py-3 px-4 text-right">Alíquota ICMS</th>
                  <th className="py-3 px-4 text-right">Preço Unitário</th>
                  <th className="py-3 px-4 text-center">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {produtos.map((prod) => (
                  <tr key={prod.id} className="hover:bg-slate-50">
                    <td className="py-3.5 px-4 font-bold text-slate-800">{prod.descricao}</td>
                    <td className="py-3.5 px-4 text-xs font-mono">{prod.codigoNcm}</td>
                    <td className="py-3.5 px-4 text-right font-medium text-blue-600">{prod.aliquotaIcms}%</td>
                    <td className="py-3.5 px-4 text-right font-bold text-slate-900">
                      {formatCurrency(prod.valorUnitario)}
                    </td>
                    <td className="py-3.5 px-4 text-center">
                      <button
                        onClick={() => handleDelete(prod.id)}
                        title="Excluir produto"
                        className="p-1.5 rounded-md hover:bg-rose-50 text-slate-400 hover:text-rose-600 transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Cadastro */}
      {modalAberto && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl shadow-xl border border-slate-200 max-w-md w-full p-6 space-y-4">
            <h3 className="text-base font-bold text-slate-800">Cadastrar Novo Produto</h3>

            <form onSubmit={handleSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Descrição do Produto *</label>
                <input
                  type="text"
                  required
                  value={formData.descricao}
                  onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  placeholder="Ex: Teclado Mecânico RGB"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Preço Unitário (R$) *</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0.01"
                    required
                    value={formData.valorUnitario || ''}
                    onChange={(e) => setFormData({ ...formData, valorUnitario: Number(e.target.value) })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="0.00"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Código NCM *</label>
                  <input
                    type="text"
                    required
                    value={formData.codigoNcm}
                    onChange={(e) => setFormData({ ...formData, codigoNcm: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="Ex: 84716052"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Alíquota ICMS (%) *</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  required
                  value={formData.aliquotaIcms}
                  onChange={(e) => setFormData({ ...formData, aliquotaIcms: Number(e.target.value) })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  placeholder="Ex: 18.0"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setModalAberto(false)}
                  className="px-4 py-2 border border-slate-300 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold"
                >
                  Salvar Produto
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Produtos;
