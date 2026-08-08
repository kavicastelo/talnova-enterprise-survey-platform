import React from 'react';
import { Modal } from './Modal';
import { Button } from './Button';
import { JobStatus } from '../../hooks/useAsyncJob';

export interface AsyncJobProgressModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  jobId: string | null;
  status: JobStatus;
  progress: number;
  errorDetails?: string | null;
  onRetry?: () => void;
  downloadUrl?: string | null;
}

export const AsyncJobProgressModal: React.FC<AsyncJobProgressModalProps> = ({
  isOpen,
  onClose,
  title,
  jobId,
  status,
  progress,
  errorDetails,
  onRetry,
  downloadUrl,
}) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={status === 'PROCESSING' || status === 'PENDING' ? () => {} : onClose}
      title={title}
      size="sm"
      footer={
        <>
          {status === 'FAILED' && onRetry && (
            <Button variant="danger" onClick={onRetry}>
              Retry Job
            </Button>
          )}
          {status === 'COMPLETED' && downloadUrl && (
            <Button variant="primary" onClick={() => window.open(downloadUrl, '_blank')}>
              Download File
            </Button>
          )}
          <Button
            variant="secondary"
            onClick={onClose}
            disabled={status === 'PROCESSING' || status === 'PENDING'}
          >
            Close
          </Button>
        </>
      }
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
          Job Reference: <code style={{ fontWeight: 700, color: '#0f172a' }}>{jobId || 'N/A'}</code>
        </div>

        {/* Progress Bar Container */}
        <div style={{ width: '100%', background: '#e2e8f0', borderRadius: '9999px', height: '12px', overflow: 'hidden' }}>
          <div
            style={{
              width: `${Math.max(5, Math.min(100, progress))}%`,
              background: status === 'FAILED' ? '#ef4444' : status === 'COMPLETED' ? '#10b981' : '#3b82f6',
              height: '100%',
              transition: 'width 0.3s ease-in-out',
            }}
          />
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', fontWeight: 600 }}>
          <span style={{ color: status === 'FAILED' ? '#dc2626' : status === 'COMPLETED' ? '#059669' : '#1d4ed8' }}>
            {status === 'PENDING' && '⏳ Job Queued...'}
            {status === 'PROCESSING' && `⚡ Processing... (${progress}%)`}
            {status === 'COMPLETED' && '✓ Execution Completed Successfully!'}
            {status === 'FAILED' && '✖ Execution Failed'}
          </span>
          <span style={{ color: '#64748b' }}>{progress}%</span>
        </div>

        {status === 'FAILED' && errorDetails && (
          <div style={{ padding: '12px', background: '#fef2f2', border: '1px solid #fca5a5', borderRadius: '8px', color: '#991b1b', fontSize: '0.8rem' }}>
            {errorDetails}
          </div>
        )}
      </div>
    </Modal>
  );
};
