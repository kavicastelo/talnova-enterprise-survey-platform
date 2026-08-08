import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { UserRole } from '../../types/auth';

interface RoleGateProps {
  allowedRoles: UserRole[];
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const RoleGate: React.FC<RoleGateProps> = ({ allowedRoles, fallback, children }) => {
  const { hasAnyRole, isLoading } = useAuth();

  if (isLoading) return null;

  if (!hasAnyRole(allowedRoles)) {
    if (fallback !== undefined) {
      return <>{fallback}</>;
    }
    return <Navigate to="/access-denied" replace />;
  }

  return <>{children}</>;
};
