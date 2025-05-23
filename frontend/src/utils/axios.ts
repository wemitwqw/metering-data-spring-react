import axios, { AxiosRequestConfig, AxiosError } from 'axios';
import { useAuthStore } from '../stores/useAuthStore';
import { API_BASE_URL, ROUTES, API_ENDPOINTS, ERROR_MESSAGES } from './constants';
import { shouldRefreshToken } from './jwt.validator';

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
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(() => {
          const newToken = useAuthStore.getState().accessToken;
          if (newToken && originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
          }
          return api(originalRequest);
        }).catch(err => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const { refreshToken } = useAuthStore.getState();

      if (!refreshToken || shouldRefreshToken()) {
        processQueue(error, null);
        isRefreshing = false;
        
        useAuthStore.getState().setError(ERROR_MESSAGES.TOKEN_REFRESH_FAILED);
        useAuthStore.getState().clearAuth();
        window.location.href = ROUTES.LOGIN;
        return Promise.reject(error);
      }

      try {
        const refreshResponse = await api.post(API_ENDPOINTS.REFRESH, {
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
        } = refreshResponse.data;

        const user = { email, firstName, lastName };
        
        useAuthStore.getState().setAuthData(
          accessToken, 
          newRefreshToken, 
          user, 
          accessTokenExpiresInSeconds,
          refreshTokenExpiresInSeconds
        );

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }

        processQueue(null, accessToken);
        isRefreshing = false;

        return api(originalRequest);

      } catch (refreshError) {
        processQueue(refreshError, null);
        isRefreshing = false;
        
        useAuthStore.getState().clearAuth();
        useAuthStore.getState().setError(ERROR_MESSAGES.TOKEN_REFRESH_FAILED);
        window.location.href = ROUTES.LOGIN;
        
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;