import React from 'react';
import { Spinner } from './Spinner';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  icon?: React.ReactNode;
  children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  isLoading = false,
  icon,
  children,
  disabled,
  style,
  ...props
}) => {
  const getVariantStyles = (): React.CSSProperties => {
    switch (variant) {
      case 'secondary':
        return { background: '#e2e8f0', color: '#1e293b', border: '1px solid #cbd5e1' };
      case 'outline':
        return { background: 'transparent', color: '#1e3a8a', border: '1px solid #3b82f6' };
      case 'danger':
        return { background: '#ef4444', color: '#ffffff', border: 'none' };
      case 'ghost':
        return { background: 'transparent', color: '#475569', border: 'none' };
      case 'primary':
      default:
        return { background: 'var(--tesp-primary-color, #1e3a8a)', color: '#ffffff', border: 'none' };
    }
  };

  const getSizeStyles = (): React.CSSProperties => {
    switch (size) {
      case 'sm':
        return { padding: '6px 12px', fontSize: '0.8rem', borderRadius: '6px' };
      case 'lg':
        return { padding: '12px 24px', fontSize: '1rem', borderRadius: '10px' };
      case 'md':
      default:
        return { padding: '9px 18px', fontSize: '0.875rem', borderRadius: '8px' };
    }
  };

  return (
    <button
      disabled={disabled || isLoading}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '8px',
        fontWeight: 600,
        cursor: disabled || isLoading ? 'not-allowed' : 'pointer',
        opacity: disabled || isLoading ? 0.65 : 1,
        transition: 'all 0.15s ease-in-out',
        boxShadow: variant === 'ghost' || variant === 'outline' ? 'none' : '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        ...getVariantStyles(),
        ...getSizeStyles(),
        ...style,
      }}
      {...props}
    >
      {isLoading ? <Spinner size="sm" color={variant === 'primary' || variant === 'danger' ? '#ffffff' : '#3b82f6'} /> : icon}
      {children}
    </button>
  );
};
