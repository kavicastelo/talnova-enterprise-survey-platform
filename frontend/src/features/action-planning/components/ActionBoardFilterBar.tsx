import React from 'react';
import { Search, Filter, RefreshCw, Layers, Tag, UserCheck } from 'lucide-react';
import { Button } from '../../../components/ui/Button';

interface ActionBoardFilterBarProps {
  nodeId: string;
  setNodeId: (val: string) => void;
  groupId: string;
  setGroupId: (val: string) => void;
  statusFilter: string;
  setStatusFilter: (val: string) => void;
  assigneeId: string;
  setAssigneeId: (val: string) => void;
  searchQuery: string;
  setSearchQuery: (val: string) => void;
  onRefresh: () => void;
  isRefreshing?: boolean;
}

export const ActionBoardFilterBar: React.FC<ActionBoardFilterBarProps> = ({
  nodeId,
  setNodeId,
  groupId,
  setGroupId,
  statusFilter,
  setStatusFilter,
  assigneeId,
  setAssigneeId,
  searchQuery,
  setSearchQuery,
  onRefresh,
  isRefreshing = false,
}) => {
  return (
    <div className="bg-white rounded-xl p-4 shadow-sm border border-slate-200 mb-6 flex flex-col lg:flex-row lg:items-center justify-between gap-4">
      {/* Search Input */}
      <div className="relative flex-1 min-w-[240px]">
        <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search by action title, ID, or description..."
          className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all font-medium"
        />
      </div>

      {/* Filter Selects */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Node Filter */}
        <div className="flex items-center space-x-1.5 bg-slate-50 px-3 py-1.5 border border-slate-200 rounded-lg">
          <Layers className="h-3.5 w-3.5 text-blue-600" />
          <select
            value={nodeId}
            onChange={(e) => setNodeId(e.target.value)}
            className="bg-transparent text-xs font-semibold text-slate-700 focus:outline-none cursor-pointer"
          >
            <option value="">All Org Nodes</option>
            <option value="N-301">Engineering Dept (N-301)</option>
            <option value="GLOBAL_ORG">Global Enterprise Scope</option>
            <option value="IT_DIVISION">IT Division (N-102)</option>
            <option value="HR_OPS">HR Operations (N-204)</option>
          </select>
        </div>

        {/* Question Group / Category Filter */}
        <div className="flex items-center space-x-1.5 bg-slate-50 px-3 py-1.5 border border-slate-200 rounded-lg">
          <Tag className="h-3.5 w-3.5 text-indigo-600" />
          <select
            value={groupId}
            onChange={(e) => setGroupId(e.target.value)}
            className="bg-transparent text-xs font-semibold text-slate-700 focus:outline-none cursor-pointer"
          >
            <option value="">All Category Themes</option>
            <option value="GRP-COMMUNICATION">Communication & Transparency</option>
            <option value="GRP-LEADERSHIP">Executive Leadership</option>
            <option value="GRP-WELLBEING">Workload & Burnout</option>
            <option value="GRP-GROWTH">Career Growth & Mentorship</option>
            <option value="QG-01">Leadership (QG-01)</option>
            <option value="QG-02">Workload (QG-02)</option>
          </select>
        </div>

        {/* Status Filter */}
        <div className="flex items-center space-x-1.5 bg-slate-50 px-3 py-1.5 border border-slate-200 rounded-lg">
          <Filter className="h-3.5 w-3.5 text-amber-600" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="bg-transparent text-xs font-semibold text-slate-700 focus:outline-none cursor-pointer"
          >
            <option value="">All Board Columns</option>
            <option value="DRAFT">Draft</option>
            <option value="PROPOSED">Proposed</option>
            <option value="APPROVED">Approved</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="VERIFIED">Verified</option>
          </select>
        </div>

        {/* Assignee Filter */}
        <div className="flex items-center space-x-1.5 bg-slate-50 px-3 py-1.5 border border-slate-200 rounded-lg">
          <UserCheck className="h-3.5 w-3.5 text-emerald-600" />
          <select
            value={assigneeId}
            onChange={(e) => setAssigneeId(e.target.value)}
            className="bg-transparent text-xs font-semibold text-slate-700 focus:outline-none cursor-pointer"
          >
            <option value="">All Assignees</option>
            <option value="EMP-1002">Alex Rivera (EMP-1002)</option>
            <option value="EMP-1005">Sarah Chen (EMP-1005)</option>
            <option value="EMP-1009">Marcus Vance (EMP-1009)</option>
            <option value="EMP-9021">David Kim (EMP-9021)</option>
          </select>
        </div>

        {/* Refresh Button */}
        <Button
          variant="outline"
          size="sm"
          onClick={onRefresh}
          isLoading={isRefreshing}
          className="flex items-center gap-1.5 border-slate-200 hover:bg-slate-50 text-xs font-semibold"
        >
          <RefreshCw className={`h-3.5 w-3.5 ${isRefreshing ? 'animate-spin' : ''}`} />
          Refresh
        </Button>
      </div>
    </div>
  );
};
