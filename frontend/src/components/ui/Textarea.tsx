import React, { forwardRef } from 'react';

export interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  helperText?: string;
  error?: string;
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, helperText, error, style, disabled, ...props }, ref) => {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
        {label && (
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
            {label}
            {props.required && <span style={{ color: '#ef4444', marginLeft: '4px' }}>*</span>}
          </label>
        )}
        <textarea
          ref={ref}
          disabled={disabled}
          style={{
            width: '100%',
            padding: '9px 12px',
            fontSize: '0.875rem',
            borderRadius: '8px',
            border: `1px solid ${error ? '#ef4444' : '#cbd5e1'}`,
            background: disabled ? '#f8fafc' : '#ffffff',
            color: '#0f172a',
            outline: 'none',
            minHeight: '80px',
            fontFamily: 'inherit',
            transition: 'border-color 0.15s ease-in-out',
            boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
            ...style,
          }}
          {...props}
        />
        {error && <span style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '4px', fontWeight: 500 }}>{error}</span>}
        {!error && helperText && <span style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>{helperText}</span>}
      </div>
    );
  }
);

Textarea.displayName = 'Textarea';
