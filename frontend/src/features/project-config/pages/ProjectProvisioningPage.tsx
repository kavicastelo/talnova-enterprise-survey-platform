import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Badge } from '../../../components/ui/Badge';
import { DataTable } from '../../../components/ui/DataTable';
import { Modal } from '../../../components/ui/Modal';
import { ConfirmDialog } from '../../../components/ui/ConfirmDialog';
import { ProjectSetupWizard } from '../../../components/project-config/ProjectSetupWizard';
import { useTenant } from '../../../context/TenantContext';
import { ProjectTenant } from '../../../types/tenant';
import { useDeleteProjectMutation } from '../api/useProjectConfigQueries';

export const ProjectProvisioningPage: React.FC = () => {
  const { projectsList, activeProject, switchProject } = useTenant();
  const [isWizardOpen, setIsWizardOpen] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<string | null>(null);

  const deleteMutation = useDeleteProjectMutation();

  const handleConfirmDelete = () => {
    if (projectToDelete) {
      deleteMutation.mutate(projectToDelete, {
        onSuccess: () => setProjectToDelete(null),
      });
    }
  };

  const columns = [
    {
      key: 'projectId',
      header: 'Project Tenant ID',
      render: (row: ProjectTenant) => <code style={{ fontWeight: 700, color: '#1d4ed8' }}>{row.projectId}</code>,
    },
    {
      key: 'companyName',
      header: 'Company / Workspace Name',
      render: (row: ProjectTenant) => (
        <div>
          <div style={{ fontWeight: 700, color: '#0f172a' }}>{row.branding.companyName}</div>
          <div style={{ fontSize: '0.75rem', color: '#64748b' }}>{row.projectName}</div>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (row: ProjectTenant) => (
        <Badge variant={row.status === 'ACTIVE' ? 'success' : 'neutral'} dot>
          {row.status}
        </Badge>
      ),
    },
    {
      key: 'defaultLocale',
      header: 'Default Language',
      render: (row: ProjectTenant) => <span>{row.defaultLocale}</span>,
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
              {isCurrent ? 'Active Tenant' : 'Switch Context'}
            </Button>
            <Button
              variant="danger"
              size="sm"
              onClick={() => setProjectToDelete(row.projectId)}
            >
              Delete
            </Button>
          </div>
        );
      },
    },
  ];

  return (
    <div>
      <PageHeader
        title="Project Workspace Tenant Provisioning"
        subtitle="Provision and manage isolated enterprise project tenants, branding, and status"
        actions={
          <Button variant="primary" onClick={() => setIsWizardOpen(true)}>
            + Provision New Project
          </Button>
        }
      />

      <Card variant="bordered" padding="0">
        <DataTable
          columns={columns}
          data={projectsList}
          keyExtractor={(row) => row.projectId}
          emptyTitle="No Projects Provisioned"
          emptyDescription="There are no active project workspace tenants configured."
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
        message={`Are you sure you want to soft delete project workspace ${projectToDelete}? Project data will be flagged as ARCHIVED.`}
        confirmText="Delete Project"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};
