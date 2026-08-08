export type UserRole =
  | 'SUPER_ADMIN'
  | 'PROJECT_ADMIN'
  | 'HR_MANAGER'
  | 'DEPARTMENT_MANAGER'
  | 'CONSULTANT_DAASH'
  | 'SURVEY_RESPONDENT';

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  department?: string;
  nodeId?: string;
  projectId?: string;
  avatarUrl?: string;
}

export interface AuthState {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export interface LoginResponse {
  token: string;
  user: UserProfile;
}
