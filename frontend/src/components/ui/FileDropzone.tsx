import React, { useState, useRef } from 'react';
import { Button } from './Button';

export interface FileDropzoneProps {
  accept?: string;
  maxSizeMb?: number;
  onFileSelected: (file: File) => void;
  label?: string;
  helperText?: string;
}

export const FileDropzone: React.FC<FileDropzoneProps> = ({
  accept = '.csv, .xlsx, .pdf',
  maxSizeMb = 10,
  onFileSelected,
  label = 'Drag & drop your file here, or browse',
  helperText = 'Supports CSV, Excel (.xlsx), or PDF up to 10MB',
}) => {
  const [isDragOver, setIsDragOver] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const validateAndPass = (file: File) => {
    setError(null);
    const sizeInMb = file.size / (1024 * 1024);
    if (sizeInMb > maxSizeMb) {
      setError(`File size exceeds maximum allowed ${maxSizeMb}MB threshold.`);
      return;
    }
    setSelectedFile(file);
    onFileSelected(file);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      validateAndPass(e.dataTransfer.files[0]);
    }
  };

  return (
    <div style={{ width: '100%' }}>
      <div
        onDragOver={(e) => {
          e.preventDefault();
          setIsDragOver(true);
        }}
        onDragLeave={() => setIsDragOver(false)}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        style={{
          padding: '32px 24px',
          borderRadius: '12px',
          border: `2px dashed ${isDragOver ? '#3b82f6' : selectedFile ? '#10b981' : '#cbd5e1'}`,
          background: isDragOver ? '#eff6ff' : selectedFile ? '#ecfdf5' : '#f8fafc',
          textAlign: 'center',
          cursor: 'pointer',
          transition: 'all 0.15s ease-in-out',
        }}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept={accept}
          style={{ display: 'none' }}
          onChange={(e) => {
            if (e.target.files && e.target.files.length > 0) {
              validateAndPass(e.target.files[0]);
            }
          }}
        />

        <div style={{ fontSize: '2rem', marginBottom: '8px', color: selectedFile ? '#10b981' : '#3b82f6' }}>
          {selectedFile ? '📄' : '📁'}
        </div>

        {selectedFile ? (
          <div>
            <div style={{ fontSize: '0.95rem', fontWeight: 700, color: '#065f46' }}>{selectedFile.name}</div>
            <div style={{ fontSize: '0.8rem', color: '#047857', marginTop: '2px' }}>
              {(selectedFile.size / 1024).toFixed(1)} KB — Ready to process
            </div>
            <Button
              variant="outline"
              size="sm"
              style={{ marginTop: '12px' }}
              onClick={(e) => {
                e.stopPropagation();
                setSelectedFile(null);
              }}
            >
              Choose Different File
            </Button>
          </div>
        ) : (
          <div>
            <div style={{ fontSize: '0.925rem', fontWeight: 700, color: '#0f172a' }}>{label}</div>
            <div style={{ fontSize: '0.8rem', color: '#64748b', marginTop: '4px' }}>{helperText}</div>
          </div>
        )}
      </div>

      {error && <div style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '6px', fontWeight: 500 }}>{error}</div>}
    </div>
  );
};
