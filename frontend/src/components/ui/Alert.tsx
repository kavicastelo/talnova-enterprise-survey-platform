import React from 'react';

export interface AlertProps {
  type?: 'info' | 'success' | 'warning' | 'error';
  title?: string;
  children: React.ReactNode;
  onClose?: () => void;
  style?: React.CSSProperties;
}

export const Alert: React.FC<AlertProps> = ({ type = 'info', title, children, onClose, style }) => {
  const getStyles = () => {
    switch (type) {
      case 'success':
        return { bg: '#ecfdf5', border: '#a7f3d0', text: '#065f46', icon: '✓' };
      case 'warning':
        return { bg: '#fffbeb', border: '#fde68a', text: '#92400e', icon: '⚠️' };
      case 'error':
        return { bg: '#fef2f2', border: '#fca5a5', text: '#991b1b', icon: '✖' };
      case 'info':
      default:
        return { bg: '#f0f9ff', border: '#bae6fd', text: '#075985', icon: 'ℹ' };
    }
  };

  const { bg, border, text, icon } = getStyles();

  return (
    <div
      style={{
        padding: '12px 16px',
        borderRadius: '8px',
        background: bg,
        border: `1px solid ${border}`,
        color: text,
        display: 'flex',
        alignItems: 'flex-start',
        gap: '12px',
        fontSize: '0.875rem',
        ...style,
      }}
    >
      <span style={{ fontWeight: 800, fontSize: '1rem', lineHeight: 1 }}>{icon}</span>
      <div style={{ flex: 1 }}>
        {title && <div style={{ fontWeight: 700, marginBottom: '2px' }}>{title}</div>}
        <div style={{ lineHeight: 1.4 }}>{children}</div>
      </div>
      {onClose && (
        <button
          onClick={onClose}
          style={{ background: 'transparent', border: 'none', cursor: 'pointer', fontWeight: 700, color: 'inherit' }}
        >
          &times;
        </button>
      )}
    </div>
  );
};
