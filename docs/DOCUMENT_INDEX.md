# TESP Engineering Documentation Index

## Overview

This index serves as the master registry of all implementation-ready engineering documentation for the Talnova Enterprise Survey Platform (TESP). Every document listed here is a single source of truth for architects, software engineers, DevOps, QA, and AI agents.

---

## Master Document Registry

| Document ID | Title | Category | Status | Dependencies | Target Next / Impacted | Document Link |
|---|---|---|---|---|---|---|
| **DOC-001** | Project Blueprint & Platform Master Architecture | Blueprint | Approved | `project-overview.md`, `ARCHITECTURE_PRINCIPLES.md`, `PROJECT_GLOSSARY.md` | DOC-002, DOC-003, DOC-004, DOC-005 | [DOC-001-PROJECT-BLUEPRINT.md](DOC-001-PROJECT-BLUEPRINT.md) |
| **DOC-002** | Service Architecture Specification | Architecture | Approved | `DOC-001-PROJECT-BLUEPRINT.md` | DOC-003, DOC-004, DOC-005 | [DOC-002-SERVICE-ARCHITECTURE.md](DOC-002-SERVICE-ARCHITECTURE.md) |
| **DOC-003** | Database Philosophy & Metamodel Specification | Database | Approved | `DOC-001-PROJECT-BLUEPRINT.md`, `DOC-002-SERVICE-ARCHITECTURE.md` | DOC-004, DOC-005 | [DOC-003-DATABASE-PHILOSOPHY-AND-METAMODEL.md](DOC-003-DATABASE-PHILOSOPHY-AND-METAMODEL.md) |
| **DOC-004** | Organization & Hierarchy Domain Architecture Specification | Domain Spec | Approved | `DOC-001`, `DOC-002`, `DOC-003` | DOC-005 | [DOC-004-ORGANIZATION-AND-HIERARCHY-DOMAIN.md](DOC-004-ORGANIZATION-AND-HIERARCHY-DOMAIN.md) |
| **DOC-005** | Survey Engine Architecture Specification | Backend | Approved | `DOC-001`, `DOC-002`, `DOC-003`, `DOC-004` | DOC-006 | [DOC-005-SURVEY-ENGINE-ARCHITECTURE.md](DOC-005-SURVEY-ENGINE-ARCHITECTURE.md) |
| **DOC-006** | Analytics Engine Architecture Specification | Analytics | Approved | `DOC-001` through `DOC-005` | DOC-007 | [DOC-006-ANALYTICS-ENGINE-ARCHITECTURE.md](DOC-006-ANALYTICS-ENGINE-ARCHITECTURE.md) |
| **DOC-007** | AI Analytics & NLP Architecture Specification | AI/NLP | Approved | `DOC-001` through `DOC-006` | DOC-008 | [DOC-007-AI-ANALYTICS-AND-NLP-SPECIFICATION.md](DOC-007-AI-ANALYTICS-AND-NLP-SPECIFICATION.md) |
| **DOC-008** | Reporting Engine Architecture Specification | Reporting | Approved | `DOC-001` through `DOC-007` | DOC-009 | [DOC-008-REPORTING-ENGINE-ARCHITECTURE.md](DOC-008-REPORTING-ENGINE-ARCHITECTURE.md) |
| **DOC-009** | Action Planning Architecture Specification | Workflow | Approved | `DOC-001` through `DOC-008` | DOC-010 | [DOC-009-ACTION-PLANNING-ARCHITECTURE.md](DOC-009-ACTION-PLANNING-ARCHITECTURE.md) |
| **DOC-010** | User Roles, RBAC & Security Specification | Security | Approved | `DOC-001` through `DOC-009` | DOC-011 | [DOC-010-USER-ROLES-RBAC-AND-SECURITY.md](DOC-010-USER-ROLES-RBAC-AND-SECURITY.md) |
| **DOC-011** | Deployment & Cloud Infrastructure Specification | DevOps | Approved | `DOC-001` through `DOC-010` | DOC-012 | [DOC-011-DEPLOYMENT-AND-CLOUD-INFRASTRUCTURE.md](DOC-011-DEPLOYMENT-AND-CLOUD-INFRASTRUCTURE.md) |
| **DOC-012** | Quality Assurance, Testing & Validation Specification | Testing / QA | Approved | `DOC-001` through `DOC-011` | Complete | [DOC-012-QUALITY-ASSURANCE-AND-TESTING.md](DOC-012-QUALITY-ASSURANCE-AND-TESTING.md) |
