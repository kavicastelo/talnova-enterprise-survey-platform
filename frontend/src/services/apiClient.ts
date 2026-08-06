import axios from 'axios';

export const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const projectId = localStorage.getItem('tesp_project_id') || 'PRJ-DEFAULT-001';
  config.headers['X-Project-ID'] = projectId;
  config.headers['X-Correlation-ID'] = crypto.randomUUID();
  return config;
});
