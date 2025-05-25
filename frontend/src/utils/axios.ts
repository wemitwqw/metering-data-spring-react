import axios, { AxiosRequestConfig, AxiosError } from 'axios';
import { useAuthStore } from '../stores/useAuthStore';
import { API_BASE_URL, ROUTES, API_ENDPOINTS, ERROR_MESSAGES } from './constants';
import { shouldRefreshToken } from './jwt.validator';
import { authService } from '../services/auth.service';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: any) => void;
  reject: (error?: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  
  failedQueue = [];
};

api.interceptors.request.use(
  (config) => {
    const accessToken = useAuthStore.getState().accessToken;

    const isAuthEndpoint = config.url?.startsWith(API_ENDPOINTS.AUTH_BASE);
    
    if (accessToken && !isAuthEndpoint) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    const handleAuthFailure = (errorMessage?: string) => {
      processQueue(error, null);
      isRefreshing = false;
      
      if (errorMessage) {
        useAuthStore.getState().setError(errorMessage);
      }
      useAuthStore.getState().clearAuth();
      window.location.href = ROUTES.LOGIN;
      return Promise.reject(error);
    };

    if (error.response?.status !== 401) {
      return Promise.reject(error);
    }

    if (originalRequest.url?.includes(API_ENDPOINTS.REFRESH)) {
      return handleAuthFailure();
    }

    if (originalRequest._retry) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      })
      .then(() => {
        const newToken = useAuthStore.getState().accessToken;
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }
        return api(originalRequest);
      })
      .catch(err => Promise.reject(err));
    }

    originalRequest._retry = true;
    isRefreshing = true;

    const { refreshToken } = useAuthStore.getState();

    if (!refreshToken || shouldRefreshToken()) {
      return handleAuthFailure(ERROR_MESSAGES.TOKEN_REFRESH_FAILED);
    }

    try {
      await authService.performRefresh(refreshToken);
      
      const newAccessToken = useAuthStore.getState().accessToken;
      if (originalRequest.headers && newAccessToken) {
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      }

      processQueue(null, newAccessToken);
      isRefreshing = false;

      return api(originalRequest);
    } catch (refreshError) {
      return handleAuthFailure();
    }
  }
);

export default api;