import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { LayoutDashboard, Network, Users, FileText, Send, BarChart2, Sparkles, AlertTriangle, Layers } from 'lucide-react';

export function App() {
  return (
    <Router>
      <div className="flex h-screen bg-slate-950 text-slate-100">
        {/* Sidebar */}
        <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col">
          <div className="p-6 border-b border-slate-800 flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-sky-500 to-indigo-600 flex items-center justify-center font-bold text-white shadow-lg">
              T
            </div>
            <div>
              <h1 className="font-bold text-sm text-slate-100 leading-tight">TALNOVA TESP</h1>
              <p className="text-xs text-sky-400 font-medium">Enterprise Analytics</p>
            </div>
          </div>

          <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
            <NavItem to="/" icon={<LayoutDashboard size={18} />} label="Overview" />
            <NavItem to="/organization" icon={<Network size={18} />} label="Org Hierarchy" />
            <NavItem to="/employees" icon={<Users size={18} />} label="Employee Roster" />
            <NavItem to="/surveys" icon={<FileText size={18} />} label="Survey Builder" />
            <NavItem to="/distributions" icon={<Send size={18} />} label="Distributions" />
            <NavItem to="/analytics" icon={<BarChart2 size={18} />} label="Heatmap Analytics" />
            <NavItem to="/ai-insights" icon={<Sparkles size={18} />} label="AI Sentiment" />
            <NavItem to="/action-plans" icon={<AlertTriangle size={18} />} label="Action Plans" />
            <NavItem to="/project-settings" icon={<Layers size={18} />} label="Project Config" />
          </nav>

          <div className="p-4 border-t border-slate-800 text-xs text-slate-500">
            Platform v1.0.0-SNAPSHOT
          </div>
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 flex flex-col overflow-hidden">
          <header className="h-16 border-b border-slate-800 bg-slate-900/50 backdrop-blur px-8 flex items-center justify-between">
            <div className="flex items-center gap-4">
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-sky-500/10 text-sky-400 border border-sky-500/20">
                Project: PRJ-DEFAULT-001
              </span>
            </div>
            <div className="flex items-center gap-3">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
              <span className="text-xs text-slate-400">Services Online (8080-8092)</span>
            </div>
          </header>

          <div className="flex-1 p-8 overflow-y-auto">
            <Routes>
              <Route path="/" element={<DashboardOverview />} />
              <Route path="/organization" element={<PlaceholderView title="Dynamic Organizational Hierarchy Engine (Materialized Path)" />} />
              <Route path="/employees" element={<PlaceholderView title="Employee Roster & CSFLE PII Vault" />} />
              <Route path="/surveys" element={<PlaceholderView title="Survey Builder & Question Logic AST" />} />
              <Route path="/distributions" element={<PlaceholderView title="Multi-Channel Distribution Engine" />} />
              <Route path="/analytics" element={<PlaceholderView title="2D Heatmap & Differential Privacy Aggregator (N < 5)" />} />
              <Route path="/ai-insights" element={<PlaceholderView title="Multi-Lingual NLP Sentiment & PII Scrubber" />} />
              <Route path="/action-plans" element={<PlaceholderView title="Remediation Kanban & Bi-Directional Jira Sync" />} />
              <Route path="/project-settings" element={<PlaceholderView title="Project Metadata & Configuration Metamodel" />} />
            </Routes>
          </div>
        </main>
      </div>
    </Router>
  );
}

function NavItem({ to, icon, label }: { to: string; icon: React.ReactNode; label: string }) {
  return (
    <Link
      to={to}
      className="flex items-center gap-3 px-3 py-2 text-sm rounded-lg text-slate-400 hover:text-slate-100 hover:bg-slate-800 transition-colors"
    >
      {icon}
      <span>{label}</span>
    </Link>
  );
}

function DashboardOverview() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-100">Talnova Platform Command Center</h2>
        <p className="text-slate-400 text-sm mt-1">Multi-tenant microservice topology & real-time people analytics</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <MetricCard title="Ingestion Throughput" value="5,000 req/s" subtitle="WebFlux Reactive SLA" color="text-sky-400" />
        <MetricCard title="Anonymity Threshold" value="N < 5" subtitle="Differential Privacy Guard" color="text-emerald-400" />
        <MetricCard title="Hierarchy Depth" value="Unlimited" subtitle="Materialized Path Tree" color="text-indigo-400" />
        <MetricCard title="Microservices" value="13 Services" subtitle="Spring Boot 3.3 / Java 21" color="text-purple-400" />
      </div>
    </div>
  );
}

function MetricCard({ title, value, subtitle, color }: { title: string; value: string; subtitle: string; color: string }) {
  return (
    <div className="p-6 rounded-xl bg-slate-900 border border-slate-800">
      <p className="text-xs font-medium text-slate-400">{title}</p>
      <p className={`text-2xl font-bold mt-2 ${color}`}>{value}</p>
      <p className="text-xs text-slate-500 mt-1">{subtitle}</p>
    </div>
  );
}

function PlaceholderView({ title }: { title: string }) {
  return (
    <div className="p-8 rounded-xl bg-slate-900 border border-slate-800">
      <h3 className="text-lg font-bold text-slate-200">{title}</h3>
      <p className="text-sm text-slate-400 mt-2">Module initial architecture ready. Subsystem initialized.</p>
    </div>
  );
}
