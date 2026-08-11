import React, { useState, useEffect } from 'react';
import { CreateEmployeeRequest, EmployeeResponse, EmployeeStatus, UpdateEmployeeRequest } from '../../types/employee';
import { useCreateEmployeeMutation, useUpdateEmployeeMutation } from '../../features/employee/api/useEmployeeQueries';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';
import { Alert } from '../ui/Alert';

interface Props {
  projectId: string;
  employeeToEdit?: EmployeeResponse | null;
  isOpen: boolean;
  onClose: () => void;
}

export const EmployeeFormModal: React.FC<Props> = ({ projectId, employeeToEdit, isOpen, onClose }) => {
  const isEditing = !!employeeToEdit;
  const createMutation = useCreateEmployeeMutation();
  const updateMutation = useUpdateEmployeeMutation();

  const [formData, setFormData] = useState<{
    employeeId: string;
    fullName: string;
    email: string;
    phoneNumber: string;
    nodeId: string;
    status: EmployeeStatus;
  }>({
    employeeId: '',
    fullName: '',
    email: '',
    phoneNumber: '',
    nodeId: 'N-201',
    status: 'ACTIVE',
  });

  const [validationError, setValidationError] = useState<string | null>(null);

  useEffect(() => {
    if (employeeToEdit) {
      setFormData({
        employeeId: employeeToEdit.employeeId,
        fullName: employeeToEdit.fullName || '',
        email: employeeToEdit.email || '',
        phoneNumber: employeeToEdit.phoneNumber || '',
        nodeId: employeeToEdit.nodeId || 'N-201',
        status: employeeToEdit.status || 'ACTIVE',
      });
    } else {
      setFormData({
        employeeId: '',
        fullName: '',
        email: '',
        phoneNumber: '',
        nodeId: 'N-201',
        status: 'ACTIVE',
      });
    }
  }, [employeeToEdit, isOpen]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-EMP-001 validation
    if (!isEditing && (!formData.employeeId || !/^[A-Za-z0-9_-]{2,30}$/.test(formData.employeeId))) {
      setValidationError('Employee ID is mandatory and must match pattern ^[A-Za-z0-9_-]{2,30}$ (VR-EMP-001, e.g. EMP-10020)');
      return;
    }

    if (!formData.fullName.trim()) {
      setValidationError('Full Name is required.');
      return;
    }

    // VR-EMP-002 validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formData.email && !emailRegex.test(formData.email.trim())) {
      setValidationError('Email address must be a valid RFC 5322 email format (VR-EMP-002).');
      return;
    }

    // VR-EMP-003 validation
    if (!formData.nodeId.trim()) {
      setValidationError('Primary Organization Node ID is required (VR-EMP-003).');
      return;
    }

    if (isEditing) {
      const payload: UpdateEmployeeRequest = {
        fullName: formData.fullName.trim(),
        email: formData.email.trim() || undefined,
        phoneNumber: formData.phoneNumber.trim() || undefined,
        nodeId: formData.nodeId.trim(),
        status: formData.status,
      };

      updateMutation.mutate(
        { employeeId: employeeToEdit!.employeeId, payload, projectId },
        {
          onSuccess: () => onClose(),
        }
      );
    } else {
      const payload: CreateEmployeeRequest = {
        projectId,
        employeeId: formData.employeeId.trim(),
        fullName: formData.fullName.trim(),
        email: formData.email.trim() || undefined,
        phoneNumber: formData.phoneNumber.trim() || undefined,
        nodeId: formData.nodeId.trim(),
        status: formData.status,
      };

      createMutation.mutate(payload, {
        onSuccess: () => onClose(),
      });
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="md">
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            {isEditing ? `Edit Employee Profile (${formData.employeeId})` : 'Create New Employee Profile'}
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Sensitive PII fields (Name, Email, Phone) will be client-side CSFLE encrypted by employee-service.
          </p>
        </div>

        {validationError && (
          <Alert type="error" title="Validation Error">
            {validationError}
          </Alert>
        )}

        {!isEditing && (
          <Input
            label="Employee ID (VR-EMP-001)"
            value={formData.employeeId}
            onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
            placeholder="EMP-10020"
            helperText="Must match pattern ^[A-Za-z0-9_-]{2,30}$"
            required
          />
        )}

        <Input
          label="Full Legal Name (Sensitive PII)"
          value={formData.fullName}
          onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
          placeholder="Alexander Aitken"
          required
        />

        <Input
          label="Corporate Email Address (VR-EMP-002)"
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          placeholder="a.aitken@aitkenspence.lk"
        />

        <Input
          label="Phone Number"
          value={formData.phoneNumber}
          onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
          placeholder="+94771234567"
        />

        <Input
          label="Primary Org Node ID (VR-EMP-003)"
          value={formData.nodeId}
          onChange={(e) => setFormData({ ...formData, nodeId: e.target.value })}
          placeholder="N-201"
          required
        />

        <Select
          label="Employment Status"
          value={formData.status}
          onChange={(e) => setFormData({ ...formData, status: e.target.value as EmployeeStatus })}
          options={[
            { value: 'ACTIVE', label: 'ACTIVE' },
            { value: 'INACTIVE', label: 'INACTIVE' },
            { value: 'TERMINATED', label: 'TERMINATED' },
          ]}
        />

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="primary"
            isLoading={createMutation.isPending || updateMutation.isPending}
          >
            {isEditing ? 'Update Profile' : 'Create Profile'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
