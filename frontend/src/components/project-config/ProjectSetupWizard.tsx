import React, { useState } from 'react';
import { ProjectCreateRequest, ProjectResponse } from '../../types/projectConfig';
import { ThemeCustomizer } from './ThemeCustomizer';
import { projectConfigApi } from '../../api/projectConfigApi';

interface ProjectSetupWizardProps {
  onSuccess?: (project: ProjectResponse) => void;
}

export const ProjectSetupWizard: React.FC<ProjectSetupWizardProps> = ({ onSuccess }) => {
  const [currentStep, setCurrentStep] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successResponse, setSuccessResponse] = useState<ProjectResponse | null>(null);

  const [formData, setFormData] = useState<ProjectCreateRequest>({
    projectId: 'PRJ-99201',
    name: 'Aitken Spence Enterprise Workspace',
    branding: {
      companyName: 'Aitken Spence PLC',
      logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png',
      primaryColor: '#1E3A8A',
      secondaryColor: '#3B82F6'
    },
    supportedLocales: ['en-US', 'si-LK', 'ta-LK'],
    defaultLocale: 'en-US',
    features: {
      aiAnalyticsEnabled: true,
      actionPlanningEnabled: true,
      kioskModeEnabled: false,
      smsDistributionEnabled: true
    }
  });

  const handleNext = () => {
    if (currentStep === 1) {
      if (!formData.projectId || !/^PRJ-[A-Z0-9]{4,10}$/.test(formData.projectId)) {
        setErrorMessage('Project ID must match pattern ^PRJ-[A-Z0-9]{4,10}$ (e.g. PRJ-99201)');
        return;
      }
      if (!formData.name.trim()) {
        setErrorMessage('Project Workspace Name is required.');
        return;
      }
    }
    setErrorMessage(null);
    setCurrentStep((prev) => Math.min(prev + 1, 4));
  };

  const handlePrev = () => {
    setErrorMessage(null);
    setCurrentStep((prev) => Math.max(prev - 1, 1));
  };

  const handleSubmit = async () => {
    setLoading(true);
    setErrorMessage(null);
    try {
      const res = await projectConfigApi.createProject(formData);
      if (res.success) {
        setSuccessResponse(res.data);
        if (onSuccess) onSuccess(res.data);
      } else {
        setErrorMessage(res.message || 'Failed to provision project workspace.');
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'Network error occurred while connecting to backend.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '32px 16px', fontFamily: "'Inter', system-ui, sans-serif" }}>
      {/* Wizard Header */}
      <div style={{ textAlign: 'center', marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2rem', fontWeight: 800, color: '#0f172a', marginBottom: '8px' }}>
          Project Workspace Provisioning
        </h1>
        <p style={{ color: '#64748b', fontSize: '1rem' }}>
          Setup enterprise tenant configuration, custom branding, WCAG accessibility themes, and feature flags.
        </p>
      </div>

      {/* Progress Stepper Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '40px', position: 'relative' }}>
        {['1. Workspace Info', '2. Branding & Theme', '3. Locales & Features', '4. Review & Deploy'].map((label, idx) => {
          const stepNum = idx + 1;
          const isActive = stepNum === currentStep;
          const isCompleted = stepNum < currentStep;

          return (
            <div key={label} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1, zIndex: 2 }}>
              <div style={{
                width: '36px',
                height: '36px',
                borderRadius: '50%',
                background: isActive ? '#2563eb' : isCompleted ? '#16a34a' : '#e2e8f0',
                color: isActive || isCompleted ? '#ffffff' : '#64748b',
                display: 'grid',
                placeItems: 'center',
                fontWeight: 700,
                fontSize: '0.9rem',
                marginBottom: '8px',
                transition: 'all 0.2s ease'
              }}>
                {isCompleted ? '✓' : stepNum}
              </div>
              <span style={{ fontSize: '0.85rem', fontWeight: isActive ? 600 : 500, color: isActive ? '#1e293b' : '#64748b' }}>
                {label}
              </span>
            </div>
          );
        })}
      </div>

      {/* Error Alert Message */}
      {errorMessage && (
        <div style={{ padding: '14px 18px', background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px', color: '#991b1b', marginBottom: '24px', fontSize: '0.9rem' }}>
          ⚠️ {errorMessage}
        </div>
      )}

      {/* Success View */}
      {successResponse ? (
        <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '12px', padding: '32px', textAlign: 'center' }}>
          <div style={{ width: '56px', height: '56px', background: '#22c55e', color: '#ffffff', borderRadius: '50%', display: 'grid', placeItems: 'center', fontSize: '1.75rem', margin: '0 auto 16px auto' }}>
            ✓
          </div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#14532d', marginBottom: '8px' }}>
            Project Workspace Successfully Provisioned!
          </h2>
          <p style={{ color: '#15803d', fontSize: '0.95rem', marginBottom: '20px' }}>
            Workspace ID: <strong>{successResponse.projectId}</strong> | Version: {successResponse.version}
          </p>

          <div style={{ background: '#ffffff', padding: '16px', borderRadius: '8px', border: '1px solid #dcfce7', textAlign: 'left', display: 'inline-block', minWidth: '320px' }}>
            <p style={{ margin: '4px 0', fontSize: '0.85rem', color: '#334155' }}><strong>Name:</strong> {successResponse.name}</p>
            <p style={{ margin: '4px 0', fontSize: '0.85rem', color: '#334155' }}><strong>Default Locale:</strong> {successResponse.defaultLocale}</p>
            <p style={{ margin: '4px 0', fontSize: '0.85rem', color: '#334155' }}><strong>Status:</strong> {successResponse.status}</p>
          </div>

          <div style={{ marginTop: '28px' }}>
            <button
              onClick={() => { setSuccessResponse(null); setCurrentStep(1); }}
              style={{ background: '#16a34a', color: '#ffffff', border: 'none', padding: '10px 24px', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
            >
              Provision Another Project
            </button>
          </div>
        </div>
      ) : (
        /* Step Forms */
        <div style={{ background: '#ffffff', padding: '32px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)' }}>
          {/* STEP 1: Workspace Metadata */}
          {currentStep === 1 && (
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', marginBottom: '20px' }}>Step 1: Workspace Metadata</h3>

              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Project ID (PRJ-XXXXX)</label>
                <input
                  type="text"
                  value={formData.projectId}
                  onChange={(e) => setFormData({ ...formData, projectId: e.target.value.toUpperCase() })}
                  style={{ width: '100%', padding: '12px 16px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '1rem', fontFamily: 'monospace' }}
                  placeholder="PRJ-99201"
                />
                <small style={{ color: '#64748b', fontSize: '0.8rem' }}>Must match pattern ^PRJ-[A-Z0-9]{'{4,10}'}$</small>
              </div>

              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Workspace Display Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  style={{ width: '100%', padding: '12px 16px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '1rem' }}
                  placeholder="e.g. Aitken Spence Enterprise Workspace"
                />
              </div>
            </div>
          )}

          {/* STEP 2: Branding & Theme */}
          {currentStep === 2 && (
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', marginBottom: '20px' }}>Step 2: Branding & Theme Customization</h3>
              <ThemeCustomizer
                branding={formData.branding}
                onChange={(updatedBranding) => setFormData({ ...formData, branding: updatedBranding })}
              />
            </div>
          )}

          {/* STEP 3: Locales & Feature Flags */}
          {currentStep === 3 && (
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', marginBottom: '20px' }}>Step 3: Locales & Feature Flags</h3>

              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Default System Locale</label>
                <select
                  value={formData.defaultLocale}
                  onChange={(e) => setFormData({ ...formData, defaultLocale: e.target.value })}
                  style={{ width: '100%', padding: '12px 16px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '1rem' }}
                >
                  <option value="en-US">English (US) - en-US</option>
                  <option value="si-LK">Sinhala (Sri Lanka) - si-LK</option>
                  <option value="ta-LK">Tamil (Sri Lanka) - ta-LK</option>
                </select>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', fontWeight: 500, color: '#334155', marginBottom: '10px' }}>Module Feature Activation Flags</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  {Object.keys(formData.features).map((featureKey) => {
                    const key = featureKey as keyof typeof formData.features;
                    return (
                      <label key={featureKey} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '12px 16px', background: '#f8fafc', borderRadius: '8px', border: '1px solid #e2e8f0', cursor: 'pointer' }}>
                        <input
                          type="checkbox"
                          checked={formData.features[key]}
                          onChange={(e) => setFormData({
                            ...formData,
                            features: { ...formData.features, [key]: e.target.checked }
                          })}
                          style={{ width: '18px', height: '18px' }}
                        />
                        <span style={{ fontSize: '0.9rem', fontWeight: 500, color: '#334155' }}>
                          {featureKey.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase())}
                        </span>
                      </label>
                    );
                  })}
                </div>
              </div>
            </div>
          )}

          {/* STEP 4: Review & Deploy */}
          {currentStep === 4 && (
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', marginBottom: '20px' }}>Step 4: Review & Deploy Configuration</h3>

              <div style={{ background: '#f8fafc', padding: '20px', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '24px' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div>
                    <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>PROJECT ID</span>
                    <p style={{ fontWeight: 700, fontFamily: 'monospace', color: '#0f172a', margin: '2px 0 12px 0' }}>{formData.projectId}</p>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>NAME</span>
                    <p style={{ fontWeight: 600, color: '#0f172a', margin: '2px 0 12px 0' }}>{formData.name}</p>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>COMPANY</span>
                    <p style={{ fontWeight: 600, color: '#0f172a', margin: '2px 0 12px 0' }}>{formData.branding.companyName}</p>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>DEFAULT LOCALE</span>
                    <p style={{ fontWeight: 600, color: '#0f172a', margin: '2px 0 12px 0' }}>{formData.defaultLocale}</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Wizard Buttons */}
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '32px', paddingTop: '20px', borderTop: '1px solid #f1f5f9' }}>
            <button
              onClick={handlePrev}
              disabled={currentStep === 1}
              style={{
                background: currentStep === 1 ? '#f1f5f9' : '#ffffff',
                color: currentStep === 1 ? '#94a3b8' : '#334155',
                border: '1px solid #cbd5e1',
                padding: '10px 20px',
                borderRadius: '8px',
                fontWeight: 600,
                cursor: currentStep === 1 ? 'not-allowed' : 'pointer'
              }}
            >
              Previous
            </button>

            {currentStep < 4 ? (
              <button
                onClick={handleNext}
                style={{ background: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 24px', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
              >
                Next Step →
              </button>
            ) : (
              <button
                onClick={handleSubmit}
                disabled={loading}
                style={{ background: '#16a34a', color: '#ffffff', border: 'none', padding: '10px 28px', borderRadius: '8px', fontWeight: 700, cursor: loading ? 'wait' : 'pointer' }}
              >
                {loading ? 'Provisioning Workspace...' : 'Deploy Project Workspace'}
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
