export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'SUSPENDED' | 'ARCHIVED' | 'PROVISIONING';

export type DataType = 'STRING' | 'NUMERIC' | 'ENUM' | 'DATE';

export interface Branding {
  companyName?: string;
  logoUrl?: string;
  primaryColor: string;
  secondaryColor: string;
  fontFamily?: string;
  customCssUrl?: string;
}

export interface FeatureFlags {
  aiAnalyticsEnabled: boolean;
  actionPlanningEnabled: boolean;
  kioskModeEnabled: boolean;
  smsDistributionEnabled?: boolean;
  emailDistributionEnabled?: boolean;
  teamsDistributionEnabled?: boolean;
  slackDistributionEnabled?: boolean;
  hrisSyncEnabled?: boolean;
  gdprAnonymizationEnabled?: boolean;
}

export interface CustomAttributeDef {
  key: string;
  displayName: string;
  dataType: DataType;
  allowedValues?: string[];
}

export interface ProjectTenant {
  id?: string;
  projectId: string;
  name: string;
  projectName: string;
  description?: string;
  status: 'ACTIVE' | 'ARCHIVED' | 'PROVISIONING';
  branding: Branding;
  supportedLocales: string[];
  defaultLocale: string;
  features: FeatureFlags;
  customAttributeDefinitions?: CustomAttributeDef[];
  code?: string;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProjectCreateRequest {
  projectId: string;
  name: string;
  branding: Branding;
  supportedLocales: string[];
  defaultLocale: string;
  features?: FeatureFlags;
  customAttributeDefinitions?: CustomAttributeDef[];
}

export interface ProjectResponse {
  id?: string;
  projectId: string;
  name: string;
  status: ProjectStatus;
  branding: Branding;
  supportedLocales: string[];
  defaultLocale: string;
  features: FeatureFlags;
  customAttributeDefinitions?: CustomAttributeDef[];
  version?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface PublicTheme {
  projectId: string;
  companyName: string;
  logoUrl?: string;
  primaryColor: string;
  secondaryColor: string;
  customCssUrl?: string;
  defaultLocale: string;
}

export interface ContrastValidationRequest {
  primaryColor: string;
  backgroundColor: string;
}

export interface ContrastValidationResponse {
  passed: boolean;
  contrastRatio: number;
  wcagLevel: 'FAIL' | 'AA' | 'AAA';
  recommendation: string;
}
