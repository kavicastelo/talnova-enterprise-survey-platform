import React, { useState, useEffect, useMemo } from 'react';
import { CreateEmployeeRequest, EmployeeResponse, EmployeeStatus, UpdateEmployeeRequest } from '../../types/employee';
import { useCreateEmployeeMutation, useUpdateEmployeeMutation } from '../../features/employee/api/useEmployeeQueries';
import { useSubtreeQuery } from '../../features/organization/api/useOrgQueries';
import { OrgNodeResponse } from '../../types/organization';
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

interface AttributePair {
  id: string;
  key: string;
  value: string;
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

export const EmployeeFormModal: React.FC<Props> = ({ projectId, employeeToEdit, isOpen, onClose }) => {
  const isEditing = !!employeeToEdit;
  const createMutation = useCreateEmployeeMutation();
  const updateMutation = useUpdateEmployeeMutation();

  // Query Org Subtree for Primary Org Node selector
  const { data: orgSubtree = [] } = useSubtreeQuery('ROOT', projectId);
  const flatOrgNodes = useMemo(() => flattenOrgNodes(orgSubtree), [orgSubtree]);

  const orgSelectOptions = useMemo(() => {
    const opts = [{ value: '', label: 'Select Primary Department / Org Unit...' }];
    flatOrgNodes.forEach((node) => {
      opts.push({ value: node.nodeId, label: `${node.name} (${node.nodeId})` });
    });
    return opts;
  }, [flatOrgNodes]);

  const [formData, setFormData] = useState<{
    employeeId: string;
    fullName: string;
    email: string;
    phoneNumber: string;
    nodeId: string;
    matrixNodeIdsStr: string;
    status: EmployeeStatus;
  }>({
    employeeId: '',
    fullName: '',
    email: '',
    phoneNumber: '',
    nodeId: '',
    matrixNodeIdsStr: '',
    status: 'ACTIVE',
  });

  const [attributePairs, setAttributePairs] = useState<AttributePair[]>([]);
  const [isAdvancedJsonMode, setIsAdvancedJsonMode] = useState(false);
  const [rawJsonStr, setRawJsonStr] = useState('');
  const [validationError, setValidationError] = useState<string | null>(null);

  useEffect(() => {
    if (employeeToEdit) {
      setFormData({
        employeeId: employeeToEdit.employeeId,
        fullName: employeeToEdit.fullName || '',
        email: employeeToEdit.email || '',
        phoneNumber: employeeToEdit.phoneNumber || '',
        nodeId: employeeToEdit.nodeId || '',
        matrixNodeIdsStr: employeeToEdit.matrixNodeIds ? employeeToEdit.matrixNodeIds.join(', ') : '',
        status: employeeToEdit.status || 'ACTIVE',
      });

      if (employeeToEdit.attributes && typeof employeeToEdit.attributes === 'object') {
        const pairs: AttributePair[] = Object.entries(employeeToEdit.attributes).map(([k, v], idx) => ({
          id: `attr-${idx}-${Date.now()}`,
          key: k,
          value: String(v ?? ''),
        }));
        setAttributePairs(pairs);
        setRawJsonStr(JSON.stringify(employeeToEdit.attributes, null, 2));
      } else {
        setAttributePairs([]);
        setRawJsonStr('');
      }
    } else {
      setFormData({
        employeeId: '',
        fullName: '',
        email: '',
        phoneNumber: '',
        nodeId: '',
        matrixNodeIdsStr: '',
        status: 'ACTIVE',
      });
      setAttributePairs([]);
      setRawJsonStr('');
    }
  }, [employeeToEdit, isOpen]);

  const handleAddAttributeRow = () => {
    setAttributePairs([...attributePairs, { id: `attr-${Date.now()}`, key: '', value: '' }]);
  };

  const handleRemoveAttributeRow = (id: string) => {
    setAttributePairs(attributePairs.filter((p) => p.id !== id));
  };

  const handleAttributeChange = (id: string, field: 'key' | 'value', val: string) => {
    setAttributePairs(
      attributePairs.map((p) => (p.id === id ? { ...p, [field]: val } : p))
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // Validation
    if (!isEditing && (!formData.employeeId || !/^[A-Za-z0-9_-]{2,30}$/.test(formData.employeeId.trim()))) {
      setValidationError('Employee ID is required and must contain 2-30 alphanumeric characters (e.g. EMP-10020).');
      return;
    }

    if (!formData.fullName.trim()) {
      setValidationError('Full Legal Name is required.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formData.email && !emailRegex.test(formData.email.trim())) {
      setValidationError('Corporate email address must be a valid email format (e.g. user@company.com).');
      return;
    }

    if (!formData.nodeId.trim()) {
      setValidationError('Primary Department / Organization Scope is required.');
      return;
    }

    // Parse matrixNodeIds
    const matrixNodeIds = formData.matrixNodeIdsStr
      ? formData.matrixNodeIdsStr.split(',').map((s) => s.trim()).filter(Boolean)
      : undefined;

    // Parse demographic attributes
    let attributes: Record<string, any> | undefined = undefined;
    if (isAdvancedJsonMode) {
      if (rawJsonStr.trim()) {
        try {
          attributes = JSON.parse(rawJsonStr);
        } catch (err) {
          setValidationError('Advanced JSON input is invalid. Please verify JSON syntax.');
          return;
        }
      }
    } else {
      if (attributePairs.length > 0) {
        const obj: Record<string, any> = {};
        for (const pair of attributePairs) {
          const k = pair.key.trim();
          if (k) {
            obj[k] = pair.value.trim();
          }
        }
        if (Object.keys(obj).length > 0) {
          attributes = obj;
        }
      }
    }

    if (isEditing) {
      const payload: UpdateEmployeeRequest = {
        fullName: formData.fullName.trim(),
        email: formData.email.trim() || undefined,
        phoneNumber: formData.phoneNumber.trim() || undefined,
        nodeId: formData.nodeId.trim(),
        matrixNodeIds,
        attributes,
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
        matrixNodeIds,
        attributes,
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
            Configure workforce profile details, organizational reporting lines, and demographic metadata.
          </p>
        </div>

        {validationError && (
          <Alert type="error" title="Validation Error">
            {validationError}
          </Alert>
        )}

        {!isEditing && (
          <Input
            label="Employee ID"
            value={formData.employeeId}
            onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
            placeholder="e.g. EMP-10020"
            helperText="Unique organization ID matching pattern A-Z, 0-9, dash or underscore (2-30 characters)"
            required
          />
        )}

        <Input
          label="Full Legal Name"
          value={formData.fullName}
          onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
          placeholder="e.g. Alexander Aitken"
          required
        />

        <Input
          label="Corporate Email Address"
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          placeholder="e.g. a.aitken@company.com"
        />

        <Input
          label="Phone Number"
          value={formData.phoneNumber}
          onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
          placeholder="e.g. +1 555-0192"
        />

        {/* Primary Org Node Dropdown Selector */}
        {orgSelectOptions.length > 1 ? (
          <Select
            label="Primary Department / Organization Scope"
            value={formData.nodeId}
            onChange={(e) => setFormData({ ...formData, nodeId: e.target.value })}
            options={orgSelectOptions}
            required
          />
        ) : (
          <Input
            label="Primary Department / Organization Scope ID"
            value={formData.nodeId}
            onChange={(e) => setFormData({ ...formData, nodeId: e.target.value })}
            placeholder="e.g. N-201"
            required
          />
        )}

        {/* Matrix Org Node Selector */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
            <span style={{ fontWeight: 700, fontSize: '0.875rem', color: '#334155' }}>
              Matrix Organization Units (Secondary Agile / Cross-Functional Teams)
            </span>
          </div>

          {orgSelectOptions.length > 1 && (
            <Select
              value=""
              onChange={(e) => {
                const selected = e.target.value;
                if (selected) {
                  const currentArr = formData.matrixNodeIdsStr
                    ? formData.matrixNodeIdsStr.split(',').map((s) => s.trim()).filter(Boolean)
                    : [];
                  if (!currentArr.includes(selected)) {
                    const newArr = [...currentArr, selected];
                    setFormData({ ...formData, matrixNodeIdsStr: newArr.join(', ') });
                  }
                }
              }}
              options={[{ value: '', label: '+ Add Secondary Matrix Team Assignment...' }, ...orgSelectOptions.filter((o) => o.value !== '')]}
            />
          )}

          <div style={{ marginTop: '8px' }}>
            <Input
              value={formData.matrixNodeIdsStr}
              onChange={(e) => setFormData({ ...formData, matrixNodeIdsStr: e.target.value })}
              placeholder="e.g. N-301, N-402"
              helperText="Secondary matrix agile or project reporting node assignments (comma-separated or select from dropdown above)"
            />
          </div>

          {formData.matrixNodeIdsStr.trim() && (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
              {formData.matrixNodeIdsStr.split(',').map((s) => s.trim()).filter(Boolean).map((nodeId) => {
                const nodeName = flatOrgNodes.find((n) => n.nodeId === nodeId)?.name || nodeId;
                return (
                  <span
                    key={nodeId}
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '6px',
                      background: '#e0e7ff',
                      color: '#3730a3',
                      padding: '2px 8px',
                      borderRadius: '12px',
                      fontSize: '0.75rem',
                      fontWeight: 600,
                    }}
                  >
                    {nodeName} ({nodeId})
                    <button
                      type="button"
                      onClick={() => {
                        const currentArr = formData.matrixNodeIdsStr
                          ? formData.matrixNodeIdsStr.split(',').map((s) => s.trim()).filter(Boolean)
                          : [];
                        const updated = currentArr.filter((id) => id !== nodeId);
                        setFormData({ ...formData, matrixNodeIdsStr: updated.join(', ') });
                      }}
                      style={{ border: 'none', background: 'none', cursor: 'pointer', color: '#3730a3', fontWeight: 700 }}
                    >
                      ×
                    </button>
                  </span>
                );
              })}
            </div>
          )}
        </div>

        {/* Dynamic Key-Value Demographic Attribute Builder */}
        <div style={{ border: '1px solid #e2e8f0', borderRadius: '8px', padding: '14px', background: '#f8fafc' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <div>
              <span style={{ fontWeight: 700, fontSize: '0.875rem', color: '#334155' }}>
                Demographic Attributes
              </span>
              <p style={{ fontSize: '0.75rem', color: '#64748b', margin: '2px 0 0 0' }}>
                Add custom demographic attributes used for survey slicing (e.g. Tenure, Work Location, Job Band)
              </p>
            </div>
            <button
              type="button"
              onClick={() => setIsAdvancedJsonMode(!isAdvancedJsonMode)}
              style={{ background: 'none', border: 'none', color: '#2563eb', cursor: 'pointer', fontSize: '0.75rem', textDecoration: 'underline' }}
            >
              {isAdvancedJsonMode ? 'Switch to Form Mode' : 'Switch to JSON Mode'}
            </button>
          </div>

          {!isAdvancedJsonMode ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {attributePairs.map((pair) => (
                <div key={pair.id} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                  <input
                    type="text"
                    placeholder="Attribute Key (e.g. Tenure)"
                    value={pair.key}
                    onChange={(e) => handleAttributeChange(pair.id, 'key', e.target.value)}
                    style={{ flex: 1, padding: '6px 10px', fontSize: '0.85rem', border: '1px solid #cbd5e1', borderRadius: '6px' }}
                  />
                  <input
                    type="text"
                    placeholder="Value (e.g. 3-5 Years)"
                    value={pair.value}
                    onChange={(e) => handleAttributeChange(pair.id, 'value', e.target.value)}
                    style={{ flex: 1, padding: '6px 10px', fontSize: '0.85rem', border: '1px solid #cbd5e1', borderRadius: '6px' }}
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => handleRemoveAttributeRow(pair.id)}
                    style={{ color: '#ef4444', padding: '4px 8px' }}
                    title="Remove Attribute"
                  >
                    ✕
                  </Button>
                </div>
              ))}
              <div style={{ marginTop: '4px' }}>
                <Button type="button" variant="outline" size="sm" onClick={handleAddAttributeRow}>
                  + Add Demographic Attribute
                </Button>
              </div>
            </div>
          ) : (
            <textarea
              rows={4}
              value={rawJsonStr}
              onChange={(e) => setRawJsonStr(e.target.value)}
              placeholder='{\n  "Tenure": "3-5 Years",\n  "WorkLocation": "HQ Colombo"\n}'
              style={{ width: '100%', fontFamily: 'monospace', fontSize: '0.8rem', padding: '8px', border: '1px solid #cbd5e1', borderRadius: '6px' }}
            />
          )}
        </div>

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
            {isEditing ? 'Save Changes' : 'Create Employee Profile'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

