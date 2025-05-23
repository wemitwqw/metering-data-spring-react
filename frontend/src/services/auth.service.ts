import api from '../utils/axios';
import { useAuthStore } from '../stores/useAuthStore';
import { ERROR_MESSAGES, API_ENDPOINTS } from '../utils/constants';
import { useMeteringPointsStore } from '../stores/useMeteringPointsStore';
import { LoginResponse, RefreshResponse, User } from 'src/types/auth.type';

class AuthService {
  private refreshPromise: Promise<void> | null = null;

  async login(email: string, password: string): Promise<void> {
    const { setLoading, setError, setAuthData, clearError } = useAuthStore.getState();
    
    setLoading(true);
    clearError();
    
    try {
      const response = await api.post<LoginResponse>(API_ENDPOINTS.LOGIN, {
        email,
        password
      });
      
      const { 
        accessToken, 
        accessTokenExpiresInSeconds,
        refreshToken, 
        refreshTokenExpiresInSeconds,
        email: userEmail, 
        firstName, 
        lastName 
      } = response.data;
      
      const user: User = {
        email: userEmail,
        firstName,
        lastName
      };
      
      setAuthData(
        accessToken, 
        refreshToken, 
        user, 
        accessTokenExpiresInSeconds,
        refreshTokenExpiresInSeconds
      );
      
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || ERROR_MESSAGES.LOGIN_FAILED;
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  async refreshAccessToken(): Promise<void> {
    if (this.refreshPromise) {
      return this.refreshPromise;
    }

    const { refreshToken } = useAuthStore.getState();
    
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    this.refreshPromise = this.performRefresh(refreshToken);
    
    try {
      await this.refreshPromise;
    } finally {
      this.refreshPromise = null;
    }
  }

  private async performRefresh(refreshToken: string): Promise<void> {
    try {
      const response = await api.post<RefreshResponse>(API_ENDPOINTS.REFRESH, {
        refreshToken
      });

      const { 
        accessToken, 
        accessTokenExpiresInSeconds,
        refreshToken: newRefreshToken, 
        refreshTokenExpiresInSeconds,
        email, 
        firstName, 
        lastName 
      } = response.data;

      const user: User = {
        email,
        firstName,
        lastName
      };

      useAuthStore.getState().setAuthData(
        accessToken, 
        newRefreshToken, 
        user, 
        accessTokenExpiresInSeconds,
        refreshTokenExpiresInSeconds
      );

    } catch (error: any) {
      useAuthStore.getState().setError(ERROR_MESSAGES.TOKEN_REFRESH_FAILED);
      useAuthStore.getState().clearAuth();
      throw error;
    }
  }
  
  logout(): void {
    const { clearAuth } = useAuthStore.getState();
    const { reset } = useMeteringPointsStore.getState();
    reset();
    clearAuth();
  }
}

export const authService = new AuthService();