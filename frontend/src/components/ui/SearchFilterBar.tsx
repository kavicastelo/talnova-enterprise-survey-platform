import React from 'react';
import { Input } from './Input';
import { Button } from './Button';

export interface SearchFilterBarProps {
  searchValue: string;
  onSearchChange: (value: string) => void;
  placeholder?: string;
  filters?: React.ReactNode;
  actions?: React.ReactNode;
  onClearFilters?: () => void;
}

export const SearchFilterBar: React.FC<SearchFilterBarProps> = ({
  searchValue,
  onSearchChange,
  placeholder = 'Search by keyword...',
  filters,
  actions,
  onClearFilters,
}) => {
  return (
    <div
      style={{
        display: 'flex',
        flexWrap: 'wrap',
        gap: '12px',
        justifyContent: 'space-between',
        alignItems: 'center',
        background: '#ffffff',
        padding: '16px',
        borderRadius: '12px',
        border: '1px solid #e2e8f0',
        marginBottom: '16px',
      }}
    >
      <div style={{ display: 'flex', flex: 1, gap: '12px', minWidth: '280px', flexWrap: 'wrap', alignItems: 'center' }}>
        <div style={{ flex: 1, minWidth: '220px' }}>
          <Input
            value={searchValue}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder={placeholder}
            icon={<span>🔍</span>}
          />
        </div>
        {filters}
        {(searchValue || onClearFilters) && (
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              onSearchChange('');
              if (onClearFilters) onClearFilters();
            }}
          >
            Clear Filters
          </Button>
        )}
      </div>

      {actions && <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>{actions}</div>}
    </div>
  );
};
