import api from '../../utils/axios';
import { authService } from '../auth.service';
import { useAuthStore } from '../../stores/useAuthStore';
import { useMeteringPointsStore } from '../../stores/useMeteringPointsStore';
import { API_ENDPOINTS, ERROR_MESSAGES } from '../../utils/constants';

jest.mock('../../utils/axios', () => ({
  post: jest.fn(),
}));

jest.mock('../../stores/useAuthStore', () => ({
  useAuthStore: {
    getState: jest.fn(),
  },
}));

jest.mock('../../stores/useMeteringPointsStore', () => ({
  useMeteringPointsStore: {
    getState: jest.fn(),
  },
}));

describe('AuthService', () => {
  const mockSetLoading = jest.fn();
  const mockSetError = jest.fn();
  const mockSetAuthData = jest.fn();
  const mockClearError = jest.fn();
  const mockClearAuth = jest.fn();
  const mockReset = jest.fn();
  
  const mockAccessToken = 'access-token-123';
  const mockRefreshToken = 'refresh-token-456';
  const mockUser = {
    email: 'test@example.com',
    firstName: 'Test',
    lastName: 'User'
  };

  beforeEach(() => {
    jest.clearAllMocks();
    
    (useAuthStore.getState as jest.Mock).mockReturnValue({
      setLoading: mockSetLoading,
      setError: mockSetError,
      setAuthData: mockSetAuthData,
      clearError: mockClearError,
      clearAuth: mockClearAuth,
      accessToken: mockAccessToken,
      refreshToken: mockRefreshToken,
      isAuthenticated: true
    });
    
    (useMeteringPointsStore.getState as jest.Mock).mockReturnValue({
      reset: mockReset,
    });
  });

  describe('login', () => {
    it('should successfully login and set auth data with tokens and expiry times', async () => {
      const email = 'test@example.com';
      const password = 'password123';
      
      const mockResponse = {
        data: {
          accessToken: mockAccessToken,
          accessTokenExpiresInSeconds: 3600,
          refreshToken: mockRefreshToken,
          refreshTokenExpiresInSeconds: 86400,
          email: mockUser.email,
          firstName: mockUser.firstName,
          lastName: mockUser.lastName
        }
      };
      
      (api.post as jest.Mock).mockResolvedValueOnce(mockResponse);

      await authService.login(email, password);

      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.post).toHaveBeenCalledWith(API_ENDPOINTS.LOGIN, { email, password });
      expect(mockSetAuthData).toHaveBeenCalledWith(
        mockAccessToken, 
        mockRefreshToken, 
        mockUser,
        3600,
        86400
      );
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
    
    it('should handle login failure and set error message', async () => {
      const email = 'test@example.com';
      const password = 'wrong-password';
      
      const mockError = {
        response: {
          data: {
            message: 'Invalid credentials'
          }
        }
      };
      
      (api.post as jest.Mock).mockRejectedValueOnce(mockError);

      await expect(authService.login(email, password)).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.post).toHaveBeenCalledWith(API_ENDPOINTS.LOGIN, { email, password });
      expect(mockSetError).toHaveBeenCalledWith('Invalid credentials');
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
    
    it('should use default error message when response has no message', async () => {
      const email = 'test@example.com';
      const password = 'wrong-password';
      
      const mockError = { response: {} };
      
      (api.post as jest.Mock).mockRejectedValueOnce(mockError);
      
      await expect(authService.login(email, password)).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.post).toHaveBeenCalledWith(API_ENDPOINTS.LOGIN, { email, password });
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.LOGIN_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });

    it('should handle network error without response object', async () => {
      const email = 'test@example.com';
      const password = 'password123';
      
      const mockError = new Error('Network Error');
      
      (api.post as jest.Mock).mockRejectedValueOnce(mockError);
      
      await expect(authService.login(email, password)).rejects.toEqual(mockError);
      
      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.LOGIN_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
  });

  describe('refreshAccessToken', () => {
    it('should successfully refresh access token', async () => {
      const mockRefreshResponse = {
        data: {
          accessToken: 'new-access-token',
          accessTokenExpiresInSeconds: 3600,
          refreshToken: 'new-refresh-token',
          refreshTokenExpiresInSeconds: 86400,
          email: mockUser.email,
          firstName: mockUser.firstName,
          lastName: mockUser.lastName
        }
      };

      (api.post as jest.Mock).mockResolvedValueOnce(mockRefreshResponse);

      await authService.refreshAccessToken();

      expect(api.post).toHaveBeenCalledWith(API_ENDPOINTS.REFRESH, {
        refreshToken: mockRefreshToken
      });
      expect(mockSetAuthData).toHaveBeenCalledWith(
        'new-access-token',
        'new-refresh-token',
        mockUser,
        3600,
        86400
      );
    });

    it('should handle refresh failure and clear auth', async () => {
      const mockError = {
        response: {
          data: {
            message: 'Invalid refresh token'
          }
        }
      };

      (api.post as jest.Mock).mockRejectedValueOnce(mockError);

      await expect(authService.refreshAccessToken()).rejects.toEqual(mockError);

      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.TOKEN_REFRESH_FAILED);
      expect(mockClearAuth).toHaveBeenCalled();
    });

    it('should throw error when no refresh token is available', async () => {
      (useAuthStore.getState as jest.Mock).mockReturnValue({
        ...useAuthStore.getState(),
        refreshToken: null
      });

      await expect(authService.refreshAccessToken()).rejects.toThrow('No refresh token available');
    });

    it('should handle concurrent refresh requests', async () => {
      const mockRefreshResponse = {
        data: {
          accessToken: 'new-access-token',
          accessTokenExpiresInSeconds: 3600,
          refreshToken: 'new-refresh-token',
          refreshTokenExpiresInSeconds: 86400,
          email: mockUser.email,
          firstName: mockUser.firstName,
          lastName: mockUser.lastName
        }
      };

      (api.post as jest.Mock).mockResolvedValueOnce(mockRefreshResponse);

      const promise1 = authService.refreshAccessToken();
      const promise2 = authService.refreshAccessToken();
      const promise3 = authService.refreshAccessToken();

      await Promise.all([promise1, promise2, promise3]);

      expect(api.post).toHaveBeenCalledTimes(1);
      expect(mockSetAuthData).toHaveBeenCalledTimes(1);
    });
  });

  describe('logout', () => {
    it('should clear auth data and reset metering points', () => {
      authService.logout();

      expect(mockReset).toHaveBeenCalled();
      expect(mockClearAuth).toHaveBeenCalled();
    });
  });
});