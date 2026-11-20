import api from './api';
import { Destinatario, DestinatarioRequest } from '../types';

export const destinatarioService = {
  getAll: async (): Promise<Destinatario[]> => {
    const response = await api.get<Destinatario[]>('/destinatarios');
    return response.data;
  },

  getById: async (id: number): Promise<Destinatario> => {
    const response = await api.get<Destinatario>(`/destinatarios/${id}`);
    return response.data;
  },

  create: async (data: DestinatarioRequest): Promise<Destinatario> => {
    const response = await api.post<Destinatario>('/destinatarios', data);
    return response.data;
  },

  update: async (id: number, data: DestinatarioRequest): Promise<Destinatario> => {
    const response = await api.put<Destinatario>(`/destinatarios/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/destinatarios/${id}`);
  }
};
