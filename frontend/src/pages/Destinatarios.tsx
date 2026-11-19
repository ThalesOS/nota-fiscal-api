import React, { useEffect, useState } from 'react';
import { destinatarioService } from '../services/destinatarioService';
import { Destinatario, DestinatarioRequest } from '../types';
import { formatCnpjCpf, formatCep } from '../utils/formatters';
import { Users, Plus, Trash2, AlertCircle } from 'lucide-react';

const Destinatarios: React.FC = () => {
  const [destinatarios, setDestinatarios] = useState<Destinatario[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  const [formData, setFormData] = useState<DestinatarioRequest>({
    documento: '',
    nome: '',
    email: '',
    logradouro: '',
    numero: '',
    cep: '',
  });

  useEffect(() => {
    carregarDestinatarios();
  }, []);

  const carregarDestinatarios = async () => {
    try {
      setLoading(true);
      const data = await destinatarioService.getAll();
      setDestinatarios(data);
    } catch (err) {
      setErro('Erro ao listar clientes destinatários.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Deseja realmente excluir este destinatário?')) return;
    try {
      setErro(null);
      await destinatarioService.delete(id);
      setSucesso('Destinatário excluído com sucesso.');
      setTimeout(() => setSucesso(null), 3000);
      carregarDestinatarios();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao excluir destinatário.';
      setErro(msg);
      setTimeout(() => setErro(null), 5000);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    const docLimpo = formData.documento.replace(/\D/g, '');
    const cepLimpo = formData.cep.replace(/\D/g, '');

    if (docLimpo.length !== 11 && docLimpo.length !== 14) {
      setErro('O documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos).');
      return;
    }
    if (cepLimpo.length !== 8) {
      setErro('O CEP deve conter exatamente 8 dígitos numéricos.');
      return;
    }

    try {
      await destinatarioService.create({
        ...formData,
        documento: docLimpo,
        cep: cepLimpo,
      });
      setModalAberto(false);
      setFormData({
        documento: '',
        nome: '',
        email: '',
        logradouro: '',
        numero: '',
        cep: '',
      });
      setSucesso('Cliente destinatário cadastrado com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      carregarDestinatarios();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Erro ao cadastrar destinatário.';
      setErro(msg);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Clientes Destinatários</h2>
          <p className="text-xs text-slate-500 mt-0.5">Cadastro de pessoas físicas ou jurídicas compradoras</p>
        </div>
        <button
          onClick={() => setModalAberto(true)}
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2.5 rounded-lg text-sm shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" /> Novo Destinatário
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

      {/* Tabela de Destinatários */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Carregando...</div>
        ) : destinatarios.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <Users className="w-12 h-12 mx-auto text-slate-300 mb-2" />
            <p className="text-sm font-semibold text-slate-700">Nenhum destinatário cadastrado.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-500 border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Nome / Razão</th>
                  <th className="py-3 px-4">CPF / CNPJ</th>
                  <th className="py-3 px-4">E-mail</th>
                  <th className="py-3 px-4">Endereço</th>
                  <th className="py-3 px-4">CEP</th>
                  <th className="py-3 px-4 text-center">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {destinatarios.map((dest) => (
                  <tr key={dest.id} className="hover:bg-slate-50">
                    <td className="py-3.5 px-4 font-bold text-slate-800">{dest.nome}</td>
                    <td className="py-3.5 px-4">{formatCnpjCpf(dest.documento)}</td>
                    <td className="py-3.5 px-4 text-xs text-slate-600">{dest.email}</td>
                    <td className="py-3.5 px-4 text-xs">
                      {dest.logradouro}, {dest.numero}
                    </td>
                    <td className="py-3.5 px-4">{formatCep(dest.cep)}</td>
                    <td className="py-3.5 px-4 text-center">
                      <button
                        onClick={() => handleDelete(dest.id)}
                        title="Excluir destinatário"
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
            <h3 className="text-base font-bold text-slate-800">Cadastrar Novo Destinatário</h3>

            <form onSubmit={handleSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Nome Completo / Razão *</label>
                <input
                  type="text"
                  required
                  value={formData.nome}
                  onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  placeholder="Ex: João da Silva"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">CPF ou CNPJ *</label>
                  <input
                    type="text"
                    required
                    value={formData.documento}
                    onChange={(e) => setFormData({ ...formData, documento: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="Somente números"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">E-mail *</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="cliente@email.com"
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
                  Salvar Destinatário
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Destinatarios;
