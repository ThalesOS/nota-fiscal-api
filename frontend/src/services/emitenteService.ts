import api from './api';
import { Emitente, EmitenteRequest } from '../types';

export const emitenteService = {
  getAll: async (): Promise<Emitente[]> => {
    const response = await api.get<Emitente[]>('/emitentes');
    return response.data;
  },

  getById: async (id: number): Promise<Emitente> => {
    const response = await api.get<Emitente>(`/emitentes/${id}`);
    return response.data;
  },

  create: async (data: EmitenteRequest): Promise<Emitente> => {
    const response = await api.post<Emitente>('/emitentes', data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/emitentes/${id}`);
  }
};
