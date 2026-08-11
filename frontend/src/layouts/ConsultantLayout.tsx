import React from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  Compass,
  BarChart3,
  BrainCircuit,
  FileText,
  CheckSquare,
  LogOut,
  Building2,
  FolderKanban,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTenant } from '../context/TenantContext';
import { Badge } from '../components/ui/Badge';

export const ConsultantLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const { activeProjectId, projects, switchProject } = useTenant();
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { label: 'Consultant Dashboard', path: '/consultant/dashboard', icon: <Compass className="w-4 h-4" /> },
    { label: 'Assigned Projects', path: '/consultant/projects', icon: <FolderKanban className="w-4 h-4" /> },
    { label: 'Engagement Analytics', path: '/consultant/analytics', icon: <BarChart3 className="w-4 h-4" /> },
    { label: 'AI Risk & Insights', path: '/consultant/ai-insights', icon: <BrainCircuit className="w-4 h-4" /> },
    { label: 'Executive Reports', path: '/consultant/reports', icon: <FileText className="w-4 h-4" /> },
    { label: 'Action Planning', path: '/consultant/actions', icon: <CheckSquare className="w-4 h-4" /> },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans">
      {/* Consultant Top Header */}
      <header className="h-16 bg-white border-b border-indigo-200 px-6 flex items-center justify-between sticky top-0 z-40 shadow-sm">
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <div className="w-9 h-9 rounded-lg bg-indigo-50 border border-indigo-200 flex items-center justify-center text-indigo-600 font-bold">
              D
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-base tracking-wide text-slate-900">DAASH GLOBAL</span>
                <Badge variant="indigo" size="sm">CONSULTANT</Badge>
              </div>
              <p className="text-[10px] text-slate-500 font-mono">Enterprise Advisory Workspace</p>
            </div>
          </div>

          <div className="h-6 w-px bg-slate-200 mx-2 hidden sm:block" />

          {/* Project Switcher */}
          <div className="flex items-center gap-2 bg-slate-100 border border-slate-300 rounded-lg px-3 py-1.5">
            <Building2 className="w-4 h-4 text-indigo-600" />
            <select
              value={activeProjectId}
              onChange={(e) => switchProject(e.target.value)}
              className="bg-transparent text-xs font-semibold text-slate-800 focus:outline-none cursor-pointer pr-2"
            >
              {projects.map((p) => (
                <option key={p.id} value={p.id} className="bg-white text-slate-900">
                  {p.name} ({p.id})
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-semibold text-slate-900">{user?.name || 'Daash Advisor'}</p>
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
          <p className="px-3 text-[10px] font-mono text-slate-400 uppercase tracking-widest mb-2">Advisory Navigation</p>
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-medium transition-colors ${
                  isActive
                    ? 'bg-indigo-50 text-indigo-700 border border-indigo-200 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
              </Link>
            );
          })}
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 p-6 overflow-y-auto bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
