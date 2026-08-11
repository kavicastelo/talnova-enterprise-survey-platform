import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { UserRole } from '../../core/auth/auth.types';

interface PermissionGateProps {
  requiredRole?: UserRole;
  allowedRoles?: UserRole[];
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const PermissionGate: React.FC<PermissionGateProps> = ({ requiredRole, allowedRoles, fallback = null, children }) => {
  const { hasRole } = useAuth();

  if (requiredRole && !hasRole([requiredRole])) {
    return <>{fallback}</>;
  }

  if (allowedRoles && !hasRole(allowedRoles)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
};
