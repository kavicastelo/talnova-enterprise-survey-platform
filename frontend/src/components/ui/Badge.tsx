import React from 'react';

export interface BadgeProps {
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  dot?: boolean;
  children: React.ReactNode;
  style?: React.CSSProperties;
}

export const Badge: React.FC<BadgeProps> = ({ variant = 'neutral', dot = false, children, style }) => {
  const getStyles = (): { bg: string; text: string; border: string; dotColor: string } => {
    switch (variant) {
      case 'success':
        return { bg: '#ecfdf5', text: '#065f46', border: '#a7f3d0', dotColor: '#10b981' };
      case 'warning':
        return { bg: '#fffbeb', text: '#92400e', border: '#fde68a', dotColor: '#f59e0b' };
      case 'danger':
        return { bg: '#fef2f2', text: '#991b1b', border: '#fca5a5', dotColor: '#ef4444' };
      case 'info':
        return { bg: '#f0f9ff', text: '#075985', border: '#bae6fd', dotColor: '#0284c7' };
      case 'neutral':
      default:
        return { bg: '#f1f5f9', text: '#334155', border: '#cbd5e1', dotColor: '#64748b' };
    }
  };

  const { bg, text, border, dotColor } = getStyles();

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '3px 10px',
        borderRadius: '9999px',
        fontSize: '0.75rem',
        fontWeight: 600,
        background: bg,
        color: text,
        border: `1px solid ${border}`,
        lineHeight: 1.25,
        ...style,
      }}
    >
      {dot && (
        <span
          style={{
            width: '6px',
            height: '6px',
            borderRadius: '50%',
            background: dotColor,
          }}
        />
      )}
      {children}
    </span>
  );
};
