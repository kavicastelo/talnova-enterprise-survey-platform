export interface Branding {
  companyName: string;
  logoUrl?: string;
  primaryColor: string;
  secondaryColor: string;
  customCssUrl?: string;
}

export interface FeatureFlags {
  aiAnalyticsEnabled: boolean;
  actionPlanningEnabled: boolean;
  kioskModeEnabled: boolean;
  smsDistributionEnabled: boolean;
}

export interface CustomAttributeDefinition {
  key: string;
  displayName: string;
  dataType: 'STRING' | 'NUMERIC' | 'ENUM' | 'DATE';
  allowedValues?: string[];
}

export interface ProjectCreateRequest {
  projectId: string;
  name: string;
  branding: Branding;
  supportedLocales: string[];
  defaultLocale: string;
  features: FeatureFlags;
  customAttributeDefinitions?: CustomAttributeDefinition[];
}

export interface ProjectResponse {
  id: string;
  projectId: string;
  name: string;
  status: 'DRAFT' | 'ACTIVE' | 'SUSPENDED' | 'ARCHIVED';
  branding: Branding;
  supportedLocales: string[];
  defaultLocale: string;
  features: FeatureFlags;
  customAttributeDefinitions: CustomAttributeDefinition[];
  version: number;
  createdAt: string;
  updatedAt: string;
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
  wcagLevel: 'AAA' | 'AA' | 'FAIL';
  recommendation: string;
}
