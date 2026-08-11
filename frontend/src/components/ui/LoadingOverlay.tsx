import React from 'react';
import { Loader2 } from 'lucide-react';

export interface LoadingOverlayProps {
  message?: string;
  fullPage?: boolean;
}

export const LoadingOverlay: React.FC<LoadingOverlayProps> = ({
  message = 'Loading data...',
  fullPage = false,
}) => {
  const content = (
    <div className="flex flex-col items-center justify-center p-6 gap-3 text-slate-700">
      <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
      <span className="text-sm font-medium tracking-wide">{message}</span>
    </div>
  );

  if (fullPage) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-white/80 backdrop-blur-sm">
        {content}
      </div>
    );
  }

  return <div className="w-full flex items-center justify-center p-12">{content}</div>;
};
