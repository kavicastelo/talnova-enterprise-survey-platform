import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';

export const AccessDeniedPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: '64px 24px', textAlign: 'center', maxWidth: '500px', margin: '0 auto' }}>
      <div style={{ fontSize: '3.5rem', marginBottom: '16px' }}>🔒</div>
      <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0f172a', margin: '0 0 8px 0' }}>
        Access Denied (403 Forbidden)
      </h2>
      <p style={{ fontSize: '0.9rem', color: '#64748b', margin: '0 0 24px 0', lineHeight: 1.5 }}>
        Your user role capability context does not possess permission to access this domain resource or administrative view.
      </p>
      <Button variant="primary" onClick={() => navigate('/dashboard')}>
        Return to Executive Dashboard
      </Button>
    </div>
  );
};
