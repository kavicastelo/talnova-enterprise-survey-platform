import React, { useState } from 'react';
import { BulkImportResult } from '../../types/employee';
import { useBulkImportCsvMutation } from '../../features/employee/api/useEmployeeQueries';
import { Modal } from '../ui/Modal';
import { FileDropzone } from '../ui/FileDropzone';
import { Button } from '../ui/Button';
import { Alert } from '../ui/Alert';

interface Props {
  projectId: string;
  isOpen: boolean;
  onClose: () => void;
}

export const BulkImportModal: React.FC<Props> = ({ projectId, isOpen, onClose }) => {
  const [file, setFile] = useState<File | null>(null);
  const [autoTerminateMissing, setAutoTerminateMissing] = useState<boolean>(false);
  const [result, setResult] = useState<BulkImportResult | null>(null);

  const importMutation = useBulkImportCsvMutation();

  const handleUpload = () => {
    if (!file) return;

    importMutation.mutate(
      { file, projectId, autoTerminateMissing },
      {
        onSuccess: (data) => {
          setResult(data);
        },
      }
    );
  };

  const handleReset = () => {
    setFile(null);
    setResult(null);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="lg">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Bulk CSV Roster Ingestion
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            High-speed streaming CSV roster import for project tenant <code style={{ fontWeight: 700 }}>{projectId}</code>.
          </p>
        </div>

        {!result ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <FileDropzone
              accept=".csv"
              maxSizeMb={50}
              onFileSelected={(selectedFile: File) => setFile(selectedFile)}
              helperText="CSV columns: employeeId, email, fullName, phoneNumber, nodeId, status"
            />

            <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#334155', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={autoTerminateMissing}
                onChange={(e) => setAutoTerminateMissing(e.target.checked)}
                style={{ width: '16px', height: '16px', borderRadius: '4px' }}
              />
              <span>Automatically flag employees missing from this CSV file as <strong>TERMINATED</strong></span>
            </label>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              <Button variant="secondary" onClick={onClose}>
                Cancel
              </Button>
              <Button
                variant="primary"
                disabled={!file}
                isLoading={importMutation.isPending}
                onClick={handleUpload}
              >
                Start Bulk CSV Import
              </Button>
            </div>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Alert type="success" title="Bulk Roster Import Completed!">
              Processed {result.totalProcessed} employee records from uploaded CSV file.
            </Alert>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: '12px' }}>
              <div style={{ background: '#f0fdf4', padding: '12px', borderRadius: '8px', border: '1px solid #bbf7d0', textAlign: 'center' }}>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#166534' }}>{result.insertedCount}</div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#15803d' }}>Inserted Records</div>
              </div>
              <div style={{ background: '#eff6ff', padding: '12px', borderRadius: '8px', border: '1px solid #bfdbfe', textAlign: 'center' }}>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#1e40af' }}>{result.updatedCount}</div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#1d4ed8' }}>Updated Records</div>
              </div>
              <div style={{ background: '#fffbeb', padding: '12px', borderRadius: '8px', border: '1px solid #fde68a', textAlign: 'center' }}>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#92400e' }}>{result.terminatedCount}</div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#b45309' }}>Terminated</div>
              </div>
              <div style={{ background: '#fef2f2', padding: '12px', borderRadius: '8px', border: '1px solid #fecaca', textAlign: 'center' }}>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#991b1b' }}>{result.failedCount}</div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#b91c1c' }}>Failed Records</div>
              </div>
            </div>

            {result.errors.length > 0 && (
              <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '12px', borderRadius: '8px' }}>
                <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: '#991b1b', margin: '0 0 8px 0' }}>Import Errors:</h4>
                <ul style={{ margin: 0, paddingLeft: '20px', fontSize: '0.8rem', color: '#7f1d1d' }}>
                  {result.errors.map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              <Button variant="secondary" onClick={handleReset}>
                Import Another File
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
