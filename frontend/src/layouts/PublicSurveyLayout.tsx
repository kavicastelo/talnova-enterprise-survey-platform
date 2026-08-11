import React, { useEffect } from 'react';
import { usePublicThemeQuery } from '../features/project-config/api/useProjectConfigQueries';
import { Skeleton } from '../components/ui/Skeleton';

interface PublicSurveyLayoutProps {
  projectId: string;
  children: React.ReactNode;
}

export const PublicSurveyLayout: React.FC<PublicSurveyLayoutProps> = ({ projectId, children }) => {
  const { data: theme, isLoading } = usePublicThemeQuery(projectId);

  useEffect(() => {
    if (theme) {
      document.documentElement.style.setProperty('--theme-primary', theme.primaryColor || '#1E3A8A');
      document.documentElement.style.setProperty('--theme-secondary', theme.secondaryColor || '#3B82F6');
    }
  }, [theme]);

  if (isLoading) {
    return (
      <div style={{ minHeight: '100vh', background: '#f8fafc', padding: '40px 20px' }}>
        <div style={{ maxWidth: '720px', margin: '0 auto' }}>
          <div style={{ marginBottom: '20px' }}>
            <Skeleton height="60px" borderRadius="12px" />
          </div>
          <Skeleton height="400px" borderRadius="12px" />
        </div>
      </div>
    );
  }

  const primaryColor = theme?.primaryColor || '#1E3A8A';
  const secondaryColor = theme?.secondaryColor || '#3B82F6';
  const companyName = theme?.companyName || 'Enterprise Survey';

  const BCP47_SHORT_NAMES: Record<string, string> = {
    'en-US': 'English (US)',
    'si-LK': 'Sinhala',
    'ta-LK': 'Tamil',
    'fr-FR': 'French',
    'de-DE': 'German',
    'ja-JP': 'Japanese',
    'es-ES': 'Spanish',
  };

  const displayLanguage = BCP47_SHORT_NAMES[theme?.defaultLocale || 'en-US'] || theme?.defaultLocale || 'en-US';

  return (
    <div style={{ minHeight: '100vh', background: '#f8fafc', display: 'flex', flexDirection: 'column' }}>
      {/* Unauthenticated White-Label Header */}
      <header
        style={{
          background: primaryColor,
          color: '#ffffff',
          padding: '16px 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '12px',
          boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {theme?.logoUrl ? (
            <img src={theme.logoUrl} alt={companyName} style={{ height: '32px', objectFit: 'contain' }} onError={(e) => (e.currentTarget.style.display = 'none')} />
          ) : (
            <div style={{ width: '32px', height: '32px', borderRadius: '6px', background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800 }}>
              {companyName.charAt(0)}
            </div>
          )}
          <span style={{ fontWeight: 800, fontSize: '1.2rem', letterSpacing: '-0.02em', textShadow: '0 1px 2px rgba(0,0,0,0.2)' }}>
            {companyName}
          </span>
        </div>

        <div
          style={{
            fontSize: '0.75rem',
            fontWeight: 700,
            background: secondaryColor,
            padding: '4px 12px',
            borderRadius: '12px',
            color: '#ffffff',
            textTransform: 'uppercase',
            letterSpacing: '0.05em',
            boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
          }}
        >
          🌐 {displayLanguage}
        </div>
      </header>

      {/* Main Survey Taker Container */}
      <main style={{ flex: 1, maxWidth: '800px', width: '100%', margin: '32px auto', padding: '0 20px' }}>
        {children}
      </main>

      {/* Footer */}
      <footer style={{ borderTop: '1px solid #e2e8f0', background: '#ffffff', padding: '16px 32px', textAlign: 'center', fontSize: '0.8rem', color: '#64748b' }}>
        <span>Powered by Talnova Enterprise Survey Platform • Privacy & Security Guaranteed</span>
      </footer>
    </div>
  );
};
