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
  rowNodes: Array<string | { id: string; name: string }>;
  columnThemes: Array<string | { id: string; name: string }>;
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

  const normalizedRowNodes = (data.rowNodes || []).map((n) =>
    typeof n === 'string' ? { id: n, name: n } : n
  );
  const normalizedColumnThemes = (data.columnThemes || []).map((c) =>
    typeof c === 'string' ? { id: c, name: c } : c
  );

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
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-900 tracking-wide">
            2D Organizational Heatmap Matrix
          </h2>
          <p className="text-xs text-slate-500">
            Cross-tabulated Department Nodes vs Engagement Themes (Differential Privacy Guard Active for N &lt; 5)
          </p>
        </div>

        {/* Demographic Slicers (Max 5 per VR-ANL-004) */}
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-xs font-semibold text-slate-600">Demographic Cohort Slicers ({Object.keys(demographicFilters).length}/5):</span>
          
          <select
            className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
            onChange={(e) => handleDemographicChange('Tenure', e.target.value)}
            value={demographicFilters['Tenure'] || ''}
          >
            <option value="">All Tenures</option>
            <option value="<1 Year">&lt; 1 Year</option>
            <option value="1-3 Years">1-3 Years</option>
            <option value="3-5 Years">3-5 Years</option>
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
            <option value="Non-Binary">Non-Binary</option>
          </select>

          <select
            className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
            onChange={(e) => handleDemographicChange('Location', e.target.value)}
            value={demographicFilters['Location'] || ''}
          >
            <option value="">All Locations</option>
            <option value="HQ - New York">HQ - New York</option>
            <option value="Factory B - Austin">Factory B - Austin</option>
            <option value="EMEA - London">EMEA - London</option>
            <option value="APAC - Singapore">APAC - Singapore</option>
          </select>

          <select
            className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
            onChange={(e) => handleDemographicChange('AgeGroup', e.target.value)}
            value={demographicFilters['AgeGroup'] || ''}
          >
            <option value="">All Age Groups</option>
            <option value="18-29">18-29 Years</option>
            <option value="30-44">30-44 Years</option>
            <option value="45+">45+ Years</option>
          </select>

          {Object.keys(demographicFilters).length > 0 && (
            <button
              onClick={() => {
                setDemographicFilters({});
                if (onFilterChange) onFilterChange({});
              }}
              className="text-[11px] font-bold text-rose-600 hover:text-rose-800 underline ml-1"
            >
              Clear Slicers
            </button>
          )}
        </div>
      </div>

      {/* Color Intensity Score Legend */}
      <div className="flex flex-wrap items-center gap-4 bg-slate-50 border border-slate-200 p-3 rounded-lg text-xs">
        <span className="font-bold text-slate-700">Heatmap Score Legend:</span>
        <div className="flex items-center gap-1.5">
          <span className="w-3 h-3 rounded-sm bg-emerald-300 border border-emerald-500 inline-block" />
          <span className="text-slate-700">&gt; 70% High Engagement</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="w-3 h-3 rounded-sm bg-amber-300 border border-amber-500 inline-block" />
          <span className="text-slate-700">50 - 70% Moderate / Concern</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="w-3 h-3 rounded-sm bg-rose-300 border border-rose-500 inline-block" />
          <span className="text-slate-700">&lt; 50% At-Risk Department</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="w-3 h-3 rounded-sm bg-slate-200 border border-slate-400 inline-block" />
          <span className="text-slate-500">Suppressed (Sample N &lt; 5)</span>
        </div>
      </div>

      {/* 2D Matrix Table */}
      {normalizedRowNodes.length === 0 ? (
        <div className="p-8 text-center bg-slate-50 border border-dashed border-slate-300 rounded-lg text-slate-500 text-xs">
          No department sub-nodes found for active scope <span className="font-mono">{data.parentNodeId}</span>.
        </div>
      ) : (
        <div className="overflow-x-auto border border-slate-200 rounded-lg">
          <table className="w-full border-collapse text-xs text-left">
            <thead>
              <tr className="border-b border-slate-200">
                <th className="p-3 bg-slate-50 text-slate-700 font-bold uppercase tracking-wider">
                  Department / Node Scope
                </th>
                {normalizedColumnThemes.map((theme) => (
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
              {normalizedRowNodes.map((node) => (
                <tr key={node.id} className="hover:bg-slate-50 transition">
                  <td className="p-3 font-semibold text-slate-900 whitespace-nowrap">
                    <div className="flex items-center gap-2">
                      <span className="font-bold">{node.name}</span>
                      <span className="text-[10px] text-slate-400 font-mono">({node.id})</span>
                    </div>
                  </td>
                  {normalizedColumnThemes.map((theme) => {
                    const cell = findCell(node.id, theme.id);
                    const color = cell ? cell.colorIntensity : 'GREY';

                    return (
                      <td key={theme.id} className="p-1.5 text-center">
                        <button
                          disabled={!cell}
                          onClick={() => cell && setSelectedCell(cell)}
                          title={
                            cell
                              ? cell.score !== null
                                ? `${theme.name} Score: ${cell.score.toFixed(1)}% (N=${cell.sampleSize})`
                                : `Suppressed: Sample Size N=${cell.sampleSize} < 5`
                              : 'No data'
                          }
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
      )}

      {/* Cell Detail Modal */}
      {selectedCell && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white border border-slate-200 rounded-xl p-6 max-w-sm w-full space-y-4 text-slate-900 shadow-2xl">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">Heatmap Cell Details</h3>
              <span
                className={`px-2 py-0.5 rounded text-[10px] font-bold border ${getCellStyles(
                  selectedCell.colorIntensity
                )}`}
              >
                {selectedCell.colorIntensity}
              </span>
            </div>

            <div className="space-y-2.5 text-xs text-slate-700">
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Node Scope ID:</span>
                <span className="font-mono font-bold text-slate-800">{selectedCell.nodeId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Theme Group ID:</span>
                <span className="font-mono font-bold text-slate-800">{selectedCell.groupId}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Sample Size (N):</span>
                <span className="font-bold text-indigo-600">{selectedCell.sampleSize} Responses</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Engagement Score:</span>
                <span className="font-extrabold text-emerald-600 text-sm">
                  {selectedCell.score !== null ? `${selectedCell.score.toFixed(1)}%` : 'SUPPRESSED'}
                </span>
              </div>
              {selectedCell.sampleSize < 5 && (
                <div className="bg-amber-50 border border-amber-200 text-amber-800 p-2.5 rounded text-[11px] font-medium leading-normal">
                  ⚠️ Privacy Protection: Department sample size N={selectedCell.sampleSize} is less than minimum threshold (N &lt; 5). Numerical score is hidden per BR-ANL-001.
                </div>
              )}
            </div>
            <button
              onClick={() => setSelectedCell(null)}
              className="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-xs font-bold text-white rounded-lg transition"
            >
              Close Details
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
