export type UserRole =
  | 'SUPER_ADMIN'
  | 'PROJECT_ADMIN'
  | 'HR_MANAGER'
  | 'EXECUTIVE'
  | 'BUSINESS_UNIT_HEAD'
  | 'DEPARTMENT_MANAGER'
  | 'CONSULTANT_DAASH'
  | 'VIEWER'
  | 'SURVEY_RESPONDENT';

export interface UserProfile {
  id: string;
  email: string;
  name: string;
  roles: UserRole[];
  projectId: string;
  nodeScope?: string;
  nodePath?: string;
  avatarUrl?: string;
}

export interface JwtClaims {
  sub: string;
  email: string;
  name?: string;
  roles: UserRole[];
  projectId: string;
  nodeScope?: string;
  nodePath?: string;
  exp: number;
  iat: number;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn: number;
  user: UserProfile;
}

export interface LoginCredentials {
  email: string;
  password?: string;
  projectId?: string;
}
