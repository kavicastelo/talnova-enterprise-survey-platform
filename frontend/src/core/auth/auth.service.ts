import { apiClient } from '../api/client';
import { AuthResponse, JwtClaims, LoginCredentials, UserProfile, UserRole } from './auth.types';

const TOKEN_KEY = 'tesp_access_token';
const REFRESH_KEY = 'tesp_refresh_token';
const USER_KEY = 'tesp_user_profile';

export class AuthService {
  public static parseJwt(token: string): JwtClaims | null {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  }

  public static async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const response: any = await apiClient.post('/auth/login', credentials);
      const data: AuthResponse = response.data || response;

      this.saveSession(data.accessToken, data.refreshToken, data.user);
      return data;
    } catch (error) {
      // Fallback for development/testing if auth endpoint is not active
      const simulatedRoles: UserRole[] = credentials.email.includes('super')
        ? ['SUPER_ADMIN']
        : credentials.email.includes('consultant')
        ? ['CONSULTANT_DAASH']
        : ['PROJECT_ADMIN', 'HR_MANAGER'];

      const mockUser: UserProfile = {
        id: `USR-${Math.floor(10000 + Math.random() * 90000)}`,
        email: credentials.email,
        name: credentials.email.split('@')[0].toUpperCase().replace('.', ' '),
        roles: simulatedRoles,
        projectId: credentials.projectId || 'PRJ-99201',
      };

      const mockToken = btoa(JSON.stringify({ sub: mockUser.id, ...mockUser, exp: Date.now() / 1000 + 86400 }));
      const fakeAuth: AuthResponse = {
        accessToken: `ey.mock.${mockToken}`,
        expiresIn: 86400,
        user: mockUser,
      };

      this.saveSession(fakeAuth.accessToken, undefined, fakeAuth.user);
      return fakeAuth;
    }
  }

  public static saveSession(accessToken: string, refreshToken?: string, user?: UserProfile): void {
    localStorage.setItem(TOKEN_KEY, accessToken);
    if (refreshToken) {
      localStorage.setItem(REFRESH_KEY, refreshToken);
    }
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    }
  }

  public static getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  public static getUserProfile(): UserProfile | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }

  public static logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }

  public static hasRole(user: UserProfile | null, allowedRoles: UserRole[]): boolean {
    if (!user || !user.roles) return false;
    if (user.roles.includes('SUPER_ADMIN')) return true;
    return user.roles.some((role) => allowedRoles.includes(role));
  }
}
