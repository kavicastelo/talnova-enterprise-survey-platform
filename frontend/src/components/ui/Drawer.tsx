import React, { useEffect } from 'react';

export interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  subtitle?: string;
  position?: 'right' | 'left';
  width?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
}

export const Drawer: React.FC<DrawerProps> = ({
  isOpen,
  onClose,
  title,
  subtitle,
  position = 'right',
  width = '480px',
  children,
  footer,
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) onClose();
    };
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = 'auto';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      onClick={onClose}
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 1000,
        background: 'rgba(15, 23, 42, 0.5)',
        backdropFilter: 'blur(3px)',
        display: 'flex',
        justifyContent: position === 'right' ? 'flex-end' : 'flex-start',
      }}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          width: '100%',
          maxWidth: width,
          height: '100%',
          background: '#ffffff',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.2)',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        {(title || subtitle) && (
          <div
            style={{
              padding: '20px 24px',
              borderBottom: '1px solid #e2e8f0',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
            }}
          >
            <div>
              {title && <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>{title}</h3>}
              {subtitle && <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>{subtitle}</p>}
            </div>
            <button
              onClick={onClose}
              style={{ background: 'transparent', border: 'none', cursor: 'pointer', fontSize: '1.25rem', color: '#94a3b8' }}
            >
              &times;
            </button>
          </div>
        )}

        <div style={{ padding: '24px', overflowY: 'auto', flex: 1 }}>{children}</div>

        {footer && (
          <div style={{ padding: '16px 24px', borderTop: '1px solid #e2e8f0', background: '#f8fafc' }}>
            {footer}
          </div>
        )}
      </div>
    </div>
  );
};
