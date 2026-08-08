import React from 'react';
import { Button } from './Button';

export interface ErrorStateProps {
  title?: string;
  message?: string;
  errorCode?: string;
  onRetry?: () => void;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  title = 'Failed to Load Data',
  message = 'An error occurred while fetching information from the backend service.',
  errorCode,
  onRetry,
}) => {
  return (
    <div
      style={{
        padding: '36px 24px',
        textAlign: 'center',
        background: '#fef2f2',
        borderRadius: '12px',
        border: '1px solid #fca5a5',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        margin: '16px 0',
      }}
    >
      <div style={{ fontSize: '2.5rem', marginBottom: '8px', color: '#ef4444' }}>⚠️</div>
      <h4 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#991b1b', margin: '0 0 6px 0' }}>
        {title}
      </h4>
      <p style={{ fontSize: '0.875rem', color: '#7f1d1d', maxWidth: '440px', margin: '0 0 16px 0', lineHeight: 1.5 }}>
        {message}
      </p>
      {errorCode && (
        <code style={{ fontSize: '0.75rem', background: '#fee2e2', color: '#991b1b', padding: '4px 8px', borderRadius: '4px', marginBottom: '16px' }}>
          Error Code: {errorCode}
        </code>
      )}
      {onRetry && (
        <Button variant="danger" size="sm" onClick={onRetry}>
          Retry Request
        </Button>
      )}
    </div>
  );
};
