export const API_BASE_URL = 'http://localhost:8080/api';
export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/auth/login`,
  METERING_POINTS: `${API_BASE_URL}/metering-points`,
};

export const STORAGE_KEYS = {
  AUTH_STORE: 'auth-storage',
};

export const ROUTES = {
  LOGIN: '/login',
  DASHBOARD: '/dashboard',
};

export const ERROR_MESSAGES = {
  LOGIN_FAILED: 'Login failed. Please check your credentials and try again.',
  FETCH_ADDRESSES_FAILED: 'Failed to fetch your metering points. Please try again later.',
  FETCH_CONSUMPTIONS_FAILED: 'Failed to fetch consumption data. Please try again later.',
  GENERIC_ERROR: 'Something went wrong. Please try again later.',
  REQUIRED_FIELD: 'This field is required',
  INVALID_EMAIL: 'Please enter a valid email address',
};

export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Login successful!',
};

export const APP_NAME = 'Metering Data Asessment';