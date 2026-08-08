import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { LocaleManagementPanel } from '../../../components/project-config/LocaleManagementPanel';
import { useTenant } from '../../../context/TenantContext';

export const LocaleManagementPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [supportedLocales, setSupportedLocales] = useState<string[]>(
    activeProject?.supportedLocales || ['en-US', 'si-LK', 'ta-LK']
  );
  const [defaultLocale, setDefaultLocale] = useState<string>(activeProject?.defaultLocale || 'en-US');

  return (
    <div>
      <PageHeader
        title="Locales & Multilingual Management"
        subtitle={`Configure supported languages and primary survey translation pack for ${activeProject?.projectId}`}
      />
      <LocaleManagementPanel
        supportedLocales={supportedLocales}
        defaultLocale={defaultLocale}
        onChange={(locales, def) => {
          setSupportedLocales(locales);
          setDefaultLocale(def);
        }}
      />
    </div>
  );
};
