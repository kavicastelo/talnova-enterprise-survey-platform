import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { ProjectTenant } from '../types/tenant';
import { Branding, FeatureFlags } from '../types/projectConfig';

interface TenantContextValue {
  activeProject: ProjectTenant | null;
  projectsList: ProjectTenant[];
  branding: Branding;
  featureFlags: FeatureFlags;
  isLoading: boolean;
  switchProject: (projectId: string) => Promise<void>;
  updateBranding: (branding: Branding) => void;
  updateFeatureFlags: (flags: FeatureFlags) => void;
}

const DEFAULT_PROJECTS: ProjectTenant[] = [
  {
    projectId: 'PRJ-99201',
    projectName: 'Aitken Spence Enterprise Survey 2026',
    description: 'Group-wide annual employee engagement and culture audit.',
    status: 'ACTIVE',
    branding: {
      companyName: 'Aitken Spence PLC',
      logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png',
      primaryColor: '#1E3A8A',
      secondaryColor: '#3B82F6',
    },
    features: {
      aiAnalyticsEnabled: true,
      actionPlanningEnabled: true,
      kioskModeEnabled: false,
      smsDistributionEnabled: true,
    },
    supportedLocales: ['en-US', 'si-LK', 'ta-LK'],
    defaultLocale: 'en-US',
  },
  {
    projectId: 'PRJ-88102',
    projectName: 'Commercial Bank Pulse Audit Q3',
    description: 'Quarterly pulse survey for digital transformation readiness.',
    status: 'ACTIVE',
    branding: {
      companyName: 'Commercial Bank PLC',
      logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-88102/logo.png',
      primaryColor: '#065F46',
      secondaryColor: '#10B981',
    },
    features: {
      aiAnalyticsEnabled: true,
      actionPlanningEnabled: false,
      kioskModeEnabled: true,
      smsDistributionEnabled: false,
    },
    supportedLocales: ['en-US', 'si-LK'],
    defaultLocale: 'en-US',
  },
  {
    projectId: 'PRJ-77403',
    projectName: 'Dilmah Tea Global Leadership Survey',
    description: 'Leadership 360-degree feedback and alignment survey.',
    status: 'ACTIVE',
    branding: {
      companyName: 'Dilmah Ceylon Tea Company',
      logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-77403/logo.png',
      primaryColor: '#7C2D12',
      secondaryColor: '#F97316',
    },
    features: {
      aiAnalyticsEnabled: false,
      actionPlanningEnabled: true,
      kioskModeEnabled: false,
      smsDistributionEnabled: true,
    },
    supportedLocales: ['en-US'],
    defaultLocale: 'en-US',
  },
];

const TenantContext = createContext<TenantContextValue | undefined>(undefined);

export const TenantProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = useQueryClient();
  const [projectsList] = useState<ProjectTenant[]>(DEFAULT_PROJECTS);
  const [activeProject, setActiveProject] = useState<ProjectTenant | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const applyThemeBranding = (branding: Branding) => {
    document.documentElement.style.setProperty('--tesp-primary-color', branding.primaryColor);
    document.documentElement.style.setProperty('--tesp-secondary-color', branding.secondaryColor);
  };

  useEffect(() => {
    const savedProjectId = localStorage.getItem('tesp_project_id') || 'PRJ-99201';
    const matched = projectsList.find((p) => p.projectId === savedProjectId) || projectsList[0];

    setActiveProject(matched);
    applyThemeBranding(matched.branding);
    localStorage.setItem('tesp_project_id', matched.projectId);
    setIsLoading(false);
  }, [projectsList]);

  const switchProject = useCallback(
    async (projectId: string) => {
      setIsLoading(true);
      const target = projectsList.find((p) => p.projectId === projectId);
      if (!target) {
        setIsLoading(false);
        throw new Error(`Project with ID ${projectId} not found`);
      }

      // CRITICAL STEP: Cancel in-flight queries and clear query cache to prevent cross-tenant stale data
      await queryClient.cancelQueries();
      queryClient.clear();

      setActiveProject(target);
      applyThemeBranding(target.branding);
      localStorage.setItem('tesp_project_id', target.projectId);
      setIsLoading(false);
    },
    [projectsList, queryClient]
  );

  const updateBranding = useCallback((branding: Branding) => {
    setActiveProject((prev) => {
      if (!prev) return null;
      const updated = { ...prev, branding };
      applyThemeBranding(branding);
      return updated;
    });
  }, []);

  const updateFeatureFlags = useCallback((features: FeatureFlags) => {
    setActiveProject((prev) => {
      if (!prev) return null;
      return { ...prev, features };
    });
  }, []);

  const branding = activeProject?.branding || DEFAULT_PROJECTS[0].branding;
  const featureFlags = activeProject?.features || DEFAULT_PROJECTS[0].features;

  return (
    <TenantContext.Provider
      value={{
        activeProject,
        projectsList,
        branding,
        featureFlags,
        isLoading,
        switchProject,
        updateBranding,
        updateFeatureFlags,
      }}
    >
      {children}
    </TenantContext.Provider>
  );
};

export const useTenant = (): TenantContextValue => {
  const context = useContext(TenantContext);
  if (!context) {
    throw new Error('useTenant must be used within a TenantProvider');
  }
  return context;
};
