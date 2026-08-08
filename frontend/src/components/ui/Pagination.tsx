import React from 'react';
import { Button } from './Button';

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements?: number;
  pageSize?: number;
  onPageChange: (page: number) => void;
}

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize = 10,
  onPageChange,
}) => {
  if (totalPages <= 1) return null;

  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = totalElements ? Math.min(currentPage * pageSize, totalElements) : currentPage * pageSize;

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '12px 16px',
        borderTop: '1px solid #e2e8f0',
        background: '#ffffff',
        fontSize: '0.85rem',
        color: '#64748b',
      }}
    >
      <div>
        Showing <span style={{ fontWeight: 700, color: '#0f172a' }}>{startItem}</span> to{' '}
        <span style={{ fontWeight: 700, color: '#0f172a' }}>{endItem}</span>
        {totalElements && (
          <>
            {' '}of <span style={{ fontWeight: 700, color: '#0f172a' }}>{totalElements}</span> results
          </>
        )}
      </div>

      <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
        <Button
          variant="secondary"
          size="sm"
          disabled={currentPage <= 1}
          onClick={() => onPageChange(currentPage - 1)}
        >
          Previous
        </Button>
        <span style={{ fontWeight: 600, color: '#0f172a', padding: '0 8px' }}>
          Page {currentPage} of {totalPages}
        </span>
        <Button
          variant="secondary"
          size="sm"
          disabled={currentPage >= totalPages}
          onClick={() => onPageChange(currentPage + 1)}
        >
          Next
        </Button>
      </div>
    </div>
  );
};
