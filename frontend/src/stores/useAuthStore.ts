import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { STORAGE_KEYS } from '../utils/constants';
import { User, AuthState } from 'src/types/auth.type';

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      accessTokenExpiresAt: null,
      refreshTokenExpiresAt: null,
      isSessionError: false,

      setAuthData: (
        accessToken: string, 
        refreshToken: string, 
        user: User,
        accessTokenExpiresInSeconds: number,
        refreshTokenExpiresInSeconds: number
      ) => {
        const now = Date.now();
        const accessTokenExpiresAt = now + (accessTokenExpiresInSeconds * 1000);
        const refreshTokenExpiresAt = now + (refreshTokenExpiresInSeconds * 1000);

        set({
          accessToken,
          refreshToken,
          user,
          isAuthenticated: true,
          error: null,
          accessTokenExpiresAt,
          refreshTokenExpiresAt
        });
      },
      
      clearAuth: () => {
        set({
          accessToken: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
          error: null,
          accessTokenExpiresAt: null,
          refreshTokenExpiresAt: null
        });
      },
      
      setLoading: (loading: boolean) => {
        set({ isLoading: loading });
      },
      
      setError: (error: string | null) => {
        set({ error });
      },

      clearError: () => {
        set({ error: null });
      },
    }),
    {
      name: STORAGE_KEYS.AUTH_STORE,
      partialize: (state) => ({ 
        accessToken: state.accessToken, 
        refreshToken: state.refreshToken,
        user: state.user, 
        isAuthenticated: state.isAuthenticated,
        accessTokenExpiresAt: state.accessTokenExpiresAt,
        refreshTokenExpiresAt: state.refreshTokenExpiresAt,
        error: state.error,
      }),
    }
  )
);