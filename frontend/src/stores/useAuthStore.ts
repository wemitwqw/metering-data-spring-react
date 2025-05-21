import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authApi, setAuthToken } from '../services/meteringPointService';
import { ERROR_MESSAGES, STORAGE_KEYS } from '../utils/constants';

interface User {
  email: string;
  firstName: string;
  lastName: string;
}

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      
      login: async (email: string, password: string) => {
        set({ isLoading: true, error: null });
        
        try {
          const response = await authApi.login(email, password);
          
          const { token, email: userEmail, firstName, lastName } = response.data;

          setAuthToken(token);
          
          set({
            token,
            user: { email: userEmail, firstName, lastName },
            isAuthenticated: true,
            isLoading: false,
            error: null
          });
          
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.response?.data?.message || ERROR_MESSAGES.LOGIN_FAILED
          });
          throw error;
        }
      },
      
      logout: () => {
        setAuthToken(null);
        
        set({
          token: null,
          user: null,
          isAuthenticated: false,
          error: null
        });
      },
      
      clearError: () => set({ error: null })
    }),
    {
      name: STORAGE_KEYS.AUTH_STORE,
      partialize: (state) => ({ 
        token: state.token, 
        user: state.user, 
        isAuthenticated: state.isAuthenticated 
      })
    }
  )
);