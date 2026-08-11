import React, { useState, useEffect } from 'react';
import { useCreateActionPlanMutation } from '../../features/action-planning/api/useActionPlanningQueries';
import { actionPlanningApi } from '../../features/action-planning/api/actionPlanningApi';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { ActionTemplate, ActionMilestone } from '../../types/actionPlanning';
import { Sparkles, Calendar, Layers, Tag, Plus, Trash2, AlertCircle } from 'lucide-react';


interface ActionPlanCreateModalProps {
  isOpen: boolean;
  projectId: string;
  onClose: () => void;
}

export const ActionPlanCreateModal: React.FC<ActionPlanCreateModalProps> = ({
  isOpen,
  projectId,
  onClose,
}) => {
  const [nodeId, setNodeId] = useState<string>('N-301');
  const [groupId, setGroupId] = useState<string>('GRP-COMMUNICATION');
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [baselineScore, setBaselineScore] = useState<number>(52.0);
  const [targetScore, setTargetScore] = useState<number>(75.0);
  const [assigneeId, setAssigneeId] = useState<string>('EMP-1002');
  
  // Default target date 30 days in future
  const defaultFutureDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
  const [targetCompletionDate, setTargetCompletionDate] = useState<string>(defaultFutureDate);

  // Milestones state
  const [milestones, setMilestones] = useState<ActionMilestone[]>([
    {
      milestoneId: 'MS-1',
      title: 'Schedule initial team Q&A huddle',
      status: 'PENDING',
      dueDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    },
  ]);
  const [newMilestoneTitle, setNewMilestoneTitle] = useState<string>('');

  // AI Templates state
  const [templates, setTemplates] = useState<ActionTemplate[]>([]);
  const [isLoadingTemplates, setIsLoadingTemplates] = useState<boolean>(false);
  const [selectedTemplateId, setSelectedTemplateId] = useState<string | null>(null);
  const [validationError, setValidationError] = useState<string | null>(null);

  const createMutation = useCreateActionPlanMutation();

  // Load AI Templates when category/groupId changes
  useEffect(() => {
    if (isOpen && groupId) {
      loadAiTemplates(groupId);
    }
  }, [isOpen, groupId]);

  const loadAiTemplates = async (targetGroup: string) => {
    setIsLoadingTemplates(true);
    try {
      const recs = await actionPlanningApi.getTemplateRecommendations(targetGroup);
      setTemplates(recs);
    } catch (err) {
      console.error('Failed to load AI recommendations', err);
    } finally {
      setIsLoadingTemplates(false);
    }
  };

  const handleApplyTemplate = (tpl: ActionTemplate) => {
    setSelectedTemplateId(tpl.templateId);
    setTitle(tpl.title);
    setDescription(tpl.description);
    setTargetScore(Math.min(baselineScore + 18, 95));

    if (tpl.milestones && tpl.milestones.length > 0) {
      setMilestones(tpl.milestones);
    } else {
      setMilestones([
        {
          milestoneId: 'MS-1',
          title: `Initiate ${tpl.title}`,
          status: 'PENDING',
          dueDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        },
        {
          milestoneId: 'MS-2',
          title: 'Review milestone progress & team feedback',
          status: 'PENDING',
          dueDate: new Date(Date.now() + 21 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        },
      ]);
    }
  };

  const handleAddMilestone = () => {
    if (!newMilestoneTitle.trim()) return;
    const newMs: ActionMilestone = {
      milestoneId: `MS-${milestones.length + 1}`,
      title: newMilestoneTitle.trim(),
      status: 'PENDING',
      dueDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    };
    setMilestones([...milestones, newMs]);
    setNewMilestoneTitle('');
  };

  const handleRemoveMilestone = (index: number) => {
    setMilestones(milestones.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-ACT-002: Completion date must be in future (min 7 days)
    const targetDateObj = new Date(targetCompletionDate);
    const minDate = new Date(Date.now() + 6 * 24 * 60 * 60 * 1000);
    if (isNaN(targetDateObj.getTime()) || targetDateObj < minDate) {
      setValidationError('Target completion date must be at least 7 days in the future (VR-ACT-002).');
      return;
    }

    // VR-ACT-003: Milestone task required
    if (milestones.length === 0) {
      setValidationError('At least one milestone task is required before creating an Action Plan (VR-ACT-003).');
      return;
    }

    createMutation.mutate(
      {
        projectId,
        campaignId: 'CMP-1001',
        nodeId,
        groupId,
        title,
        description,
        baselineScore,
        targetScore,
        assigneeId,
        targetCompletionDate: new Date(targetCompletionDate).toISOString(),
        milestones,
        status: 'DRAFT',
      },
      {
        onSuccess: () => {
          onClose();
        },
      }
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Create Remediation Action Plan">
      <form onSubmit={handleSubmit} className="space-y-4">
        {validationError && (
          <div className="bg-red-50 border border-red-200 text-red-800 p-3 rounded-lg text-xs font-semibold flex items-center gap-2">
            <AlertCircle className="h-4 w-4 text-red-600 shrink-0" />
            <span>{validationError}</span>
          </div>
        )}

        {/* AI Action Template Recommender */}
        <div className="bg-gradient-to-r from-blue-50 via-indigo-50 to-purple-50 p-3.5 rounded-xl border border-blue-200">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center space-x-1.5">
              <Sparkles className="h-4 w-4 text-blue-600" />
              <h4 className="text-xs font-extrabold text-slate-900 uppercase tracking-wider">
                Daash AI Template Recommender (US-ACT-002)
              </h4>
            </div>
            {isLoadingTemplates && <span className="text-[10px] font-semibold text-blue-600 animate-pulse">Fetching...</span>}
          </div>
          <p className="text-[11px] text-slate-600 mb-2">
            Pre-validated action templates derived from Daash Global consulting methodologies tailored to category deficits.
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            {templates.length > 0 ? (
              templates.map((tpl) => (
                <div
                  key={tpl.templateId}
                  onClick={() => handleApplyTemplate(tpl)}
                  className={`p-2.5 bg-white rounded-lg border text-xs cursor-pointer transition-all hover:border-blue-500 hover:shadow-sm ${
                    selectedTemplateId === tpl.templateId ? 'ring-2 ring-blue-500 border-blue-500 bg-blue-50/50' : 'border-slate-200'
                  }`}
                >
                  <p className="font-bold text-slate-900 line-clamp-1">{tpl.title}</p>
                  <p className="text-[10px] text-slate-500 line-clamp-2 mt-0.5">{tpl.description}</p>
                </div>
              ))
            ) : (
              <div className="col-span-2 text-center py-2 text-[11px] text-slate-400 italic">
                Select a category group below to view AI recommended templates
              </div>
            )}
          </div>
        </div>

        {/* Scope Selectors: Node & Group */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="block text-[11px] font-bold uppercase text-slate-600 mb-1 flex items-center gap-1">
              <Layers className="h-3 w-3 text-blue-600" /> Target Org Node
            </label>
            <select
              value={nodeId}
              onChange={(e) => setNodeId(e.target.value)}
              className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-semibold text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="N-301">Engineering Dept (N-301)</option>
              <option value="GLOBAL_ORG">Global Enterprise Scope</option>
              <option value="IT_DIVISION">IT Division (N-102)</option>
              <option value="HR_OPS">HR Operations (N-204)</option>
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold uppercase text-slate-600 mb-1 flex items-center gap-1">
              <Tag className="h-3 w-3 text-indigo-600" /> Category Theme / Group
            </label>
            <select
              value={groupId}
              onChange={(e) => setGroupId(e.target.value)}
              className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-semibold text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="GRP-COMMUNICATION">Communication & Transparency</option>
              <option value="GRP-LEADERSHIP">Executive Leadership</option>
              <option value="GRP-WELLBEING">Workload & Burnout</option>
              <option value="GRP-GROWTH">Career Growth & Mentorship</option>
            </select>
          </div>
        </div>

        {/* Title & Description */}
        <div>
          <label className="block text-[11px] font-bold uppercase text-slate-600 mb-1">Action Plan Title *</label>
          <input
            type="text"
            required
            placeholder="e.g. Senior Leadership Weekly Transparency Town Halls"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 font-semibold focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-[11px] font-bold uppercase text-slate-600 mb-1">Description & Scope</label>
          <textarea
            rows={2}
            placeholder="Detailed description of planned interventions..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Scores & Assignee */}
        <div className="grid grid-cols-3 gap-3">
          <div>
            <label className="block text-[10px] font-bold uppercase text-slate-600 mb-1">Baseline Score (%)</label>
            <input
              type="number"
              step="0.1"
              required
              value={baselineScore}
              onChange={(e) => setBaselineScore(parseFloat(e.target.value))}
              className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-bold text-red-600 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-slate-600 mb-1">Target Score (%)</label>
            <input
              type="number"
              step="0.1"
              required
              value={targetScore}
              onChange={(e) => setTargetScore(parseFloat(e.target.value))}
              className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-bold text-emerald-600 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-slate-600 mb-1">Assignee</label>
            <select
              value={assigneeId}
              onChange={(e) => setAssigneeId(e.target.value)}
              className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-semibold text-slate-800"
            >
              <option value="EMP-1002">Alex Rivera (EMP-1002)</option>
              <option value="EMP-1005">Sarah Chen (EMP-1005)</option>
              <option value="EMP-1009">Marcus Vance (EMP-1009)</option>
            </select>
          </div>
        </div>

        {/* Target Completion Date */}
        <div>
          <label className="block text-[11px] font-bold uppercase text-slate-600 mb-1 flex items-center gap-1">
            <Calendar className="h-3 w-3 text-slate-500" /> Target Completion Date (VR-ACT-002)
          </label>
          <input
            type="date"
            required
            value={targetCompletionDate}
            onChange={(e) => setTargetCompletionDate(e.target.value)}
            className="w-full p-2 bg-slate-50 border border-slate-200 rounded-lg text-xs font-semibold text-slate-800 focus:outline-none"
          />
        </div>

        {/* Milestones Checklist */}
        <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
          <label className="block text-[11px] font-bold uppercase text-slate-700 mb-1.5">
            Initial Milestone Checklist (VR-ACT-003)
          </label>

          <div className="space-y-1.5 mb-2 max-h-32 overflow-y-auto">
            {milestones.map((ms, index) => (
              <div key={ms.milestoneId} className="flex items-center justify-between bg-white p-2 rounded-lg border border-slate-200 text-xs">
                <span className="font-semibold text-slate-800 truncate flex-1">{ms.title}</span>
                <button
                  type="button"
                  onClick={() => handleRemoveMilestone(index)}
                  className="text-slate-400 hover:text-red-600 p-1"
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </button>
              </div>
            ))}
          </div>

          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Add milestone task..."
              value={newMilestoneTitle}
              onChange={(e) => setNewMilestoneTitle(e.target.value)}
              className="flex-1 p-1.5 bg-white border border-slate-200 rounded-lg text-xs font-medium focus:outline-none"
            />
            <Button variant="outline" type="button" size="sm" onClick={handleAddMilestone} className="text-xs py-1">
              <Plus className="h-3 w-3" /> Add Task
            </Button>
          </div>
        </div>

        {/* Form Controls */}
        <div className="flex justify-end space-x-2 pt-2 border-t border-slate-100">
          <Button variant="outline" type="button" onClick={onClose} disabled={createMutation.isPending}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" isLoading={createMutation.isPending}>
            Create Action Plan
          </Button>
        </div>
      </form>
    </Modal>
  );
};
