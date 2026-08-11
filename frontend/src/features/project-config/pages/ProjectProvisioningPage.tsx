import React, { useState, useMemo } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Badge } from '../../../components/ui/Badge';
import { Input } from '../../../components/ui/Input';
import { Select } from '../../../components/ui/Select';
import { DataTable } from '../../../components/ui/DataTable';
import { Modal } from '../../../components/ui/Modal';
import { ConfirmDialog } from '../../../components/ui/ConfirmDialog';
import { ProjectSetupWizard } from '../../../components/project-config/ProjectSetupWizard';
import { useTenant } from '../../../context/TenantContext';
import { ProjectTenant } from '../../../types/projectConfig';
import { useDeleteProjectMutation } from '../api/useProjectConfigQueries';
import { RoleGate } from '../../../components/auth/RoleGate';

export const ProjectProvisioningPage: React.FC = () => {
  const { projectsList, activeProject, switchProject, isLoadingProjects } = useTenant();
  const [isWizardOpen, setIsWizardOpen] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const deleteMutation = useDeleteProjectMutation();

  const handleConfirmDelete = () => {
    if (projectToDelete) {
      deleteMutation.mutate(projectToDelete, {
        onSuccess: () => setProjectToDelete(null),
      });
    }
  };

  const handleClearFilters = () => {
    setSearchTerm('');
    setStatusFilter('ALL');
  };

  const filteredProjects = useMemo(() => {
    return projectsList.filter((proj) => {
      const companyName = proj.branding?.companyName || proj.name || proj.projectName || '';
      const projId = proj.projectId || '';
      const matchesSearch =
        projId.toLowerCase().includes(searchTerm.toLowerCase()) ||
        companyName.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesStatus = statusFilter === 'ALL' || proj.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [projectsList, searchTerm, statusFilter]);

  const hasActiveFilters = searchTerm !== '' || statusFilter !== 'ALL';

  const columns = [
    {
      key: 'projectId',
      header: 'Project Tenant ID',
      render: (row: ProjectTenant) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <code style={{ fontWeight: 700, color: '#1d4ed8', background: '#eff6ff', padding: '2px 6px', borderRadius: '4px' }}>
            {row.projectId}
          </code>
          {activeProject?.projectId === row.projectId && (
            <Badge variant="indigo">Active</Badge>
          )}
        </div>
      ),
    },
    {
      key: 'companyName',
      header: 'Company / Workspace Name',
      render: (row: ProjectTenant) => (
        <div>
          <div style={{ fontWeight: 700, color: '#0f172a' }}>{row.branding?.companyName || row.name || row.projectName}</div>
          <div style={{ fontSize: '0.75rem', color: '#64748b' }}>{row.name || row.projectName}</div>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (row: ProjectTenant) => (
        <Badge variant={row.status === 'ACTIVE' ? 'success' : row.status === 'PROVISIONING' ? 'warning' : 'neutral'} dot>
          {row.status}
        </Badge>
      ),
    },
    {
      key: 'defaultLocale',
      header: 'Default Language',
      render: (row: ProjectTenant) => (
        <code style={{ fontSize: '0.8rem', color: '#334155' }}>{row.defaultLocale}</code>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: ProjectTenant) => {
        const isCurrent = activeProject?.projectId === row.projectId;
        return (
          <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
            <Button
              variant={isCurrent ? 'secondary' : 'outline'}
              size="sm"
              disabled={isCurrent}
              onClick={() => switchProject(row.projectId)}
            >
              {isCurrent ? 'Active Context' : 'Switch Context'}
            </Button>
            <RoleGate allowedRoles={['SUPER_ADMIN']}>
              <Button
                variant="danger"
                size="sm"
                onClick={() => setProjectToDelete(row.projectId)}
              >
                Delete
              </Button>
            </RoleGate>
          </div>
        );
      },
    },
  ];

  return (
    <div>
      <PageHeader
        title="Project Workspace Tenant Provisioning"
        subtitle="Provision and manage isolated enterprise project tenants, white-label branding, and status"
        actions={
          <RoleGate allowedRoles={['SUPER_ADMIN']}>
            <Button variant="primary" onClick={() => setIsWizardOpen(true)}>
              + Provision New Project
            </Button>
          </RoleGate>
        }
      />

      {/* Filter and Search Bar */}
      <Card variant="bordered" padding="16px" className="mb-4">
        <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'flex-end', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', flex: 1, minWidth: '280px' }}>
            <div style={{ flex: 1, minWidth: '200px' }}>
              <Input
                label="Search Workspace Tenants"
                placeholder="Search by Project ID or Company Name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div style={{ width: '180px' }}>
              <Select
                label="Status Filter"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                options={[
                  { value: 'ALL', label: 'All Statuses' },
                  { value: 'ACTIVE', label: 'Active' },
                  { value: 'PROVISIONING', label: 'Provisioning' },
                  { value: 'DRAFT', label: 'Draft' },
                  { value: 'SUSPENDED', label: 'Suspended' },
                  { value: 'ARCHIVED', label: 'Archived' },
                ]}
              />
            </div>
          </div>
          {hasActiveFilters && (
            <Button variant="secondary" size="sm" onClick={handleClearFilters}>
              Clear Filters
            </Button>
          )}
        </div>
      </Card>

      <Card variant="bordered" padding="0">
        <DataTable
          columns={columns}
          data={filteredProjects}
          keyExtractor={(row) => row.projectId}
          isLoading={isLoadingProjects}
          emptyTitle={
            hasActiveFilters
              ? 'No Matching Workspace Tenants'
              : 'No Projects Provisioned Yet'
          }
          emptyDescription={
            hasActiveFilters
              ? 'No project workspace tenants match your search query or status filter criteria.'
              : 'There are no active multi-tenant project workspace tenants configured in the platform.'
          }
          emptyAction={
            hasActiveFilters ? (
              <Button variant="secondary" size="sm" onClick={handleClearFilters}>
                Clear Active Filters
              </Button>
            ) : (
              <RoleGate allowedRoles={['SUPER_ADMIN']}>
                <Button variant="primary" size="sm" onClick={() => setIsWizardOpen(true)}>
                  + Provision First Project
                </Button>
              </RoleGate>
            )
          }
        />
      </Card>

      {/* Provisioning Wizard Modal */}
      <Modal isOpen={isWizardOpen} onClose={() => setIsWizardOpen(false)} size="lg">
        <ProjectSetupWizard
          onSuccess={() => setIsWizardOpen(false)}
          onCancel={() => setIsWizardOpen(false)}
        />
      </Modal>

      {/* Confirm Soft Delete Dialog */}
      <ConfirmDialog
        isOpen={!!projectToDelete}
        onClose={() => setProjectToDelete(null)}
        onConfirm={handleConfirmDelete}
        title="Confirm Soft Delete Project"
        message={`Are you sure you want to soft delete project workspace ${projectToDelete}? Project data will be flagged as ARCHIVED (BR-CFG-001).`}
        confirmText="Delete Project"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

