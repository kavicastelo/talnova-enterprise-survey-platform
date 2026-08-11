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
        title="Employee Roster & Demographic Directory"
        subtitle={`Manage workforce profiles, department assignments, demographic attributes, and automated HRIS synchronization.`}
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
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '20px' }}>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                  Automated HRIS Roster Synchronization
                </h3>
                <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                  Synchronize workforce directory profiles automatically from Workday RaaS API, SAP SuccessFactors OData, or BambooHR.
                </p>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: '#f0fdf4', padding: '8px 14px', borderRadius: '8px', border: '1px solid #bbf7d0' }}>
                <span style={{ width: '10px', height: '10px', borderRadius: '50%', background: '#16a34a', display: 'inline-block' }}></span>
                <div>
                  <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#166534' }}>Automated Connector Active</div>
                  <div style={{ fontSize: '0.725rem', color: '#15803d' }}>Nightly Schedule: 02:00 UTC (Redis Cron Engine)</div>
                </div>
              </div>
            </div>

            <form onSubmit={handleHrisSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '680px' }}>
              <Select
                label="HRIS System Provider"
                value={hrisForm.provider}
                onChange={(e) => {
                  const p = e.target.value as any;
                  let defaultUrl = '';
                  if (p === 'WORKDAY_RAAS') defaultUrl = 'https://wd2-impl-services1.workday.com/ccx/service/customreport2/tenant/user/RaaS_Employee_Roster?format=json';
                  else if (p === 'SUCCESSFACTORS_ODATA') defaultUrl = 'https://api.successfactors.eu/odata/v2/User?$format=json';
                  else if (p === 'BAMBOOHR') defaultUrl = 'https://api.bamboohr.com/api/gateway.php/company/v1/employees/directory';

                  setHrisForm({ ...hrisForm, provider: p, apiEndpoint: defaultUrl });
                }}
                options={[
                  { value: 'WORKDAY_RAAS', label: 'Workday RaaS Custom Report API (REST JSON)' },
                  { value: 'SUCCESSFACTORS_ODATA', label: 'SAP SuccessFactors OData v4 API' },
                  { value: 'BAMBOOHR', label: 'BambooHR Employee Directory API' },
                ]}
              />

              <Input
                label="HRIS Report API Endpoint URL"
                value={hrisForm.apiEndpoint}
                onChange={(e) => setHrisForm({ ...hrisForm, apiEndpoint: e.target.value })}
                placeholder="https://wd2-impl-services1.workday.com/ccx/service/customreport2/..."
                helperText="OAuth2 REST URL endpoint providing standard workforce json payload"
                required
              />

              <Input
                label="API Secret Access Token / Bearer Key"
                type="password"
                value={hrisForm.apiKey}
                onChange={(e) => setHrisForm({ ...hrisForm, apiKey: e.target.value })}
                placeholder="Enter secret token or OAuth2 bearer key"
                helperText="Encrypted via KMS key before storing in project configuration"
              />

              <div style={{ background: '#fffbeb', border: '1px solid #fde68a', padding: '14px', borderRadius: '8px' }}>
                <label style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', cursor: 'pointer', fontSize: '0.85rem', color: '#78350f' }}>
                  <input
                    type="checkbox"
                    checked={hrisForm.autoTerminateMissing}
                    onChange={(e) => setHrisForm({ ...hrisForm, autoTerminateMissing: e.target.checked })}
                    style={{ width: '18px', height: '18px', marginTop: '2px', borderRadius: '4px' }}
                  />
                  <div>
                    <strong>Enable Delta Auto-Termination:</strong> Automatically mark active employees unlisted in HRIS response as <code style={{ background: '#fef3c7', padding: '1px 4px', borderRadius: '4px' }}>TERMINATED</code>.
                    <p style={{ margin: '4px 0 0 0', fontSize: '0.775rem', color: '#92400e' }}>
                      ⚠️ Ensure your HRIS API query returns all active workforce members before enabling auto-termination.
                    </p>
                  </div>
                </label>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-start', marginTop: '4px' }}>
                <Button type="submit" variant="primary" isLoading={hrisSyncMutation.isPending}>
                  🔄 Trigger On-Demand HRIS Roster Sync
                </Button>
              </div>
            </form>

            {/* Sync Results Metric Summary Panel */}
            {hrisSyncMutation.data && (
              <div style={{ marginTop: '24px', borderTop: '1px solid #e2e8f0', paddingTop: '20px' }}>
                <div style={{ marginBottom: '12px' }}>
                  <h4 style={{ fontSize: '1rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                    Latest HRIS Sync Summary ({hrisSyncMutation.data.jobId})
                  </h4>
                  <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>
                    Results from manual trigger execution for project {hrisSyncMutation.data.projectId}.
                  </p>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))', gap: '12px' }}>
                  <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0', textAlign: 'center' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#2563eb' }}>{hrisSyncMutation.data.totalProcessed}</div>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#475569' }}>Total Processed</div>
                  </div>
                  <div style={{ background: '#f0fdf4', padding: '14px', borderRadius: '8px', border: '1px solid #bbf7d0', textAlign: 'center' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#166534' }}>{hrisSyncMutation.data.insertedCount}</div>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#15803d' }}>Inserted</div>
                  </div>
                  <div style={{ background: '#eff6ff', padding: '14px', borderRadius: '8px', border: '1px solid #bfdbfe', textAlign: 'center' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#1e40af' }}>{hrisSyncMutation.data.updatedCount}</div>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#1d4ed8' }}>Updated</div>
                  </div>
                  <div style={{ background: '#fffbeb', padding: '14px', borderRadius: '8px', border: '1px solid #fde68a', textAlign: 'center' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#92400e' }}>{hrisSyncMutation.data.terminatedCount}</div>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#b45309' }}>Auto-Terminated</div>
                  </div>
                  <div style={{ background: '#fef2f2', padding: '14px', borderRadius: '8px', border: '1px solid #fecaca', textAlign: 'center' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#991b1b' }}>{hrisSyncMutation.data.failedCount}</div>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#b91c1c' }}>Failed</div>
                  </div>
                </div>

                {hrisSyncMutation.data.errors && hrisSyncMutation.data.errors.length > 0 && (
                  <div style={{ marginTop: '12px', background: '#fef2f2', border: '1px solid #fecaca', padding: '12px', borderRadius: '8px' }}>
                    <h5 style={{ fontSize: '0.85rem', fontWeight: 700, color: '#991b1b', margin: '0 0 6px 0' }}>Sync Diagnostics & Warnings:</h5>
                    <ul style={{ margin: 0, paddingLeft: '20px', fontSize: '0.8rem', color: '#7f1d1d' }}>
                      {hrisSyncMutation.data.errors.map((err, idx) => (
                        <li key={idx}>{err}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}
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
