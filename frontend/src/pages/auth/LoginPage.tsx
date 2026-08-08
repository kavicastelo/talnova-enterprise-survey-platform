import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { UserRole } from '../../types/auth';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { Select } from '../../components/ui/Select';
import { Button } from '../../components/ui/Button';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@aitkenspence.lk');
  const [role, setRole] = useState<UserRole>('SUPER_ADMIN');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    await login(email, role);
    setIsSubmitting(false);
    navigate('/dashboard');
  };

  return (
    <Card variant="bordered" padding="32px">
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            background: 'linear-gradient(135deg, #3b82f6, #1d4ed8)',
            display: 'grid',
            placeItems: 'center',
            margin: '0 auto 12px auto',
            fontWeight: 800,
            color: '#ffffff',
            fontSize: '1.4rem',
          }}
        >
          T
        </div>
        <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          TESP Platform Sign In
        </h2>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Select user identity and role context for API Gateway testing
        </p>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <Input
          label="Corporate Email Address"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <Select
          label="Role & RBAC Security Context"
          value={role}
          onChange={(e) => setRole(e.target.value as UserRole)}
          options={[
            { value: 'SUPER_ADMIN', label: 'SUPER_ADMIN — Platform Super Administrator' },
            { value: 'PROJECT_ADMIN', label: 'PROJECT_ADMIN — Tenant Project Admin' },
            { value: 'HR_MANAGER', label: 'HR_MANAGER — Human Resources Manager' },
            { value: 'DEPARTMENT_MANAGER', label: 'DEPARTMENT_MANAGER — Department Line Manager' },
            { value: 'CONSULTANT_DAASH', label: 'CONSULTANT_DAASH — Third-Party Analyst' },
            { value: 'SURVEY_RESPONDENT', label: 'SURVEY_RESPONDENT — Survey Participant' },
          ]}
        />

        <Button type="submit" isLoading={isSubmitting} style={{ marginTop: '8px' }}>
          Sign In to Platform
        </Button>
      </form>
    </Card>
  );
};
