import React, { useState } from 'react';
import { LogicRule, SurveyPage } from '../../types/survey';

interface SurveyLogicSimulatorModalProps {
  isOpen: boolean;
  onClose: () => void;
  pages: SurveyPage[];
  defaultLocale?: string;
}

export const SurveyLogicSimulatorModal: React.FC<SurveyLogicSimulatorModalProps> = ({
  isOpen,
  onClose,
  pages,
  defaultLocale = 'en-US'
}) => {
  const [deviceFrame, setDeviceFrame] = useState<'desktop' | 'mobile'>('desktop');
  const [activeLocale, setActiveLocale] = useState<string>(defaultLocale);
  const [currentPageIndex, setCurrentPageIndex] = useState<number>(0);
  const [userAnswers, setUserAnswers] = useState<Record<string, string>>({});
  const [pageHistory, setPageHistory] = useState<number[]>([0]);
  const [auditLog, setAuditLog] = useState<string[]>(['Simulator initialized at Page 1.']);

  if (!isOpen || !pages || pages.length === 0) return null;

  const currentPage = pages[currentPageIndex] || pages[0];

  const handleAnswerChange = (questionId: string, value: string) => {
    setUserAnswers({
      ...userAnswers,
      [questionId]: value
    });
  };

  const evaluateRule = (rule: LogicRule, answerValue: string): boolean => {
    if (!answerValue || !rule || !rule.operator) return false;
    const compVal = rule.comparisonValue || '';
    const ans = answerValue.trim();

    switch (rule.operator) {
      case 'EQUALS':
        return ans.toLowerCase() === compVal.trim().toLowerCase();
      case 'NOT_EQUALS':
        return ans.toLowerCase() !== compVal.trim().toLowerCase();
      case 'LESS_THAN': {
        const numAns = parseFloat(ans);
        const numComp = parseFloat(compVal);
        return !isNaN(numAns) && !isNaN(numComp) && numAns < numComp;
      }
      case 'GREATER_THAN': {
        const numAns = parseFloat(ans);
        const numComp = parseFloat(compVal);
        return !isNaN(numAns) && !isNaN(numComp) && numAns > numComp;
      }
      default:
        return false;
    }
  };

  const handleNextPage = () => {
    let nextTargetPageId: string | null = null;
    let triggeredRuleInfo: string | null = null;

    // Check logic rules on current page questions
    for (const section of currentPage.sections) {
      for (const question of section.questions) {
        const answer = userAnswers[question.questionId];
        if (answer && question.logicRules && question.logicRules.length > 0) {
          for (const rule of question.logicRules) {
            if (evaluateRule(rule, answer)) {
              nextTargetPageId = rule.targetPageId;
              triggeredRuleInfo = `Question ${question.questionId} ('${answer}') matched rule (${rule.operator} ${rule.comparisonValue}) -> Skip to ${rule.targetPageId}`;
              break;
            }
          }
        }
        if (nextTargetPageId) break;
      }
      if (nextTargetPageId) break;
    }

    let targetIndex = currentPageIndex + 1;

    if (nextTargetPageId) {
      const foundIdx = pages.findIndex((p) => p.pageId === nextTargetPageId);
      if (foundIdx !== -1) {
        targetIndex = foundIdx;
        setAuditLog((prev) => [...prev, `⚡ Branch Jump: ${triggeredRuleInfo}`]);
      } else {
        setAuditLog((prev) => [...prev, `Sequential step: Page ${currentPageIndex + 1} -> Page ${currentPageIndex + 2}`]);
      }
    } else {
      setAuditLog((prev) => [...prev, `Sequential step: Page ${currentPageIndex + 1} -> Page ${currentPageIndex + 2}`]);
    }

    if (targetIndex < pages.length) {
      setPageHistory([...pageHistory, targetIndex]);
      setCurrentPageIndex(targetIndex);
    }
  };

  const handlePrevPage = () => {
    if (pageHistory.length > 1) {
      const newHistory = [...pageHistory];
      newHistory.pop();
      const prevIndex = newHistory[newHistory.length - 1];
      setPageHistory(newHistory);
      setCurrentPageIndex(prevIndex);
      setAuditLog((prev) => [...prev, `Navigated back to Page ${prevIndex + 1}`]);
    }
  };

  const resolveText = (textMap?: Record<string, string>): string => {
    if (!textMap) return '';
    if (textMap[activeLocale] && textMap[activeLocale].trim() !== '') {
      return textMap[activeLocale];
    }
    if (textMap[defaultLocale] && textMap[defaultLocale].trim() !== '') {
      return textMap[defaultLocale];
    }
    return Object.values(textMap)[0] || '';
  };

  const progressPercent = Math.round(((currentPageIndex + 1) / pages.length) * 100);

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(15, 23, 42, 0.75)',
        backdropFilter: 'blur(8px)',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 1000,
        padding: '24px'
      }}
    >
      <div
        style={{
          background: '#f8fafc',
          borderRadius: '20px',
          width: '100%',
          maxWidth: '1200px',
          height: '90vh',
          display: 'flex',
          flexDirection: 'column',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
          overflow: 'hidden'
        }}
      >
        {/* Simulator Top Bar */}
        <div
          style={{
            background: '#0f172a',
            color: '#ffffff',
            padding: '16px 24px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 800 }}>
              📱 Real-Time Survey Logic & Multi-Device Simulator
            </h3>
            <span style={{ background: '#3b82f6', padding: '2px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700 }}>
              Page {currentPageIndex + 1} of {pages.length}
            </span>
          </div>

          {/* Device & Locale Controls */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{ background: '#1e293b', padding: '4px', borderRadius: '8px', display: 'flex', gap: '4px' }}>
              <button
                onClick={() => setDeviceFrame('desktop')}
                style={{
                  background: deviceFrame === 'desktop' ? '#3b82f6' : 'transparent',
                  color: '#ffffff',
                  border: 'none',
                  padding: '6px 12px',
                  borderRadius: '6px',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                💻 Desktop View
              </button>
              <button
                onClick={() => setDeviceFrame('mobile')}
                style={{
                  background: deviceFrame === 'mobile' ? '#3b82f6' : 'transparent',
                  color: '#ffffff',
                  border: 'none',
                  padding: '6px 12px',
                  borderRadius: '6px',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                📱 Mobile View (375px)
              </button>
            </div>

            <select
              value={activeLocale}
              onChange={(e) => setActiveLocale(e.target.value)}
              style={{
                background: '#1e293b',
                color: '#ffffff',
                border: '1px solid #334155',
                padding: '6px 12px',
                borderRadius: '6px',
                fontSize: '0.85rem',
                fontWeight: 600
              }}
            >
              <option value="en-US">English (en-US)</option>
              <option value="si-LK">Sinhala (si-LK)</option>
              <option value="ta-LK">Tamil (ta-LK)</option>
            </select>

            <button
              onClick={onClose}
              style={{
                background: '#ef4444',
                color: '#ffffff',
                border: 'none',
                padding: '6px 14px',
                borderRadius: '6px',
                fontWeight: 700,
                cursor: 'pointer'
              }}
            >
              Close
            </button>
          </div>
        </div>

        {/* Workspace Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', flex: 1, overflow: 'hidden' }}>
          {/* Central Frame Viewport Container */}
          <div style={{ background: '#e2e8f0', padding: '32px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', overflowY: 'auto' }}>
            <div
              style={{
                width: deviceFrame === 'mobile' ? '375px' : '100%',
                maxWidth: deviceFrame === 'mobile' ? '375px' : '860px',
                background: '#ffffff',
                borderRadius: deviceFrame === 'mobile' ? '32px' : '16px',
                border: deviceFrame === 'mobile' ? '12px solid #0f172a' : '1px solid #cbd5e1',
                boxShadow: deviceFrame === 'mobile' ? '0 20px 25px -5px rgba(0,0,0,0.3)' : '0 4px 6px -1px rgba(0,0,0,0.05)',
                padding: '32px',
                minHeight: '520px',
                display: 'flex',
                flexDirection: 'column',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
              }}
            >
              {/* Progress Bar */}
              <div style={{ width: '100%', background: '#e2e8f0', height: '6px', borderRadius: '3px', marginBottom: '24px', overflow: 'hidden' }}>
                <div style={{ width: `${progressPercent}%`, background: '#3b82f6', height: '100%', transition: 'width 0.3s ease' }} />
              </div>

              <h2 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', marginBottom: '20px' }}>
                {resolveText(currentPage.title) || `Page ${currentPage.pageOrder}`}
              </h2>

              {/* Page Sections & Questions */}
              <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '24px' }}>
                {currentPage.sections.map((section) => (
                  <div key={section.sectionId}>
                    {section.title && (
                      <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#475569', marginBottom: '12px' }}>
                        {resolveText(section.title)}
                      </h4>
                    )}

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                      {section.questions.map((question) => (
                        <div key={question.questionId} style={{ background: '#f8fafc', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                          <label style={{ display: 'block', fontWeight: 700, color: '#1e293b', marginBottom: '8px', fontSize: '0.9rem' }}>
                            {resolveText(question.prompt)} {question.isMandatory && <span style={{ color: '#ef4444' }}>*</span>}
                          </label>

                          {question.type === 'LIKERT' && (
                            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                              {['Strongly Disagree', 'Disagree', 'Neutral', 'Agree', 'Strongly Agree'].map((opt, i) => (
                                <button
                                  key={opt}
                                  onClick={() => handleAnswerChange(question.questionId, String(i + 1))}
                                  style={{
                                    flex: 1,
                                    padding: '8px',
                                    borderRadius: '6px',
                                    border: userAnswers[question.questionId] === String(i + 1) ? '2px solid #3b82f6' : '1px solid #cbd5e1',
                                    background: userAnswers[question.questionId] === String(i + 1) ? '#eff6ff' : '#ffffff',
                                    fontWeight: 600,
                                    fontSize: '0.75rem',
                                    cursor: 'pointer'
                                  }}
                                >
                                  {opt}
                                </button>
                              ))}
                            </div>
                          )}

                          {question.type === 'NPS' && (
                            <div style={{ display: 'flex', gap: '4px', overflowX: 'auto' }}>
                              {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
                                <button
                                  key={num}
                                  onClick={() => handleAnswerChange(question.questionId, String(num))}
                                  style={{
                                    width: '32px',
                                    height: '36px',
                                    borderRadius: '6px',
                                    border: userAnswers[question.questionId] === String(num) ? '2px solid #3b82f6' : '1px solid #cbd5e1',
                                    background: userAnswers[question.questionId] === String(num) ? '#eff6ff' : '#ffffff',
                                    fontWeight: 700,
                                    fontSize: '0.8rem',
                                    cursor: 'pointer'
                                  }}
                                >
                                  {num}
                                </button>
                              ))}
                            </div>
                          )}

                          {question.type !== 'LIKERT' && question.type !== 'NPS' && (
                            <input
                              type="text"
                              value={userAnswers[question.questionId] || ''}
                              onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                              placeholder="Type response answer..."
                              style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                            />
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>

              {/* Navigation Actions */}
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '32px', paddingTop: '16px', borderTop: '1px solid #e2e8f0' }}>
                <button
                  onClick={handlePrevPage}
                  disabled={pageHistory.length <= 1}
                  style={{
                    background: '#cbd5e1',
                    color: '#1e293b',
                    border: 'none',
                    padding: '10px 20px',
                    borderRadius: '8px',
                    fontWeight: 600,
                    cursor: pageHistory.length <= 1 ? 'not-allowed' : 'pointer'
                  }}
                >
                  ← Back
                </button>

                <button
                  onClick={handleNextPage}
                  disabled={currentPageIndex >= pages.length - 1}
                  style={{
                    background: 'linear-gradient(135deg, #3b82f6, #1d4ed8)',
                    color: '#ffffff',
                    border: 'none',
                    padding: '10px 24px',
                    borderRadius: '8px',
                    fontWeight: 700,
                    cursor: currentPageIndex >= pages.length - 1 ? 'not-allowed' : 'pointer'
                  }}
                >
                  Next Page →
                </button>
              </div>
            </div>
          </div>

          {/* Right Column: Logic Inspector Audit Drawer */}
          <div style={{ background: '#1e293b', color: '#ffffff', padding: '20px', display: 'flex', flexDirection: 'column' }}>
            <h4 style={{ fontSize: '1rem', fontWeight: 800, borderBottom: '1px solid #334155', paddingBottom: '10px', margin: '0 0 16px 0' }}>
              ⚡ AST Branching Audit Log
            </h4>

            <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {auditLog.map((logItem, idx) => (
                <div
                  key={idx}
                  style={{
                    background: '#0f172a',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    fontSize: '0.75rem',
                    fontFamily: 'monospace',
                    color: logItem.includes('⚡') ? '#60a5fa' : '#94a3b8',
                    borderLeft: logItem.includes('⚡') ? '3px solid #3b82f6' : 'none'
                  }}
                >
                  {logItem}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
