import React from 'react';

export interface TabItem {
  id: string;
  label: string;
  badge?: React.ReactNode;
  icon?: React.ReactNode;
  disabled?: boolean;
}

export interface TabsProps {
  tabs: TabItem[];
  activeTab: string;
  onChange: (tabId: string) => void;
  variant?: 'underline' | 'pills';
}

export const Tabs: React.FC<TabsProps> = ({ tabs, activeTab, onChange, variant = 'underline' }) => {
  return (
    <div
      style={{
        display: 'flex',
        gap: variant === 'pills' ? '8px' : '24px',
        borderBottom: variant === 'underline' ? '1px solid #e2e8f0' : 'none',
        paddingBottom: variant === 'underline' ? '0' : '4px',
      }}
    >
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;

        return (
          <button
            key={tab.id}
            disabled={tab.disabled}
            onClick={() => !tab.disabled && onChange(tab.id)}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px',
              padding: variant === 'pills' ? '8px 16px' : '12px 4px',
              fontSize: '0.875rem',
              fontWeight: isActive ? 700 : 500,
              color: isActive ? '#1e3a8a' : '#64748b',
              background: variant === 'pills' ? (isActive ? '#eff6ff' : 'transparent') : 'transparent',
              borderRadius: variant === 'pills' ? '8px' : '0',
              border: 'none',
              borderBottom: variant === 'underline' ? (isActive ? '2px solid #1e3a8a' : '2px solid transparent') : 'none',
              cursor: tab.disabled ? 'not-allowed' : 'pointer',
              opacity: tab.disabled ? 0.5 : 1,
              transition: 'all 0.15s ease-in-out',
            }}
          >
            {tab.icon}
            {tab.label}
            {tab.badge}
          </button>
        );
      })}
    </div>
  );
};
