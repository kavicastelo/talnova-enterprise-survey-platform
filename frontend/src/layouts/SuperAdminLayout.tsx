import React from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  ShieldAlert,
  Building2,
  Users,
  Flag,
  Activity,
  LogOut,
  Sliders,
  FileCheck2,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/ui/Badge';

export const SuperAdminLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { label: 'Platform Dashboard', path: '/super-admin/dashboard', icon: <Activity className="w-4 h-4" /> },
    { label: 'Tenant Projects', path: '/super-admin/projects', icon: <Building2 className="w-4 h-4" /> },
    { label: 'Consultants', path: '/super-admin/consultants', icon: <Users className="w-4 h-4" /> },
    { label: 'Feature Flags', path: '/super-admin/feature-flags', icon: <Flag className="w-4 h-4" /> },
    { label: 'Audit Trail', path: '/super-admin/audit', icon: <FileCheck2 className="w-4 h-4" /> },
    { label: 'System Health', path: '/super-admin/system', icon: <Sliders className="w-4 h-4" /> },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans">
      {/* Super Admin Top Header */}
      <header className="h-16 bg-white border-b border-rose-200 px-6 flex items-center justify-between sticky top-0 z-40 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-rose-50 border border-rose-200 flex items-center justify-center text-rose-600">
            <ShieldAlert className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-bold text-base tracking-wide text-slate-900">TALNOVA</span>
              <Badge variant="danger" size="sm">SUPER ADMIN</Badge>
            </div>
            <p className="text-[10px] font-mono text-slate-500">Platform Governance Console</p>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-semibold text-slate-900">{user?.name || 'Super Admin'}</p>
            <p className="text-[10px] text-slate-500 font-mono">{user?.email}</p>
          </div>
          <button
            onClick={handleLogout}
            className="p-2 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-lg transition-colors"
            title="Sign Out"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </header>

      <div className="flex-1 flex">
        {/* Sidebar */}
        <aside className="w-64 bg-white border-r border-slate-200 p-4 flex flex-col gap-1">
          <p className="px-3 text-[10px] font-mono text-slate-400 uppercase tracking-widest mb-2">Platform Administration</p>
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-medium transition-colors ${
                  isActive
                    ? 'bg-rose-50 text-rose-700 border border-rose-200 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
              </Link>
            );
          })}

          <div className="mt-auto p-3 bg-slate-50 border border-slate-200 rounded-lg text-[11px] text-slate-600">
            <p className="font-semibold text-slate-900 mb-1">Gateway Status</p>
            <div className="flex items-center gap-2 text-emerald-600">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              <span>Port 8080 Active</span>
            </div>
          </div>
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 p-6 overflow-y-auto bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
