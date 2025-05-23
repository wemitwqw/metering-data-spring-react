import api from '../../utils/axios';
import { authService } from '../auth.service';
import { useAuthStore } from '../../stores/useAuthStore';
import { useMeteringPointsStore } from '../../stores/useMeteringPointsStore';
import { ERROR_MESSAGES } from '../../utils/constants';

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
  
  const mockToken = 'test-token';
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
      token: mockToken,
      isAuthenticated: true
    });
    
    (useMeteringPointsStore.getState as jest.Mock).mockReturnValue({
      reset: mockReset,
    });
  });

  describe('login', () => {
    it('should successfully login and set auth data', async () => {
      const email = 'test@example.com';
      const password = 'password123';
      
      const mockResponse = {
        data: {
          token: mockToken,
          email: mockUser.email,
          firstName: mockUser.firstName,
          lastName: mockUser.lastName
        }
      };
      
      (api.post as jest.Mock).mockResolvedValueOnce(mockResponse);

      await authService.login(email, password);

      expect(mockSetLoading).toHaveBeenCalledWith(true);
      expect(mockClearError).toHaveBeenCalled();
      expect(api.post).toHaveBeenCalledWith('/auth/login', { email, password });
      expect(mockSetAuthData).toHaveBeenCalledWith(mockToken, mockUser);
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
      expect(api.post).toHaveBeenCalledWith('/auth/login', { email, password });
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
      expect(api.post).toHaveBeenCalledWith('/auth/login', { email, password });
      expect(mockSetError).toHaveBeenCalledWith(ERROR_MESSAGES.LOGIN_FAILED);
      expect(mockSetLoading).toHaveBeenCalledWith(false);
    });
  });

  describe('logout', () => {
    it('should clear auth data and reset metering points', () => {
      authService.logout();

      expect(mockReset).toHaveBeenCalled();
      expect(mockClearAuth).toHaveBeenCalled();
    });
  });
  
  describe('getToken', () => {
    it('should return the token from the store', () => {
      const result = authService.getToken();

      expect(result).toBe(mockToken);
    });
  });
  
  describe('isAuthenticated', () => {
    it('should return the authentication status from the store', () => {
      const result = authService.isAuthenticated();

      expect(result).toBe(true);
    });
  });
});