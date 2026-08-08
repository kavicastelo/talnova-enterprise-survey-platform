import React, { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTenant } from '../context/TenantContext';
import { Badge } from '../components/ui/Badge';

export const MainPlatformLayout: React.FC = () => {
  const { user, logout, hasAnyRole } = useAuth();
  const { activeProject, projectsList, switchProject, branding, featureFlags } = useTenant();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const navigate = useNavigate();

  const navItems = [
    { label: 'Executive Dashboard', path: '/dashboard', icon: '📊', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] as const },
    { label: 'Tenant Provisioning', path: '/settings/projects', icon: '🏢', roles: ['SUPER_ADMIN'] as const },
    { label: 'White-Label Branding', path: '/settings/branding', icon: '🎨', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] as const },
    { label: 'Feature Flag Matrix', path: '/settings/features', icon: '🚩', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] as const },
    { label: 'Locales & Languages', path: '/settings/locales', icon: '🌐', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] as const },
    { label: 'Org Hierarchy Tree', path: '/organization', icon: '🌳', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] as const },
    { label: 'Employee Roster', path: '/employees', icon: '👥', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] as const },
    { label: 'Survey Campaigns', path: '/surveys', icon: '📋', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] as const },
    { label: 'Distribution & Tokens', path: '/distribution', icon: '🚀', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER'] as const },
    { label: 'Analytics Engine', path: '/analytics', icon: '📈', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] as const },
    { label: 'AI Sentiment & Summaries', path: '/ai-insights', icon: '✨', flag: 'aiAnalyticsEnabled' as const, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'CONSULTANT_DAASH'] as const },
    { label: 'Report Job Generator', path: '/reports', icon: '📑', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH'] as const },
    { label: 'Action Planning Kanban', path: '/action-plans', icon: '🎯', flag: 'actionPlanningEnabled' as const, roles: ['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER'] as const },
    { label: 'Notification Logs', path: '/notifications', icon: '🔔', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] as const },
    { label: 'Immutable Audit Trail', path: '/audit', icon: '🛡️', roles: ['SUPER_ADMIN', 'PROJECT_ADMIN'] as const },
  ];

  const filteredNavItems = navItems.filter((item) => {
    if (!hasAnyRole([...item.roles])) return false;
    if (item.flag && !featureFlags[item.flag]) return false;
    return true;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', background: '#f8fafc' }}>
      {/* Top Application Header */}
      <header
        style={{
          height: '64px',
          background: '#0f172a',
          color: '#ffffff',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '0 24px',
          boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)',
          position: 'sticky',
          top: 0,
          zIndex: 50,
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            style={{
              background: 'transparent',
              border: 'none',
              color: '#ffffff',
              fontSize: '1.25rem',
              cursor: 'pointer',
              display: 'none',
            }}
            className="mobile-hamburger"
          >
            ☰
          </button>

          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '8px',
                background: `linear-gradient(135deg, ${branding.primaryColor}, ${branding.secondaryColor})`,
                display: 'grid',
                placeItems: 'center',
                fontWeight: 800,
                color: '#ffffff',
                fontSize: '1.1rem',
              }}
            >
              T
            </div>
            <div>
              <span style={{ fontWeight: 800, fontSize: '1.1rem', letterSpacing: '-0.025em', display: 'block', lineHeight: 1.1 }}>
                TESP Platform
              </span>
              <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>Talnova Enterprise Survey Platform</span>
            </div>
          </div>

          {/* Project Tenant Selector Dropdown */}
          <div style={{ marginLeft: '24px', borderLeft: '1px solid #334155', paddingLeft: '16px' }}>
            <select
              value={activeProject?.projectId || ''}
              onChange={(e) => switchProject(e.target.value)}
              style={{
                background: '#1e293b',
                color: '#ffffff',
                border: '1px solid #475569',
                borderRadius: '6px',
                padding: '6px 12px',
                fontSize: '0.825rem',
                fontWeight: 600,
                outline: 'none',
                cursor: 'pointer',
              }}
            >
              {projectsList.map((p) => (
                <option key={p.projectId} value={p.projectId}>
                  {p.projectId} — {p.branding.companyName}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* User Session Profile Menu */}
        <div style={{ position: 'relative', display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '0.85rem', fontWeight: 700, color: '#ffffff' }}>{user?.fullName}</div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '6px', marginTop: '2px' }}>
              <Badge variant={user?.role === 'SUPER_ADMIN' ? 'danger' : 'info'}>{user?.role}</Badge>
            </div>
          </div>

          <button
            onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
            style={{
              width: '36px',
              height: '36px',
              borderRadius: '50%',
              background: '#334155',
              border: '2px solid #3b82f6',
              color: '#ffffff',
              fontWeight: 700,
              cursor: 'pointer',
              display: 'grid',
              placeItems: 'center',
            }}
          >
            {user?.fullName.charAt(0)}
          </button>

          {isUserMenuOpen && (
            <div
              style={{
                position: 'absolute',
                top: '100%',
                right: 0,
                marginTop: '8px',
                width: '220px',
                background: '#ffffff',
                borderRadius: '8px',
                boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
                border: '1px solid #cbd5e1',
                padding: '8px 0',
                zIndex: 100,
                color: '#0f172a',
              }}
            >
              <div style={{ padding: '8px 16px', borderBottom: '1px solid #f1f5f9', fontSize: '0.8rem', color: '#64748b' }}>
                Signed in as <strong style={{ color: '#0f172a' }}>{user?.email}</strong>
              </div>
              <button
                onClick={() => {
                  setIsUserMenuOpen(false);
                  logout();
                  navigate('/login');
                }}
                style={{
                  width: '100%',
                  textAlign: 'left',
                  padding: '8px 16px',
                  background: 'transparent',
                  border: 'none',
                  color: '#ef4444',
                  fontWeight: 600,
                  fontSize: '0.85rem',
                  cursor: 'pointer',
                }}
              >
                🚪 Sign Out
              </button>
            </div>
          )}
        </div>
      </header>

      {/* Main Body Layout with Sidebar + Content */}
      <div style={{ display: 'flex', flex: 1 }}>
        {/* Navigation Sidebar */}
        <aside
          style={{
            width: '260px',
            background: '#ffffff',
            borderRight: '1px solid #e2e8f0',
            padding: '16px 12px',
            display: 'flex',
            flexDirection: 'column',
            gap: '4px',
          }}
        >
          <div style={{ padding: '8px 12px', fontSize: '0.7rem', fontWeight: 700, color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Domain Modules ({filteredNavItems.length})
          </div>

          {filteredNavItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              style={({ isActive }) => ({
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                padding: '9px 12px',
                borderRadius: '8px',
                fontSize: '0.875rem',
                fontWeight: isActive ? 700 : 500,
                color: isActive ? '#1d4ed8' : '#475569',
                background: isActive ? '#eff6ff' : 'transparent',
                textDecoration: 'none',
                transition: 'all 0.15s ease-in-out',
              })}
            >
              <span>{item.icon}</span>
              <span>{item.label}</span>
            </NavLink>
          ))}
        </aside>

        {/* Main Content Area */}
        <main style={{ flex: 1, padding: '28px 32px', overflowY: 'auto' }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};
