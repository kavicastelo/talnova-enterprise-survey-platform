import React, { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Building2,
  Users,
  FileQuestion,
  Send,
  BarChart3,
  BrainCircuit,
  FileText,
  CheckSquare,
  Bell,
  FileCheck,
  Sliders,
  Flag,
  Globe,
  Palette,
  LogOut,
  ChevronDown,
  Menu,
  X,
  Shield,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTenant } from '../context/TenantContext';
import { Badge } from '../components/ui/Badge';
import { UserRole } from '../core/auth/auth.types';

export const MainPlatformLayout: React.FC = () => {
  const { user, logout, hasRole } = useAuth();
  const { activeProjectId, projects, switchProject } = useTenant();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const navigate = useNavigate();

  const navItems: Array<{
    label: string;
    path: string;
    icon: React.ReactNode;
    roles: UserRole[];
  }> = [
    { label: 'Executive Dashboard', path: '/dashboard', icon: <LayoutDashboard className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] },
    { label: 'Org Hierarchy Tree', path: '/organization', icon: <Building2 className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] },
    { label: 'Employee Roster', path: '/employees', icon: <Users className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] },
    { label: 'Survey Campaigns', path: '/surveys', icon: <FileQuestion className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] },
    { label: 'Distribution & Tokens', path: '/distribution', icon: <Send className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] },
    { label: 'Analytics Engine', path: '/analytics', icon: <BarChart3 className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] },
    { label: 'AI Sentiment & Risk', path: '/ai-insights', icon: <BrainCircuit className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'CONSULTANT_DAASH'] },
    { label: 'Reporting Center', path: '/reports', icon: <FileText className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] },
    { label: 'Action Planning', path: '/action-plans', icon: <CheckSquare className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER'] },
    { label: 'Notification Logs', path: '/notifications', icon: <Bell className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] },
    { label: 'Audit Trail', path: '/audit', icon: <FileCheck className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] },
    { label: 'Project Settings', path: '/settings/projects', icon: <Sliders className="w-4 h-4" />, roles: ['SUPER_ADMIN'] },
    { label: 'Branding Configuration', path: '/settings/branding', icon: <Palette className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] },
    { label: 'Feature Flags', path: '/settings/features', icon: <Flag className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] },
    { label: 'Locale Management', path: '/settings/locales', icon: <Globe className="w-4 h-4" />, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] },
  ];

  const filteredNavItems = navItems.filter((item) => hasRole(item.roles));

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans">
      {/* Top Application Header */}
      <header className="h-16 bg-white border-b border-slate-200 px-4 md:px-6 flex items-center justify-between sticky top-0 z-40 shadow-sm">
        <div className="flex items-center gap-3 md:gap-4">
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="md:hidden text-slate-500 hover:text-slate-900 p-1"
          >
            {isMobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>

          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-lg bg-indigo-600 border border-indigo-400/40 flex items-center justify-center font-bold text-white shadow-md">
              T
            </div>
            <div className="hidden sm:block">
              <span className="font-bold text-base tracking-tight text-slate-900 block leading-tight">
                TESP PLATFORM
              </span>
              <span className="text-[10px] text-slate-500 font-mono">Talnova Enterprise Survey Platform</span>
            </div>
          </div>

          <div className="h-6 w-px bg-slate-200 mx-1 hidden sm:block" />

          {/* Project Tenant Selector */}
          <div className="flex items-center gap-2 bg-slate-100 border border-slate-300 rounded-lg px-2.5 py-1.5">
            <Building2 className="w-4 h-4 text-indigo-600" />
            <select
              value={activeProjectId}
              onChange={(e) => switchProject(e.target.value)}
              className="bg-transparent text-xs font-semibold text-slate-800 focus:outline-none cursor-pointer pr-1"
            >
              {projects.map((p) => (
                <option key={p.id} value={p.id} className="bg-white text-slate-900">
                  {p.name} ({p.id})
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* User Identity & Portal Portals Links */}
        <div className="flex items-center gap-3">
          {hasRole(['SUPER_ADMIN']) && (
            <NavLink
              to="/super-admin/dashboard"
              className="hidden lg:flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-rose-50 border border-rose-200 text-rose-700 text-xs font-semibold hover:bg-rose-100 transition-colors"
            >
              <Shield className="w-3.5 h-3.5" />
              <span>Super Admin</span>
            </NavLink>
          )}

          {hasRole(['CONSULTANT_DAASH']) && (
            <NavLink
              to="/consultant/dashboard"
              className="hidden lg:flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-indigo-50 border border-indigo-200 text-indigo-700 text-xs font-semibold hover:bg-indigo-100 transition-colors"
            >
              <Badge variant="indigo" size="sm">Daash Consultant</Badge>
            </NavLink>
          )}

          <div className="relative">
            <button
              onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-slate-100 transition-colors"
            >
              <div className="w-8 h-8 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center font-bold text-xs text-indigo-600">
                {user?.name ? user.name[0] : 'U'}
              </div>
              <div className="text-left hidden md:block">
                <p className="text-xs font-semibold text-slate-900 leading-tight">{user?.name || 'User Session'}</p>
                <p className="text-[10px] text-slate-500">{user?.roles?.[0] || 'Authenticated'}</p>
              </div>
              <ChevronDown className="w-4 h-4 text-slate-500" />
            </button>

            {isUserMenuOpen && (
              <div className="absolute right-0 mt-2 w-56 bg-white border border-slate-200 rounded-xl shadow-xl py-2 z-50">
                <div className="px-4 py-2 border-b border-slate-100">
                  <p className="text-xs font-semibold text-slate-900">{user?.name}</p>
                  <p className="text-[11px] text-slate-500 font-mono truncate">{user?.email}</p>
                  <div className="mt-1 flex flex-wrap gap-1">
                    {user?.roles?.map((r) => (
                      <Badge key={r} variant="indigo" size="sm">
                        {r}
                      </Badge>
                    ))}
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center gap-2 px-4 py-2.5 text-xs text-rose-600 hover:bg-slate-50 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  <span>Sign Out</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      <div className="flex-1 flex">
        {/* Sidebar Navigation */}
        <aside
          className={`w-64 bg-white border-r border-slate-200 p-4 flex flex-col gap-1 fixed md:static inset-y-16 left-0 z-30 transition-transform md:translate-x-0 ${
            isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
          }`}
        >
          <p className="px-3 text-[10px] font-mono text-slate-400 uppercase tracking-widest mb-2">Platform Features</p>
          <div className="flex-1 overflow-y-auto flex flex-col gap-1 pr-1">
            {filteredNavItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={() => setIsMobileMenuOpen(false)}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-medium transition-colors ${
                    isActive
                      ? 'bg-indigo-600 text-white font-semibold shadow-sm'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                  }`
                }
              >
                {item.icon}
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>

          <div className="pt-3 border-t border-slate-200 text-[10px] font-mono text-slate-500 flex items-center justify-between">
            <span>Project: {activeProjectId}</span>
            <span className="text-emerald-600 font-semibold">Gateway Ready</span>
          </div>
        </aside>

        {/* Main View Area */}
        <main className="flex-1 p-4 md:p-6 overflow-y-auto bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
