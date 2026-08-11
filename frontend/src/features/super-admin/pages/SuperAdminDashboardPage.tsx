import React from 'react';
import { Building2, Users, FileQuestion, Activity, ShieldCheck, Cpu } from 'lucide-react';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';

export const SuperAdminDashboardPage: React.FC = () => {
  const systemMetrics = [
    { title: 'Total Tenant Projects', value: '14', change: '+2 this month', icon: <Building2 className="w-5 h-5 text-indigo-400" /> },
    { title: 'Active Consultants', value: '8', change: 'Daash Global', icon: <Users className="w-5 h-5 text-sky-400" /> },
    { title: 'Published Surveys', value: '142', change: 'Across all tenants', icon: <FileQuestion className="w-5 h-5 text-emerald-400" /> },
    { title: 'Total Ingested Responses', value: '48,920', change: '+12.4% vs prev week', icon: <Activity className="w-5 h-5 text-rose-400" /> },
  ];

  const microservices = [
    { name: 'api-gateway', port: 8080, status: 'UP', latency: '12ms' },
    { name: 'project-config-service', port: 8081, status: 'UP', latency: '18ms' },
    { name: 'organization-service', port: 8082, status: 'UP', latency: '15ms' },
    { name: 'employee-service', port: 8083, status: 'UP', latency: '22ms' },
    { name: 'survey-builder-service', port: 8084, status: 'UP', latency: '14ms' },
    { name: 'survey-distribution-service', port: 8085, status: 'UP', latency: '25ms' },
    { name: 'response-ingestion-service', port: 8086, status: 'UP', latency: '19ms' },
    { name: 'analytics-engine-service', port: 8087, status: 'UP', latency: '31ms' },
    { name: 'ai-analytics-service', port: 8088, status: 'UP', latency: '45ms' },
    { name: 'reporting-service', port: 8089, status: 'UP', latency: '28ms' },
    { name: 'action-planning-service', port: 8090, status: 'UP', latency: '17ms' },
    { name: 'notification-service', port: 8091, status: 'UP', latency: '16ms' },
    { name: 'audit-service', port: 8092, status: 'UP', latency: '11ms' },
  ];

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <ShieldCheck className="w-7 h-7 text-rose-600" />
            Super Admin Console
          </h1>
          <p className="text-xs text-slate-500 mt-1">Platform-wide tenant administration, system health, and microservice monitoring.</p>
        </div>
        <Button variant="danger" size="sm">
          Run System Diagnostics
        </Button>
      </div>

      {/* Scorecard Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {systemMetrics.map((m) => (
          <Card key={m.title} className="bg-white border-slate-200">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">{m.title}</span>
              <div className="p-2 bg-slate-100 rounded-lg">{m.icon}</div>
            </div>
            <div className="mt-3">
              <span className="text-3xl font-extrabold text-slate-900">{m.value}</span>
              <p className="text-xs text-slate-500 mt-1">{m.change}</p>
            </div>
          </Card>
        ))}
      </div>

      {/* Microservice Reactor Status */}
      <Card title="Backend Microservice Reactor Status (13 Services)" extra={<Badge variant="success">All Systems Operational</Badge>}>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
          {microservices.map((s) => (
            <div key={s.name} className="p-3.5 bg-slate-50 border border-slate-200 rounded-xl flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Cpu className="w-5 h-5 text-indigo-600" />
                <div>
                  <p className="text-xs font-semibold text-slate-900">{s.name}</p>
                  <p className="text-[10px] text-slate-500 font-mono">Port :{s.port}</p>
                </div>
              </div>
              <div className="text-right">
                <Badge variant="success" size="sm">{s.status}</Badge>
                <p className="text-[10px] text-slate-500 font-mono mt-1">{s.latency}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};
