import React, { useState, useMemo } from 'react';
import { EmployeeResponse } from '../../types/employee';
import { useGdprAnonymizeMutation } from '../../features/employee/api/useEmployeeQueries';
import { useSubtreeQuery } from '../../features/organization/api/useOrgQueries';
import { OrgNodeResponse } from '../../types/organization';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { DataTable } from '../ui/DataTable';
import { SearchFilterBar } from '../ui/SearchFilterBar';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { Select } from '../ui/Select';
import { EmptyState } from '../ui/EmptyState';
import { DemographicSnapshotModal } from './DemographicSnapshotModal';

interface EmployeeDataGridProps {
  employees: EmployeeResponse[];
  projectId?: string;
  onSelectEmployee?: (employee: EmployeeResponse) => void;
  onOpenImportWizard?: () => void;
  onOpenCreateModal?: () => void;
}

/** Recursively flattens org node tree into flat array for lookups and select options */
function flattenOrgNodes(nodes: OrgNodeResponse[] = []): { nodeId: string; name: string }[] {
  let result: { nodeId: string; name: string }[] = [];
  for (const node of nodes) {
    if (node.nodeId && node.name) {
      result.push({ nodeId: node.nodeId, name: node.name });
    }
    if (node.children && node.children.length > 0) {
      result = result.concat(flattenOrgNodes(node.children));
    }
  }
  return result;
}

export const EmployeeDataGrid: React.FC<EmployeeDataGridProps> = ({
  employees,
  projectId = '',
  onSelectEmployee,
  onOpenImportWizard,
  onOpenCreateModal,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [orgNodeFilter, setOrgNodeFilter] = useState<string>('ALL');
  const [employeeToAnonymize, setEmployeeToAnonymize] = useState<string | null>(null);
  const [isSnapshotModalOpen, setIsSnapshotModalOpen] = useState(false);

  // Query Org Subtree to resolve human-readable Org Node Names
  const { data: orgSubtree = [] } = useSubtreeQuery('ROOT', projectId);

  const flatOrgNodes = useMemo(() => flattenOrgNodes(orgSubtree), [orgSubtree]);

  const orgNodeMap = useMemo(() => {
    const map = new Map<string, string>();
    flatOrgNodes.forEach((item) => map.set(item.nodeId, item.name));
    return map;
  }, [flatOrgNodes]);

  const orgFilterOptions = useMemo(() => {
    const opts = [{ value: 'ALL', label: 'All Organizations / Departments' }];
    const knownNodeIds = new Set<string>();
    flatOrgNodes.forEach((n) => knownNodeIds.add(n.nodeId));
    employees.forEach((emp) => {
      if (emp.nodeId) knownNodeIds.add(emp.nodeId);
    });

    knownNodeIds.forEach((nodeId) => {
      const labelName = orgNodeMap.get(nodeId) || nodeId;
      opts.push({ value: nodeId, label: `${labelName} (${nodeId})` });
    });
    return opts;
  }, [flatOrgNodes, employees, orgNodeMap]);

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

  const handleClearFilters = () => {
    setSearchQuery('');
    setStatusFilter('ALL');
    setOrgNodeFilter('ALL');
  };

  const hasActiveFilters = searchQuery.trim() !== '' || statusFilter !== 'ALL' || orgNodeFilter !== 'ALL';

  const filteredEmployees = useMemo(() => {
    return employees.filter((emp) => {
      const matchesSearch =
        !searchQuery ||
        emp.employeeId?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.email?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.nodeId?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (orgNodeMap.get(emp.nodeId || '') || '').toLowerCase().includes(searchQuery.toLowerCase());

      const matchesStatus = statusFilter === 'ALL' || emp.status === statusFilter;
      const matchesOrgNode = orgNodeFilter === 'ALL' || emp.nodeId === orgNodeFilter;

      return matchesSearch && matchesStatus && matchesOrgNode;
    });
  }, [employees, searchQuery, statusFilter, orgNodeFilter, orgNodeMap]);

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
      header: 'Primary Org Scope',
      render: (row: EmployeeResponse) => {
        const nodeName = orgNodeMap.get(row.nodeId);
        return (
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontWeight: 600, color: '#334155', fontSize: '0.85rem' }}>
              {nodeName || row.nodeId}
            </span>
            {nodeName && (
              <code style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                {row.nodeId}
              </code>
            )}
          </div>
        );
      },
    },
    {
      key: 'matrixNodeIds',
      header: 'Matrix Scope',
      render: (row: EmployeeResponse) => {
        if (!row.matrixNodeIds || row.matrixNodeIds.length === 0) {
          return <span style={{ color: '#94a3b8', fontSize: '0.8rem' }}>—</span>;
        }
        return (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
            {row.matrixNodeIds.map((mId) => {
              const name = orgNodeMap.get(mId) || mId;
              return (
                <span
                  key={mId}
                  style={{
                    background: '#f1f5f9',
                    color: '#475569',
                    padding: '2px 6px',
                    borderRadius: '4px',
                    fontSize: '0.75rem',
                    fontWeight: 500,
                  }}
                  title={`Matrix ID: ${mId}`}
                >
                  {name}
                </span>
              );
            })}
          </div>
        );
      },
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
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                Enterprise Employee Directory
              </h3>
              <Badge variant="neutral">
                {filteredEmployees.length} {filteredEmployees.length === 1 ? 'Employee' : 'Employees'}
              </Badge>
            </div>
            <p style={{ color: '#64748b', fontSize: '0.85rem', margin: '4px 0 0 0' }}>
              View and manage workforce profiles, team assignments, matrix lines, and compliance status.
            </p>
          </div>

          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <Button variant="outline" onClick={() => setIsSnapshotModalOpen(true)}>
              📸 Demographic Snapshots
            </Button>
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
          placeholder="Search by Employee ID, Name, Email, or Department..."
          searchValue={searchQuery}
          onSearchChange={setSearchQuery}
          onClearFilters={hasActiveFilters ? handleClearFilters : undefined}
          filters={
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
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
              <Select
                value={orgNodeFilter}
                onChange={(e) => setOrgNodeFilter(e.target.value)}
                options={orgFilterOptions}
              />
            </div>
          }
        />

        {/* Active Filter Chips Bar */}
        {hasActiveFilters && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              flexWrap: 'wrap',
              background: '#f8fafc',
              padding: '10px 14px',
              borderRadius: '8px',
              border: '1px solid #e2e8f0',
              fontSize: '0.825rem',
            }}
          >
            <span style={{ fontWeight: 600, color: '#475569' }}>Active Filters:</span>
            {searchQuery && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '6px',
                  background: '#e0f2fe',
                  color: '#0369a1',
                  padding: '2px 10px',
                  borderRadius: '12px',
                  fontWeight: 500,
                }}
              >
                Search: "{searchQuery}"
                <button
                  onClick={() => setSearchQuery('')}
                  style={{ border: 'none', background: 'none', cursor: 'pointer', color: '#0369a1', fontWeight: 700 }}
                  title="Remove filter"
                >
                  ×
                </button>
              </span>
            )}
            {statusFilter !== 'ALL' && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '6px',
                  background: '#fef3c7',
                  color: '#92400e',
                  padding: '2px 10px',
                  borderRadius: '12px',
                  fontWeight: 500,
                }}
              >
                Status: {statusFilter}
                <button
                  onClick={() => setStatusFilter('ALL')}
                  style={{ border: 'none', background: 'none', cursor: 'pointer', color: '#92400e', fontWeight: 700 }}
                  title="Remove filter"
                >
                  ×
                </button>
              </span>
            )}
            {orgNodeFilter !== 'ALL' && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '6px',
                  background: '#f3e8ff',
                  color: '#6b21a8',
                  padding: '2px 10px',
                  borderRadius: '12px',
                  fontWeight: 500,
                }}
              >
                Org: {orgNodeMap.get(orgNodeFilter) || orgNodeFilter}
                <button
                  onClick={() => setOrgNodeFilter('ALL')}
                  style={{ border: 'none', background: 'none', cursor: 'pointer', color: '#6b21a8', fontWeight: 700 }}
                  title="Remove filter"
                >
                  ×
                </button>
              </span>
            )}
            <Button variant="ghost" size="sm" onClick={handleClearFilters} style={{ marginLeft: 'auto', padding: '2px 8px' }}>
              Clear All Filters
            </Button>
          </div>
        )}

        {/* Roster Data Table with Contextual Empty States */}
        {employees.length === 0 ? (
          <div style={{ padding: '32px 16px', background: '#f8fafc', borderRadius: '12px', border: '1px border-dashed #cbd5e1' }}>
            <EmptyState
              icon="👥"
              title="No Employees in Roster Yet"
              description="Get started by adding individual employee profiles or bulk importing your workforce directory via CSV."
              action={
                <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', marginTop: '12px' }}>
                  {onOpenCreateModal && (
                    <Button variant="primary" onClick={onOpenCreateModal}>
                      + Add First Employee Profile
                    </Button>
                  )}
                  {onOpenImportWizard && (
                    <Button variant="secondary" onClick={onOpenImportWizard}>
                      📥 Import Roster CSV
                    </Button>
                  )}
                </div>
              }
            />
          </div>
        ) : (
          <DataTable
            columns={columns}
            data={filteredEmployees}
            keyExtractor={(row) => row.employeeId}
            emptyTitle="No Matching Employees Found"
            emptyDescription="There are no employee profiles matching your filter criteria. Try adjusting or clearing your active filters."
            emptyAction={
              <Button variant="outline" size="sm" onClick={handleClearFilters}>
                Clear All Filters
              </Button>
            }
          />
        )}
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

      {/* Demographic Snapshot Compiler Modal */}
      <DemographicSnapshotModal
        projectId={projectId}
        employees={employees}
        isOpen={isSnapshotModalOpen}
        onClose={() => setIsSnapshotModalOpen(false)}
      />
    </Card>
  );
};

