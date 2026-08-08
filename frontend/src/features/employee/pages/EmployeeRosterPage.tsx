import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';
import { Select } from '../../../components/ui/Select';
import { EmployeeDataGrid } from '../../../components/employee/EmployeeDataGrid';
import { BulkImportModal } from '../../../components/employee/BulkImportModal';
import { EmployeeFormModal } from '../../../components/employee/EmployeeFormModal';
import { useTenant } from '../../../context/TenantContext';
import { EmployeeResponse } from '../../../types/employee';
import { useHrisSyncMutation } from '../api/useEmployeeQueries';

export const EmployeeRosterPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('roster');
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [employeeToEdit, setEmployeeToEdit] = useState<EmployeeResponse | null>(null);

  // HRIS Sync Form State
  const [hrisForm, setHrisForm] = useState({
    provider: 'WORKDAY_RAAS' as const,
    apiEndpoint: 'https://wd2-impl-services1.workday.com/ccx/service/customreport2/tenant/raas/EmployeeRosterReport',
    apiKey: 'secret_wd_token_889201',
    autoTerminateMissing: false,
  });

  const hrisSyncMutation = useHrisSyncMutation();

  // Demo initial employee dataset
  const demoEmployees: EmployeeResponse[] = [
    {
      id: '1',
      projectId: activeProject?.projectId || 'PRJ-99201',
      employeeId: 'EMP-10020',
      fullName: 'Alexander Aitken',
      email: 'a.aitken@aitkenspence.lk',
      phoneNumber: '+94771234567',
      nodeId: 'N-201',
      matrixNodeIds: ['N-301'],
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: '2',
      projectId: activeProject?.projectId || 'PRJ-99201',
      employeeId: 'EMP-10021',
      fullName: 'J*** C****',
      email: 'j.c****@aitkenspence.lk',
      phoneNumber: '+9477****568',
      nodeId: 'N-201',
      matrixNodeIds: [],
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: '3',
      projectId: activeProject?.projectId || 'PRJ-99201',
      employeeId: 'EMP-10022',
      fullName: 'Samantha Perera',
      email: 's.perera@aitkenspence.lk',
      phoneNumber: '+94779876543',
      nodeId: 'N-101',
      matrixNodeIds: ['N-201'],
      status: 'INACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
  ];

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
    hrisSyncMutation.mutate({
      projectId: activeProject?.projectId || 'PRJ-99201',
      provider: hrisForm.provider,
      apiEndpoint: hrisForm.apiEndpoint,
      apiKey: hrisForm.apiKey,
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
        subtitle={`Manage workforce profiles, CSFLE PII encryption, matrix reporting lines, and HRIS sync for ${activeProject?.projectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'roster' && (
          <EmployeeDataGrid
            employees={demoEmployees}
            projectId={activeProject?.projectId || 'PRJ-99201'}
            onSelectEmployee={handleEditEmployee}
            onOpenImportWizard={() => setIsImportModalOpen(true)}
            onOpenCreateModal={handleCreateEmployee}
          />
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
        projectId={activeProject?.projectId || 'PRJ-99201'}
        isOpen={isImportModalOpen}
        onClose={() => setIsImportModalOpen(false)}
      />

      {/* Create / Edit Employee Profile Modal */}
      <EmployeeFormModal
        projectId={activeProject?.projectId || 'PRJ-99201'}
        employeeToEdit={employeeToEdit}
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
      />
    </div>
  );
};
