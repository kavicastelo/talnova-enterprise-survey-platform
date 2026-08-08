import React, { useState, useMemo } from 'react';
import { EmployeeResponse } from '../../types/employee';
import { useGdprAnonymizeMutation } from '../../features/employee/api/useEmployeeQueries';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { DataTable } from '../ui/DataTable';
import { SearchFilterBar } from '../ui/SearchFilterBar';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { Select } from '../ui/Select';

interface EmployeeDataGridProps {
  employees: EmployeeResponse[];
  projectId?: string;
  onSelectEmployee?: (employee: EmployeeResponse) => void;
  onOpenImportWizard?: () => void;
  onOpenCreateModal?: () => void;
}

export const EmployeeDataGrid: React.FC<EmployeeDataGridProps> = ({
  employees,
  projectId = 'PRJ-99201',
  onSelectEmployee,
  onOpenImportWizard,
  onOpenCreateModal,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [employeeToAnonymize, setEmployeeToAnonymize] = useState<string | null>(null);

  const anonymizeMutation = useGdprAnonymizeMutation();

  const handleConfirmGdprAnonymize = () => {
    if (employeeToAnonymize) {
      anonymizeMutation.mutate(
        { employeeId: employeeToAnonymize, projectId },
        {
          onSuccess: () => setEmployeeToAnonymize(null),
        }
      );
    }
  };

  const filteredEmployees = useMemo(() => {
    return employees.filter((emp) => {
      const matchesSearch =
        !searchQuery ||
        emp.employeeId?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.email?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.nodeId?.toLowerCase().includes(searchQuery.toLowerCase());

      const matchesStatus = statusFilter === 'ALL' || emp.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [employees, searchQuery, statusFilter]);

  const columns = [
    {
      key: 'employeeId',
      header: 'Employee ID',
      render: (row: EmployeeResponse) => <code style={{ fontWeight: 700, color: '#1d4ed8' }}>{row.employeeId}</code>,
    },
    {
      key: 'fullName',
      header: 'Full Legal Name',
      render: (row: EmployeeResponse) => {
        const isMasked = row.fullName.includes('*');
        return (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontWeight: 600, color: '#0f172a' }}>{row.fullName}</span>
            {isMasked && <Badge variant="warning">PII Masked</Badge>}
          </div>
        );
      },
    },
    {
      key: 'email',
      header: 'Email Address',
      render: (row: EmployeeResponse) => <span style={{ color: '#475569' }}>{row.email || '—'}</span>,
    },
    {
      key: 'nodeId',
      header: 'Primary Org Node',
      render: (row: EmployeeResponse) => (
        <code style={{ background: '#f1f5f9', padding: '2px 6px', borderRadius: '4px', fontSize: '0.8rem' }}>
          {row.nodeId}
        </code>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (row: EmployeeResponse) => (
        <Badge
          variant={row.status === 'ACTIVE' ? 'success' : row.status === 'INACTIVE' ? 'warning' : 'danger'}
          dot
        >
          {row.status}
        </Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: EmployeeResponse) => (
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          {onSelectEmployee && (
            <Button variant="outline" size="sm" onClick={() => onSelectEmployee(row)}>
              Edit Profile
            </Button>
          )}
          <Button
            variant="danger"
            size="sm"
            onClick={() => setEmployeeToAnonymize(row.employeeId)}
          >
            GDPR Anonymize
          </Button>
        </div>
      ),
    },
  ];

  return (
    <Card variant="bordered" padding="24px">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* Header Controls */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
              Enterprise Employee Roster Studio
            </h3>
            <p style={{ color: '#64748b', fontSize: '0.85rem', margin: '4px 0 0 0' }}>
              Manage profiles, CSFLE PII encryption, matrix node reporting, and GDPR Right-to-be-Forgotten compliance.
            </p>
          </div>

          <div style={{ display: 'flex', gap: '12px' }}>
            {onOpenImportWizard && (
              <Button variant="secondary" onClick={onOpenImportWizard}>
                📥 Import CSV Roster
              </Button>
            )}
            {onOpenCreateModal && (
              <Button variant="primary" onClick={onOpenCreateModal}>
                + Add Employee Profile
              </Button>
            )}
          </div>
        </div>

        {/* Filter & Search Bar */}
        <SearchFilterBar
          placeholder="Search by Employee ID, Name, Email, or Org Node..."
          searchValue={searchQuery}
          onSearchChange={setSearchQuery}
          filters={
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              options={[
                { value: 'ALL', label: 'All Statuses' },
                { value: 'ACTIVE', label: 'Active Only' },
                { value: 'INACTIVE', label: 'Inactive Only' },
                { value: 'TERMINATED', label: 'Terminated Only' },
              ]}
            />
          }
        />

        {/* Roster Data Table */}
        <DataTable
          columns={columns}
          data={filteredEmployees}
          keyExtractor={(row) => row.employeeId}
          emptyTitle="No Employee Profiles Found"
          emptyDescription="There are no employee profiles matching your filter criteria."
        />
      </div>

      {/* GDPR Anonymize Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!employeeToAnonymize}
        onClose={() => setEmployeeToAnonymize(null)}
        onConfirm={handleConfirmGdprAnonymize}
        title="GDPR Right-to-be-Forgotten Anonymization"
        message={`Are you sure you want to scramble PII for employee ${employeeToAnonymize}? Sensitive PII (Name, Email, Phone) will be overwritten with scramble hashes and profile flagged as soft-deleted.`}
        confirmText="Confirm PII Anonymization"
        isLoading={anonymizeMutation.isPending}
      />
    </Card>
  );
};
