import React from 'react';
import { Compass, Building2, BarChart3, FileText, TrendingUp, AlertTriangle } from 'lucide-react';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';

export const ConsultantDashboardPage: React.FC = () => {
  const assignedProjects = [
    { id: 'PRJ-99201', name: 'Aitken Spence Enterprise Portal', status: 'ACTIVE', responseRate: '78.4%', enps: '+42' },
    { id: 'PRJ-88102', name: 'Talnova Global Workforce', status: 'ACTIVE', responseRate: '82.1%', enps: '+51' },
    { id: 'PRJ-77303', name: 'Asia Telecom Operations', status: 'IN_REVIEW', responseRate: '64.5%', enps: '+28' },
  ];

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Compass className="w-7 h-7 text-indigo-600" />
            Daash Global Consultant Workspace
          </h1>
          <p className="text-xs text-slate-500 mt-1">Multi-project advisory dashboard, eNPS scorecards, AI sentiment analysis, and action planning.</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" size="sm" leftIcon={<FileText className="w-4 h-4" />}>
            Generate Executive Brief
          </Button>
        </div>
      </div>

      {/* Summary Scorecards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="bg-white border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Assigned Projects</span>
            <Building2 className="w-5 h-5 text-indigo-600" />
          </div>
          <div className="mt-3">
            <span className="text-3xl font-extrabold text-slate-900">3</span>
            <p className="text-xs text-slate-500 mt-1">Active client portfolios</p>
          </div>
        </Card>

        <Card className="bg-white border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg Response Rate</span>
            <TrendingUp className="w-5 h-5 text-emerald-600" />
          </div>
          <div className="mt-3">
            <span className="text-3xl font-extrabold text-emerald-600">75.0%</span>
            <p className="text-xs text-emerald-700 mt-1">+4.2% vs industry benchmark</p>
          </div>
        </Card>

        <Card className="bg-white border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Portfolio eNPS</span>
            <BarChart3 className="w-5 h-5 text-sky-600" />
          </div>
          <div className="mt-3">
            <span className="text-3xl font-extrabold text-sky-600">+40.3</span>
            <p className="text-xs text-slate-500 mt-1">Strong promoter ratio</p>
          </div>
        </Card>

        <Card className="bg-white border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">AI Risk Indicators</span>
            <AlertTriangle className="w-5 h-5 text-amber-600" />
          </div>
          <div className="mt-3">
            <span className="text-3xl font-extrabold text-amber-600">2 Alerts</span>
            <p className="text-xs text-amber-700 mt-1">Engineering attrition risk</p>
          </div>
        </Card>
      </div>

      {/* Assigned Projects Table */}
      <Card title="Assigned Client Portfolios" subtitle="Real-time performance metrics across assigned client projects">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-slate-200 text-slate-500 font-mono uppercase tracking-wider">
                <th className="pb-3 px-3">Project ID</th>
                <th className="pb-3 px-3">Client Name</th>
                <th className="pb-3 px-3">Status</th>
                <th className="pb-3 px-3">Response Rate</th>
                <th className="pb-3 px-3">eNPS Score</th>
                <th className="pb-3 px-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-800">
              {assignedProjects.map((p) => (
                <tr key={p.id} className="hover:bg-slate-50 transition-colors">
                  <td className="py-3 px-3 font-mono text-indigo-600 font-semibold">{p.id}</td>
                  <td className="py-3 px-3 font-medium text-slate-900">{p.name}</td>
                  <td className="py-3 px-3">
                    <Badge variant={p.status === 'ACTIVE' ? 'success' : 'warning'}>{p.status}</Badge>
                  </td>
                  <td className="py-3 px-3 font-semibold text-emerald-600">{p.responseRate}</td>
                  <td className="py-3 px-3 font-semibold text-sky-600">{p.enps}</td>
                  <td className="py-3 px-3 text-right">
                    <Button variant="ghost" size="sm">
                      Open Insights
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};
