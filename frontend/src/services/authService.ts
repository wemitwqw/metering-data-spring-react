import api from '../utils/axios';
import { useAuthStore } from '../stores/useAuthStore';
import { ERROR_MESSAGES } from '../utils/constants';
import { useMeteringPointsStore } from '../stores/useMeteringPointsStore';

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  email: string;
  firstName: string;
  lastName: string;
}

interface User {
  email: string;
  firstName: string;
  lastName: string;
}

class AuthService {
  async login(email: string, password: string): Promise<void> {
    const { setLoading, setError, setAuthData, clearError } = useAuthStore.getState();
    
    setLoading(true);
    clearError();
    
    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        email,
        password
      });
      
      const { accessToken, refreshToken, email: userEmail, firstName, lastName } = response.data;
      
      const user: User = {
        email: userEmail,
        firstName,
        lastName
      };
      
      setAuthData(accessToken, refreshToken, user);
      
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || ERROR_MESSAGES.LOGIN_FAILED;
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  }
  
  logout(): void {
    const { clearAuth } = useAuthStore.getState();
    const { reset } = useMeteringPointsStore.getState();
    reset();
    clearAuth();
  }
  
  getAccessToken(): string | null {
    return useAuthStore.getState().accessToken;
  }

  getRefreshToken(): string | null {
    return useAuthStore.getState().refreshToken;
  }
  
  isAuthenticated(): boolean {
    return useAuthStore.getState().isAuthenticated;
  }
}

export const authService = new AuthService();