import React from 'react';
import { Card } from './Card';

export interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: {
    value: string;
    isPositive: boolean;
  };
  icon?: React.ReactNode;
}

export const StatCard: React.FC<StatCardProps> = ({ title, value, subtitle, trend, icon }) => {
  return (
    <Card variant="default" padding="20px">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <span style={{ fontSize: '0.8rem', fontWeight: 600, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            {title}
          </span>
          <div style={{ fontSize: '2rem', fontWeight: 800, color: '#0f172a', margin: '4px 0', letterSpacing: '-0.025em' }}>
            {value}
          </div>
        </div>
        {icon && (
          <div style={{ width: '42px', height: '42px', borderRadius: '10px', background: '#eff6ff', display: 'grid', placeItems: 'center', color: '#1d4ed8' }}>
            {icon}
          </div>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '8px', fontSize: '0.8rem' }}>
        {trend && (
          <span
            style={{
              fontWeight: 700,
              color: trend.isPositive ? '#059669' : '#dc2626',
              background: trend.isPositive ? '#ecfdf5' : '#fef2f2',
              padding: '2px 6px',
              borderRadius: '4px',
            }}
          >
            {trend.isPositive ? '↑' : '↓'} {trend.value}
          </span>
        )}
        {subtitle && <span style={{ color: '#64748b' }}>{subtitle}</span>}
      </div>
    </Card>
  );
};
