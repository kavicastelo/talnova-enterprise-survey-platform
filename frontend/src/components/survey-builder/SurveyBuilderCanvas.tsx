import React, { useState, useMemo } from 'react';
import {
  QuestionType,
  SurveyPage,
  SurveyQuestion,
  SurveySaveDraftRequest,
} from '../../types/survey';
import {
  useSurveyQuery,
  useSaveDraftMutation,
  usePublishSurveyMutation,
  useCreateNewVersionMutation,
  useAnalyzeBiasMutation,
  useTranslateTextMutation,
  useQuestionLibraryQuery,
  useCreateQuestionTemplateMutation,
} from '../../features/survey-builder/api/useSurveyQueries';
import { SurveyLogicSimulatorModal } from './SurveyLogicSimulatorModal';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Alert } from '../ui/Alert';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Textarea } from '../ui/Textarea';
import { Skeleton } from '../ui/Skeleton';
import { ErrorState } from '../ui/ErrorState';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { QuestionLibraryModal } from './QuestionLibraryModal';

interface SurveyBuilderCanvasProps {
  projectId: string;
  surveyId: string;
}

/** Validates page jump graph for circular logic cycles (BR-SRV-004) */
function detectBranchingCycle(pages: SurveyPage[]): { hasCycle: boolean; cycleNodeId?: string } {
  const adj = new Map<string, string[]>();
  pages.forEach((p, idx) => {
    const targets: string[] = [];
    if (idx + 1 < pages.length) {
      targets.push(pages[idx + 1].pageId);
    }
    p.sections.forEach((sec) => {
      sec.questions.forEach((q) => {
        if (q.logicRules) {
          q.logicRules.forEach((rule) => {
            if (rule.targetPageId && !targets.includes(rule.targetPageId)) {
              targets.push(rule.targetPageId);
            }
          });
        }
      });
    });
    adj.set(p.pageId, targets);
  });

  const visited = new Set<string>();
  const recStack = new Set<string>();

  function dfs(nodeId: string): boolean {
    if (recStack.has(nodeId)) return true;
    if (visited.has(nodeId)) return false;

    visited.add(nodeId);
    recStack.add(nodeId);

    const neighbors = adj.get(nodeId) || [];
    for (const neighbor of neighbors) {
      if (dfs(neighbor)) return true;
    }

    recStack.delete(nodeId);
    return false;
  }

  for (const page of pages) {
    if (!visited.has(page.pageId)) {
      if (dfs(page.pageId)) {
        return { hasCycle: true, cycleNodeId: page.pageId };
      }
    }
  }

  return { hasCycle: false };
}

export const SurveyBuilderCanvas: React.FC<SurveyBuilderCanvasProps> = ({
  projectId,
  surveyId,
}) => {
  const { data: surveyData, isLoading, isError, refetch } = useSurveyQuery(surveyId, projectId);
  const { data: libraryItems = [] } = useQuestionLibraryQuery();

  const saveDraftMutation = useSaveDraftMutation();
  const publishMutation = usePublishSurveyMutation();
  const newVersionMutation = useCreateNewVersionMutation();
  const biasMutation = useAnalyzeBiasMutation();

  const [activeLocale, setActiveLocale] = useState<string>('en-US');
  const [isSimulatorOpen, setIsSimulatorOpen] = useState<boolean>(false);
  const [biasResult, setBiasResult] = useState<any>(null);

  const [surveyTitle, setSurveyTitle] = useState<Record<string, string>>({
    'en-US': '2026 Annual Employee Engagement Survey',
  });

  const [pages, setPages] = useState<SurveyPage[]>([
    {
      pageId: 'PAGE-1',
      pageOrder: 1,
      title: { 'en-US': 'Leadership & Strategy' },
      sections: [
        {
          sectionId: 'SEC-1',
          sectionOrder: 1,
          title: { 'en-US': 'Executive Direction' },
          questions: [
            {
              questionId: 'Q-101',
              type: 'LIKERT',
              groupId: 'GRP-LEADERSHIP',
              prompt: {
                'en-US': 'My direct manager provides clear direction.',
                'si-LK': 'මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.',
                'ta-LK': 'எனது மேலாளர் தெளிவான கருத்துக்களை வழங்குகிறார்.',
              },
              isMandatory: true,
              questionOrder: 1,
              logicRules: [],
            },
          ],
        },
      ],
    },
  ]);

  const cycleCheck = useMemo(() => detectBranchingCycle(pages), [pages]);

  const [selectedQuestion, setSelectedQuestion] = useState<{
    pageIndex: number;
    sectionIndex: number;
    questionIndex: number;
  } | null>({ pageIndex: 0, sectionIndex: 0, questionIndex: 0 });

  React.useEffect(() => {
    if (surveyData) {
      if (surveyData.title) setSurveyTitle(surveyData.title);
      if (surveyData.pages && surveyData.pages.length > 0) setPages(surveyData.pages);
    }
  }, [surveyData]);


  const currentStatus = surveyData?.status || 'DRAFT';
  const currentVersion = surveyData?.version || 1;
  const isPublished = currentStatus === 'PUBLISHED';

  // Helper functions
  const duplicateQuestion = (pIdx: number, sIdx: number, qIdx: number) => {
    if (isPublished) return;
    const targetQ = pages[pIdx].sections[sIdx].questions[qIdx];
    const duplicated: SurveyQuestion = {
      ...targetQ,
      questionId: `Q-COPY-${Date.now().toString().slice(-4)}`,
      prompt: { ...targetQ.prompt, 'en-US': `${targetQ.prompt['en-US'] || ''} (Copy)` },
      questionOrder: targetQ.questionOrder + 1,
    };
    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions.splice(qIdx + 1, 0, duplicated);
    setPages(updatedPages);
    setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: qIdx + 1 });
  };

  const deleteQuestion = (pIdx: number, sIdx: number, qIdx: number) => {
    if (isPublished) return;
    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions.splice(qIdx, 1);
    setPages(updatedPages);
    if (
      selectedQuestion?.pageIndex === pIdx &&
      selectedQuestion?.sectionIndex === sIdx &&
      selectedQuestion?.questionIndex === qIdx
    ) {
      setSelectedQuestion(null);
    }
  };

  const moveQuestion = (pIdx: number, sIdx: number, qIdx: number, direction: 'UP' | 'DOWN') => {
    if (isPublished) return;
    const questions = pages[pIdx].sections[sIdx].questions;
    const targetIdx = direction === 'UP' ? qIdx - 1 : qIdx + 1;
    if (targetIdx < 0 || targetIdx >= questions.length) return;

    const updatedPages = [...pages];
    const item = updatedPages[pIdx].sections[sIdx].questions.splice(qIdx, 1)[0];
    updatedPages[pIdx].sections[sIdx].questions.splice(targetIdx, 0, item);
    setPages(updatedPages);
    setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: targetIdx });
  };

  const [isLibraryModalOpen, setIsLibraryModalOpen] = useState(false);
  const [targetLocation, setTargetLocation] = useState<{ pIdx: number; sIdx: number }>({ pIdx: 0, sIdx: 0 });

  const openLibraryModal = (pIdx: number, sIdx: number) => {
    if (isPublished) return;
    setTargetLocation({ pIdx, sIdx });
    setIsLibraryModalOpen(true);
  };

  const handleInsertLibraryTemplate = (template: SurveyQuestion) => {
    const { pIdx, sIdx } = targetLocation;
    const updatedPages = [...pages];
    const section = updatedPages[pIdx]?.sections[sIdx];
    if (section) {
      template.questionOrder = section.questions.length + 1;
      section.questions.push(template);
      setPages(updatedPages);
      setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: section.questions.length - 1 });
    }
  };

  const addLibraryQuestion = (pIdx: number, sIdx: number) => {
    openLibraryModal(pIdx, sIdx);
  };

  const addPage = () => {
    if (isPublished) return;
    const newPageOrder = pages.length + 1;
    const newPage: SurveyPage = {
      pageId: `PAGE-${newPageOrder}`,
      pageOrder: newPageOrder,
      title: { [activeLocale]: `Page ${newPageOrder}` },
      sections: [
        {
          sectionId: `SEC-${newPageOrder}-1`,
          sectionOrder: 1,
          title: { [activeLocale]: 'Section 1' },
          questions: [],
        },
      ],
    };
    setPages([...pages, newPage]);
  };

  const addQuestion = (pIdx: number, sIdx: number) => {
    if (isPublished) return;
    const section = pages[pIdx].sections[sIdx];
    const qOrder = section.questions.length + 1;
    const newQuestion: SurveyQuestion = {
      questionId: `Q-${Date.now().toString().slice(-4)}`,
      type: 'LIKERT',
      groupId: 'GRP-GENERAL',
      prompt: { 'en-US': 'Please rate your level of agreement with the statement.' },
      isMandatory: true,
      questionOrder: qOrder,
      logicRules: [],
    };
    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions.push(newQuestion);
    setPages(updatedPages);
    setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: section.questions.length - 1 });
  };

  const activeQuestion: SurveyQuestion | null = selectedQuestion
    ? pages[selectedQuestion.pageIndex]?.sections[selectedQuestion.sectionIndex]?.questions[selectedQuestion.questionIndex] || null
    : null;

  const updateActiveQuestion = (field: keyof SurveyQuestion, value: any) => {
    if (!selectedQuestion || isPublished) return;
    const { pageIndex, sectionIndex, questionIndex } = selectedQuestion;
    const updatedPages = [...pages];
    const q = updatedPages[pageIndex].sections[sectionIndex].questions[questionIndex];
    updatedPages[pageIndex].sections[sectionIndex].questions[questionIndex] = {
      ...q,
      [field]: value,
    };
    setPages(updatedPages);
  };

  const handleSaveDraft = () => {
    const payload: SurveySaveDraftRequest = {
      projectId,
      surveyId,
      title: surveyTitle,
      pages,
    };

    saveDraftMutation.mutate({ surveyId, payload });
  };

  const [isPublishConfirmOpen, setIsPublishConfirmOpen] = useState(false);
  const [publishValidationError, setPublishValidationError] = useState<string | null>(null);

  const validateSurveyCompleteness = (): { isValid: boolean; error?: string } => {
    if (!pages || pages.length === 0) {
      return { isValid: false, error: 'Survey must contain at least 1 Page (VR-SRV-002).' };
    }

    let totalSections = 0;
    let totalQuestions = 0;
    let missingGroupQId: string | null = null;

    for (const page of pages) {
      totalSections += page.sections.length;
      for (const sec of page.sections) {
        totalQuestions += sec.questions.length;
        for (const q of sec.questions) {
          if (['LIKERT', 'NPS', 'MATRIX'].includes(q.type) && !q.groupId?.trim()) {
            missingGroupQId = q.questionId;
          }
        }
      }
    }

    if (totalSections === 0) {
      return { isValid: false, error: 'Survey must contain at least 1 Section (VR-SRV-002).' };
    }
    if (totalQuestions === 0) {
      return { isValid: false, error: 'Survey must contain at least 1 Question before publishing (VR-SRV-002).' };
    }
    if (missingGroupQId) {
      return { isValid: false, error: `Question ${missingGroupQId} requires a valid Question Group ID for quantitative analytics (BR-SRV-002).` };
    }

    return { isValid: true };
  };

  const handleOpenPublishConfirm = () => {
    const check = validateSurveyCompleteness();
    if (!check.isValid) {
      setPublishValidationError(check.error || 'Completeness check failed.');
      return;
    }
    setPublishValidationError(null);
    setIsPublishConfirmOpen(true);
  };

  const handleConfirmPublish = () => {
    setIsPublishConfirmOpen(false);
    publishMutation.mutate({ surveyId, projectId });
  };

  const handleNewVersion = () => {
    newVersionMutation.mutate({ surveyId, projectId });
  };

  const handleAnalyzeBias = () => {
    if (!activeQuestion) return;
    const promptText = activeQuestion.prompt[activeLocale] || activeQuestion.prompt['en-US'] || '';

    biasMutation.mutate(
      { questionPrompt: promptText, locale: activeLocale },
      {
        onSuccess: (data) => setBiasResult(data),
      }
    );
  };

  const translateMutation = useTranslateTextMutation();
  const createTemplateMutation = useCreateQuestionTemplateMutation();

  const handleAutoTranslate = () => {
    if (!activeQuestion) return;
    const sourceText = activeQuestion.prompt['en-US'] || activeQuestion.prompt[activeLocale] || '';
    if (!sourceText) return;

    translateMutation.mutate(
      { sourceText, targetLocales: ['si-LK', 'ta-LK'], sourceLocale: 'en-US' },
      {
        onSuccess: (res) => {
          if (res && res.translations) {
            const updatedPrompt = {
              ...activeQuestion.prompt,
              ...res.translations,
            };
            updateActiveQuestion('prompt', updatedPrompt);
          }
        },
      }
    );
  };

  const handleSaveToLibrary = () => {
    if (!activeQuestion) return;
    createTemplateMutation.mutate({
      category: 'ENGAGEMENT',
      themeGroup: activeQuestion.groupId || 'GRP-GENERAL',
      defaultPrompt: activeQuestion.prompt,
      questionType: activeQuestion.type,
    });
  };

  if (isLoading) {
    return (
      <Card variant="bordered" padding="24px">
        <Skeleton height="400px" borderRadius="12px" />
      </Card>
    );
  }

  if (isError) {
    return (
      <Card variant="bordered" padding="24px">
        <ErrorState
          title="Failed to Load Survey Questionnaire AST"
          message="Could not retrieve survey payload from survey-builder-service via Gateway."
          onRetry={refetch}
        />
      </Card>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Top Action Header */}
      <Card variant="bordered" padding="20px">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Input
                  value={surveyTitle[activeLocale] || surveyTitle['en-US'] || ''}
                  onChange={(e) => setSurveyTitle({ ...surveyTitle, [activeLocale]: e.target.value })}
                  placeholder="Survey Title"
                />
                <Badge variant={isPublished ? 'success' : 'warning'} dot>
                  {currentStatus} v{currentVersion}
                </Badge>
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Survey ID: <code>{surveyId}</code> | Project: <code>{projectId}</code>
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <Select
              value={activeLocale}
              onChange={(e) => setActiveLocale(e.target.value)}
              options={[
                { value: 'en-US', label: 'English (en-US)' },
                { value: 'si-LK', label: 'Sinhala (si-LK)' },
                { value: 'ta-LK', label: 'Tamil (ta-LK)' },
              ]}
            />

            <Button variant="outline" onClick={() => setIsSimulatorOpen(true)}>
              📱 Launch Simulator
            </Button>

            {isPublished ? (
              <Button variant="primary" onClick={handleNewVersion} isLoading={newVersionMutation.isPending}>
                + Create Version {currentVersion + 1}
              </Button>
            ) : (
              <>
                <Button variant="secondary" onClick={handleSaveDraft} isLoading={saveDraftMutation.isPending}>
                  Save Draft
                </Button>
                <Button variant="primary" onClick={handleOpenPublishConfirm} isLoading={publishMutation.isPending}>
                  Publish Survey
                </Button>
              </>
            )}
          </div>
        </div>
      </Card>

      {publishValidationError && (
        <Alert type="error" title="Publish Validation Guard Failed (VR-SRV-002)">
          {publishValidationError}
        </Alert>
      )}

      {/* DAG Cycle Prevention Alert (BR-SRV-004) */}
      {cycleCheck.hasCycle && (
        <Alert type="error" title="Circular Skip Logic Cycle Detected (BR-SRV-004)">
          A circular page jump loop was detected starting at <code>{cycleCheck.cycleNodeId}</code>. Please review skip logic rules to ensure forward-only navigation DAG.
        </Alert>
      )}

      {/* Published Structural Lock Alert */}
      {isPublished && (
        <Alert type="info" title="Survey Structure Locked">
          This survey version is PUBLISHED. Structural edits are locked. Click "+ Create Version {currentVersion + 1}" to create an editable draft version.
        </Alert>
      )}

      {/* Main 2-Column Canvas Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '24px' }}>
        {/* Left Column: AST Pages & Sections */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>
              Questionnaire Canvas ({pages.length} Pages)
            </h3>
            {!isPublished && (
              <Button variant="secondary" size="sm" onClick={addPage}>
                + Add Page
              </Button>
            )}
          </div>

          {pages.map((page, pIdx) => (
            <Card key={page.pageId} variant="bordered" padding="16px">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <Badge variant="info">PAGE {page.pageOrder}</Badge>
                  <span style={{ fontWeight: 700, color: '#0f172a' }}>
                    {page.title?.[activeLocale] || page.title?.['en-US'] || `Page ${page.pageOrder}`}
                  </span>
                </div>
              </div>

              {/* Sections */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {page.sections.map((section, sIdx) => (
                  <div key={section.sectionId} style={{ background: '#f8fafc', border: '1px dashed #cbd5e1', padding: '12px', borderRadius: '8px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                      <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#475569' }}>
                        {section.title?.[activeLocale] || 'Section'}
                      </span>
                      {!isPublished && (
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              const firstItem = libraryItems.length > 0 ? libraryItems[0] : null;
                              const libraryTemplate: SurveyQuestion = {
                                questionId: `Q-LIB-${Date.now().toString().slice(-4)}`,
                                type: firstItem ? firstItem.questionType : 'LIKERT',
                                groupId: firstItem ? firstItem.themeGroup : 'GRP-WELLBEING',
                                prompt: firstItem ? firstItem.defaultPrompt : {
                                  'en-US': 'My workload allows me to maintain a healthy work-life balance.',
                                  'si-LK': 'මගේ වැඩ ප්‍රමාණය මට සෞඛ්‍ය සම්පන්න වැඩ-ජීවිත සමබරතාවයක් පවත්වා ගැනීමට ඉඩ සලසයි.',
                                  'ta-LK': 'எனது பணிச்சுமை எனக்கு ஆரோக்கியமான பணி-வாழ்க்கை சமநிலையை பராமரிக்க அனுமதிக்கிறது.',
                                },
                                isMandatory: true,
                                questionOrder: section.questions.length + 1,
                                logicRules: [],
                              };
                              const updatedPages = [...pages];
                              updatedPages[pIdx].sections[sIdx].questions.push(libraryTemplate);
                              setPages(updatedPages);
                              setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: section.questions.length });
                            }}
                          >
                            📚 + Library Item
                          </Button>
                          <Button variant="outline" size="sm" onClick={() => addQuestion(pIdx, sIdx)}>
                            + Question
                          </Button>
                        </div>
                      )}
                    </div>

                    {/* Question Cards or Section Empty State */}
                    {section.questions.length === 0 ? (
                      <div style={{ padding: '24px', textAlign: 'center', border: '1px dashed #cbd5e1', borderRadius: '8px', background: '#ffffff' }}>
                        <p style={{ margin: '0 0 12px 0', fontSize: '0.85rem', color: '#64748b' }}>
                          No questions added to this section yet.
                        </p>
                        {!isPublished && (
                          <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
                            <Button variant="primary" size="sm" onClick={() => addQuestion(pIdx, sIdx)}>
                              + Add First Question
                            </Button>
                            <Button variant="outline" size="sm" onClick={() => addLibraryQuestion(pIdx, sIdx)}>
                              📚 Pick from Library
                            </Button>
                          </div>
                        )}
                      </div>
                    ) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        {section.questions.map((q, qIdx) => {
                          const isSelected =
                            selectedQuestion?.pageIndex === pIdx &&
                            selectedQuestion?.sectionIndex === sIdx &&
                            selectedQuestion?.questionIndex === qIdx;

                          return (
                            <div
                              key={q.questionId}
                              onClick={() => setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: qIdx })}
                              style={{
                                padding: '12px',
                                borderRadius: '8px',
                                background: isSelected ? '#eff6ff' : '#ffffff',
                                border: `2px solid ${isSelected ? '#2563eb' : '#e2e8f0'}`,
                                cursor: 'pointer',
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                              }}
                            >
                              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <Badge variant="neutral">{q.type}</Badge>
                                <div>
                                  <div style={{ fontWeight: 600, color: '#0f172a', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    {q.prompt[activeLocale] || q.prompt['en-US']}
                                    {q.logicRules && q.logicRules.length > 0 && (
                                      <Badge variant="info">
                                        ⚡ Skip Logic ({q.logicRules.length})
                                      </Badge>
                                    )}
                                  </div>
                                  {q.groupId && (
                                    <span style={{ fontSize: '0.725rem', color: '#64748b', background: '#f1f5f9', padding: '1px 6px', borderRadius: '4px' }}>
                                      Group: {q.groupId}
                                    </span>
                                  )}
                                </div>
                              </div>

                              {!isPublished && (
                                <div style={{ display: 'flex', gap: '4px' }} onClick={(e) => e.stopPropagation()}>
                                  <button
                                    type="button"
                                    title="Move Question Up"
                                    disabled={qIdx === 0}
                                    onClick={() => moveQuestion(pIdx, sIdx, qIdx, 'UP')}
                                    style={{ border: '1px solid #cbd5e1', background: '#fff', borderRadius: '4px', padding: '2px 6px', cursor: qIdx === 0 ? 'not-allowed' : 'pointer', opacity: qIdx === 0 ? 0.4 : 1 }}
                                  >
                                    ↑
                                  </button>
                                  <button
                                    type="button"
                                    title="Move Question Down"
                                    disabled={qIdx === section.questions.length - 1}
                                    onClick={() => moveQuestion(pIdx, sIdx, qIdx, 'DOWN')}
                                    style={{ border: '1px solid #cbd5e1', background: '#fff', borderRadius: '4px', padding: '2px 6px', cursor: qIdx === section.questions.length - 1 ? 'not-allowed' : 'pointer', opacity: qIdx === section.questions.length - 1 ? 0.4 : 1 }}
                                  >
                                    ↓
                                  </button>
                                  <button
                                    type="button"
                                    title="Duplicate Question"
                                    onClick={() => duplicateQuestion(pIdx, sIdx, qIdx)}
                                    style={{ border: '1px solid #cbd5e1', background: '#fff', borderRadius: '4px', padding: '2px 6px', cursor: 'pointer' }}
                                  >
                                    📋
                                  </button>
                                  <button
                                    type="button"
                                    title="Delete Question"
                                    onClick={() => deleteQuestion(pIdx, sIdx, qIdx)}
                                    style={{ border: '1px solid #fecaca', background: '#fef2f2', color: '#dc2626', borderRadius: '4px', padding: '2px 6px', cursor: 'pointer' }}
                                  >
                                    🗑️
                                  </button>
                                </div>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          ))}
        </div>

        {/* Right Column: Question Properties & AI Inspector */}
        <Card variant="bordered" padding="20px">
          <h4 style={{ fontSize: '1rem', fontWeight: 700, color: '#0f172a', margin: '0 0 16px 0', borderBottom: '1px solid #e2e8f0', paddingBottom: '8px' }}>
            Question Inspector
          </h4>

          {activeQuestion ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <Select
                label="Question Type"
                value={activeQuestion.type}
                onChange={(e) => updateActiveQuestion('type', e.target.value as QuestionType)}
                options={[
                  { value: 'LIKERT', label: 'Likert Scale (Quantitative)' },
                  { value: 'NPS', label: 'Net Promoter Score (Quantitative)' },
                  { value: 'MATRIX', label: 'Matrix Grid (Quantitative)' },
                  { value: 'SINGLE_CHOICE', label: 'Single Choice' },
                  { value: 'MULTIPLE_CHOICE', label: 'Multiple Choice' },
                  { value: 'RANKING', label: 'Ranking Scale' },
                  { value: 'SHORT_TEXT', label: 'Short Text' },
                  { value: 'LONG_TEXT', label: 'Long Text' },
                  { value: 'NUMERIC', label: 'Numeric' },
                  { value: 'DATE', label: 'Date Picker' },
                ]}
                disabled={isPublished}
              />

              <div>
                <Select
                  label="Question Group Dimension (Required for Quantitative)"
                  value={activeQuestion.groupId || 'GRP-GENERAL'}
                  onChange={(e) => updateActiveQuestion('groupId', e.target.value)}
                  options={[
                    { value: 'GRP-LEADERSHIP', label: 'Leadership & Strategic Alignment (GRP-LEADERSHIP)' },
                    { value: 'GRP-CULTURE', label: 'Workplace Culture & Belonging (GRP-CULTURE)' },
                    { value: 'GRP-WELLBEING', label: 'Employee Wellbeing & Balance (GRP-WELLBEING)' },
                    { value: 'GRP-MANAGEMENT', label: 'Managerial Support & Direction (GRP-MANAGEMENT)' },
                    { value: 'GRP-CAREER', label: 'Career Growth & Learning (GRP-CAREER)' },
                    { value: 'GRP-COMPENSATION', label: 'Rewards & Recognition (GRP-COMPENSATION)' },
                    { value: 'GRP-GENERAL', label: 'General Feedback (GRP-GENERAL)' },
                  ]}
                  disabled={isPublished}
                />
                {['LIKERT', 'NPS', 'MATRIX'].includes(activeQuestion.type) && !activeQuestion.groupId?.trim() && (
                  <p style={{ color: '#dc2626', fontSize: '0.75rem', marginTop: '4px' }}>
                    ⚠️ BR-SRV-002: Quantitative questions require a valid Question Group for analytics aggregation.
                  </p>
                )}
              </div>

              {/* Multi-Language Translation Dictionary Status Chips */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                  <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#334155' }}>
                    Localization Dictionary Status ({activeLocale})
                  </span>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    <span
                      style={{
                        fontSize: '0.7rem',
                        fontWeight: 700,
                        padding: '2px 6px',
                        borderRadius: '4px',
                        background: activeQuestion.prompt['en-US']?.trim() ? '#dcfce7' : '#fee2e2',
                        color: activeQuestion.prompt['en-US']?.trim() ? '#15803d' : '#b91c1c',
                      }}
                      title="Default Locale (en-US)"
                    >
                      EN {activeQuestion.prompt['en-US']?.trim() ? '✓' : '✗'}
                    </span>
                    <span
                      style={{
                        fontSize: '0.7rem',
                        fontWeight: 700,
                        padding: '2px 6px',
                        borderRadius: '4px',
                        background: activeQuestion.prompt['si-LK']?.trim() ? '#e0e7ff' : '#f1f5f9',
                        color: activeQuestion.prompt['si-LK']?.trim() ? '#3730a3' : '#64748b',
                      }}
                      title="Sinhala (si-LK)"
                    >
                      SI {activeQuestion.prompt['si-LK']?.trim() ? '✓' : '—'}
                    </span>
                    <span
                      style={{
                        fontSize: '0.7rem',
                        fontWeight: 700,
                        padding: '2px 6px',
                        borderRadius: '4px',
                        background: activeQuestion.prompt['ta-LK']?.trim() ? '#e0e7ff' : '#f1f5f9',
                        color: activeQuestion.prompt['ta-LK']?.trim() ? '#3730a3' : '#64748b',
                      }}
                      title="Tamil (ta-LK)"
                    >
                      TA {activeQuestion.prompt['ta-LK']?.trim() ? '✓' : '—'}
                    </span>
                  </div>
                </div>

                <Textarea
                  label={`Prompt (${activeLocale})`}
                  rows={3}
                  value={activeQuestion.prompt[activeLocale] || ''}
                  onChange={(e) => {
                    const updated = { ...activeQuestion.prompt, [activeLocale]: e.target.value };
                    updateActiveQuestion('prompt', updated);
                  }}
                  disabled={isPublished}
                />

                {!activeQuestion.prompt['en-US']?.trim() && (
                  <p style={{ color: '#dc2626', fontSize: '0.75rem', marginTop: '4px' }}>
                    ⚠️ VR-SRV-004: English (en-US) prompt is required as the default fallback locale before saving/publishing.
                  </p>
                )}
              </div>

              {/* Declarative Branching Logic Rules Editor */}
              <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '16px', marginTop: '8px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <label style={{ fontWeight: 700, fontSize: '0.85rem', color: '#0f172a' }}>
                    ⚡ Branching / Skip Logic Rules ({activeQuestion.logicRules?.length || 0})
                  </label>
                  {!isPublished && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => {
                        if (!selectedQuestion) return;
                        const currentPIdx = selectedQuestion.pageIndex;
                        const availableTargetPages = pages.filter((_, idx) => idx > currentPIdx);
                        if (availableTargetPages.length === 0) {
                          alert('Forward skip rules require at least 1 subsequent page (VR-SRV-003).');
                          return;
                        }
                        const newRule = {
                          ruleId: `RULE-${Date.now().toString().slice(-4)}`,
                          operator: 'EQUALS' as const,
                          comparisonValue: '1',
                          targetPageId: availableTargetPages[0].pageId,
                        };
                        const currentRules = activeQuestion.logicRules || [];
                        updateActiveQuestion('logicRules', [...currentRules, newRule]);
                      }}
                    >
                      + Add Rule
                    </Button>
                  )}
                </div>

                {(!activeQuestion.logicRules || activeQuestion.logicRules.length === 0) ? (
                  <p style={{ color: '#94a3b8', fontSize: '0.75rem', margin: 0 }}>
                    No logic rules configured for this question. Sequential navigation will apply.
                  </p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {activeQuestion.logicRules.map((rule, rIdx) => {
                      const currentPIdx = selectedQuestion?.pageIndex ?? 0;
                      const validTargetPages = pages.filter((_, idx) => idx > currentPIdx);

                      return (
                        <div
                          key={rule.ruleId || rIdx}
                          style={{ background: '#f8fafc', border: '1px solid #cbd5e1', padding: '8px', borderRadius: '6px', fontSize: '0.8rem' }}
                        >
                          <div style={{ display: 'flex', gap: '6px', marginBottom: '6px' }}>
                            <Select
                              value={rule.operator}
                              onChange={(e) => {
                                const updatedRules = [...(activeQuestion.logicRules || [])];
                                updatedRules[rIdx] = { ...rule, operator: e.target.value as any };
                                updateActiveQuestion('logicRules', updatedRules);
                              }}
                              options={[
                                { value: 'EQUALS', label: 'IF Equals' },
                                { value: 'NOT_EQUALS', label: 'IF Not Equals' },
                                { value: 'LESS_THAN', label: 'IF Less Than' },
                                { value: 'GREATER_THAN', label: 'IF Greater Than' },
                              ]}
                              disabled={isPublished}
                            />
                            <Input
                              value={rule.comparisonValue}
                              onChange={(e) => {
                                const updatedRules = [...(activeQuestion.logicRules || [])];
                                updatedRules[rIdx] = { ...rule, comparisonValue: e.target.value };
                                updateActiveQuestion('logicRules', updatedRules);
                              }}
                              placeholder="Value (e.g. 7)"
                              disabled={isPublished}
                            />
                          </div>

                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '6px' }}>
                            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#475569' }}>SKIP TO:</span>
                            <Select
                              value={rule.targetPageId}
                              onChange={(e) => {
                                const updatedRules = [...(activeQuestion.logicRules || [])];
                                updatedRules[rIdx] = { ...rule, targetPageId: e.target.value };
                                updateActiveQuestion('logicRules', updatedRules);
                              }}
                              options={validTargetPages.map((p) => ({
                                value: p.pageId,
                                label: `Page ${p.pageOrder}: ${p.title?.[activeLocale] || p.title?.['en-US'] || p.pageId}`,
                              }))}
                              disabled={isPublished}
                            />
                            {!isPublished && (
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => {
                                  const updatedRules = (activeQuestion.logicRules || []).filter((_, idx) => idx !== rIdx);
                                  updateActiveQuestion('logicRules', updatedRules);
                                }}
                              >
                                ✕
                              </Button>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', borderTop: '1px solid #e2e8f0', paddingTop: '12px' }}>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleAutoTranslate}
                  isLoading={translateMutation.isPending}
                  disabled={isPublished}
                >
                  🌐 AI Multi-Language Auto-Translate (si-LK, ta-LK)
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleAnalyzeBias}
                  isLoading={biasMutation.isPending}
                >
                  🤖 Analyze Question Bias with AI
                </Button>

                <Button
                  variant="secondary"
                  size="sm"
                  onClick={handleSaveToLibrary}
                  isLoading={createTemplateMutation.isPending}
                >
                  💾 Save Question to Library Catalog
                </Button>
              </div>

              {biasResult && (
                <Alert
                  type={biasResult.biasDetected ? 'warning' : 'success'}
                  title={`AI Bias Risk Score: ${biasResult.riskScore}/100`}
                >
                  {biasResult.message}
                  {biasResult.suggestedPrompts?.length > 0 && (
                    <div style={{ marginTop: '6px' }}>
                      <strong>Suggestions:</strong> {biasResult.suggestedPrompts.join(', ')}
                    </div>
                  )}
                </Alert>
              )}
            </div>
          ) : (
            <p style={{ color: '#64748b', fontSize: '0.85rem' }}>Select a question on the canvas to inspect properties.</p>
          )}
        </Card>
      </div>

      {/* Simulator Modal */}
      <SurveyLogicSimulatorModal
        isOpen={isSimulatorOpen}
        onClose={() => setIsSimulatorOpen(false)}
        pages={pages}
        defaultLocale={activeLocale}
      />

      {/* Publish Confirmation Dialog */}
      <ConfirmDialog
        isOpen={isPublishConfirmOpen}
        onClose={() => setIsPublishConfirmOpen(false)}
        onConfirm={handleConfirmPublish}
        title={`Publish Survey Version ${currentVersion}`}
        message={`Are you sure you want to publish Survey Version ${currentVersion}? Publishing freezes questionnaire AST structure and locks edits (BR-SRV-001). Active distribution campaigns will begin consuming this version.`}
        confirmText="Confirm & Freeze Version"
        isLoading={publishMutation.isPending}
      />

      {/* Question Library Catalog Modal */}
      <QuestionLibraryModal
        isOpen={isLibraryModalOpen}
        onClose={() => setIsLibraryModalOpen(false)}
        onSelectTemplate={handleInsertLibraryTemplate}
      />
    </div>
  );
};
