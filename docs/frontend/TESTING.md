# Testing & Quality Assurance Architecture

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** TEST-001  
**Status:** Production Standard

---

## 1. Test Suite Structure & Tools

- **Runner & Assertion Framework:** `Vitest` v2.0.2 with `@testing-library/react`.
- **Unit Tests:** `src/core/api/__tests__`, `src/core/auth/__tests__`, `src/utils/__tests__`.
- **Component Tests:** Primitive UI components, form validation, modal behavior.
- **Integration Tests:** Login flows, project context switching, survey builder canvas state, report generation.

---

## 2. Command Execution Standards

```bash
# Run unit and component tests
npm run test

# Typecheck TypeScript codebase
npm run build
```
