import React, { forwardRef } from 'react';

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  options: SelectOption[];
  helperText?: string;
  error?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, options, helperText, error, style, disabled, ...props }, ref) => {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
        {label && (
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
            {label}
            {props.required && <span style={{ color: '#ef4444', marginLeft: '4px' }}>*</span>}
          </label>
        )}
        <select
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
            cursor: disabled ? 'not-allowed' : 'pointer',
            transition: 'border-color 0.15s ease-in-out',
            boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
            ...style,
          }}
          {...props}
        >
          {options.map((opt) => (
            <option key={opt.value} value={opt.value} disabled={opt.disabled}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <span style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '4px', fontWeight: 500 }}>{error}</span>}
        {!error && helperText && <span style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>{helperText}</span>}
      </div>
    );
  }
);

Select.displayName = 'Select';
