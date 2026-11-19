import api from './api';
import { NotaFiscal, NotaFiscalRequest } from '../types';

export const notaFiscalService = {
  getAll: async (): Promise<NotaFiscal[]> => {
    const response = await api.get<NotaFiscal[]>('/notas-fiscais');
    return response.data;
  },

  getById: async (id: number): Promise<NotaFiscal> => {
    const response = await api.get<NotaFiscal>(`/notas-fiscais/${id}`);
    return response.data;
  },

  emitir: async (data: NotaFiscalRequest): Promise<NotaFiscal> => {
    const response = await api.post<NotaFiscal>('/notas-fiscais', data);
    return response.data;
  },

  cancelar: async (id: number): Promise<NotaFiscal> => {
    try {
      const response = await api.patch<NotaFiscal>(`/notas-fiscais/${id}/cancelar`);
      return response.data;
    } catch (err: any) {
      const response = await api.put<NotaFiscal>(`/notas-fiscais/${id}/cancelar`);
      return response.data;
    }
  },

  downloadXml: async (id: number, numeroNota: string) => {
    const response = await api.get(`/notas-fiscais/${id}/xml`, {
      responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/xml' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `NFe-${numeroNota}.xml`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },

  downloadPdf: async (id: number, numeroNota: string) => {
    const response = await api.get(`/notas-fiscais/${id}/pdf`, {
      responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `DANFE-${numeroNota}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },

  visualizarPdf: (id: number) => {
    const pdfUrl = `${api.defaults.baseURL}/notas-fiscais/${id}/pdf`;
    window.open(pdfUrl, '_blank');
  }
};
