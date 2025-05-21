import axios from 'axios';
import { useAuthStore } from '../stores/useAuthStore';
import { API_BASE_URL, ROUTES } from '../utils/constants';

const api = axios.create({
  baseURL: API_BASE_URL
});

api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
      window.location.href = ROUTES.LOGIN;
    }
    return Promise.reject(error);
  }
);

export default api;