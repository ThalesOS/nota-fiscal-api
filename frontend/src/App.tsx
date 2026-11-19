import React from 'react';
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import NotasFiscais from './pages/NotasFiscais';
import EmitirNota from './pages/EmitirNota';
import Emitentes from './pages/Emitentes';
import Destinatarios from './pages/Destinatarios';
import Produtos from './pages/Produtos';

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const location = useLocation();

  const getPageInfo = () => {
    switch (location.pathname) {
      case '/':
        return { title: 'Dashboard', subtitle: 'Métricas gerais e atalhos rápidos' };
      case '/notas':
        return { title: 'Notas Fiscais', subtitle: 'Listagem, downloads e cancelamentos' };
      case '/emitir':
        return { title: 'Emitir Nota Fiscal', subtitle: 'Emissão e cálculo de impostos' };
      case '/emitentes':
        return { title: 'Emitentes', subtitle: 'Cadastro de empresas emissoras' };
      case '/destinatarios':
        return { title: 'Destinatários', subtitle: 'Cadastro de clientes' };
      case '/produtos':
        return { title: 'Produtos', subtitle: 'Catálogo e alíquotas de ICMS' };
      default:
        return { title: 'Sistema Fiscal', subtitle: '' };
    }
  };

  const { title, subtitle } = getPageInfo();

  return (
    <div className="flex min-h-screen bg-slate-50">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Header title={title} subtitle={subtitle} />
        <main className="flex-1 p-8 max-w-7xl w-full mx-auto">
          {children}
        </main>
      </div>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/notas" element={<NotasFiscais />} />
          <Route path="/emitir" element={<EmitirNota />} />
          <Route path="/emitentes" element={<Emitentes />} />
          <Route path="/destinatarios" element={<Destinatarios />} />
          <Route path="/produtos" element={<Produtos />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
};

export default App;
