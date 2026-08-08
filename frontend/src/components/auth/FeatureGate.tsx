import React from 'react';
import { useTenant } from '../../context/TenantContext';
import { FeatureFlags } from '../../types/projectConfig';

interface FeatureGateProps {
  flag: keyof FeatureFlags;
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const FeatureGate: React.FC<FeatureGateProps> = ({ flag, fallback = null, children }) => {
  const { featureFlags } = useTenant();

  if (!featureFlags || !featureFlags[flag]) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
};
