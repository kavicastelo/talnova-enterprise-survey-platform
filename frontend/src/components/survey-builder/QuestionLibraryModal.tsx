import React, { useState, useMemo } from 'react';
import { QuestionLibraryTemplate, SurveyQuestion } from '../../types/survey';
import { useQuestionLibraryQuery } from '../../features/survey-builder/api/useSurveyQueries';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Select } from '../ui/Select';
import { Skeleton } from '../ui/Skeleton';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSelectTemplate: (question: SurveyQuestion) => void;
}

export const QuestionLibraryModal: React.FC<Props> = ({ isOpen, onClose, onSelectTemplate }) => {
  const { data: libraryItems = [], isLoading } = useQuestionLibraryQuery();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedGroup, setSelectedGroup] = useState<string>('ALL');
  const [activeLocale, setActiveLocale] = useState<string>('en-US');

  const filteredItems = useMemo(() => {
    return libraryItems.filter((item) => {
      const matchesSearch =
        !searchQuery ||
        item.libraryId?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.themeGroup?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (item.defaultPrompt?.['en-US'] || '').toLowerCase().includes(searchQuery.toLowerCase());

      const matchesGroup = selectedGroup === 'ALL' || item.themeGroup === selectedGroup;

      return matchesSearch && matchesGroup;
    });
  }, [libraryItems, searchQuery, selectedGroup]);

  const handleInsert = (item: QuestionLibraryTemplate) => {
    const question: SurveyQuestion = {
      questionId: `Q-LIB-${Date.now().toString().slice(-4)}`,
      type: item.questionType || 'LIKERT',
      groupId: item.themeGroup || 'GRP-GENERAL',
      prompt: item.defaultPrompt || { 'en-US': 'Default prompt' },
      isMandatory: true,
      questionOrder: 1,
      logicRules: [],
    };

    onSelectTemplate(question);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="lg">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
              Daash Verified Question Library Catalog
            </h3>
            <Badge variant="success">Methodology Validated</Badge>
          </div>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Browse standardized engagement questions with multi-language dictionaries for seamless questionnaire reuse.
          </p>
        </div>

        {/* Filter Bar */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 220px 160px', gap: '12px' }}>
          <Input
            placeholder="Search templates or keywords..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />

          <Select
            value={selectedGroup}
            onChange={(e) => setSelectedGroup(e.target.value)}
            options={[
              { value: 'ALL', label: 'All Question Groups' },
              { value: 'GRP-LEADERSHIP', label: 'Leadership & Strategy' },
              { value: 'GRP-CULTURE', label: 'Workplace Culture' },
              { value: 'GRP-WELLBEING', label: 'Wellbeing & Balance' },
              { value: 'GRP-MANAGEMENT', label: 'Managerial Support' },
              { value: 'GRP-CAREER', label: 'Career Growth' },
              { value: 'GRP-COMPENSATION', label: 'Rewards & Benefits' },
              { value: 'GRP-GENERAL', label: 'General Feedback' },
            ]}
          />

          <Select
            value={activeLocale}
            onChange={(e) => setActiveLocale(e.target.value)}
            options={[
              { value: 'en-US', label: 'English (en-US)' },
              { value: 'si-LK', label: 'Sinhala (si-LK)' },
              { value: 'ta-LK', label: 'Tamil (ta-LK)' },
            ]}
          />
        </div>

        {/* Template List */}
        {isLoading ? (
          <Skeleton height="240px" borderRadius="12px" />
        ) : filteredItems.length === 0 ? (
          <div style={{ padding: '32px', textAlign: 'center', background: '#f8fafc', borderRadius: '12px', border: '1px border-dashed #cbd5e1' }}>
            <p style={{ fontSize: '0.9rem', color: '#64748b', margin: 0 }}>
              No question templates matched your search criteria. Try clearing search filters.
            </p>
          </div>
        ) : (
          <div style={{ maxHeight: '340px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {filteredItems.map((item) => {
              const displayPrompt = item.defaultPrompt?.[activeLocale] || item.defaultPrompt?.['en-US'] || '';
              return (
                <div
                  key={item.libraryId}
                  style={{
                    background: '#ffffff',
                    border: '1px solid #e2e8f0',
                    borderRadius: '10px',
                    padding: '14px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    gap: '16px',
                  }}
                >
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Badge variant="neutral">{item.questionType}</Badge>
                      <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#2563eb', background: '#eff6ff', padding: '2px 8px', borderRadius: '4px' }}>
                        {item.themeGroup}
                      </span>
                    </div>

                    <div style={{ fontWeight: 600, color: '#0f172a', fontSize: '0.9rem' }}>
                      {displayPrompt}
                    </div>

                    <div style={{ display: 'flex', gap: '8px', fontSize: '0.725rem', color: '#64748b' }}>
                      <span>Locales: {Object.keys(item.defaultPrompt || {}).join(', ')}</span>
                      <span>•</span>
                      <span>Benchmark Weight: 1.0</span>
                    </div>
                  </div>

                  <Button variant="primary" size="sm" onClick={() => handleInsert(item)}>
                    + Insert Question
                  </Button>
                </div>
              );
            })}
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '4px' }}>
          <Button variant="secondary" onClick={onClose}>
            Close Catalog
          </Button>
        </div>
      </div>
    </Modal>
  );
};
