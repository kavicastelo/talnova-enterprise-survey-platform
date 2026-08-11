# Design System & UI Primitive Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** UI-001  
**Status:** Production Standard

---

## 1. Design Token Palette & Typography

- **Primary Brand:** Slate / Indigo enterprise palette (`bg-slate-900`, `text-slate-100`, `bg-indigo-600`, `hover:bg-indigo-700`).
- **Accent & Status Colors:**
  - Success / Promoters: `emerald-600` / `emerald-100`
  - Warning / Passives: `amber-600` / `amber-100`
  - Danger / Detractors / High Risk: `rose-600` / `rose-100`
  - Neutral / Info: `sky-600` / `sky-100`
- **Typography:** Inter / System UI stack with strong hierarchy (`text-2xl font-bold`, `text-sm font-medium`, `text-xs text-slate-500`).

---

## 2. Centralized Primitive UI Components (`src/components/ui`)

Every UI element must utilize standardized primitive components instead of inline styled raw HTML:
- `<Button variant="primary|secondary|danger|ghost" size="sm|md|lg" isLoading={...} />`
- `<Input label="..." error="..." leftIcon={...} />`
- `<Select label="..." options={[...]} />`
- `<Modal isOpen={...} onClose={...} title="...">`
- `<Card title="..." extra={...} className="...">`
- `<Badge variant="success|warning|danger|info|neutral" />`
- `<Tabs items={[...]} activeKey={...} onChange={...} />`
- `<EmptyState title="..." description="..." action={...} />`
- `<LoadingOverlay message="..." />`
- `<ErrorAlert message="..." onRetry={...} />`
