import React, { useState } from 'react';
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

interface SurveyBuilderCanvasProps {
  projectId: string;
  surveyId: string;
}

export const SurveyBuilderCanvas: React.FC<SurveyBuilderCanvasProps> = ({
  projectId,
  surveyId,
}) => {
  const { data: surveyData, isLoading, isError, refetch } = useSurveyQuery(surveyId, projectId);

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

  const [selectedQuestion, setSelectedQuestion] = useState<{
    pageIndex: number;
    sectionIndex: number;
    questionIndex: number;
  } | null>({ pageIndex: 0, sectionIndex: 0, questionIndex: 0 });

  const currentStatus = surveyData?.status || 'DRAFT';
  const currentVersion = surveyData?.version || 1;
  const isPublished = currentStatus === 'PUBLISHED';

  // Helper functions
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

  const handlePublish = () => {
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
                <Button variant="primary" onClick={handlePublish} isLoading={publishMutation.isPending}>
                  Publish Survey
                </Button>
              </>
            )}
          </div>
        </div>
      </Card>

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
                        <Button variant="outline" size="sm" onClick={() => addQuestion(pIdx, sIdx)}>
                          + Question
                        </Button>
                      )}
                    </div>

                    {/* Question Cards */}
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
                            <div>
                              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <Badge variant="neutral">{q.type}</Badge>
                                <span style={{ fontWeight: 600, color: '#0f172a', fontSize: '0.9rem' }}>
                                  {q.prompt[activeLocale] || q.prompt['en-US']}
                                </span>
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
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
                  { value: 'SHORT_TEXT', label: 'Short Text' },
                  { value: 'LONG_TEXT', label: 'Long Text' },
                  { value: 'NUMERIC', label: 'Numeric' },
                  { value: 'DATE', label: 'Date Picker' },
                ]}
                disabled={isPublished}
              />

              <Input
                label="Question Group ID"
                value={activeQuestion.groupId || ''}
                onChange={(e) => updateActiveQuestion('groupId', e.target.value)}
                placeholder="GRP-LEADERSHIP"
                disabled={isPublished}
              />

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

              <Button
                variant="outline"
                size="sm"
                onClick={handleAnalyzeBias}
                isLoading={biasMutation.isPending}
              >
                🤖 Analyze Question Bias with AI
              </Button>

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
    </div>
  );
};
