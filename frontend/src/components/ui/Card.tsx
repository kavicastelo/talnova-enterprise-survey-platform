import React from 'react';

export interface CardProps {
  title?: React.ReactNode;
  subtitle?: React.ReactNode;
  action?: React.ReactNode;
  variant?: 'default' | 'bordered' | 'hoverable';
  padding?: string;
  children: React.ReactNode;
  style?: React.CSSProperties;
}

export const Card: React.FC<CardProps> = ({
  title,
  subtitle,
  action,
  variant = 'default',
  padding = '20px',
  children,
  style,
}) => {
  return (
    <div
      style={{
        background: '#ffffff',
        borderRadius: '14px',
        border: '1px solid #e2e8f0',
        boxShadow: variant === 'hoverable' ? '0 4px 6px -1px rgba(0,0,0,0.05)' : '0 1px 3px 0 rgba(0,0,0,0.05)',
        transition: 'all 0.15s ease-in-out',
        display: 'flex',
        flexDirection: 'column',
        ...style,
      }}
    >
      {(title || action) && (
        <div
          style={{
            padding: '16px 20px',
            borderBottom: '1px solid #f1f5f9',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div>
            {typeof title === 'string' ? (
              <h4 style={{ fontSize: '1rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>{title}</h4>
            ) : (
              title
            )}
            {subtitle && <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>{subtitle}</p>}
          </div>
          {action && <div>{action}</div>}
        </div>
      )}
      <div style={{ padding, flex: 1 }}>{children}</div>
    </div>
  );
};
