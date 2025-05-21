import axios from 'axios';
import { API_BASE_URL, ROUTES } from '../utils/constants';
import { useAuthStore } from '../stores/useAuthStore';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const initializeApi = () => {
  const token = useAuthStore.getState().token;
  if (token) {
    setAuthToken(token);
  }
};

export const setAuthToken = (token: string | null) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};

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

export const authApi = {
  login: (email: string, password: string) => 
    api.post('/auth/login', { email, password })
};

export const meteringPointsApi = {
  fetchMeteringPoints: () => api.get('/metering-points')
};

export default api;