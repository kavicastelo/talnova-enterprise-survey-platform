import React from 'react';
import { Outlet } from 'react-router-dom';

export const AuthLayout: React.FC = () => {
  return (
    <div
      style={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        display: 'grid',
        placeItems: 'center',
        padding: '24px',
        fontFamily: "'Inter', system-ui, sans-serif",
      }}
    >
      <div style={{ width: '100%', maxWidth: '440px' }}>
        <Outlet />
      </div>
    </div>
  );
};
