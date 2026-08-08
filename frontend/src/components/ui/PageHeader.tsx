import React from 'react';
import { Breadcrumbs, BreadcrumbItem } from './Breadcrumbs';

export interface PageHeaderProps {
  title: string;
  subtitle?: string;
  badge?: React.ReactNode;
  actions?: React.ReactNode;
  breadcrumbs?: BreadcrumbItem[];
}

export const PageHeader: React.FC<PageHeaderProps> = ({
  title,
  subtitle,
  badge,
  actions,
  breadcrumbs,
}) => {
  return (
    <div style={{ marginBottom: '24px' }}>
      <Breadcrumbs items={breadcrumbs} />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0f172a', letterSpacing: '-0.025em', margin: 0 }}>
              {title}
            </h1>
            {badge}
          </div>
          {subtitle && <p style={{ fontSize: '0.9rem', color: '#64748b', margin: '4px 0 0 0', maxWidth: '700px' }}>{subtitle}</p>}
        </div>
        {actions && <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>{actions}</div>}
      </div>
    </div>
  );
};
