import React from 'react';

export interface CardProps {
  title?: React.ReactNode;
  subtitle?: string;
  extra?: React.ReactNode;
  action?: React.ReactNode;
  variant?: string;
  padding?: string;
  style?: React.CSSProperties;
  children: React.ReactNode;
  className?: string;
  headerClassName?: string;
  bodyClassName?: string;
}

export const Card: React.FC<CardProps> = ({
  title,
  subtitle,
  extra,
  action,
  variant: _variant,
  padding,
  style,
  children,
  className = '',
  headerClassName = '',
  bodyClassName = '',
}) => {
  const topExtra = extra || action;

  return (
    <div
      style={style}
      className={`bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden ${className}`}
    >
      {(title || topExtra) && (
        <div className={`px-5 py-4 border-b border-slate-200 flex items-center justify-between ${headerClassName}`}>
          <div>
            {typeof title === 'string' ? (
              <h3 className="text-base font-semibold text-slate-900">{title}</h3>
            ) : (
              title
            )}
            {subtitle && <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>}
          </div>
          {topExtra && <div>{topExtra}</div>}
        </div>
      )}
      <div className={`p-5 ${bodyClassName}`} style={padding ? { padding } : undefined}>
        {children}
      </div>
    </div>
  );
};
