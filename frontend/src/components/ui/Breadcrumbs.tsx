import React from 'react';
import { Link, useLocation } from 'react-router-dom';

export interface BreadcrumbItem {
  label: string;
  path?: string;
}

export interface BreadcrumbsProps {
  items?: BreadcrumbItem[];
}

const PATH_MAP: Record<string, string> = {
  dashboard: 'Executive Dashboard',
  settings: 'Settings & Administration',
  projects: 'Project Tenant Provisioning',
  branding: 'White-Label Branding',
  features: 'Feature Flag Matrix',
  locales: 'Locales & Multilingual',
  organization: 'Organizational Hierarchy',
  employees: 'Employee Roster Studio',
  surveys: 'Survey Campaigns',
  build: 'Survey Builder Studio',
  distribution: 'Campaign Distribution',
  analytics: 'Executive Analytics Engine',
  'ai-insights': 'AI Sentiment & Summaries',
  reports: 'Reporting & Export Jobs',
  'action-plans': 'Action Planning Kanban',
  notifications: 'Notification Logs',
  audit: 'Immutable Audit Trail',
};

export const Breadcrumbs: React.FC<BreadcrumbsProps> = ({ items }) => {
  const location = useLocation();

  const generatedItems: BreadcrumbItem[] = React.useMemo(() => {
    if (items) return items;

    const segments = location.pathname.split('/').filter(Boolean);
    const crumbs: BreadcrumbItem[] = [{ label: 'Home', path: '/dashboard' }];

    let currentPath = '';
    segments.forEach((seg) => {
      currentPath += `/${seg}`;
      const label = PATH_MAP[seg] || seg.toUpperCase();
      crumbs.push({ label, path: currentPath });
    });

    return crumbs;
  }, [items, location.pathname]);

  return (
    <nav style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.8rem', color: '#64748b', marginBottom: '12px' }}>
      {generatedItems.map((crumb, index) => {
        const isLast = index === generatedItems.length - 1;

        return (
          <React.Fragment key={index}>
            {index > 0 && <span style={{ color: '#cbd5e1' }}>/</span>}
            {isLast || !crumb.path ? (
              <span style={{ fontWeight: 600, color: '#0f172a' }}>{crumb.label}</span>
            ) : (
              <Link to={crumb.path} style={{ color: '#3b82f6', textDecoration: 'none', fontWeight: 500 }}>
                {crumb.label}
              </Link>
            )}
          </React.Fragment>
        );
      })}
    </nav>
  );
};
