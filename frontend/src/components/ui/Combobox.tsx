import React, { useState, useRef, useEffect } from 'react';
import { SelectOption } from './Select';

export interface ComboboxProps {
  label?: string;
  options: SelectOption[];
  value?: string;
  onChange: (value: string) => void;
  placeholder?: string;
  disabled?: boolean;
  error?: string;
  helperText?: string;
}

export const Combobox: React.FC<ComboboxProps> = ({
  label,
  options,
  value,
  onChange,
  placeholder = 'Select or search...',
  disabled = false,
  error,
  helperText,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState('');
  const containerRef = useRef<HTMLDivElement>(null);

  const selectedOption = options.find((opt) => opt.value === value);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const filteredOptions = options.filter((opt) =>
    opt.label.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div ref={containerRef} style={{ display: 'flex', flexDirection: 'column', width: '100%', position: 'relative' }}>
      {label && <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>{label}</label>}

      <div
        onClick={() => !disabled && setIsOpen(!isOpen)}
        style={{
          padding: '9px 12px',
          fontSize: '0.875rem',
          borderRadius: '8px',
          border: `1px solid ${error ? '#ef4444' : '#cbd5e1'}`,
          background: disabled ? '#f8fafc' : '#ffffff',
          color: selectedOption ? '#0f172a' : '#94a3b8',
          cursor: disabled ? 'not-allowed' : 'pointer',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        }}
      >
        <span>{selectedOption ? selectedOption.label : placeholder}</span>
        <span style={{ fontSize: '0.75rem', color: '#64748b' }}>▼</span>
      </div>

      {isOpen && (
        <div
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            zIndex: 100,
            marginTop: '4px',
            background: '#ffffff',
            border: '1px solid #cbd5e1',
            borderRadius: '8px',
            boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
            maxHeight: '220px',
            overflowY: 'auto',
            padding: '6px',
          }}
        >
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Filter options..."
            autoFocus
            style={{
              width: '100%',
              padding: '6px 10px',
              fontSize: '0.8rem',
              borderRadius: '6px',
              border: '1px solid #e2e8f0',
              marginBottom: '6px',
              outline: 'none',
            }}
          />
          {filteredOptions.length === 0 ? (
            <div style={{ padding: '8px', fontSize: '0.8rem', color: '#94a3b8', textAlign: 'center' }}>No options found</div>
          ) : (
            filteredOptions.map((opt) => (
              <div
                key={opt.value}
                onClick={() => {
                  onChange(opt.value);
                  setIsOpen(false);
                  setSearch('');
                }}
                style={{
                  padding: '8px 10px',
                  fontSize: '0.85rem',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  background: opt.value === value ? '#eff6ff' : 'transparent',
                  color: opt.value === value ? '#1d4ed8' : '#334155',
                  fontWeight: opt.value === value ? 600 : 400,
                }}
              >
                {opt.label}
              </div>
            ))
          )}
        </div>
      )}

      {error && <span style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '4px', fontWeight: 500 }}>{error}</span>}
      {!error && helperText && <span style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>{helperText}</span>}
    </div>
  );
};
