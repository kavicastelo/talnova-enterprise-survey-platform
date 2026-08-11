import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { employeeApi } from './employeeApi';
import {
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  HrisSyncRequest,
} from '../../../types/employee';
import { useToast } from '../../../context/ToastContext';

export function useEmployeesQuery(projectId: string | undefined, nodeId?: string, status?: string) {
  return useQuery({
    queryKey: ['employees', projectId, nodeId, status],
    queryFn: () => employeeApi.getEmployees(projectId, nodeId, status),
    enabled: !!projectId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useEmployeeQuery(employeeId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['employee', projectId, employeeId],
    queryFn: () => employeeApi.getEmployee(employeeId!, projectId),
    enabled: !!employeeId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useCreateEmployeeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: CreateEmployeeRequest) => employeeApi.createEmployee(payload),
    onSuccess: (data) => {
      showSuccess(`Employee profile ${data.employeeId} (${data.fullName}) created!`);
      queryClient.invalidateQueries({ queryKey: ['employees', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to create employee profile.');
    },
  });
}

export function useUpdateEmployeeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ employeeId, payload, projectId }: { employeeId: string; payload: UpdateEmployeeRequest; projectId?: string }) =>
      employeeApi.updateEmployee(employeeId, payload, projectId),
    onSuccess: (data) => {
      showSuccess(`Employee profile ${data.employeeId} updated!`);
      queryClient.invalidateQueries({ queryKey: ['employee', data.projectId, data.employeeId] });
      queryClient.invalidateQueries({ queryKey: ['employees', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to update employee profile.');
    },
  });
}

export function useBulkImportCsvMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ file, projectId, autoTerminateMissing }: { file: File; projectId?: string; autoTerminateMissing?: boolean }) =>
      employeeApi.bulkImportCsv(file, projectId, autoTerminateMissing),
    onSuccess: (result) => {
      showSuccess(`Bulk CSV import completed: ${result.insertedCount} inserted, ${result.updatedCount} updated.`);
      queryClient.invalidateQueries({ queryKey: ['employees', result.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Bulk CSV roster import failed.');
    },
  });
}

export function useHrisSyncMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: HrisSyncRequest) => employeeApi.syncHrisRoster(payload),
    onSuccess: (result) => {
      showSuccess(`HRIS roster sync completed: ${result.totalProcessed} records processed.`);
      queryClient.invalidateQueries({ queryKey: ['employees', result.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'HRIS roster sync failed.');
    },
  });
}

export function useGdprAnonymizeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ employeeId, projectId }: { employeeId: string; projectId?: string }) =>
      employeeApi.anonymizeEmployee(employeeId, projectId),
    onSuccess: (data) => {
      showSuccess(`Employee PII for ${data.employeeId} anonymized for GDPR compliance.`);
      queryClient.invalidateQueries({ queryKey: ['employee', data.projectId, data.employeeId] });
      queryClient.invalidateQueries({ queryKey: ['employees', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to anonymize employee profile.');
    },
  });
}
