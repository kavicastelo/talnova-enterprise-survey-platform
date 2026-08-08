import React from 'react';

interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  color?: string;
  label?: string;
}

export const Spinner: React.FC<SpinnerProps> = ({ size = 'md', color = '#3b82f6', label }) => {
  const pixelSize = size === 'sm' ? 16 : size === 'lg' ? 40 : 24;

  return (
    <div style={{ display: 'inline-flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
      <div
        style={{
          width: `${pixelSize}px`,
          height: `${pixelSize}px`,
          border: `3px solid #e2e8f0`,
          borderTopColor: color,
          borderRadius: '50%',
          animation: 'spin 0.8s linear infinite',
        }}
      />
      {label && <span style={{ fontSize: '0.875rem', color: '#64748b', fontWeight: 500 }}>{label}</span>}
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};
