import React, { createContext, useContext, useEffect, useState } from 'react';
import { AuthService } from '../core/auth/auth.service';
import { LoginCredentials, UserProfile, UserRole } from '../core/auth/auth.types';
import { setAuthTokenGetter } from '../core/api/client';

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
  hasRole: (roles: UserRole[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    // Register token getter for API client
    setAuthTokenGetter(() => AuthService.getAccessToken());

    // Restore session
    const savedUser = AuthService.getUserProfile();
    const token = AuthService.getAccessToken();

    if (savedUser && token) {
      setUser(savedUser);
    }
    setIsLoading(false);

    // Listen for unauthorized events
    const handleUnauthorized = () => {
      logout();
    };
    window.addEventListener('tesp:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('tesp:unauthorized', handleUnauthorized);
  }, []);

  const login = async (credentials: LoginCredentials) => {
    setIsLoading(true);
    try {
      const auth = await AuthService.login(credentials);
      setUser(auth.user);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    AuthService.logout();
    setUser(null);
  };

  const hasRole = (roles: UserRole[]): boolean => {
    return AuthService.hasRole(user, roles);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
