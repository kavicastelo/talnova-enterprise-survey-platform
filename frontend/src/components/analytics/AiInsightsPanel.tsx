import React from 'react';
import { AnomalyReport, KeyDriver } from '../../types/analytics';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Alert } from '../ui/Alert';

interface AiInsightsPanelProps {
  anomalies: AnomalyReport[];
  keyDrivers: KeyDriver[];
}

export const AiInsightsPanel: React.FC<AiInsightsPanelProps> = ({ anomalies, keyDrivers }) => {
  return (
    <div className="space-y-6">
      {/* Key Drivers Regression Analysis */}
      <Card variant="bordered" padding="24px">
        <div className="space-y-4">
          <div>
            <h3 className="text-base font-bold text-slate-900">
              Key Driver Regression Analysis (Multiple Linear Regression)
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Identifies which Question Group Themes have the highest statistical correlation with overall eNPS.
            </p>
          </div>

          {keyDrivers.length === 0 ? (
            <div className="p-6 text-center bg-slate-50 border border-dashed border-slate-200 rounded-lg text-slate-500 text-xs">
              Insufficient response variance to generate key driver regression model.
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {keyDrivers.map((driver) => (
                <div
                  key={driver.themeGroupId}
                  className="bg-white border border-slate-200 p-4 rounded-xl shadow-sm space-y-2 hover:border-indigo-300 transition"
                >
                  <div className="flex justify-between items-start">
                    <span className="font-bold text-xs text-slate-900">{driver.themeName}</span>
                    <Badge
                      variant={
                        driver.impactCategory === 'HIGH_IMPACT'
                          ? 'success'
                          : driver.impactCategory === 'MEDIUM_IMPACT'
                          ? 'warning'
                          : 'neutral'
                      }
                    >
                      {driver.impactCategory.replace('_', ' ')}
                    </Badge>
                  </div>
                  <div className="text-2xl font-extrabold text-indigo-700">
                    {(driver.importanceWeight * 100).toFixed(0)}%
                    <span className="text-xs font-normal text-slate-500 ml-1">eNPS Variance Impact</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                    <div
                      className="bg-indigo-600 h-1.5 rounded-full"
                      style={{ width: `${Math.min(100, driver.importanceWeight * 100)}%` }}
                    />
                  </div>
                  <div className="text-[11px] text-slate-500">
                    Correlation Score: <span className="font-mono text-slate-700">{driver.correlationScore.toFixed(2)}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </Card>

      {/* Automated Score Drop Anomaly Alerts (> 10% Drop) */}
      <Card variant="bordered" padding="24px">
        <div className="space-y-4">
          <div>
            <h3 className="text-base font-bold text-slate-900">
              Automated Score Drop Anomaly Alerts (&gt; 10% Drop)
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              AI background worker scans baseline deltas across organizational nodes, flagging at-risk departments.
            </p>
          </div>

          {anomalies.length === 0 ? (
            <Alert type="success" title="No Score Drop Anomalies Detected">
              All department nodes are performing within normal baseline tolerance (&lt; 10% score variance).
            </Alert>
          ) : (
            <div className="space-y-3">
              {anomalies.map((anomaly, idx) => (
                <div
                  key={idx}
                  className={`p-4 rounded-xl border flex flex-col md:flex-row md:items-center justify-between gap-4 ${
                    anomaly.severity === 'CRITICAL'
                      ? 'bg-rose-50/60 border-rose-200'
                      : 'bg-amber-50/60 border-amber-200'
                  }`}
                >
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-xs text-slate-900">{anomaly.nodeName}</span>
                      <span className="font-mono text-[10px] text-slate-500">({anomaly.nodeId})</span>
                      <Badge variant={anomaly.severity === 'CRITICAL' ? 'danger' : 'warning'}>
                        {anomaly.severity} DROP
                      </Badge>
                    </div>
                    <p className="text-xs text-slate-700">{anomaly.insightMessage}</p>
                  </div>

                  <div className="text-right whitespace-nowrap">
                    <div className="text-lg font-extrabold text-rose-700">
                      -{anomaly.scoreDropDelta.toFixed(1)}%
                    </div>
                    <div className="text-[11px] text-slate-500">
                      Baseline: {anomaly.baselineScore.toFixed(1)}% → Current: {anomaly.currentScore.toFixed(1)}%
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </Card>
    </div>
  );
};
