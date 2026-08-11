import axios, { AxiosError } from 'axios';
import { ApiError } from './error';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_GATEWAY_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Helper functions for token and tenant management
let getTokenFn: (() => string | null) | null = null;
let getProjectIdFn: (() => string | null) | null = null;

export const setAuthTokenGetter = (fn: () => string | null) => {
  getTokenFn = fn;
};

export const setProjectIdGetter = (fn: () => string | null) => {
  getProjectIdFn = fn;
};

apiClient.interceptors.request.use(
  (config) => {
    // Inject Authorization header if token exists
    const token = getTokenFn ? getTokenFn() : localStorage.getItem('tesp_access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Inject X-Project-ID header if present
    const projectId = getProjectIdFn ? getProjectIdFn() : localStorage.getItem('tesp_project_id') || 'PRJ-99201';
    if (projectId) {
      config.headers['X-Project-ID'] = projectId;
    }

    // Inject X-Correlation-ID header
    config.headers['X-Correlation-ID'] = crypto.randomUUID();

    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response.data,
  (error: AxiosError<any>) => {
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;

      // Handle 401 Unauthorized globally if needed
      if (status === 401 && !error.config?.url?.includes('/login')) {
        window.dispatchEvent(new CustomEvent('tesp:unauthorized'));
      }

      return Promise.reject(ApiError.fromResponse(status, data));
    } else if (error.request) {
      return Promise.reject(
        new ApiError('Network error: Unable to reach API Gateway at port 8080', 503, 'SERVICE_UNAVAILABLE')
      );
    }
    return Promise.reject(new ApiError(error.message || 'An unexpected error occurred', 500, 'UNKNOWN_ERROR'));
  }
);
