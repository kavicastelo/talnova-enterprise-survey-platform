import { Branding, FeatureFlags } from './projectConfig';

export interface ProjectTenant {
  projectId: string;
  projectName: string;
  description?: string;
  status: 'ACTIVE' | 'ARCHIVED' | 'PROVISIONING';
  branding: Branding;
  features: FeatureFlags;
  supportedLocales: string[];
  defaultLocale: string;
}

export interface TenantState {
  activeProject: ProjectTenant | null;
  projectsList: ProjectTenant[];
  isLoading: boolean;
}
