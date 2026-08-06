import React, { useEffect, useState } from 'react';
import { SurveyResponse as SurveyAST } from '../../types/survey';
import { SurveyPlayerPage } from './SurveyPlayerPage';
import { getQueuedOfflineResponseCount } from '../../utils/offlineQueueSync';

interface Props {
  projectId: string;
  campaignId: string;
  surveyId: string;
  survey: SurveyAST;
}

export const KioskPlayerPage: React.FC<Props> = ({ projectId, campaignId, surveyId, survey }) => {
  const [pin, setPin] = useState<string>('');
  const [isPinValid, setIsPinValid] = useState<boolean>(false);
  const [submissionComplete, setSubmissionComplete] = useState<boolean>(false);
  const [resetCountdown, setResetCountdown] = useState<number>(5);
  const [offlineCount, setOfflineCount] = useState<number>(0);

  useEffect(() => {
    getQueuedOfflineResponseCount().then(setOfflineCount).catch(() => {});
  }, [submissionComplete]);

  // 60-second inactivity reset timer
  useEffect(() => {
    if (!isPinValid || submissionComplete) return;

    let inactivityTimer: NodeJS.Timeout;
    const resetInactivity = () => {
      clearTimeout(inactivityTimer);
      inactivityTimer = setTimeout(() => {
        setPin('');
        setIsPinValid(false);
      }, 60000);
    };

    resetInactivity();
    window.addEventListener('touchstart', resetInactivity);
    window.addEventListener('click', resetInactivity);

    return () => {
      clearTimeout(inactivityTimer);
      window.removeEventListener('touchstart', resetInactivity);
      window.removeEventListener('click', resetInactivity);
    };
  }, [isPinValid, submissionComplete]);

  // 5-second post-submission auto-reset timer
  useEffect(() => {
    if (!submissionComplete) return;

    const interval = setInterval(() => {
      setResetCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          setPin('');
          setIsPinValid(false);
          setSubmissionComplete(false);
          return 5;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [submissionComplete]);

  const handleKeyPress = (digit: string) => {
    if (pin.length < 6) {
      const nextPin = pin + digit;
      setPin(nextPin);
      if (nextPin.length === 6) {
        setIsPinValid(true);
      }
    }
  };

  const handleClearPin = () => {
    setPin('');
  };

  const handleSubmitted = () => {
    setSubmissionComplete(true);
    setResetCountdown(5);
  };

  if (submissionComplete) {
    return (
      <div style={{ minHeight: '100vh', background: '#0f172a', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'system-ui, sans-serif', color: '#fff' }}>
        <div style={{ textAlign: 'center', background: '#1e293b', padding: '48px', borderRadius: '24px', maxWidth: '550px', border: '1px solid #334155' }}>
          <div style={{ fontSize: '64px', marginBottom: '16px' }}>🎉</div>
          <h2 style={{ fontSize: '28px', fontWeight: '800', margin: '0 0 12px 0', color: '#38bdf8' }}>Survey Submitted!</h2>
          <p style={{ color: '#94a3b8', fontSize: '16px', margin: '0 0 24px 0' }}>
            Thank you for your valuable feedback. Your session is complete.
          </p>
          <div style={{ background: '#0284c7', padding: '12px 24px', borderRadius: '30px', fontWeight: '700', fontSize: '15px', display: 'inline-block' }}>
            🔄 Resetting to PIN screen in {resetCountdown}s...
          </div>
        </div>
      </div>
    );
  }

  if (isPinValid) {
    return (
      <div style={{ padding: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', background: '#0f172a', color: '#fff', padding: '12px 20px', borderRadius: '8px' }}>
          <span>🏬 Kiosk Mode Active | PIN: <code>••••••</code></span>
          {offlineCount > 0 && (
            <span style={{ background: '#eab308', color: '#000', padding: '4px 10px', borderRadius: '12px', fontWeight: '700', fontSize: '12px' }}>
              📡 {offlineCount} Queued Offline
            </span>
          )}
          <button onClick={() => setIsPinValid(false)} style={{ background: '#ef4444', color: '#fff', border: 'none', padding: '6px 14px', borderRadius: '6px', cursor: 'pointer', fontWeight: '600' }}>
            Exit Session
          </button>
        </div>
        <SurveyPlayerPage
          projectId={projectId}
          campaignId={campaignId}
          surveyId={surveyId}
          responseToken={`KIOSK-PIN-${pin}`}
          respondentType="KIOSK"
          survey={survey}
          onSubmitted={handleSubmitted}
        />
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', background: '#0f172a', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'system-ui, sans-serif', color: '#fff' }}>
      <div style={{ background: '#1e293b', padding: '40px', borderRadius: '24px', border: '1px solid #334155', width: '380px', textAlign: 'center' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: '800', margin: 0, color: '#f8fafc' }}>🏬 Factory Tablet Kiosk</h2>
          {offlineCount > 0 && (
            <span style={{ background: '#eab308', color: '#000', padding: '2px 8px', borderRadius: '10px', fontWeight: '700', fontSize: '11px' }}>
              {offlineCount} Offline
            </span>
          )}
        </div>
        <p style={{ color: '#94a3b8', fontSize: '13px', margin: '0 0 24px 0' }}>Enter your 6-digit numeric Kiosk PIN to begin survey.</p>

        {/* PIN Display Dots */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: '12px', marginBottom: '28px' }}>
          {[0, 1, 2, 3, 4, 5].map((idx) => (
            <div
              key={idx}
              style={{
                width: '18px',
                height: '18px',
                borderRadius: '50%',
                background: idx < pin.length ? '#38bdf8' : '#334155',
                border: '2px solid #475569',
                transition: 'all 0.2s ease',
              }}
            />
          ))}
        </div>

        {/* Touch-Optimized Numeric Keypad */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginBottom: '16px' }}>
          {['1', '2', '3', '4', '5', '6', '7', '8', '9'].map((digit) => (
            <button
              key={digit}
              type="button"
              onClick={() => handleKeyPress(digit)}
              style={{
                padding: '18px 0',
                borderRadius: '14px',
                border: '1px solid #475569',
                background: '#0f172a',
                color: '#f8fafc',
                fontSize: '22px',
                fontWeight: '700',
                cursor: 'pointer',
              }}
            >
              {digit}
            </button>
          ))}
          <button
            type="button"
            onClick={handleClearPin}
            style={{
              padding: '18px 0',
              borderRadius: '14px',
              border: '1px solid #475569',
              background: '#334155',
              color: '#94a3b8',
              fontSize: '14px',
              fontWeight: '700',
              cursor: 'pointer',
            }}
          >
            CLEAR
          </button>
          <button
            type="button"
            onClick={() => handleKeyPress('0')}
            style={{
              padding: '18px 0',
              borderRadius: '14px',
              border: '1px solid #475569',
              background: '#0f172a',
              color: '#f8fafc',
              fontSize: '22px',
              fontWeight: '700',
              cursor: 'pointer',
            }}
          >
            0
          </button>
        </div>
      </div>
    </div>
  );
};
