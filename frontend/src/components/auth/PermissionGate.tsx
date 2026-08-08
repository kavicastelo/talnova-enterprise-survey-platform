import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { UserRole } from '../../types/auth';

interface PermissionGateProps {
  requiredRole?: UserRole;
  allowedRoles?: UserRole[];
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const PermissionGate: React.FC<PermissionGateProps> = ({ requiredRole, allowedRoles, fallback = null, children }) => {
  const { hasRole, hasAnyRole } = useAuth();

  if (requiredRole && !hasRole(requiredRole)) {
    return <>{fallback}</>;
  }

  if (allowedRoles && !hasAnyRole(allowedRoles)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
};
