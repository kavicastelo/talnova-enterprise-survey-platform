import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';
import { Select } from '../../../components/ui/Select';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { EmployeeDataGrid } from '../../../components/employee/EmployeeDataGrid';
import { BulkImportModal } from '../../../components/employee/BulkImportModal';
import { EmployeeFormModal } from '../../../components/employee/EmployeeFormModal';
import { useTenant } from '../../../context/TenantContext';
import { EmployeeResponse } from '../../../types/employee';
import { useEmployeesQuery, useHrisSyncMutation } from '../api/useEmployeeQueries';

export const EmployeeRosterPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('roster');
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [employeeToEdit, setEmployeeToEdit] = useState<EmployeeResponse | null>(null);

  // Real Gateway Query
  const { data: employees = [], isLoading, isError, refetch } = useEmployeesQuery(activeProject?.projectId);

  // HRIS Sync Form State
  const [hrisForm, setHrisForm] = useState({
    provider: 'WORKDAY_RAAS' as const,
    apiEndpoint: '',
    apiKey: '',
    autoTerminateMissing: false,
  });

  const hrisSyncMutation = useHrisSyncMutation();

  const handleEditEmployee = (emp: EmployeeResponse) => {
    setEmployeeToEdit(emp);
    setIsFormModalOpen(true);
  };

  const handleCreateEmployee = () => {
    setEmployeeToEdit(null);
    setIsFormModalOpen(true);
  };

  const handleHrisSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeProject?.projectId) return;
    hrisSyncMutation.mutate({
      projectId: activeProject.projectId,
      provider: hrisForm.provider,
      apiEndpoint: hrisForm.apiEndpoint,
      apiKey: hrisForm.apiKey || undefined,
      autoTerminateMissing: hrisForm.autoTerminateMissing,
    });
  };

  const tabs = [
    { id: 'roster', label: 'Employee Roster Grid' },
    { id: 'hris', label: 'Automated HRIS Roster Sync' },
  ];

  return (
    <div>
      <PageHeader
        title="Employee Roster & Demographic Attribute Studio"
        subtitle={`Manage workforce profiles, CSFLE PII encryption, matrix reporting lines, and HRIS sync for ${activeProject?.projectId || 'Active Project'}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'roster' && (
          <>
            {isLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="320px" borderRadius="12px" />
              </Card>
            ) : isError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Failed to Load Employee Directory"
                  message="Could not retrieve employee roster records from Gateway employee-service."
                  onRetry={refetch}
                />
              </Card>
            ) : (
              <EmployeeDataGrid
                employees={employees}
                projectId={activeProject?.projectId || ''}
                onSelectEmployee={handleEditEmployee}
                onOpenImportWizard={() => setIsImportModalOpen(true)}
                onOpenCreateModal={handleCreateEmployee}
              />
            )}
          </>
        )}

        {activeTab === 'hris' && (
          <Card variant="bordered" padding="24px">
            <div style={{ marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                Automated HRIS Roster Synchronization
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Synchronize employee profiles automatically from Workday RaaS API, SAP SuccessFactors OData, or BambooHR.
              </p>
            </div>

            <form onSubmit={handleHrisSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '600px' }}>
              <Select
                label="HRIS System Provider"
                value={hrisForm.provider}
                onChange={(e) => setHrisForm({ ...hrisForm, provider: e.target.value as any })}
                options={[
                  { value: 'WORKDAY_RAAS', label: 'Workday RaaS Custom Report API' },
                  { value: 'SUCCESSFACTORS_ODATA', label: 'SAP SuccessFactors OData v4 API' },
                  { value: 'BAMBOOHR', label: 'BambooHR Employee Directory API' },
                ]}
              />

              <Input
                label="HRIS Report API Endpoint URL"
                value={hrisForm.apiEndpoint}
                onChange={(e) => setHrisForm({ ...hrisForm, apiEndpoint: e.target.value })}
                placeholder="https://wd2-impl-services1.workday.com/ccx/service/..."
                required
              />

              <Input
                label="API Access Token / Bearer Key"
                type="password"
                value={hrisForm.apiKey}
                onChange={(e) => setHrisForm({ ...hrisForm, apiKey: e.target.value })}
                placeholder="Enter secret token"
              />

              <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#334155', cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={hrisForm.autoTerminateMissing}
                  onChange={(e) => setHrisForm({ ...hrisForm, autoTerminateMissing: e.target.checked })}
                  style={{ width: '16px', height: '16px', borderRadius: '4px' }}
                />
                <span>Auto-terminate employees missing from HRIS sync response</span>
              </label>

              <div style={{ display: 'flex', justifyContent: 'flex-start', marginTop: '8px' }}>
                <Button type="submit" variant="primary" isLoading={hrisSyncMutation.isPending}>
                  🔄 Trigger HRIS Roster Sync Now
                </Button>
              </div>
            </form>
          </Card>
        )}
      </div>

      {/* Bulk CSV Import Modal */}
      <BulkImportModal
        projectId={activeProject?.projectId || ''}
        isOpen={isImportModalOpen}
        onClose={() => setIsImportModalOpen(false)}
      />

      {/* Create / Edit Employee Profile Modal */}
      <EmployeeFormModal
        projectId={activeProject?.projectId || ''}
        employeeToEdit={employeeToEdit}
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
      />
    </div>
  );
};
