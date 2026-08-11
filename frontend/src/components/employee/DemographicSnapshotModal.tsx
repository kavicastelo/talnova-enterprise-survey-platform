import React, { useState } from 'react';
import { DemographicSnapshotResponse, EmployeeResponse } from '../../types/employee';
import { useCompileSnapshotMutation } from '../../features/employee/api/useEmployeeQueries';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Alert } from '../ui/Alert';
import { Badge } from '../ui/Badge';

interface Props {
  projectId: string;
  employees: EmployeeResponse[];
  isOpen: boolean;
  onClose: () => void;
}

export const DemographicSnapshotModal: React.FC<Props> = ({ projectId, employees, isOpen, onClose }) => {
  const [surveyId, setSurveyId] = useState('SURVEY-2026-ANNUAL');
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState<string[]>(
    employees.slice(0, 5).map((e) => e.employeeId)
  );
  const [snapshots, setSnapshots] = useState<DemographicSnapshotResponse[] | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const compileMutation = useCompileSnapshotMutation();

  const handleToggleEmployee = (id: string) => {
    if (selectedEmployeeIds.includes(id)) {
      setSelectedEmployeeIds(selectedEmployeeIds.filter((eId) => eId !== id));
    } else {
      setSelectedEmployeeIds([...selectedEmployeeIds, id]);
    }
  };

  const handleSelectAll = () => {
    if (selectedEmployeeIds.length === employees.length) {
      setSelectedEmployeeIds([]);
    } else {
      setSelectedEmployeeIds(employees.map((e) => e.employeeId));
    }
  };

  const handleCompile = () => {
    if (!surveyId.trim()) {
      setErrorMsg('Survey ID is required to anchor demographic snapshots.');
      return;
    }
    if (selectedEmployeeIds.length === 0) {
      setErrorMsg('Select at least one employee to compile demographic snapshots.');
      return;
    }
    setErrorMsg(null);

    compileMutation.mutate(
      {
        projectId,
        surveyId: surveyId.trim(),
        employeeIds: selectedEmployeeIds,
      },
      {
        onSuccess: (data) => {
          setSnapshots(data);
        },
        onError: (err: any) => {
          setErrorMsg(err.message || 'Snapshot compilation failed.');
        },
      }
    );
  };

  const handleReset = () => {
    setSnapshots(null);
    setErrorMsg(null);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="lg">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Demographic Snapshot Compiler & Preview
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Freeze employee demographic profiles into immutable JSON snapshots at survey launch time for longitudinal analytics.
          </p>
        </div>

        {errorMsg && (
          <Alert type="error" title="Snapshot Compiler Error">
            {errorMsg}
          </Alert>
        )}

        {!snapshots ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Input
              label="Survey Identifier (Target Campaign)"
              value={surveyId}
              onChange={(e) => setSurveyId(e.target.value)}
              placeholder="e.g. SURVEY-2026-ANNUAL"
              helperText="Demographic snapshots will be immutably tagged to this Survey ID"
              required
            />

            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '0.875rem', fontWeight: 700, color: '#334155' }}>
                  Target Audience Employees ({selectedEmployeeIds.length} Selected)
                </span>
                <Button variant="ghost" size="sm" onClick={handleSelectAll}>
                  {selectedEmployeeIds.length === employees.length ? 'Deselect All' : 'Select All'}
                </Button>
              </div>

              <div style={{ maxHeight: '180px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '8px', padding: '8px', background: '#f8fafc' }}>
                {employees.length === 0 ? (
                  <p style={{ fontSize: '0.85rem', color: '#94a3b8', margin: 0, padding: '12px', textAlign: 'center' }}>
                    No employees available in current roster.
                  </p>
                ) : (
                  employees.map((emp) => (
                    <label
                      key={emp.employeeId}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '6px 10px',
                        borderRadius: '6px',
                        cursor: 'pointer',
                        fontSize: '0.85rem',
                        borderBottom: '1px solid #f1f5f9',
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={selectedEmployeeIds.includes(emp.employeeId)}
                        onChange={() => handleToggleEmployee(emp.employeeId)}
                      />
                      <span style={{ fontWeight: 600, color: '#0f172a' }}>{emp.fullName}</span>
                      <code style={{ fontSize: '0.75rem', color: '#2563eb' }}>({emp.employeeId})</code>
                      <span style={{ fontSize: '0.75rem', color: '#64748b', marginLeft: 'auto' }}>
                        Dept: {emp.nodeId}
                      </span>
                    </label>
                  ))
                )}
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              <Button variant="secondary" onClick={onClose}>
                Cancel
              </Button>
              <Button variant="primary" isLoading={compileMutation.isPending} onClick={handleCompile}>
                📸 Compile Immutable Snapshots
              </Button>
            </div>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Alert type="success" title="Demographic Snapshots Frozen Successfully!">
              Compiled {snapshots.length} immutable JSON demographic records for survey campaign {surveyId}.
            </Alert>

            <div style={{ maxHeight: '280px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {snapshots.map((snap) => (
                <div key={snap.snapshotId} style={{ background: '#f8fafc', border: '1px solid #cbd5e1', borderRadius: '8px', padding: '12px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span style={{ fontWeight: 700, color: '#0f172a' }}>Employee ID: {snap.employeeId}</span>
                      <Badge variant="success">Frozen Snapshot</Badge>
                    </div>
                    <code style={{ fontSize: '0.75rem', color: '#64748b' }}>
                      Snapshot ID: {snap.snapshotId}
                    </code>
                  </div>
                  <pre style={{ margin: 0, padding: '8px', background: '#0f172a', color: '#38bdf8', borderRadius: '6px', fontSize: '0.775rem', overflowX: 'auto' }}>
                    {JSON.stringify(snap.attributes || snap, null, 2)}
                  </pre>
                </div>
              ))}
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              <Button variant="secondary" onClick={handleReset}>
                Compile Another Batch
              </Button>
              <Button variant="primary" onClick={onClose}>
                Done
              </Button>
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};
