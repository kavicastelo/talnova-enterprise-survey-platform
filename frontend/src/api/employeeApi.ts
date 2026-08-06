import {
  EmployeeProfile,
  CreateEmployeePayload,
  UpdateEmployeePayload,
  HeaderMapping,
  BulkImportResult
} from '../types/employee';

const BASE_URL = '/api/v1/employees';

export const employeeApi = {
  async getEmployee(projectId: string, employeeId: string): Promise<EmployeeProfile> {
    const res = await fetch(`${BASE_URL}/${employeeId}?projectId=${encodeURIComponent(projectId)}`);
    if (!res.ok) throw new Error('Failed to fetch employee profile');
    const json = await res.json();
    return json.data;
  },

  async createEmployee(payload: CreateEmployeePayload): Promise<EmployeeProfile> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error('Failed to create employee profile');
    const json = await res.json();
    return json.data;
  },

  async updateEmployee(projectId: string, employeeId: string, payload: UpdateEmployeePayload): Promise<EmployeeProfile> {
    const res = await fetch(`${BASE_URL}/${employeeId}?projectId=${encodeURIComponent(projectId)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error('Failed to update employee profile');
    const json = await res.json();
    return json.data;
  },

  async bulkImportCsv(projectId: string, file: File, autoTerminate: boolean): Promise<BulkImportResult> {
    const formData = new FormData();
    formData.append('projectId', projectId);
    formData.append('file', file);
    formData.append('autoTerminateMissing', String(autoTerminate));

    const res = await fetch(`${BASE_URL}/bulk-import`, {
      method: 'POST',
      body: formData
    });
    if (!res.ok) throw new Error('Failed to upload and import CSV roster');
    const json = await res.json();
    return json.data;
  },

  async mapHeadersWithAi(headers: string[]): Promise<HeaderMapping[]> {
    const res = await fetch(`${BASE_URL}/ai/map-headers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ headers })
    });
    if (!res.ok) throw new Error('Failed to perform AI header mapping');
    const json = await res.json();
    return json.data.mappings;
  }
};
