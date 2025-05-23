export interface LoginResponse {
  accessToken: string;
  accessTokenExpiresInSeconds: number;
  refreshToken: string;
  refreshTokenExpiresInSeconds: number;
  email: string;
  firstName: string;
  lastName: string;
};

export interface RefreshResponse {
  accessToken: string;
  accessTokenExpiresInSeconds: number;
  refreshToken: string;
  refreshTokenExpiresInSeconds: number;
  email: string;
  firstName: string;
  lastName: string;
};

export interface User {
  email: string;
  firstName: string;
  lastName: string;
};

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  accessTokenExpiresAt: number | null;
  refreshTokenExpiresAt: number | null;

  setAuthData: (
    accessToken: string, 
    refreshToken: string, 
    user: User,
    accessTokenExpiresInSeconds: number,
    refreshTokenExpiresInSeconds: number
  ) => void;
  clearAuth: () => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
};