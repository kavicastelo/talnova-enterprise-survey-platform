import React, { useState } from 'react';

export interface HeatmapCell {
  nodeId: string;
  groupId: string;
  sampleSize: number;
  score: number | null;
  colorIntensity: 'RED' | 'YELLOW' | 'GREEN' | 'GREY';
}

export interface HeatmapData {
  parentNodeId: string;
  rowNodes: Array<{ id: string; name: string }>;
  columnThemes: Array<{ id: string; name: string }>;
  cells: HeatmapCell[];
}

interface OrganizationalHeatmapGridProps {
  data: HeatmapData;
  onFilterChange?: (filters: Record<string, string>) => void;
  isLoading?: boolean;
}

export const OrganizationalHeatmapGrid: React.FC<OrganizationalHeatmapGridProps> = ({
  data,
  onFilterChange,
  isLoading,
}) => {
  const [selectedCell, setSelectedCell] = useState<HeatmapCell | null>(null);
  const [demographicFilters, setDemographicFilters] = useState<Record<string, string>>({});

  if (isLoading) {
    return (
      <div className="p-6 bg-white border border-slate-200 rounded-xl animate-pulse text-slate-700 shadow-sm">
        Loading 2D Organizational Heatmap Grid...
      </div>
    );
  }

  const findCell = (nodeId: string, groupId: string): HeatmapCell | undefined => {
    return data.cells.find((c) => c.nodeId === nodeId && c.groupId === groupId);
  };

  const getCellStyles = (color: 'RED' | 'YELLOW' | 'GREEN' | 'GREY') => {
    switch (color) {
      case 'RED':
        return 'bg-rose-100 text-rose-900 border-rose-300 hover:bg-rose-200';
      case 'YELLOW':
        return 'bg-amber-100 text-amber-900 border-amber-300 hover:bg-amber-200';
      case 'GREEN':
        return 'bg-emerald-100 text-emerald-900 border-emerald-300 hover:bg-emerald-200';
      case 'GREY':
      default:
        return 'bg-slate-100 text-slate-400 border-slate-200 cursor-not-allowed';
    }
  };

  const handleDemographicChange = (key: string, value: string) => {
    const updated = { ...demographicFilters };
    if (value) {
      if (Object.keys(updated).length >= 5 && !updated[key]) {
        alert('Maximum 5 demographic slicer filters allowed per VR-ANL-004');
        return;
      }
      updated[key] = value;
    } else {
      delete updated[key];
    }
    setDemographicFilters(updated);
    if (onFilterChange) onFilterChange(updated);
  };

  return (
    <div className="p-6 bg-white border border-slate-200 rounded-xl shadow-sm space-y-6">
      {/* Header & Demographic Filter Slicers */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-900 tracking-wide">
            2D Organizational Heatmap Matrix
          </h2>
          <p className="text-xs text-slate-500">
            Cross-tabulated Department Nodes vs Question Group Themes (Suppressed if N &lt; 5)
          </p>
        </div>

        {/* Demographic Slicers (Max 5) */}
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-xs font-semibold text-slate-600">Slicers ({Object.keys(demographicFilters).length}/5):</span>
          <select
            className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
            onChange={(e) => handleDemographicChange('Tenure', e.target.value)}
            value={demographicFilters['Tenure'] || ''}
          >
            <option value="">All Tenures</option>
            <option value="<1 Year">&lt; 1 Year</option>
            <option value="1-3 Years">1-3 Years</option>
            <option value="5+ Years">5+ Years</option>
          </select>

          <select
            className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
            onChange={(e) => handleDemographicChange('Gender', e.target.value)}
            value={demographicFilters['Gender'] || ''}
          >
            <option value="">All Genders</option>
            <option value="Female">Female</option>
            <option value="Male">Male</option>
          </select>
        </div>
      </div>

      {/* 2D Matrix Table */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full border-collapse text-xs text-left">
          <thead>
            <tr className="border-b border-slate-200">
              <th className="p-3 bg-slate-50 text-slate-700 font-bold uppercase tracking-wider">
                Department / Node
              </th>
              {data.columnThemes.map((theme) => (
                <th
                  key={theme.id}
                  className="p-3 bg-slate-50 text-slate-700 font-bold uppercase tracking-wider text-center"
                >
                  {theme.name}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.rowNodes.map((node) => (
              <tr key={node.id} className="hover:bg-slate-50 transition">
                <td className="p-3 font-semibold text-slate-900 whitespace-nowrap">
                  {node.name}
                </td>
                {data.columnThemes.map((theme) => {
                  const cell = findCell(node.id, theme.id);
                  const color = cell ? cell.colorIntensity : 'GREY';

                  return (
                    <td key={theme.id} className="p-1.5 text-center">
                      <button
                        disabled={!cell || cell.sampleSize < 5}
                        onClick={() => cell && setSelectedCell(cell)}
                        className={`w-full py-2.5 px-3 rounded-lg border font-extrabold text-sm transition ${getCellStyles(
                          color
                        )}`}
                      >
                        {cell && cell.score !== null ? `${cell.score.toFixed(1)}%` : '—'}
                      </button>
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Cell Detail Modal */}
      {selectedCell && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white border border-slate-200 rounded-xl p-6 max-w-sm w-full space-y-4 text-slate-900 shadow-2xl">
            <h3 className="text-base font-bold text-slate-900">Heatmap Cell Details</h3>
            <div className="space-y-2 text-xs text-slate-700">
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Node ID:</span>
                <span className="font-mono text-slate-800">{selectedCell.nodeId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Theme Group ID:</span>
                <span className="font-mono text-slate-800">{selectedCell.groupId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Sample Size (N):</span>
                <span className="font-bold text-indigo-600">{selectedCell.sampleSize} Responses</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Score:</span>
                <span className="font-extrabold text-emerald-600">
                  {selectedCell.score !== null ? `${selectedCell.score.toFixed(1)}%` : 'Suppressed'}
                </span>
              </div>
            </div>
            <button
              onClick={() => setSelectedCell(null)}
              className="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-xs font-bold text-white rounded-lg transition"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
