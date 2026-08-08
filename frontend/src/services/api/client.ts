/// <reference types="vite/client" />
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ApiError, ApiErrorPayload } from '../../types/api';

export const GATEWAY_BASE_URL = (import.meta as any).env?.VITE_API_GATEWAY_URL || '/api/v1';

export function generateCorrelationId(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return `CORR-${crypto.randomUUID().substring(0, 8).toUpperCase()}`;
  }
  return `CORR-${Math.random().toString(36).substring(2, 10).toUpperCase()}`;
}

export const axiosInstance: AxiosInstance = axios.create({
  baseURL: GATEWAY_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const projectId = localStorage.getItem('tesp_project_id') || 'PRJ-99201';
  const token = localStorage.getItem('tesp_auth_token');
  const userId = localStorage.getItem('tesp_user_id');
  const userRoles = localStorage.getItem('tesp_user_roles');

  config.headers.set('X-Project-ID', projectId);

  if (!config.headers.has('X-Correlation-ID')) {
    config.headers.set('X-Correlation-ID', generateCorrelationId());
  }

  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`);
  }

  if (userId) {
    config.headers.set('X-User-ID', userId);
  }

  if (userRoles) {
    config.headers.set('X-User-Roles', userRoles);
  }

  return config;
});

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: any) => {
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data as ApiErrorPayload;
      const message = data?.message || error.message || 'An unexpected API error occurred.';
      const errorCode = data?.errorCode || `HTTP_${status}`;
      const details = data?.errors;

      throw new ApiError(message, status, errorCode, details);
    } else if (error.request) {
      throw new ApiError('API Gateway unreachable. Please check network connection.', 503, 'GATEWAY_UNREACHABLE');
    } else {
      throw new ApiError(error.message || 'Network request failed', 500, 'CLIENT_REQUEST_ERROR');
    }
  }
);

export const apiClient = {
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const res = await axiosInstance.get<T>(url, config);
    return res.data;
  },

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const res = await axiosInstance.post<T>(url, data, config);
    return res.data;
  },

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const res = await axiosInstance.put<T>(url, data, config);
    return res.data;
  },

  async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const res = await axiosInstance.patch<T>(url, data, config);
    return res.data;
  },

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const res = await axiosInstance.delete<T>(url, config);
    return res.data;
  },

  async downloadFile(url: string, filename?: string): Promise<void> {
    const response = await axiosInstance.get(url, {
      responseType: 'blob',
    });
    const blobUrl = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = blobUrl;
    link.setAttribute('download', filename || 'download');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(blobUrl);
  },
};
