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
      <div className="p-6 bg-slate-900 border border-slate-800 rounded-xl animate-pulse text-white">
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
        return 'bg-rose-950/80 text-rose-300 border-rose-800 hover:bg-rose-900';
      case 'YELLOW':
        return 'bg-amber-950/80 text-amber-300 border-amber-800 hover:bg-amber-900';
      case 'GREEN':
        return 'bg-emerald-950/80 text-emerald-300 border-emerald-800 hover:bg-emerald-900';
      case 'GREY':
      default:
        return 'bg-slate-800/80 text-slate-500 border-slate-700 cursor-not-allowed';
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
    <div className="p-6 bg-slate-900 border border-slate-800 rounded-xl shadow-xl space-y-6">
      {/* Header & Demographic Filter Slicers */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-white tracking-wide">
            2D Organizational Heatmap Matrix
          </h2>
          <p className="text-xs text-slate-400">
            Cross-tabulated Department Nodes vs Question Group Themes (Suppressed if N &lt; 5)
          </p>
        </div>

        {/* Demographic Slicers (Max 5) */}
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-xs font-semibold text-slate-400">Slicers ({Object.keys(demographicFilters).length}/5):</span>
          <select
            className="bg-slate-800 border border-slate-700 text-xs text-slate-200 rounded px-2.5 py-1"
            onChange={(e) => handleDemographicChange('Tenure', e.target.value)}
            value={demographicFilters['Tenure'] || ''}
          >
            <option value="">All Tenures</option>
            <option value="<1 Year">&lt; 1 Year</option>
            <option value="1-3 Years">1-3 Years</option>
            <option value="5+ Years">5+ Years</option>
          </select>

          <select
            className="bg-slate-800 border border-slate-700 text-xs text-slate-200 rounded px-2.5 py-1"
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
      <div className="overflow-x-auto">
        <table className="w-full border-collapse text-xs text-left">
          <thead>
            <tr className="border-b border-slate-800">
              <th className="p-3 bg-slate-950/80 text-slate-300 font-bold uppercase tracking-wider">
                Department / Node
              </th>
              {data.columnThemes.map((theme) => (
                <th
                  key={theme.id}
                  className="p-3 bg-slate-950/80 text-slate-300 font-bold uppercase tracking-wider text-center"
                >
                  {theme.name}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {data.rowNodes.map((node) => (
              <tr key={node.id} className="hover:bg-slate-850/50 transition">
                <td className="p-3 font-semibold text-slate-200 whitespace-nowrap">
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
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 max-w-sm w-full space-y-4 text-white shadow-2xl">
            <h3 className="text-base font-bold text-white">Heatmap Cell Details</h3>
            <div className="space-y-2 text-xs text-slate-300">
              <div className="flex justify-between border-b border-slate-800 pb-1">
                <span className="text-slate-400">Node ID:</span>
                <span className="font-mono">{selectedCell.nodeId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-800 pb-1">
                <span className="text-slate-400">Theme Group ID:</span>
                <span className="font-mono">{selectedCell.groupId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-800 pb-1">
                <span className="text-slate-400">Sample Size (N):</span>
                <span className="font-bold text-indigo-400">{selectedCell.sampleSize} Responses</span>
              </div>
              <div className="flex justify-between border-b border-slate-800 pb-1">
                <span className="text-slate-400">Score:</span>
                <span className="font-extrabold text-emerald-400">
                  {selectedCell.score !== null ? `${selectedCell.score.toFixed(1)}%` : 'Suppressed'}
                </span>
              </div>
            </div>
            <button
              onClick={() => setSelectedCell(null)}
              className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-xs font-bold rounded-lg transition"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
