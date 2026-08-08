import React, { useEffect, useState } from 'react';
import { RespondentType, ResponseSubmission } from '../../types/response';
import { SurveyResponse as SurveyAST } from '../../types/survey';
import { submitResponse } from '../../services/responseIngestionApi';
import { evaluateBranchingTargetPage } from '../../utils/logicEvaluator';

interface Props {
  projectId: string;
  campaignId: string;
  surveyId: string;
  responseToken?: string;
  respondentType: RespondentType;
  nodeId?: string;
  survey: SurveyAST;
  onSubmitted?: (responseId: string) => void;
}

export const SurveyPlayerPage: React.FC<Props> = ({
  projectId,
  campaignId,
  surveyId,
  responseToken,
  respondentType,
  nodeId,
  survey,
  onSubmitted,
}) => {
  const STORAGE_KEY = `tesp_survey_draft_${surveyId}`;

  const [currentPageIndex, setCurrentPageIndex] = useState<number>(0);
  const [answers, setAnswers] = useState<Record<string, { numericValue?: number; textValue?: string; selectedOptions?: string[] }>>(() => {
    try {
      const saved = sessionStorage.getItem(STORAGE_KEY);
      return saved ? JSON.parse(saved) : {};
    } catch {
      return {};
    }
  });

  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
  const [submittedResponseId, setSubmittedResponseId] = useState<string>('');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    try {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(answers));
    } catch {
      // ignore storage errors
    }
  }, [answers, STORAGE_KEY]);

  const pages = survey.pages || [];
  const currentPage = pages[currentPageIndex] || { sections: [] };
  const totalPages = pages.length || 1;
  const progressPercent = Math.round(((currentPageIndex + 1) / totalPages) * 100);

  const handleNumericAnswer = (questionId: string, value: number) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: { ...prev[questionId], numericValue: value },
    }));
  };

  const handleTextAnswer = (questionId: string, text: string) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: { ...prev[questionId], textValue: text },
    }));
  };

  const handleOptionToggle = (questionId: string, option: string, isSingleChoice: boolean) => {
    setAnswers((prev) => {
      const existing = prev[questionId]?.selectedOptions || [];
      let updated: string[];
      if (isSingleChoice) {
        updated = [option];
      } else {
        updated = existing.includes(option) ? existing.filter((o) => o !== option) : [...existing, option];
      }
      return {
        ...prev,
        [questionId]: { ...prev[questionId], selectedOptions: updated },
      };
    });
  };

  const handleNextPage = () => {
    // Check if branching rule triggers jump to specific page ID
    const currentQuestions = currentPage.sections?.flatMap((s) => s.questions || []) || [];
    let targetPageId: string | null = null;

    for (const q of currentQuestions) {
      if (q.logicRules) {
        targetPageId = evaluateBranchingTargetPage(answers, q.logicRules);
        if (targetPageId) break;
      }
    }

    if (targetPageId) {
      const targetIndex = pages.findIndex((p) => p.pageId === targetPageId);
      if (targetIndex !== -1) {
        setCurrentPageIndex(targetIndex);
        return;
      }
    }

    setCurrentPageIndex((prev) => Math.min(totalPages - 1, prev + 1));
  };

  const handleSubmitSurvey = async () => {
    setIsSubmitting(true);
    setErrorMsg(null);

    const answerItems = Object.entries(answers).map(([questionId, ans]) => ({
      questionId,
      questionType: 'AST_QUESTION',
      numericValue: ans.numericValue,
      textValue: ans.textValue,
      selectedOptions: ans.selectedOptions,
    }));

    const payload: ResponseSubmission = {
      projectId,
      campaignId,
      surveyId,
      surveyVersion: survey.version || 1,
      responseToken,
      respondentType,
      nodeId,
      answers: answerItems,
    };

    try {
      const res = await submitResponse(payload);
      sessionStorage.removeItem(STORAGE_KEY);
      setIsSubmitted(true);
      setSubmittedResponseId(res.responseId);
      if (onSubmitted) {
        onSubmitted(res.responseId);
      }
    } catch (err: any) {
      setErrorMsg(err.message || 'Submission failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSubmitted) {
    return (
      <div style={{ maxWidth: '650px', margin: '60px auto', padding: '40px', background: '#fff', borderRadius: '16px', border: '1px solid #e2e8f0', textAlign: 'center', fontFamily: 'system-ui, sans-serif' }}>
        <div style={{ fontSize: '48px', marginBottom: '16px' }}>🎉</div>
        <h2 style={{ fontSize: '24px', fontWeight: '800', color: '#0f172a', margin: '0 0 12px 0' }}>Thank You for Your Feedback!</h2>
        <p style={{ color: '#64748b', fontSize: '15px', margin: '0 0 24px 0' }}>
          Your survey response has been securely ingested into the platform dataset.
        </p>
        <div style={{ background: '#f8fafc', padding: '12px 20px', borderRadius: '8px', fontSize: '13px', color: '#475569', display: 'inline-block' }}>
          Response Identifier: <code>{submittedResponseId}</code>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', fontFamily: 'system-ui, -apple-system, sans-serif', color: '#1e293b' }}>
      {/* Header & Progress Bar */}
      <div style={{ background: '#0f172a', borderRadius: '12px', padding: '24px', color: '#fff', marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
          <h2 style={{ margin: 0, fontSize: '20px', fontWeight: '700' }}>
            {survey.title?.['en'] || survey.title?.['en-US'] || 'Enterprise Pulse Survey'}
          </h2>
          <span style={{ fontSize: '13px', color: '#38bdf8', fontWeight: '600' }}>
            Page {currentPageIndex + 1} of {totalPages}
          </span>
        </div>
        <div style={{ width: '100%', height: '8px', background: '#334155', borderRadius: '4px', overflow: 'hidden' }}>
          <div style={{ width: `${progressPercent}%`, height: '100%', background: '#38bdf8', transition: 'width 0.4s ease' }} />
        </div>
      </div>

      {errorMsg && (
        <div style={{ background: '#fef2f2', border: '1px solid #fca5a5', color: '#991b1b', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px' }}>
          ⚠️ {errorMsg}
        </div>
      )}

      {/* Sections and Questions Container */}
      <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '32px', marginBottom: '24px' }}>
        {currentPage.sections?.map((section) => (
          <div key={section.sectionId} style={{ marginBottom: '28px' }}>
            {section.title && (
              <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#334155', borderBottom: '2px solid #f1f5f9', paddingBottom: '8px', marginBottom: '20px' }}>
                {section.title['en'] || Object.values(section.title)[0]}
              </h3>
            )}

            {section.questions?.map((q) => {
              const promptText = q.prompt?.['en'] || Object.values(q.prompt)[0] || 'Question Prompt';
              const currentAns = answers[q.questionId] || {};

              return (
                <div key={q.questionId} style={{ marginBottom: '28px', background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: '10px', padding: '20px' }}>
                  <label style={{ display: 'block', fontWeight: '600', fontSize: '15px', marginBottom: '14px' }}>
                    {promptText} {q.isMandatory && <span style={{ color: '#ef4444' }}>*</span>}
                  </label>

                  {/* LIKERT Scale (1-5 Buttons) */}
                  {(q.type === 'LIKERT' || (q.type as string) === 'RATING_STARS') && (
                    <div style={{ display: 'flex', gap: '10px' }}>
                      {[1, 2, 3, 4, 5].map((val) => (
                        <button
                          key={val}
                          type="button"
                          onClick={() => handleNumericAnswer(q.questionId, val)}
                          style={{
                            flex: 1,
                            padding: '12px 0',
                            borderRadius: '8px',
                            border: currentAns.numericValue === val ? '2px solid #0284c7' : '1px solid #cbd5e1',
                            background: currentAns.numericValue === val ? '#e0f2fe' : '#fff',
                            color: currentAns.numericValue === val ? '#0369a1' : '#475569',
                            fontWeight: '700',
                            cursor: 'pointer',
                            fontSize: '15px',
                          }}
                        >
                          {(q.type as string) === 'RATING_STARS' ? `${'★'.repeat(val)}` : val}
                        </button>
                      ))}
                    </div>
                  )}

                  {/* NPS Scale (0-10 Buttons) */}
                  {q.type === 'NPS' && (
                    <div>
                      <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '6px' }}>
                        {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((val) => (
                          <button
                            key={val}
                            type="button"
                            onClick={() => handleNumericAnswer(q.questionId, val)}
                            style={{
                              width: '40px',
                              height: '42px',
                              borderRadius: '8px',
                              border: currentAns.numericValue === val ? '2px solid #0284c7' : '1px solid #cbd5e1',
                              background: currentAns.numericValue === val ? '#e0f2fe' : '#fff',
                              color: currentAns.numericValue === val ? '#0369a1' : '#475569',
                              fontWeight: '700',
                              cursor: 'pointer',
                              fontSize: '14px',
                            }}
                          >
                            {val}
                          </button>
                        ))}
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b', fontSize: '12px', marginTop: '6px' }}>
                        <span>Not likely at all</span>
                        <span>Extremely likely</span>
                      </div>
                    </div>
                  )}

                  {/* Open Text Input / Textarea */}
                  {((q.type as string) === 'TEXT_OPEN' || q.type === 'LONG_TEXT' || q.type === 'SHORT_TEXT') && (
                    <textarea
                      rows={q.type === 'LONG_TEXT' ? 4 : 2}
                      value={currentAns.textValue || ''}
                      onChange={(e) => handleTextAnswer(q.questionId, e.target.value)}
                      placeholder="Type your open response here..."
                      style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px', fontFamily: 'inherit' }}
                    />
                  )}

                  {/* Numeric Input */}
                  {q.type === 'NUMERIC' && (
                    <input
                      type="number"
                      value={currentAns.numericValue !== undefined ? currentAns.numericValue : ''}
                      onChange={(e) => handleNumericAnswer(q.questionId, parseFloat(e.target.value) || 0)}
                      style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px' }}
                    />
                  )}

                  {/* Choice Select Options */}
                  {(q.type === 'SINGLE_CHOICE' || q.type === 'MULTIPLE_CHOICE') && (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      {['Option 1', 'Option 2', 'Option 3'].map((opt) => {
                        const isChecked = currentAns.selectedOptions?.includes(opt) || false;
                        return (
                          <label key={opt} style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '14px', cursor: 'pointer' }}>
                            <input
                              type={q.type === 'SINGLE_CHOICE' ? 'radio' : 'checkbox'}
                              checked={isChecked}
                              onChange={() => handleOptionToggle(q.questionId, opt, q.type === 'SINGLE_CHOICE')}
                            />
                            <span>{opt}</span>
                          </label>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        ))}

        {/* Page Navigation Controls */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
          <button
            type="button"
            onClick={() => setCurrentPageIndex((prev) => Math.max(0, prev - 1))}
            disabled={currentPageIndex === 0}
            style={{
              padding: '10px 20px',
              borderRadius: '8px',
              border: '1px solid #cbd5e1',
              background: '#fff',
              cursor: currentPageIndex === 0 ? 'not-allowed' : 'pointer',
              opacity: currentPageIndex === 0 ? 0.5 : 1,
            }}
          >
            ← Previous Page
          </button>

          {currentPageIndex < totalPages - 1 ? (
            <button
              type="button"
              onClick={handleNextPage}
              style={{ padding: '10px 24px', borderRadius: '8px', border: 'none', background: '#0284c7', color: '#fff', fontWeight: '600', cursor: 'pointer' }}
            >
              Next Page →
            </button>
          ) : (
            <button
              type="button"
              onClick={handleSubmitSurvey}
              disabled={isSubmitting}
              style={{ padding: '12px 28px', borderRadius: '8px', border: 'none', background: '#16a34a', color: '#fff', fontWeight: '700', fontSize: '15px', cursor: 'pointer' }}
            >
              {isSubmitting ? 'Submitting...' : 'Submit Final Response 🚀'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
