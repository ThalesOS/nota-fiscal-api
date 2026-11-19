import React, { useEffect, useState } from 'react';
import { emitenteService } from '../services/emitenteService';
import { Emitente, EmitenteRequest } from '../types';
import { formatCnpjCpf, formatCep } from '../utils/formatters';
import { Building2, Plus, Trash2, AlertCircle } from 'lucide-react';

const Emitentes: React.FC = () => {
  const [emitentes, setEmitentes] = useState<Emitente[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  const [formData, setFormData] = useState<EmitenteRequest>({
    cnpj: '',
    razaoSocial: '',
    inscricaoEstadual: '',
    logradouro: '',
    numero: '',
    bairro: '',
    cep: '',
  });

  useEffect(() => {
    carregarEmitentes();
  }, []);

  const carregarEmitentes = async () => {
    try {
      setLoading(true);
      const data = await emitenteService.getAll();
      setEmitentes(data);
    } catch (err) {
      setErro('Erro ao listar empresas emitentes.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Deseja realmente excluir este emitente?')) return;
    try {
      setErro(null);
      await emitenteService.delete(id);
      setSucesso('Emitente excluído com sucesso.');
      setTimeout(() => setSucesso(null), 3000);
      carregarEmitentes();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao excluir emitente.';
      setErro(msg);
      setTimeout(() => setErro(null), 5000);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    // Validação de CNPJ e CEP
    const cnpjLimpo = formData.cnpj.replace(/\D/g, '');
    const cepLimpo = formData.cep.replace(/\D/g, '');

    if (cnpjLimpo.length !== 14) {
      setErro('O CNPJ deve conter exatamente 14 dígitos numéricos.');
      return;
    }
    if (cepLimpo.length !== 8) {
      setErro('O CEP deve conter exatamente 8 dígitos numéricos.');
      return;
    }

    try {
      await emitenteService.create({
        ...formData,
        cnpj: cnpjLimpo,
        cep: cepLimpo,
      });
      setModalAberto(false);
      setFormData({
        cnpj: '',
        razaoSocial: '',
        inscricaoEstadual: '',
        logradouro: '',
        numero: '',
        bairro: '',
        cep: '',
      });
      setSucesso('Emitente cadastrado com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      carregarEmitentes();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao salvar emitente.';
      setErro(msg);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Empresas Emitentes</h2>
          <p className="text-xs text-slate-500 mt-0.5">Cadastro de empresas responsáveis pela emissão fiscal</p>
        </div>
        <button
          onClick={() => setModalAberto(true)}
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2.5 rounded-lg text-sm shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" /> Novo Emitente
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

      {/* Tabela de Emitentes */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Carregando...</div>
        ) : emitentes.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <Building2 className="w-12 h-12 mx-auto text-slate-300 mb-2" />
            <p className="text-sm font-semibold text-slate-700">Nenhum emitente cadastrado.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-500 border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Razão Social</th>
                  <th className="py-3 px-4">CNPJ</th>
                  <th className="py-3 px-4">Inscrição Estadual</th>
                  <th className="py-3 px-4">Endereço / Bairro</th>
                  <th className="py-3 px-4">CEP</th>
                  <th className="py-3 px-4 text-center">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {emitentes.map((emit) => (
                  <tr key={emit.id} className="hover:bg-slate-50">
                    <td className="py-3.5 px-4 font-bold text-slate-800">{emit.razaoSocial}</td>
                    <td className="py-3.5 px-4">{formatCnpjCpf(emit.cnpj)}</td>
                    <td className="py-3.5 px-4">{emit.inscricaoEstadual}</td>
                    <td className="py-3.5 px-4 text-xs">
                      {emit.logradouro}, {emit.numero} - {emit.bairro}
                    </td>
                    <td className="py-3.5 px-4">{formatCep(emit.cep)}</td>
                    <td className="py-3.5 px-4 text-center">
                      <button
                        onClick={() => handleDelete(emit.id)}
                        title="Excluir emitente"
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
          <div className="bg-white rounded-xl shadow-xl border border-slate-200 max-w-lg w-full p-6 space-y-4">
            <h3 className="text-base font-bold text-slate-800">Cadastrar Nova Empresa Emitente</h3>

            <form onSubmit={handleSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Razão Social *</label>
                <input
                  type="text"
                  required
                  value={formData.razaoSocial}
                  onChange={(e) => setFormData({ ...formData, razaoSocial: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  placeholder="Ex: Tech Soluções LTDA"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">CNPJ (14 dígitos) *</label>
                  <input
                    type="text"
                    required
                    maxLength={14}
                    value={formData.cnpj}
                    onChange={(e) => setFormData({ ...formData, cnpj: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="Somente números"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Inscrição Estadual *</label>
                  <input
                    type="text"
                    required
                    value={formData.inscricaoEstadual}
                    onChange={(e) => setFormData({ ...formData, inscricaoEstadual: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div className="col-span-2">
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Logradouro / Rua *</label>
                  <input
                    type="text"
                    required
                    value={formData.logradouro}
                    onChange={(e) => setFormData({ ...formData, logradouro: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Número *</label>
                  <input
                    type="text"
                    required
                    value={formData.numero}
                    onChange={(e) => setFormData({ ...formData, numero: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Bairro *</label>
                  <input
                    type="text"
                    required
                    value={formData.bairro}
                    onChange={(e) => setFormData({ ...formData, bairro: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">CEP (8 dígitos) *</label>
                  <input
                    type="text"
                    required
                    maxLength={8}
                    value={formData.cep}
                    onChange={(e) => setFormData({ ...formData, cep: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="00000000"
                  />
                </div>
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
                  Salvar Emitente
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Emitentes;
