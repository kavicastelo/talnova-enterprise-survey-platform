import React, { forwardRef } from 'react';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  helperText?: string;
  error?: string;
  icon?: React.ReactNode;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, helperText, error, icon, style, disabled, ...props }, ref) => {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
        {label && (
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
            {label}
            {props.required && <span style={{ color: '#ef4444', marginLeft: '4px' }}>*</span>}
          </label>
        )}
        <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
          {icon && (
            <div style={{ position: 'absolute', left: '12px', display: 'flex', alignItems: 'center', pointerEvents: 'none', color: '#94a3b8' }}>
              {icon}
            </div>
          )}
          <input
            ref={ref}
            disabled={disabled}
            style={{
              width: '100%',
              padding: icon ? '9px 12px 9px 38px' : '9px 12px',
              fontSize: '0.875rem',
              borderRadius: '8px',
              border: `1px solid ${error ? '#ef4444' : '#cbd5e1'}`,
              background: disabled ? '#f8fafc' : '#ffffff',
              color: '#0f172a',
              outline: 'none',
              transition: 'border-color 0.15s ease-in-out',
              boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
              ...style,
            }}
            {...props}
          />
        </div>
        {error && <span style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '4px', fontWeight: 500 }}>{error}</span>}
        {!error && helperText && <span style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>{helperText}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';
