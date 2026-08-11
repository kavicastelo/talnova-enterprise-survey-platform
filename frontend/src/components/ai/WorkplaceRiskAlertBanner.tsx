import React from 'react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';

export interface RiskAlertItem {
  id: string;
  category: 'SAFETY' | 'HARASSMENT' | 'BURNOUT' | 'COMPLIANCE';
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  keyword: string;
  sanitizedSnippet: string;
  detectedAt?: string;
}

interface WorkplaceRiskAlertBannerProps {
  alerts: RiskAlertItem[];
  onInvestigate?: (alert: RiskAlertItem) => void;
}

export const WorkplaceRiskAlertBanner: React.FC<WorkplaceRiskAlertBannerProps> = ({
  alerts,
  onInvestigate,
}) => {
  if (!alerts || alerts.length === 0) return null;

  const criticalCount = alerts.filter((a) => a.severity === 'CRITICAL').length;

  return (
    <Card variant="bordered" padding="20px" className="border-rose-300 bg-rose-50/50">
      <div className="space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-rose-200 pb-3">
          <div className="flex items-center gap-2">
            <span className="text-xl">🚨</span>
            <div>
              <h3 className="text-base font-bold text-rose-950">
                Workplace Risk &amp; Compliance Alerts (FR-AI-004)
              </h3>
              <p className="text-xs text-rose-800">
                Automated risk engine detected {alerts.length} flagged qualitative comment(s) (
                {criticalCount} Critical).
              </p>
            </div>
          </div>
          <Badge variant="danger">{criticalCount} Critical Event(s)</Badge>
        </div>

        <div className="space-y-3">
          {alerts.map((alert) => (
            <div
              key={alert.id}
              className={`p-3.5 rounded-xl border flex flex-col md:flex-row md:items-center justify-between gap-3 ${
                alert.severity === 'CRITICAL'
                  ? 'bg-rose-100/80 border-rose-300'
                  : alert.severity === 'HIGH'
                  ? 'bg-amber-100/80 border-amber-300'
                  : 'bg-white border-slate-200'
              }`}
            >
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <Badge variant={alert.severity === 'CRITICAL' ? 'danger' : 'warning'}>
                    {alert.severity} RISK
                  </Badge>
                  <span className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                    Category: {alert.category}
                  </span>
                  <span className="text-xs text-slate-500 font-mono">
                    Keyword: "{alert.keyword}"
                  </span>
                </div>
                <p className="text-xs font-medium text-slate-900 italic">
                  "{alert.sanitizedSnippet}"
                </p>
              </div>

              {onInvestigate && (
                <Button
                  size="sm"
                  variant={alert.severity === 'CRITICAL' ? 'primary' : 'outline'}
                  onClick={() => onInvestigate(alert)}
                >
                  Create Remediation Plan →
                </Button>
              )}
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
};
