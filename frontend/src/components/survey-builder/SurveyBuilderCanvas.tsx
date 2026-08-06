import React, { useState } from 'react';
import {
  QuestionType,
  SurveyPage,
  SurveyQuestion,
  SurveyResponse,
  SurveySaveDraft,
  SurveySection
} from '../../types/survey';
import { surveyBuilderApi } from '../../services/surveyBuilderApi';
import { SurveyLogicSimulatorModal } from './SurveyLogicSimulatorModal';

interface SurveyBuilderCanvasProps {
  projectId: string;
  surveyId: string;
  initialData?: SurveyResponse;
}

export const SurveyBuilderCanvas: React.FC<SurveyBuilderCanvasProps> = ({
  projectId,
  surveyId,
  initialData
}) => {
  const [surveyTitle, setSurveyTitle] = useState<Record<string, string>>(
    initialData?.title || { 'en-US': '2026 Annual Employee Engagement Survey' }
  );

  const [version, setVersion] = useState<number>(initialData?.version || 1);
  const [status, setStatus] = useState<string>(initialData?.status || 'DRAFT');
  const [activeLocale, setActiveLocale] = useState<string>('en-US');
  const [isSimulatorOpen, setIsSimulatorOpen] = useState<boolean>(false);

  const [pages, setPages] = useState<SurveyPage[]>(
    initialData?.pages || [
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
                  'ta-LK': 'எனது மேலாளர் தெளிவான கருத்துக்களை வழங்குகிறார்.'
                },
                isMandatory: true,
                questionOrder: 1,
                logicRules: [
                  {
                    ruleId: 'RULE-1',
                    operator: 'EQUALS',
                    comparisonValue: '1',
                    targetPageId: 'PAGE-2'
                  }
                ]
              }
            ]
          }
        ]
      },
      {
        pageId: 'PAGE-2',
        pageOrder: 2,
        title: { 'en-US': 'Follow-Up Deep Dive' },
        sections: [
          {
            sectionId: 'SEC-2',
            sectionOrder: 1,
            title: { 'en-US': 'Managerial Support Details' },
            questions: [
              {
                questionId: 'Q-201',
                type: 'TEXT_OPEN',
                groupId: 'GRP-LEADERSHIP',
                prompt: { 'en-US': 'Please share specific areas where your manager can improve guidance.' },
                isMandatory: false,
                questionOrder: 1,
                logicRules: []
              }
            ]
          }
        ]
      }
    ]
  );

  const [selectedQuestion, setSelectedQuestion] = useState<{
    pageIndex: number;
    sectionIndex: number;
    questionIndex: number;
  } | null>({ pageIndex: 0, sectionIndex: 0, questionIndex: 0 });

  const [isSaving, setIsSaving] = useState<boolean>(false);
  const [isPublishing, setIsPublishing] = useState<boolean>(false);
  const [notification, setNotification] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  // Helper functions for manipulating pages, sections, and questions
  const addPage = () => {
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
          questions: []
        }
      ]
    };
    setPages([...pages, newPage]);
    setNotification({ type: 'success', message: `Page ${newPageOrder} added to AST canvas.` });
  };

  const removePage = (pIdx: number) => {
    if (pages.length <= 1) {
      setNotification({ type: 'error', message: 'Survey must contain at least one page.' });
      return;
    }
    const updated = pages.filter((_, idx) => idx !== pIdx).map((p, idx) => ({ ...p, pageOrder: idx + 1 }));
    setPages(updated);
    if (selectedQuestion?.pageIndex === pIdx) {
      setSelectedQuestion(null);
    }
  };

  const movePage = (pIdx: number, direction: 'up' | 'down') => {
    const targetIdx = direction === 'up' ? pIdx - 1 : pIdx + 1;
    if (targetIdx < 0 || targetIdx >= pages.length) return;
    const updated = [...pages];
    const temp = updated[pIdx];
    updated[pIdx] = updated[targetIdx];
    updated[targetIdx] = temp;
    const reordered = updated.map((p, idx) => ({ ...p, pageOrder: idx + 1 }));
    setPages(reordered);
  };

  const addSection = (pIdx: number) => {
    const page = pages[pIdx];
    const secOrder = page.sections.length + 1;
    const newSection: SurveySection = {
      sectionId: `SEC-${pIdx + 1}-${secOrder}`,
      sectionOrder: secOrder,
      title: { [activeLocale]: `Section ${secOrder}` },
      questions: []
    };
    const updatedPages = [...pages];
    updatedPages[pIdx].sections.push(newSection);
    setPages(updatedPages);
  };

  const addQuestion = (pIdx: number, sIdx: number) => {
    const section = pages[pIdx].sections[sIdx];
    const qOrder = section.questions.length + 1;
    const newQuestion: SurveyQuestion = {
      questionId: `Q-${Date.now().toString().slice(-4)}`,
      type: 'LIKERT',
      groupId: 'GRP-GENERAL',
      prompt: { 'en-US': 'Please rate your level of agreement with the following statement.' },
      isMandatory: true,
      questionOrder: qOrder,
      logicRules: []
    };
    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions.push(newQuestion);
    setPages(updatedPages);
    setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: section.questions.length - 1 });
  };

  const removeQuestion = (pIdx: number, sIdx: number, qIdx: number) => {
    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions = updatedPages[pIdx].sections[sIdx].questions
      .filter((_, idx) => idx !== qIdx)
      .map((q, idx) => ({ ...q, questionOrder: idx + 1 }));
    setPages(updatedPages);
    setSelectedQuestion(null);
  };

  const moveQuestion = (pIdx: number, sIdx: number, qIdx: number, direction: 'up' | 'down') => {
    const questions = pages[pIdx].sections[sIdx].questions;
    const targetIdx = direction === 'up' ? qIdx - 1 : qIdx + 1;
    if (targetIdx < 0 || targetIdx >= questions.length) return;
    const updatedQ = [...questions];
    const temp = updatedQ[qIdx];
    updatedQ[qIdx] = updatedQ[targetIdx];
    updatedQ[targetIdx] = temp;
    const reorderedQ = updatedQ.map((q, idx) => ({ ...q, questionOrder: idx + 1 }));

    const updatedPages = [...pages];
    updatedPages[pIdx].sections[sIdx].questions = reorderedQ;
    setPages(updatedPages);
    setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: targetIdx });
  };

  const activeQuestion: SurveyQuestion | null = selectedQuestion
    ? pages[selectedQuestion.pageIndex]?.sections[selectedQuestion.sectionIndex]?.questions[selectedQuestion.questionIndex] || null
    : null;

  const updateActiveQuestion = (field: keyof SurveyQuestion, value: any) => {
    if (!selectedQuestion) return;
    const { pageIndex, sectionIndex, questionIndex } = selectedQuestion;
    const updatedPages = [...pages];
    const q = updatedPages[pageIndex].sections[sectionIndex].questions[questionIndex];
    updatedPages[pageIndex].sections[sectionIndex].questions[questionIndex] = {
      ...q,
      [field]: value
    };
    setPages(updatedPages);
  };

  const handleSaveDraft = async () => {
    setIsSaving(true);
    setNotification(null);
    try {
      const payload: SurveySaveDraft = {
        projectId,
        surveyId,
        title: surveyTitle,
        pages
      };
      const res = await surveyBuilderApi.saveDraft(surveyId, payload);
      setStatus(res.status);
      setVersion(res.version);
      setNotification({ type: 'success', message: `Draft version ${res.version} saved successfully!` });
    } catch (err: any) {
      setNotification({ type: 'error', message: err.message || 'Error saving survey draft' });
    } finally {
      setIsSaving(false);
    }
  };

  const handlePublish = async () => {
    setIsPublishing(true);
    setNotification(null);
    try {
      const res = await surveyBuilderApi.publishSurvey(projectId, surveyId);
      setStatus(res.status);
      setVersion(res.version);
      setNotification({ type: 'success', message: `Survey version ${res.version} published successfully to production!` });
    } catch (err: any) {
      setNotification({ type: 'error', message: err.message || 'Error publishing survey' });
    } finally {
      setIsPublishing(false);
    }
  };

  const handleNewVersion = async () => {
    try {
      const res = await surveyBuilderApi.createDraftVersion(projectId, surveyId);
      setStatus(res.status);
      setVersion(res.version);
      setPages(res.pages);
      setNotification({ type: 'success', message: `New draft version ${res.version} created for design editing!` });
    } catch (err: any) {
      setNotification({ type: 'error', message: err.message || 'Error creating new draft version' });
    }
  };

  return (
    <div style={{ maxWidth: '1400px', margin: '0 auto', fontFamily: "'Inter', system-ui, sans-serif" }}>
      {/* Top Header Banner */}
      <header
        style={{
          background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
          color: '#ffffff',
          padding: '24px 32px',
          borderRadius: '16px',
          marginBottom: '24px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          boxShadow: '0 10px 25px -5px rgba(15, 23, 42, 0.25)'
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '8px' }}>
            <input
              type="text"
              value={surveyTitle[activeLocale] || surveyTitle['en-US'] || ''}
              onChange={(e) => setSurveyTitle({ ...surveyTitle, [activeLocale]: e.target.value })}
              style={{
                background: 'transparent',
                border: '1px solid transparent',
                color: '#ffffff',
                fontWeight: 800,
                fontSize: '1.5rem',
                padding: '2px 6px',
                borderRadius: '4px'
              }}
            />
            <span
              style={{
                background: status === 'PUBLISHED' ? '#10b981' : '#f59e0b',
                color: '#ffffff',
                padding: '4px 12px',
                borderRadius: '9999px',
                fontSize: '0.75rem',
                fontWeight: 700,
                letterSpacing: '0.05em'
              }}
            >
              {status} v{version}
            </span>
          </div>
          <p style={{ color: '#94a3b8', fontSize: '0.875rem', margin: 0 }}>
            Project ID: <strong style={{ color: '#e2e8f0' }}>{projectId}</strong> | Survey ID: <strong style={{ color: '#e2e8f0' }}>{surveyId}</strong>
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <select
            value={activeLocale}
            onChange={(e) => setActiveLocale(e.target.value)}
            style={{
              background: '#334155',
              color: '#ffffff',
              border: '1px solid #475569',
              padding: '8px 12px',
              borderRadius: '8px',
              fontWeight: 600,
              fontSize: '0.85rem'
            }}
          >
            <option value="en-US">en-US</option>
            <option value="si-LK">si-LK</option>
            <option value="ta-LK">ta-LK</option>
          </select>
          <button
            onClick={() => setIsSimulatorOpen(true)}
            style={{
              background: 'linear-gradient(135deg, #8b5cf6, #6d28d9)',
              color: '#ffffff',
              border: 'none',
              padding: '10px 20px',
              borderRadius: '8px',
              fontWeight: 700,
              cursor: 'pointer',
              boxShadow: '0 4px 12px rgba(139, 92, 246, 0.3)'
            }}
          >
            📱 Launch Logic Simulator
          </button>

          {status === 'PUBLISHED' ? (
            <button
              onClick={handleNewVersion}
              style={{
                background: 'linear-gradient(135deg, #3b82f6, #2563eb)',
                color: '#ffffff',
                border: 'none',
                padding: '10px 20px',
                borderRadius: '8px',
                fontWeight: 700,
                cursor: 'pointer',
                boxShadow: '0 4px 12px rgba(59, 130, 246, 0.3)'
              }}
            >
              + Create Version {version + 1}
            </button>
          ) : (
            <>
              <button
                onClick={handleSaveDraft}
                disabled={isSaving}
                style={{
                  background: '#334155',
                  color: '#ffffff',
                  border: 'none',
                  padding: '10px 20px',
                  borderRadius: '8px',
                  fontWeight: 600,
                  cursor: isSaving ? 'not-allowed' : 'pointer'
                }}
              >
                {isSaving ? 'Saving Draft...' : 'Save Draft'}
              </button>
              <button
                onClick={handlePublish}
                disabled={isPublishing}
                style={{
                  background: 'linear-gradient(135deg, #10b981, #059669)',
                  color: '#ffffff',
                  border: 'none',
                  padding: '10px 20px',
                  borderRadius: '8px',
                  fontWeight: 700,
                  cursor: isPublishing ? 'not-allowed' : 'pointer',
                  boxShadow: '0 4px 12px rgba(16, 185, 129, 0.3)'
                }}
              >
                {isPublishing ? 'Publishing...' : 'Publish Survey'}
              </button>
            </>
          )}
        </div>
      </header>

      {/* Notification Toast */}
      {notification && (
        <div
          style={{
            padding: '12px 20px',
            borderRadius: '8px',
            marginBottom: '20px',
            fontWeight: 600,
            background: notification.type === 'success' ? '#ecfdf5' : '#fef2f2',
            color: notification.type === 'success' ? '#047857' : '#b91c1c',
            border: `1px solid ${notification.type === 'success' ? '#a7f3d0' : '#fecaca'}`
          }}
        >
          {notification.message}
        </div>
      )}

      {/* Main Canvas Workspace (2 Columns) */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 420px', gap: '24px' }}>
        {/* Left Column: Interactive Page & Section AST Canvas */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#1e293b', margin: 0 }}>
              Survey Structure ({pages.length} Pages)
            </h2>
            <button
              onClick={addPage}
              style={{
                background: '#1e293b',
                color: '#ffffff',
                border: 'none',
                padding: '8px 16px',
                borderRadius: '8px',
                fontWeight: 600,
                fontSize: '0.875rem',
                cursor: 'pointer'
              }}
            >
              + Add Page
            </button>
          </div>

          {pages.map((page, pIdx) => (
            <div
              key={page.pageId}
              style={{
                background: '#ffffff',
                borderRadius: '12px',
                border: '1px solid #e2e8f0',
                boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
                overflow: 'hidden'
              }}
            >
              {/* Page Header */}
              <div
                style={{
                  background: '#f8fafc',
                  padding: '14px 20px',
                  borderBottom: '1px solid #e2e8f0',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span style={{ fontWeight: 800, color: '#3b82f6', background: '#eff6ff', padding: '4px 10px', borderRadius: '6px', fontSize: '0.85rem' }}>
                    PAGE {page.pageOrder}
                  </span>
                  <input
                    type="text"
                    value={page.title?.[activeLocale] || ''}
                    onChange={(e) => {
                      const updated = [...pages];
                      updated[pIdx].title = { ...updated[pIdx].title, [activeLocale]: e.target.value };
                      setPages(updated);
                    }}
                    placeholder="Page Title..."
                    style={{
                      border: '1px solid transparent',
                      background: 'transparent',
                      fontWeight: 700,
                      fontSize: '1rem',
                      color: '#0f172a',
                      padding: '4px 8px',
                      borderRadius: '4px'
                    }}
                  />
                </div>

                <div style={{ display: 'flex', gap: '6px' }}>
                  <button
                    onClick={() => movePage(pIdx, 'up')}
                    disabled={pIdx === 0}
                    style={{ background: '#e2e8f0', border: 'none', padding: '4px 10px', borderRadius: '4px', cursor: 'pointer' }}
                  >
                    ↑
                  </button>
                  <button
                    onClick={() => movePage(pIdx, 'down')}
                    disabled={pIdx === pages.length - 1}
                    style={{ background: '#e2e8f0', border: 'none', padding: '4px 10px', borderRadius: '4px', cursor: 'pointer' }}
                  >
                    ↓
                  </button>
                  <button
                    onClick={() => addSection(pIdx)}
                    style={{ background: '#3b82f6', color: '#fff', border: 'none', padding: '4px 10px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 600, cursor: 'pointer' }}
                  >
                    + Section
                  </button>
                  <button
                    onClick={() => removePage(pIdx)}
                    style={{ background: '#fee2e2', color: '#ef4444', border: 'none', padding: '4px 10px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 600, cursor: 'pointer' }}
                  >
                    Delete
                  </button>
                </div>
              </div>

              {/* Sections Container */}
              <div style={{ padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {page.sections.map((section, sIdx) => (
                  <div
                    key={section.sectionId}
                    style={{
                      background: '#fafafa',
                      borderRadius: '8px',
                      border: '1px border-dashed #cbd5e1',
                      padding: '16px'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#64748b' }}>
                        Section {section.sectionOrder}: {section.title?.[activeLocale] || 'Untitled Section'}
                      </span>
                      <button
                        onClick={() => addQuestion(pIdx, sIdx)}
                        style={{
                          background: '#10b981',
                          color: '#ffffff',
                          border: 'none',
                          padding: '6px 12px',
                          borderRadius: '6px',
                          fontSize: '0.8rem',
                          fontWeight: 600,
                          cursor: 'pointer'
                        }}
                      >
                        + Add Question
                      </button>
                    </div>

                    {/* Questions Cards */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                      {section.questions.map((question, qIdx) => {
                        const isSelected =
                          selectedQuestion?.pageIndex === pIdx &&
                          selectedQuestion?.sectionIndex === sIdx &&
                          selectedQuestion?.questionIndex === qIdx;

                        const isQuantitative =
                          question.type === 'LIKERT' || question.type === 'NPS' || question.type === 'MATRIX';
                        const missingGroup = isQuantitative && (!question.groupId || question.groupId.trim() === '');

                        return (
                          <div
                            key={question.questionId}
                            onClick={() => setSelectedQuestion({ pageIndex: pIdx, sectionIndex: sIdx, questionIndex: qIdx })}
                            style={{
                              background: isSelected ? '#eff6ff' : '#ffffff',
                              border: `2px solid ${isSelected ? '#3b82f6' : '#e2e8f0'}`,
                              borderRadius: '8px',
                              padding: '12px 16px',
                              cursor: 'pointer',
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                              boxShadow: isSelected ? '0 4px 6px -1px rgba(59, 130, 246, 0.15)' : 'none'
                            }}
                          >
                            <div style={{ flex: 1, paddingRight: '16px' }}>
                              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                                <span style={{ background: '#334155', color: '#fff', fontSize: '0.7rem', fontWeight: 700, padding: '2px 6px', borderRadius: '4px' }}>
                                  {question.type}
                                </span>
                                <strong style={{ color: '#1e293b', fontSize: '0.9rem' }}>
                                  {question.prompt[activeLocale] || question.prompt['en-US'] || 'Untitled Prompt'}
                                </strong>
                              </div>
                              {missingGroup && (
                                <span style={{ color: '#ef4444', fontSize: '0.75rem', fontWeight: 600 }}>
                                  ⚠️ BR-SRV-002 Violation: Quantitative question requires non-blank groupId!
                                </span>
                              )}
                            </div>

                            <div style={{ display: 'flex', gap: '4px' }} onClick={(e) => e.stopPropagation()}>
                              <button
                                onClick={() => moveQuestion(pIdx, sIdx, qIdx, 'up')}
                                disabled={qIdx === 0}
                                style={{ background: '#f1f5f9', border: 'none', padding: '2px 6px', borderRadius: '4px', cursor: 'pointer' }}
                              >
                                ↑
                              </button>
                              <button
                                onClick={() => moveQuestion(pIdx, sIdx, qIdx, 'down')}
                                disabled={qIdx === section.questions.length - 1}
                                style={{ background: '#f1f5f9', border: 'none', padding: '2px 6px', borderRadius: '4px', cursor: 'pointer' }}
                              >
                                ↓
                              </button>
                              <button
                                onClick={() => removeQuestion(pIdx, sIdx, qIdx)}
                                style={{ background: '#fee2e2', color: '#ef4444', border: 'none', padding: '2px 6px', borderRadius: '4px', cursor: 'pointer', fontSize: '0.75rem' }}
                              >
                                ✕
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Right Column: Question Properties & Logic Configuration Inspector */}
        <div style={{ background: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', padding: '24px', position: 'sticky', top: '24px', height: 'fit-content' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0f172a', marginBottom: '16px', borderBottom: '1px solid #e2e8f0', paddingBottom: '12px' }}>
            Question Properties Inspector
          </h3>

          {activeQuestion ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#475569', marginBottom: '6px' }}>
                  Question Type
                </label>
                <select
                  value={activeQuestion.type}
                  onChange={(e) => updateActiveQuestion('type', e.target.value as QuestionType)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontWeight: 600 }}
                >
                  <option value="LIKERT">Likert Scale (Quantitative)</option>
                  <option value="NPS">Net Promoter Score (Quantitative)</option>
                  <option value="MATRIX">Matrix Grid (Quantitative)</option>
                  <option value="MULTIPLE_CHOICE">Multiple Choice</option>
                  <option value="SINGLE_CHOICE">Single Choice</option>
                  <option value="TEXT_OPEN">Text Open Feedback</option>
                  <option value="NUMERIC">Numeric Input</option>
                  <option value="RATING_STARS">Star Rating</option>
                  <option value="SLIDER">Numeric Slider</option>
                  <option value="DATE_PICKER">Date Picker</option>
                </select>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#475569', marginBottom: '6px' }}>
                  Question Group ID (FR-SRV-003)
                </label>
                <input
                  type="text"
                  value={activeQuestion.groupId || ''}
                  onChange={(e) => updateActiveQuestion('groupId', e.target.value)}
                  placeholder="e.g. GRP-LEADERSHIP"
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    border: `1px solid ${
                      (activeQuestion.type === 'LIKERT' || activeQuestion.type === 'NPS' || activeQuestion.type === 'MATRIX') &&
                      (!activeQuestion.groupId || activeQuestion.groupId.trim() === '')
                        ? '#ef4444'
                        : '#cbd5e1'
                    }`
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#475569', marginBottom: '6px' }}>
                  Localized Question Prompt (en-US)
                </label>
                <textarea
                  rows={3}
                  value={activeQuestion.prompt['en-US'] || ''}
                  onChange={(e) => {
                    const updatedPrompt = { ...activeQuestion.prompt, 'en-US': e.target.value };
                    updateActiveQuestion('prompt', updatedPrompt);
                  }}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.875rem' }}
                />
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <input
                  type="checkbox"
                  id="mandatoryToggle"
                  checked={activeQuestion.isMandatory}
                  onChange={(e) => updateActiveQuestion('isMandatory', e.target.checked)}
                />
                <label htmlFor="mandatoryToggle" style={{ fontSize: '0.85rem', fontWeight: 600, color: '#1e293b' }}>
                  Mandatory Response Required
                </label>
              </div>
            </div>
          ) : (
            <p style={{ color: '#94a3b8', fontSize: '0.875rem' }}>
              Select a question card on the canvas to configure properties and logic rules.
            </p>
          )}
        </div>
      </div>

      {/* Simulator Modal */}
      <SurveyLogicSimulatorModal
        isOpen={isSimulatorOpen}
        onClose={() => setIsSimulatorOpen(false)}
        pages={pages}
      />
    </div>
  );
};
