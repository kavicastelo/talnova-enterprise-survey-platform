import React, { useState } from 'react';
import { ProjectSetupWizard } from './components/project-config/ProjectSetupWizard';
import { ThemeCustomizer } from './components/project-config/ThemeCustomizer';
import { FeatureFlagMatrix } from './components/project-config/FeatureFlagMatrix';
import { LocaleManagementPanel } from './components/project-config/LocaleManagementPanel';
import { Branding, FeatureFlags, ProjectResponse } from './types/projectConfig';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'wizard' | 'customizer' | 'features' | 'locales'>('wizard');

  const [demoBranding, setDemoBranding] = useState<Branding>({
    companyName: 'Aitken Spence PLC',
    logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png',
    primaryColor: '#1E3A8A',
    secondaryColor: '#3B82F6'
  });

  const [demoFeatures, setDemoFeatures] = useState<FeatureFlags>({
    aiAnalyticsEnabled: true,
    actionPlanningEnabled: true,
    kioskModeEnabled: false,
    smsDistributionEnabled: true
  });

  const [supportedLocales, setSupportedLocales] = useState<string[]>(['en-US', 'si-LK', 'ta-LK']);
  const [defaultLocale, setDefaultLocale] = useState<string>('en-US');

  return (
    <div style={{ minHeight: '100vh', background: '#f8fafc', fontFamily: "'Inter', system-ui, sans-serif" }}>
      {/* Top Application Navigation Bar */}
      <header style={{ background: '#0f172a', color: '#ffffff', padding: '16px 32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{ width: '32px', height: '32px', borderRadius: '8px', background: 'linear-gradient(135deg, #3b82f6, #1d4ed8)', display: 'grid', placeItems: 'center', fontWeight: 800, color: '#ffffff' }}>
            T
          </div>
          <span style={{ fontWeight: 800, fontSize: '1.2rem', letterSpacing: '-0.025em' }}>Talnova Enterprise Survey Platform</span>
        </div>

        <nav style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={() => setActiveTab('wizard')}
            style={{
              background: activeTab === 'wizard' ? '#334155' : 'transparent',
              color: '#ffffff',
              border: 'none',
              padding: '8px 16px',
              borderRadius: '6px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
          >
            Provisioning Wizard
          </button>
          <button
            onClick={() => setActiveTab('customizer')}
            style={{
              background: activeTab === 'customizer' ? '#334155' : 'transparent',
              color: '#ffffff',
              border: 'none',
              padding: '8px 16px',
              borderRadius: '6px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
          >
            Theme Customizer
          </button>
          <button
            onClick={() => setActiveTab('features')}
            style={{
              background: activeTab === 'features' ? '#334155' : 'transparent',
              color: '#ffffff',
              border: 'none',
              padding: '8px 16px',
              borderRadius: '6px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
          >
            Feature Flags
          </button>
          <button
            onClick={() => setActiveTab('locales')}
            style={{
              background: activeTab === 'locales' ? '#334155' : 'transparent',
              color: '#ffffff',
              border: 'none',
              padding: '8px 16px',
              borderRadius: '6px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
          >
            Locales & Multilingual
          </button>
        </nav>
      </header>

      {/* Main Content Area */}
      <main style={{ padding: '32px 16px' }}>
        {activeTab === 'wizard' && (
          <ProjectSetupWizard onSuccess={(proj: ProjectResponse) => console.log('Provisioned:', proj)} />
        )}

        {activeTab === 'customizer' && (
          <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a', marginBottom: '8px' }}>
              White-Label Brand & Accessibility Studio
            </h2>
            <p style={{ color: '#64748b', marginBottom: '24px' }}>
              Test primary and secondary theme color variables with real-time WCAG 2.1 contrast accessibility feedback.
            </p>
            <ThemeCustomizer branding={demoBranding} onChange={(b: Branding) => setDemoBranding(b)} />
          </div>
        )}

        {activeTab === 'features' && (
          <div style={{ maxWidth: '900px', margin: '0 auto' }}>
            <FeatureFlagMatrix
              projectId="PRJ-99201"
              initialFeatures={demoFeatures}
              onUpdate={(updated) => setDemoFeatures(updated)}
            />
          </div>
        )}

        {activeTab === 'locales' && (
          <div style={{ maxWidth: '900px', margin: '0 auto' }}>
            <LocaleManagementPanel
              supportedLocales={supportedLocales}
              defaultLocale={defaultLocale}
              onChange={(locs, def) => {
                setSupportedLocales(locs);
                setDefaultLocale(def);
              }}
            />
          </div>
        )}
      </main>
    </div>
  );
};

export default App;
