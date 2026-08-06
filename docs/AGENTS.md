# AGENTS.md

# Talnova Enterprise Survey Platform (TESP)

This repository is implemented using an Agentic AI workflow.

The AI is NOT allowed to freely generate code.

Every implementation MUST follow the project architecture documents and feature specifications.

---

# Mission

Implement the platform feature-by-feature while maintaining a single consistent enterprise architecture.

The objective is:

- zero duplicate implementations
- zero architecture drift
- zero assumptions
- production-grade code only

---

# Source of Truth Priority

When multiple documents exist, ALWAYS follow this order.

Priority 1
Project Blueprint Documents

DOC-001
DOC-002
DOC-003
...
DOC-012

Priority 2

Current Feature Specification

FEAT-00X.md

Priority 3

Current Feature Backlog

FEAT-00X-BACKLOG.md

Priority 4

Already implemented code

Priority 5

Previous generated code

Never violate higher priority documents.

---

# Implementation Order

The backend MUST be implemented strictly in this order.

FEAT-001
FEAT-002
FEAT-003
FEAT-004
FEAT-005
FEAT-006
FEAT-007
FEAT-008
FEAT-009
FEAT-010

Skipping features is prohibited unless dependency documents explicitly allow it.

---

# Feature Completion Rule

A feature is NOT complete until ALL backlog tasks are complete.

Completion checklist:

✓ Domain models

✓ DTOs

✓ Validation

✓ Repository

✓ Services

✓ REST APIs

✓ Security

✓ Exception handling

✓ Events

✓ Redis

✓ Kafka

✓ Tests

✓ Documentation updates

Only after every backlog item is completed may the AI continue to the next feature.

---

# Existing Code Rule

Before generating code:

1. Inspect existing implementation.

2. Reuse existing components.

3. Extend existing code.

4. Never duplicate logic.

5. Never create another implementation if one already exists.

---

# Enterprise Coding Standards

Use:

Java 21

Spring Boot 3.4+

Spring Security

Spring Validation

Spring Data MongoDB

Redis

Kafka

Mongock

Lombok only where project allows

Constructor Injection only

Virtual Threads

RFC7807 Problem Details

Hexagonal Architecture

DDD-inspired package structure

SOLID

Clean Architecture

---

# Multi-Tenant Rule

Every database query MUST be tenant scoped.

projectId is mandatory.

No exceptions.

---

# Security Rule

Never expose endpoints without explicit authorization.

RBAC must follow DOC-010.

---

# MongoDB Rule

Repositories must never expose unrestricted findAll().

Soft delete must always be respected.

Tenant filters are mandatory.

Indexes must match the database documentation.

---

# Validation Rule

Implement every validation described inside

Validation Rules

Business Rules

Permission Rules

Acceptance Criteria

No validation may be skipped.

---

# Backlog Rule

The backlog document defines the engineering tasks.

Every task must be implemented exactly as described.

Story ordering should follow dependency graph.

---

# Testing Rule

Every completed backlog task requires:

Unit Tests

Integration Tests

Negative Tests

Permission Tests

Validation Tests

---

# AI Restrictions

The AI must NEVER

invent APIs

invent database fields

invent events

invent security roles

invent Mongo collections

invent architecture

invent DTOs unless explicitly needed

invent package names contradicting project structure

---

# If information is missing

Stop.

Report exactly what document or dependency is missing.

Never guess.

---

# Output Rule

After completing a feature produce:

Implemented backlog items

Remaining backlog items

Architecture decisions

Files created

Files modified

Tests added

Known TODOs

Only then continue.