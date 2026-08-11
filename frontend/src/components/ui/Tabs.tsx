import React from 'react';

export interface TabItem {
  id?: string;
  key?: string;
  label: React.ReactNode;
  icon?: React.ReactNode;
  badge?: React.ReactNode;
}

export interface TabsProps {
  items?: TabItem[];
  tabs?: TabItem[];
  activeKey?: string;
  activeTab?: string;
  onChange: (key: string) => void;
  className?: string;
}

export const Tabs: React.FC<TabsProps> = ({
  items,
  tabs,
  activeKey,
  activeTab,
  onChange,
  className = '',
}) => {
  const tabList = items || tabs || [];
  const currentKey = activeKey || activeTab || '';

  return (
    <div className={`border-b border-slate-200 flex items-center gap-1 overflow-x-auto ${className}`}>
      {tabList.map((tab) => {
        const key = tab.key || tab.id || String(tab.label);
        const isActive = key === currentKey;
        return (
          <button
            key={key}
            onClick={() => onChange(key)}
            className={`flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 whitespace-nowrap focus:outline-none ${
              isActive
                ? 'border-indigo-600 text-indigo-600 font-semibold'
                : 'border-transparent text-slate-500 hover:text-slate-900 hover:border-slate-300'
            }`}
          >
            {tab.icon}
            <span>{tab.label}</span>
            {tab.badge}
          </button>
        );
      })}
    </div>
  );
};
