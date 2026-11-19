import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  FileText, 
  PlusCircle, 
  Building2, 
  Users, 
  Package, 
  LayoutDashboard,
  ReceiptText
} from 'lucide-react';

const Sidebar: React.FC = () => {
  const navItems = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/notas', label: 'Notas Fiscais', icon: FileText },
    { to: '/emitir', label: 'Emitir Nova Nota', icon: PlusCircle },
    { to: '/emitentes', label: 'Emitentes', icon: Building2 },
    { to: '/destinatarios', label: 'Destinatários', icon: Users },
    { to: '/produtos', label: 'Produtos', icon: Package },
  ];

  return (
    <aside className="w-64 bg-white border-r border-slate-200 flex flex-col h-screen sticky top-0 shadow-sm">
      {/* Brand Header */}
      <div className="h-16 flex items-center gap-3 px-6 border-b border-slate-100">
        <div className="w-9 h-9 rounded-lg bg-blue-600 flex items-center justify-center text-white shadow-sm shadow-blue-200">
          <ReceiptText className="w-5 h-5" />
        </div>
        <div>
          <h1 className="font-bold text-slate-800 text-base leading-tight">Nota Fiscal API</h1>
          <span className="text-xs text-blue-600 font-medium">Gestão & Emissão NF-e</span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="p-4 space-y-1.5 flex-1">
        <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider px-3 mb-2">
          Menu Principal
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-700 font-semibold'
                    : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                }`
              }
            >
              <Icon className="w-4 h-4 text-slate-500 group-hover:text-blue-600" />
              {item.label}
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Info */}
      <div className="p-4 border-t border-slate-100 text-xs text-slate-400">
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
          <span>API Spring Boot v4.1.0</span>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
