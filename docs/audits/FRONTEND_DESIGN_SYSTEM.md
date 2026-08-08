# TESP Enterprise Design System & Component Guidelines

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Styling Framework:** TailwindCSS v3.4.4 + Lucide Icons  
**Typography:** Inter / System UI Font Stack  
**Accessibility Level:** WCAG 2.1 AA Compliance Certified

---

## 1. Design Tokens & Color Palette

### 1.1 Brand & Surface Palette
- **Primary Brand:** Deep Sapphire `#1E3A8A` (`bg-blue-900`) / Royal Blue `#3B82F6` (`bg-blue-500`)
- **Secondary Brand:** Slate Blue `#475569` (`bg-slate-600`) / Indigo `#6366F1` (`bg-indigo-500`)
- **Neutral Dark (Header/Sidebar):** Slate Dark `#0F172A` (`bg-slate-900`) / `#1E293B` (`bg-slate-800`)
- **Neutral Light (App Background):** `#F8FAFC` (`bg-slate-50`) / Surface Cards `#FFFFFF` (`bg-white`)
- **Text Primary:** `#0F172A` (`text-slate-900`)
- **Text Secondary:** `#64748B` (`text-slate-500`)
- **Text Muted:** `#94A3B8` (`text-slate-400`)

### 1.2 Status & Alert Palette
- **Success:** Emerald `#10B981` (`bg-emerald-500`, `text-emerald-700`, `bg-emerald-50`)
- **Warning:** Amber `#F59E0B` (`bg-amber-500`, `text-amber-700`, `bg-amber-50`)
- **Danger / Violation:** Rose `#EF4444` (`bg-rose-500`, `text-rose-700`, `bg-rose-50`)
- **Info / Neutral:** Sky Blue `#0284C7` (`bg-sky-500`, `text-sky-700`, `bg-sky-50`)

---

## 2. Standard Primitive Components (`src/components/ui/`)

To eliminate inline style sprawl and duplicate DOM elements, the following standardized primitives will be created in `src/components/ui/`:

### 2.1 Primitive Inventory

| Component Name | File Path | Variants | Key Props |
| :--- | :--- | :--- | :--- |
| `<Button>` | `src/components/ui/Button.tsx` | `primary`, `secondary`, `outline`, `danger`, `ghost` | `size`, `isLoading`, `icon`, `disabled` |
| `<Input>` | `src/components/ui/Input.tsx` | `default`, `error`, `search` | `label`, `error`, `icon`, `helperText` |
| `<Select>` | `src/components/ui/Select.tsx` | `default`, `error` | `label`, `options`, `value`, `onChange` |
| `<Badge>` | `src/components/ui/Badge.tsx` | `success`, `warning`, `danger`, `info`, `neutral` | `dot`, `children` |
| `<Card>` | `src/components/ui/Card.tsx` | `default`, `bordered`, `hoverable` | `title`, `subtitle`, `action`, `children` |
| `<Modal>` | `src/components/ui/Modal.tsx` | `sm`, `md`, `lg`, `xl`, `full` | `isOpen`, `onClose`, `title`, `children`, `footer` |
| `<Tabs>` | `src/components/ui/Tabs.tsx` | `underline`, `pills` | `tabs`, `activeTab`, `onChange` |
| `<DataTable>` | `src/components/ui/DataTable.tsx` | Standard data grid | `columns`, `data`, `pagination`, `isLoading` |
| `<Toast>` | `src/components/ui/Toast.tsx` | Toast notification popup | `type`, `message`, `onClose` |
| `<EmptyState>`| `src/components/ui/EmptyState.tsx` | Illustrative fallback | `icon`, `title`, `description`, `action` |
| `<Spinner>` | `src/components/ui/Spinner.tsx` | Loading spinner indicator | `size`, `color` |

---

## 3. Visual Layout Guidelines

1. **Card Containers:** Rounded-xl border (`border border-slate-200 shadow-sm bg-white rounded-xl`).
2. **Interactive States:** Hover transitions (`transition-all duration-150 ease-in-out hover:shadow-md`).
3. **Typography Scale:**
   - Page Titles: `text-2xl font-extrabold text-slate-900 tracking-tight`
   - Section Headers: `text-lg font-bold text-slate-800`
   - Body Text: `text-sm font-normal text-slate-600`
   - Caption / Metadata: `text-xs font-medium text-slate-400`
4. **Form Field Alignment:** Labels stacked above inputs with `text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5`.
