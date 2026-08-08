import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { UserProfile, UserRole } from '../types/auth';

interface AuthContextValue {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, role?: UserRole) => Promise<void>;
  logout: () => void;
  hasRole: (role: UserRole) => boolean;
  hasAnyRole: (roles: UserRole[]) => boolean;
}

const DEFAULT_USER: UserProfile = {
  id: 'USR-ADMIN-88',
  email: 'admin@aitkenspence.lk',
  fullName: 'Alexander Aitken (Platform Admin)',
  role: 'SUPER_ADMIN',
  department: 'Executive Office',
  projectId: 'PRJ-99201',
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('tesp_auth_token');
    const storedUserJson = localStorage.getItem('tesp_user_profile');

    if (storedToken && storedUserJson) {
      try {
        const parsedUser = JSON.parse(storedUserJson) as UserProfile;
        setUser(parsedUser);
        setToken(storedToken);
      } catch (err) {
        console.error('Failed to parse stored user profile session', err);
        localStorage.removeItem('tesp_auth_token');
        localStorage.removeItem('tesp_user_profile');
      }
    } else {
      // Default to SUPER_ADMIN session for development ease
      const mockToken = 'JWT-DEMO-TOKEN-SUPER-ADMIN-9901';
      setUser(DEFAULT_USER);
      setToken(mockToken);
      localStorage.setItem('tesp_auth_token', mockToken);
      localStorage.setItem('tesp_user_id', DEFAULT_USER.id);
      localStorage.setItem('tesp_user_roles', DEFAULT_USER.role);
      localStorage.setItem('tesp_user_profile', JSON.stringify(DEFAULT_USER));
    }
    setIsLoading(false);
  }, []);

  const login = useCallback(async (email: string, role: UserRole = 'PROJECT_ADMIN') => {
    setIsLoading(true);
    const mockUser: UserProfile = {
      id: `USR-${Math.floor(Math.random() * 9000 + 1000)}`,
      email,
      fullName: email.split('@')[0].replace('.', ' ').toUpperCase(),
      role,
      department: 'Corporate Strategy',
      projectId: localStorage.getItem('tesp_project_id') || 'PRJ-99201',
    };
    const mockToken = `JWT-${role}-${Date.now()}`;

    setUser(mockUser);
    setToken(mockToken);
    localStorage.setItem('tesp_auth_token', mockToken);
    localStorage.setItem('tesp_user_id', mockUser.id);
    localStorage.setItem('tesp_user_roles', mockUser.role);
    localStorage.setItem('tesp_user_profile', JSON.stringify(mockUser));
    setIsLoading(false);
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('tesp_auth_token');
    localStorage.removeItem('tesp_user_id');
    localStorage.removeItem('tesp_user_roles');
    localStorage.removeItem('tesp_user_profile');
  }, []);

  const hasRole = useCallback(
    (role: UserRole): boolean => {
      if (!user) return false;
      if (user.role === 'SUPER_ADMIN') return true;
      return user.role === role;
    },
    [user]
  );

  const hasAnyRole = useCallback(
    (roles: UserRole[]): boolean => {
      if (!user) return false;
      if (user.role === 'SUPER_ADMIN') return true;
      return roles.includes(user.role);
    },
    [user]
  );

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        isLoading,
        login,
        logout,
        hasRole,
        hasAnyRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextValue => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
