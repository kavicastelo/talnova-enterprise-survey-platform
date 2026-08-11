import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, Mail, Shield } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { Select } from '../../components/ui/Select';
import { Button } from '../../components/ui/Button';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@aitkenspence.lk');
  const [password, setPassword] = useState('password123');
  const [projectId, setProjectId] = useState('PRJ-99201');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await login({ email, password, projectId });
      toast.success('Authentication successful', `Welcome back, ${email}`);

      if (email.includes('super')) {
        navigate('/super-admin/dashboard');
      } else if (email.includes('consultant')) {
        navigate('/consultant/dashboard');
      } else {
        navigate('/dashboard');
      }
    } catch (err: any) {
      toast.error('Authentication failed', err.message || 'Invalid credentials');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Card className="max-w-md w-full border-slate-200 bg-white shadow-xl p-6 sm:p-8">
      <div className="text-center mb-6">
        <div className="w-12 h-12 rounded-xl bg-indigo-600 border border-indigo-400/40 flex items-center justify-center mx-auto mb-3 font-bold text-white text-xl shadow-lg">
          T
        </div>
        <h2 className="text-xl font-bold text-slate-900 tracking-tight">
          TESP Platform Sign In
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Enterprise Survey Platform Single Sign-On
        </p>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <Input
          label="Corporate Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          leftIcon={<Mail className="w-4 h-4" />}
          required
        />

        <Input
          label="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          leftIcon={<Lock className="w-4 h-4" />}
          required
        />

        <Select
          label="Tenant Project Scope"
          value={projectId}
          onChange={(e) => setProjectId(e.target.value)}
          options={[
            { value: 'PRJ-99201', label: 'PRJ-99201 — Aitken Spence Enterprise Portal' },
            { value: 'PRJ-88102', label: 'PRJ-88102 — Talnova Global Workforce' },
            { value: 'PRJ-77303', label: 'PRJ-77303 — Asia Telecom Operations' },
          ]}
        />

        <div className="pt-2">
          <Button
            type="submit"
            isLoading={isSubmitting}
            className="w-full"
            leftIcon={<Shield className="w-4 h-4" />}
          >
            Authenticate & Access Platform
          </Button>
        </div>
      </form>

      <div className="mt-6 pt-4 border-t border-slate-200 text-center">
        <p className="text-[11px] text-slate-500 font-mono">
          API Gateway Ingress: <span className="text-indigo-600 font-semibold">http://localhost:8080/api/v1</span>
        </p>
      </div>
    </Card>
  );
};
