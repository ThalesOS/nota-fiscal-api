export type StatusNotaFiscal = 'EMITIDA' | 'CANCELADA';

export interface Emitente {
  id: number;
  cnpj: string;
  razaoSocial: string;
  inscricaoEstadual: string;
  logradouro: string;
  numero: string;
  bairro: string;
  cep: string;
}

export interface EmitenteRequest {
  cnpj: string;
  razaoSocial: string;
  inscricaoEstadual: string;
  logradouro: string;
  numero: string;
  bairro: string;
  cep: string;
}

export interface Destinatario {
  id: number;
  documento: string;
  nome: string;
  email: string;
  logradouro: string;
  numero: string;
  cep: string;
}

export interface DestinatarioRequest {
  documento: string;
  nome: string;
  email: string;
  logradouro: string;
  numero: string;
  cep: string;
}

export interface Produto {
  id: number;
  descricao: string;
  valorUnitario: number;
  codigoNcm: string;
  aliquotaIcms: number;
}

export interface ProdutoRequest {
  descricao: string;
  valorUnitario: number;
  codigoNcm: string;
  aliquotaIcms: number;
}

export interface ItemNotaFiscal {
  id?: number;
  produto: Produto;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
  aliquotaIcms: number;
  valorIcms: number;
}

export interface ItemNotaFiscalRequest {
  produtoId: number;
  quantidade: number;
}

export interface NotaFiscal {
  id: number;
  numeroNotaFiscal: string;
  numeroDeSerie: string;
  dataEmissao: string;
  status: StatusNotaFiscal;
  emitente: Emitente;
  destinatario: Destinatario;
  itens: ItemNotaFiscal[];
  valorTotalProdutos: number;
  valorTotalIcms: number;
  valorTotalNotaFiscal: number;
}

export interface NotaFiscalRequest {
  numeroNotaFiscal: string;
  numeroDeSerie: string;
  emitenteId: number;
  destinatarioId: number;
  items: ItemNotaFiscalRequest[];
}
