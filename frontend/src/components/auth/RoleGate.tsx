import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { UserRole } from '../../core/auth/auth.types';

interface RoleGateProps {
  allowedRoles: UserRole[];
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const RoleGate: React.FC<RoleGateProps> = ({ allowedRoles, fallback, children }) => {
  const { hasRole, isLoading } = useAuth();

  if (isLoading) return null;

  if (!hasRole(allowedRoles)) {
    if (fallback !== undefined) {
      return <>{fallback}</>;
    }
    return <Navigate to="/access-denied" replace />;
  }

  return <>{children}</>;
};
