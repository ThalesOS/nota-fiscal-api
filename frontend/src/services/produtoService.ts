import api from './api';
import { Produto, ProdutoRequest } from '../types';

export const produtoService = {
  getAll: async (): Promise<Produto[]> => {
    const response = await api.get<Produto[]>('/produtos');
    return response.data;
  },

  getById: async (id: number): Promise<Produto> => {
    const response = await api.get<Produto>(`/produtos/${id}`);
    return response.data;
  },

  create: async (data: ProdutoRequest): Promise<Produto> => {
    const response = await api.post<Produto>('/produtos', data);
    return response.data;
  },

  update: async (id: number, data: ProdutoRequest): Promise<Produto> => {
    const response = await api.put<Produto>(`/produtos/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/produtos/${id}`);
  }
};
