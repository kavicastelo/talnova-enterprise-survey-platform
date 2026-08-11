import React from 'react';
import { DragDropContext, Droppable, Draggable, DropResult } from '@hello-pangea/dnd';
import { ActionStatus, ActionPlanResponse } from '../../../types/actionPlanning';
import { Button } from '../../../components/ui/Button';
import {
  FileText,
  Clock,
  CheckCircle2,
  PlayCircle,
  Award,
  ExternalLink,
  UserCheck,
  Layers,
  Sparkles,
} from 'lucide-react';


export interface ColumnDefinition {
  key: ActionStatus;
  label: string;
  badgeBg: string;
  badgeText: string;
  columnBg: string;
  borderColor: string;
  icon: React.ReactNode;
}

export const KANBAN_COLUMNS: ColumnDefinition[] = [
  {
    key: 'DRAFT',
    label: 'Draft',
    badgeBg: 'bg-slate-100',
    badgeText: 'text-slate-700',
    columnBg: 'bg-slate-50/80',
    borderColor: 'border-slate-200',
    icon: <FileText className="h-3.5 w-3.5 text-slate-500" />,
  },
  {
    key: 'PROPOSED',
    label: 'Proposed',
    badgeBg: 'bg-amber-100',
    badgeText: 'text-amber-800',
    columnBg: 'bg-amber-50/40',
    borderColor: 'border-amber-200',
    icon: <Clock className="h-3.5 w-3.5 text-amber-600" />,
  },
  {
    key: 'APPROVED',
    label: 'Approved HR',
    badgeBg: 'bg-blue-100',
    badgeText: 'text-blue-800',
    columnBg: 'bg-blue-50/40',
    borderColor: 'border-blue-200',
    icon: <CheckCircle2 className="h-3.5 w-3.5 text-blue-600" />,
  },
  {
    key: 'IN_PROGRESS',
    label: 'In Progress',
    badgeBg: 'bg-indigo-100',
    badgeText: 'text-indigo-800',
    columnBg: 'bg-indigo-50/40',
    borderColor: 'border-indigo-200',
    icon: <PlayCircle className="h-3.5 w-3.5 text-indigo-600" />,
  },
  {
    key: 'COMPLETED',
    label: 'Completed',
    badgeBg: 'bg-purple-100',
    badgeText: 'text-purple-800',
    columnBg: 'bg-purple-50/40',
    borderColor: 'border-purple-200',
    icon: <CheckCircle2 className="h-3.5 w-3.5 text-purple-600" />,
  },
  {
    key: 'VERIFIED',
    label: 'Verified (+Δ Score)',
    badgeBg: 'bg-emerald-100',
    badgeText: 'text-emerald-800',
    columnBg: 'bg-emerald-50/40',
    borderColor: 'border-emerald-200',
    icon: <Award className="h-3.5 w-3.5 text-emerald-600" />,
  },
];

interface ActionKanbanBoardProps {
  cards: ActionPlanResponse[];
  onSelectCard: (card: ActionPlanResponse) => void;
  onRequestTransition: (card: ActionPlanResponse, targetStatus: ActionStatus) => void;
}

export const ActionKanbanBoard: React.FC<ActionKanbanBoardProps> = ({
  cards,
  onSelectCard,
  onRequestTransition,
}) => {
  const handleDragEnd = (result: DropResult) => {
    const { destination, source, draggableId } = result;

    if (!destination) return;
    if (destination.droppableId === source.droppableId) return;

    const draggedCard = cards.find((c) => c.actionPlanId === draggableId);
    if (!draggedCard) return;

    const newStatus = destination.droppableId as ActionStatus;
    onRequestTransition(draggedCard, newStatus);
  };

  return (
    <DragDropContext onDragEnd={handleDragEnd}>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 items-start pb-6">
        {KANBAN_COLUMNS.map((col) => {
          const colCards = cards.filter((c) => c.status === col.key);

          return (
            <Droppable key={col.key} droppableId={col.key}>
              {(provided, snapshot) => (
                <div
                  ref={provided.innerRef}
                  {...provided.droppableProps}
                  className={`flex flex-col rounded-xl ${col.columnBg} p-3 border ${col.borderColor} min-h-[580px] transition-colors ${
                    snapshot.isDraggingOver ? 'ring-2 ring-blue-400 bg-blue-50/30' : ''
                  }`}
                >
                  {/* Column Header */}
                  <div className="flex items-center justify-between mb-3 pb-2 border-b border-slate-200/80">
                    <div className="flex items-center space-x-1.5">
                      {col.icon}
                      <h3 className="text-xs font-extrabold text-slate-800 tracking-tight">{col.label}</h3>
                    </div>
                    <span
                      className={`px-2 py-0.5 rounded-full text-[10px] font-extrabold ${col.badgeBg} ${col.badgeText} border border-slate-200`}
                    >
                      {colCards.length}
                    </span>
                  </div>

                  {/* Cards List */}
                  <div className="flex-1 space-y-3">
                    {colCards.length === 0 ? (
                      <div className="h-32 border-2 border-dashed border-slate-200 rounded-lg flex flex-col items-center justify-center p-4 text-center">
                        <p className="text-[11px] font-semibold text-slate-400 italic">No action plans</p>
                        <p className="text-[10px] text-slate-300 mt-0.5">Drag card here to update</p>
                      </div>
                    ) : (
                      colCards.map((card, index) => (
                        <Draggable key={card.actionPlanId} draggableId={card.actionPlanId} index={index}>
                          {(dragProvided, dragSnapshot) => (
                            <div
                              ref={dragProvided.innerRef}
                              {...dragProvided.draggableProps}
                              {...dragProvided.dragHandleProps}
                              onClick={() => onSelectCard(card)}
                              className={`group relative rounded-xl bg-white p-3.5 shadow-sm border border-slate-200/90 hover:shadow-md hover:border-blue-300 transition-all cursor-pointer ${
                                dragSnapshot.isDragging ? 'shadow-xl ring-2 ring-blue-500 rotate-1 scale-[1.02] z-50' : ''
                              }`}
                            >
                              {/* Header Badges */}
                              <div className="flex items-center justify-between gap-1 mb-2">
                                <span className="font-mono text-[10px] font-bold text-blue-600 bg-blue-50 px-1.5 py-0.5 rounded border border-blue-100">
                                  {card.actionPlanId}
                                </span>
                                <div className="flex items-center space-x-1">
                                  {card.externalSyncSystem && (
                                    <span className="px-1.5 py-0.5 text-[9px] font-bold bg-indigo-50 text-indigo-700 border border-indigo-200 rounded flex items-center gap-1">
                                      <ExternalLink className="h-2.5 w-2.5" />
                                      {card.externalSyncSystem}
                                    </span>
                                  )}
                                  {card.nodeId && (
                                    <span className="px-1.5 py-0.5 text-[9px] font-semibold bg-slate-100 text-slate-600 rounded flex items-center gap-0.5">
                                      <Layers className="h-2.5 w-2.5" />
                                      {card.nodeId}
                                    </span>
                                  )}
                                </div>
                              </div>

                              {/* Title & Description */}
                              <h4 className="text-xs font-bold text-slate-900 line-clamp-2 leading-snug group-hover:text-blue-600 transition-colors">
                                {card.title}
                              </h4>
                              {card.description && (
                                <p className="mt-1 text-[11px] text-slate-500 line-clamp-2 leading-tight font-normal">
                                  {card.description}
                                </p>
                              )}

                              {/* Score Target Progress */}
                              <div className="mt-3 pt-2.5 border-t border-slate-100">
                                <div className="flex justify-between items-center text-[10px] font-semibold mb-1">
                                  <span className="text-slate-500">Baseline vs Target Score</span>
                                  <span>
                                    <strong className="text-red-500">{card.baselineScore ?? 50}%</strong>
                                    {' → '}
                                    <strong className="text-emerald-600">{card.targetScore ?? 75}%</strong>
                                  </span>
                                </div>
                                <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden flex">
                                  <div
                                    className="bg-red-400 h-full"
                                    style={{ width: `${Math.min(card.baselineScore || 50, 100)}%` }}
                                  />
                                  <div
                                    className="bg-emerald-400 h-full opacity-60"
                                    style={{
                                      width: `${Math.max(
                                        (card.targetScore || 75) - (card.baselineScore || 50),
                                        0
                                      )}%`,
                                    }}
                                  />
                                </div>
                              </div>

                              {/* Footer Meta */}
                              <div className="mt-3 flex items-center justify-between pt-2 border-t border-slate-100 text-[10px] text-slate-500">
                                <span className="flex items-center gap-1 font-medium">
                                  <UserCheck className="h-3 w-3 text-slate-400" />
                                  {card.assigneeId || 'Unassigned'}
                                </span>
                                <span className="font-semibold text-slate-600 bg-slate-50 px-1.5 py-0.5 rounded border border-slate-200">
                                  {card.milestones?.length || card.milestoneCount || 0} tasks
                                </span>
                              </div>

                              {/* Quick Action Buttons based on Status */}
                              <div className="mt-3 pt-2 border-t border-slate-100 flex items-center justify-between" onClick={(e) => e.stopPropagation()}>
                                {card.status === 'DRAFT' && (
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    className="w-full text-[10px] py-1 h-7 border-amber-300 text-amber-800 hover:bg-amber-50"
                                    onClick={() => onRequestTransition(card, 'PROPOSED')}
                                  >
                                    Submit Proposal →
                                  </Button>
                                )}
                                {card.status === 'PROPOSED' && (
                                  <div className="flex w-full space-x-1.5">
                                    <Button
                                      variant="primary"
                                      size="sm"
                                      className="flex-1 text-[10px] py-1 h-7 bg-emerald-600 hover:bg-emerald-700"
                                      onClick={() => onRequestTransition(card, 'APPROVED')}
                                    >
                                      ✓ Approve HR
                                    </Button>
                                    <Button
                                      variant="danger"
                                      size="sm"
                                      className="text-[10px] py-1 h-7 px-2"
                                      onClick={() => onRequestTransition(card, 'REJECTED')}
                                    >
                                      Reject
                                    </Button>
                                  </div>
                                )}
                                {card.status === 'APPROVED' && (
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    className="w-full text-[10px] py-1 h-7 border-indigo-300 text-indigo-800 hover:bg-indigo-50"
                                    onClick={() => onRequestTransition(card, 'IN_PROGRESS')}
                                  >
                                    Start Progress ▶
                                  </Button>
                                )}
                                {card.status === 'IN_PROGRESS' && (
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    className="w-full text-[10px] py-1 h-7 border-purple-300 text-purple-800 hover:bg-purple-50"
                                    onClick={() => onRequestTransition(card, 'COMPLETED')}
                                  >
                                    Mark Complete ✓
                                  </Button>
                                )}
                                {card.status === 'COMPLETED' && (
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    className="w-full text-[10px] py-1 h-7 border-emerald-300 text-emerald-800 hover:bg-emerald-50 flex items-center justify-center gap-1"
                                    onClick={() => onRequestTransition(card, 'VERIFIED')}
                                  >
                                    <Sparkles className="h-3 w-3 text-emerald-600" />
                                    Verify Score Delta
                                  </Button>
                                )}
                                {card.status === 'VERIFIED' && (
                                  <div className="w-full text-center text-[10px] font-bold text-emerald-700 bg-emerald-50 py-1 rounded border border-emerald-200">
                                    Verified ROI (+{((card.postActionScore || 78) - (card.baselineScore || 50)).toFixed(0)}% Score Delta)
                                  </div>
                                )}
                              </div>
                            </div>
                          )}
                        </Draggable>
                      ))
                    )}
                    {provided.placeholder}
                  </div>
                </div>
              )}
            </Droppable>
          );
        })}
      </div>
    </DragDropContext>
  );
};
