import React, { createContext, useContext, useState, useCallback } from 'react';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastMessage {
  id: string;
  type: ToastType;
  title?: string;
  message: string;
  duration?: number;
}

interface ToastContextValue {
  toasts: ToastMessage[];
  showToast: (toast: Omit<ToastMessage, 'id'>) => void;
  showSuccess: (message: string, title?: string) => void;
  showError: (message: string, title?: string) => void;
  showWarning: (message: string, title?: string) => void;
  showInfo: (message: string, title?: string) => void;
  dismissToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const dismissToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const showToast = useCallback(
    (toast: Omit<ToastMessage, 'id'>) => {
      const id = `toast-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;
      const newToast: ToastMessage = { ...toast, id };
      setToasts((prev) => [...prev.slice(-4), newToast]); // keep max 5

      const duration = toast.duration ?? 5000;
      if (duration > 0) {
        setTimeout(() => {
          dismissToast(id);
        }, duration);
      }
    },
    [dismissToast]
  );

  const showSuccess = useCallback((message: string, title?: string) => showToast({ type: 'success', message, title }), [showToast]);
  const showError = useCallback((message: string, title?: string) => showToast({ type: 'error', message, title }), [showToast]);
  const showWarning = useCallback((message: string, title?: string) => showToast({ type: 'warning', message, title }), [showToast]);
  const showInfo = useCallback((message: string, title?: string) => showToast({ type: 'info', message, title }), [showToast]);

  return (
    <ToastContext.Provider
      value={{
        toasts,
        showToast,
        showSuccess,
        showError,
        showWarning,
        showInfo,
        dismissToast,
      }}
    >
      {children}
      <ToastContainer toasts={toasts} onDismiss={dismissToast} />
    </ToastContext.Provider>
  );
};

export const useToast = (): ToastContextValue => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

const ToastContainer: React.FC<{ toasts: ToastMessage[]; onDismiss: (id: string) => void }> = ({ toasts, onDismiss }) => {
  if (toasts.length === 0) return null;

  return (
    <div
      style={{
        position: 'fixed',
        bottom: '24px',
        right: '24px',
        zIndex: 9999,
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        maxWidth: '400px',
        width: '100%',
        pointerEvents: 'none',
      }}
    >
      {toasts.map((toast) => (
        <div
          key={toast.id}
          style={{
            pointerEvents: 'auto',
            background: toast.type === 'error' ? '#fef2f2' : toast.type === 'success' ? '#ecfdf5' : toast.type === 'warning' ? '#fffbeb' : '#f0f9ff',
            border: `1px solid ${
              toast.type === 'error' ? '#fca5a5' : toast.type === 'success' ? '#6ee7b7' : toast.type === 'warning' ? '#fde68a' : '#7dd3fc'
            }`,
            color: toast.type === 'error' ? '#991b1b' : toast.type === 'success' ? '#065f46' : toast.type === 'warning' ? '#92400e' : '#075985',
            padding: '12px 16px',
            borderRadius: '8px',
            boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            fontSize: '0.875rem',
            animation: 'fadeIn 0.2s ease-in-out',
          }}
        >
          <div>
            {toast.title && <div style={{ fontWeight: 700, marginBottom: '2px' }}>{toast.title}</div>}
            <div>{toast.message}</div>
          </div>
          <button
            onClick={() => onDismiss(toast.id)}
            style={{
              background: 'transparent',
              border: 'none',
              cursor: 'pointer',
              fontWeight: 700,
              fontSize: '1.1rem',
              color: 'inherit',
              marginLeft: '12px',
              lineHeight: 1,
            }}
          >
            &times;
          </button>
        </div>
      ))}
    </div>
  );
};
