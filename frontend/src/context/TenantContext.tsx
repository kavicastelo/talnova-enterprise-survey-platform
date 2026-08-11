import React, { createContext, useContext, useEffect, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { setProjectIdGetter } from '../core/api/client';
import { Branding, FeatureFlags, ProjectTenant } from '../types/projectConfig';

export type { FeatureFlags, Branding, ProjectTenant };

export type TenantBranding = Branding;
export type ProjectInfo = ProjectTenant;

interface TenantContextType {
  activeProjectId: string;
  activeProject: ProjectTenant;
  projects: ProjectTenant[];
  projectsList: ProjectTenant[];
  branding: Branding;
  featureFlags: FeatureFlags;
  switchProject: (projectId: string) => void;
  updateBranding: (branding: Partial<Branding>) => void;
  updateFeatureFlags: (flags: Partial<FeatureFlags>) => void;
  isLoadingProjects: boolean;
}

const DEFAULT_BRANDING: Branding = {
  companyName: 'Aitken Spence PLC',
  primaryColor: '#4f46e5',
  secondaryColor: '#0284c7',
  fontFamily: 'Inter',
};

const DEFAULT_FLAGS: FeatureFlags = {
  aiAnalyticsEnabled: true,
  actionPlanningEnabled: true,
  kioskModeEnabled: true,
  hrisSyncEnabled: true,
  gdprAnonymizationEnabled: true,
  smsDistributionEnabled: true,
  emailDistributionEnabled: true,
  teamsDistributionEnabled: true,
  slackDistributionEnabled: true,
};

const DEFAULT_PROJECTS: ProjectTenant[] = [
  {
    id: 'PRJ-99201',
    projectId: 'PRJ-99201',
    name: 'Aitken Spence Enterprise Portal',
    projectName: 'Aitken Spence Enterprise Portal',
    code: 'ASP-ENT',
    status: 'ACTIVE',
    branding: DEFAULT_BRANDING,
    features: DEFAULT_FLAGS,
    supportedLocales: ['en-US', 'si-LK', 'ta-LK'],
    defaultLocale: 'en-US',
  },
  {
    id: 'PRJ-88102',
    projectId: 'PRJ-88102',
    name: 'Talnova Global Workforce',
    projectName: 'Talnova Global Workforce',
    code: 'TAL-GLB',
    status: 'ACTIVE',
    branding: DEFAULT_BRANDING,
    features: DEFAULT_FLAGS,
    supportedLocales: ['en-US'],
    defaultLocale: 'en-US',
  },
  {
    id: 'PRJ-77303',
    projectId: 'PRJ-77303',
    name: 'Asia Telecom Operations',
    projectName: 'Asia Telecom Operations',
    code: 'ATO-OPS',
    status: 'PROVISIONING',
    branding: DEFAULT_BRANDING,
    features: DEFAULT_FLAGS,
    supportedLocales: ['en-US'],
    defaultLocale: 'en-US',
  },
];

const TenantContext = createContext<TenantContextType | undefined>(undefined);

export const TenantProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = useQueryClient();
  const [activeProjectId, setActiveProjectId] = useState<string>(() => {
    return localStorage.getItem('tesp_project_id') || 'PRJ-99201';
  });
  const [projects] = useState<ProjectTenant[]>(DEFAULT_PROJECTS);
  const [branding, setBranding] = useState<Branding>(DEFAULT_BRANDING);
  const [featureFlags, setFeatureFlags] = useState<FeatureFlags>(DEFAULT_FLAGS);
  const [isLoadingProjects] = useState<boolean>(false);

  const activeProject = projects.find((p) => p.projectId === activeProjectId) || projects[0];

  useEffect(() => {
    setProjectIdGetter(() => activeProjectId);
    localStorage.setItem('tesp_project_id', activeProjectId);
  }, [activeProjectId]);

  const switchProject = (projectId: string) => {
    if (projectId === activeProjectId) return;
    setActiveProjectId(projectId);
    localStorage.setItem('tesp_project_id', projectId);
    queryClient.invalidateQueries();
  };

  const updateBranding = (newBranding: Partial<Branding>) => {
    setBranding((prev) => ({ ...prev, ...newBranding }));
  };

  const updateFeatureFlags = (newFlags: Partial<FeatureFlags>) => {
    setFeatureFlags((prev) => ({ ...prev, ...newFlags }));
  };

  return (
    <TenantContext.Provider
      value={{
        activeProjectId,
        activeProject,
        projects,
        projectsList: projects,
        branding,
        featureFlags,
        switchProject,
        updateBranding,
        updateFeatureFlags,
        isLoadingProjects,
      }}
    >
      {children}
    </TenantContext.Provider>
  );
};

export const useTenant = (): TenantContextType => {
  const context = useContext(TenantContext);
  if (!context) {
    throw new Error('useTenant must be used within a TenantProvider');
  }
  return context;
};
