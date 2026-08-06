import React, { useState, useMemo } from 'react';
import { EmployeeProfile, EmployeeStatus } from '../../types/employee';

interface EmployeeDataGridProps {
  employees: EmployeeProfile[];
  onSelectEmployee?: (employee: EmployeeProfile) => void;
  onUpdateStatus?: (employeeId: string, status: EmployeeStatus) => void;
  onOpenImportWizard?: () => void;
}

export const EmployeeDataGrid: React.FC<EmployeeDataGridProps> = ({
  employees,
  onSelectEmployee,
  onUpdateStatus,
  onOpenImportWizard
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const filteredEmployees = useMemo(() => {
    return employees.filter(emp => {
      const matchesSearch =
        !searchQuery ||
        emp.employeeId?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.email?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        emp.nodeId?.toLowerCase().includes(searchQuery.toLowerCase());

      const matchesStatus =
        statusFilter === 'ALL' || emp.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [employees, searchQuery, statusFilter]);

  const dynamicAttributeKeys = useMemo(() => {
    const keys = new Set<string>();
    employees.forEach(emp => {
      if (emp.attributes) {
        Object.keys(emp.attributes).forEach(k => keys.add(k));
      }
    });
    return Array.from(keys);
  }, [employees]);

  const renderStatusDropdown = (emp: EmployeeProfile) => {
    return (
      <select
        value={emp.status}
        onChange={e => onUpdateStatus && onUpdateStatus(emp.employeeId, e.target.value as EmployeeStatus)}
        disabled={!onUpdateStatus}
        style={{
          padding: '4px 8px',
          borderRadius: '12px',
          background:
            emp.status === 'ACTIVE'
              ? 'rgba(16, 185, 129, 0.2)'
              : emp.status === 'INACTIVE'
              ? 'rgba(245, 158, 11, 0.2)'
              : 'rgba(239, 68, 68, 0.2)',
          color:
            emp.status === 'ACTIVE'
              ? '#10b981'
              : emp.status === 'INACTIVE'
              ? '#f59e0b'
              : '#ef4444',
          fontWeight: 600,
          fontSize: '0.75rem',
          border: 'none',
          outline: 'none',
          cursor: onUpdateStatus ? 'pointer' : 'default'
        }}
      >
        <option value="ACTIVE" style={{ background: '#0f172a', color: '#10b981' }}>ACTIVE</option>
        <option value="INACTIVE" style={{ background: '#0f172a', color: '#f59e0b' }}>INACTIVE</option>
        <option value="TERMINATED" style={{ background: '#0f172a', color: '#ef4444' }}>TERMINATED</option>
      </select>
    );
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', gap: '16px', background: '#0f172a', color: '#f8fafc', padding: '20px', borderRadius: '12px', border: '1px solid #1e293b' }}>
      {/* Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 700, background: 'linear-gradient(135deg, #38bdf8 0%, #818cf8 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Enterprise Employee Roster
          </h2>
          <span style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
            Total Roster: {employees.length} | Filtered: {filteredEmployees.length} Records
          </span>
        </div>

        <div style={{ display: 'flex', gap: '12px' }}>
          {onOpenImportWizard && (
            <button
              onClick={onOpenImportWizard}
              style={{
                padding: '8px 16px',
                background: 'linear-gradient(135deg, #0284c7 0%, #2563eb 100%)',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
                fontWeight: 600,
                cursor: 'pointer',
                boxShadow: '0 4px 12px rgba(37, 99, 235, 0.3)'
              }}
            >
              📥 Import CSV Roster
            </button>
          )}
        </div>
      </div>

      {/* Filter & Search Bar */}
      <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
        <input
          type="text"
          placeholder="Search by Employee ID, Name, Email, or Node..."
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          style={{
            flex: 1,
            padding: '10px 14px',
            background: '#1e293b',
            border: '1px solid #334155',
            borderRadius: '8px',
            color: '#f8fafc',
            outline: 'none',
            fontSize: '0.9rem'
          }}
        />

        <select
          value={statusFilter}
          onChange={e => setStatusFilter(e.target.value)}
          style={{
            padding: '10px 14px',
            background: '#1e293b',
            border: '1px solid #334155',
            borderRadius: '8px',
            color: '#f8fafc',
            outline: 'none',
            fontSize: '0.9rem'
          }}
        >
          <option value="ALL">All Statuses</option>
          <option value="ACTIVE">Active Only</option>
          <option value="INACTIVE">Inactive Only</option>
          <option value="TERMINATED">Terminated Only</option>
        </select>
      </div>

      {/* Data Grid Table */}
      <div style={{ flex: 1, overflow: 'auto', border: '1px solid #334155', borderRadius: '8px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
          <thead>
            <tr style={{ background: '#1e293b', color: '#cbd5e1', position: 'sticky', top: 0, zIndex: 10 }}>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Employee ID</th>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Full Name</th>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Email Address</th>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Org Node</th>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Matrix Nodes</th>
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Status</th>
              {dynamicAttributeKeys.map(attrKey => (
                <th key={attrKey} style={{ padding: '12px 16px', borderBottom: '1px solid #334155', color: '#94a3b8' }}>
                  {attrKey}
                </th>
              ))}
              <th style={{ padding: '12px 16px', borderBottom: '1px solid #334155' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.length === 0 ? (
              <tr>
                <td colSpan={7 + dynamicAttributeKeys.length} style={{ padding: '32px', textAlign: 'center', color: '#64748b' }}>
                  No employee profiles match the specified filters.
                </td>
              </tr>
            ) : (
              filteredEmployees.map((emp, idx) => (
                <tr
                  key={emp.id || emp.employeeId}
                  style={{
                    background: idx % 2 === 0 ? '#0f172a' : '#172554',
                    borderBottom: '1px solid #1e293b',
                    transition: 'background 0.2s ease'
                  }}
                >
                  <td style={{ padding: '12px 16px', fontWeight: 600, color: '#38bdf8' }}>{emp.employeeId}</td>
                  <td style={{ padding: '12px 16px', fontWeight: 500 }}>{emp.fullName || '—'}</td>
                  <td style={{ padding: '12px 16px', color: '#cbd5e1' }}>{emp.email || '—'}</td>
                  <td style={{ padding: '12px 16px' }}>
                    <span style={{ padding: '2px 6px', background: '#334155', borderRadius: '4px', fontSize: '0.8rem', fontFamily: 'monospace' }}>
                      {emp.nodeId}
                    </span>
                  </td>
                  <td style={{ padding: '12px 16px', color: '#94a3b8' }}>
                    {emp.matrixNodeIds && emp.matrixNodeIds.length > 0 ? emp.matrixNodeIds.join(', ') : '—'}
                  </td>
                  <td style={{ padding: '12px 16px' }}>{renderStatusDropdown(emp)}</td>
                  {dynamicAttributeKeys.map(attrKey => (
                    <td key={attrKey} style={{ padding: '12px 16px', color: '#94a3b8' }}>
                      {emp.attributes && emp.attributes[attrKey] !== undefined ? String(emp.attributes[attrKey]) : '—'}
                    </td>
                  ))}
                  <td style={{ padding: '12px 16px' }}>
                    {onSelectEmployee && (
                      <button
                        onClick={() => onSelectEmployee(emp)}
                        style={{
                          padding: '4px 10px',
                          background: 'transparent',
                          border: '1px solid #38bdf8',
                          color: '#38bdf8',
                          borderRadius: '6px',
                          fontSize: '0.75rem',
                          cursor: 'pointer'
                        }}
                      >
                        Edit
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
