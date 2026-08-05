# Talnova Enterprise Survey Platform (TESP)

## Comprehensive Project Overview & System Architecture

**Version:** 1.0

**Status:** Planning

**Prepared by:** Talnova (Pvt) Ltd.

**Partner:** Daash Global

---

# Table of Contents

```
1. Executive Summary

2. Vision & Objectives

3. Platform Philosophy

4. Target Customers

5. Business Model

6. Project-Based Architecture

7. High-Level System Architecture

8. Organization Management

9. Employee Management

10. Survey Engine

11. Survey Distribution Engine

12. Authentication & Anonymous Response System

13. Survey Response Engine

14. Analytics Engine

15. Reporting Engine

16. Dashboard Engine

17. Action Planning Module

18. AI Analytics Module

19. Administration Module

20. User Roles & Permissions

21. Notification System

22. Multi-language Framework

23. Branding & White-labeling

24. HRIS Integration

25. External Integrations

26. Security Architecture

27. Project Configuration Framework

28. Database Design Principles

29. Technical Architecture

30. Scalability Strategy

31. Deployment Models

32. Future Roadmap
```

---

# 1. Executive Summary

## Overview

Talnova Enterprise Survey Platform (TESP) is a configurable enterprise-grade survey, engagement, analytics, and organizational insights platform developed by Talnova in partnership with Daash Global.

Unlike traditional SaaS survey tools, TESP is designed as a configurable enterprise platform where every client implementation is treated as an independent project. Each project can define its own organizational hierarchy, employee metadata, survey workflows, reporting structures, distribution channels, branding, integrations, and analytics without requiring changes to the platform source code.

The platform is intended to support organizations ranging from a few hundred employees to global enterprises with tens of thousands of employees operating across multiple countries, subsidiaries, business units, and organizational structures.

---

## Core Philosophy

TESP is not a survey application.

TESP is a Survey Engine Platform.

Every enterprise is different.

Therefore:

* organization structures differ
* hierarchy differs
* HR systems differ
* reports differ
* KPIs differ
* authentication differs
* distribution differs

The platform must adapt to organizations rather than requiring organizations to adapt to the software.

---

# 2. Vision

Create the most configurable enterprise employee insight platform capable of supporting any organizational structure with minimal code customization.

---

# 3. Primary Objectives

## Business Objectives

• Reduce implementation time for enterprise survey projects

• Support project-based enterprise deployments

• Enable white-labelled enterprise implementations

• Support consulting-driven engagements

• Provide enterprise analytics

• Generate actionable organizational insights

---

## Technical Objectives

The platform must be:

* Metadata-driven
* Configuration-first
* Multi-tenant capable (future)
* White-label ready
* Modular
* API-first
* Cloud-native
* Highly scalable

---

# 4. Platform Philosophy

Instead of

```
Company
     ↓
Survey
     ↓
Reports
```

TESP follows

```
Platform

    ↓

Project

    ↓

Configuration

    ↓

Modules

    ↓

Users
```

Every project becomes an isolated configurable implementation.

---

# 5. Project-Based Architecture

Each client implementation is known as a Project.

Example

```
Project

Aitken Spence

Project

Nestle

Project

Dialog

Project

MAS Holdings

Project

John Keells

Project

ABC Manufacturing
```

Each project contains:

* Branding
* Organization Structure
* Employee Dataset
* Survey Templates
* Distribution Rules
* Dashboard Configuration
* Reports
* Integrations
* User Roles
* Languages
* AI Configuration

---

# 6. High-Level Modules

The platform consists of independent modules.

```
Project Management

Organization Management

Employee Management

Survey Builder

Survey Distribution

Response Engine

Analytics Engine

Report Engine

Dashboard Engine

Action Planning

AI Analytics

Notification Engine

Authentication

Administration

Audit System

Integration Layer
```

Each module should operate independently while communicating through internal services and events.

---

# 7. Organization Management

## Dynamic Organizational Hierarchy

No hierarchy should ever be hardcoded.

Instead, the platform represents the organization as a hierarchical tree.

Example

```
Company

    Sector

        Segment

            Business

                Branch

                    Department

                        Team
```

Another company may instead define:

```
Country

Region

Division

Business Unit

Department

Team
```

The hierarchy depth is unlimited.

Each node contains:

* Name
* Type
* Parent
* Display Order
* Status
* Metadata
* Custom Attributes

---

## Organization Node Types

Examples include:

* Company
* Sector
* Segment
* Division
* Region
* Country
* Department
* Branch
* Team
* Office
* Factory
* Store
* Hotel
* Business Unit

Projects may create additional custom node types.

---

# 8. Employee Management

Employee management must support:

* HRIS imports
* CSV imports
* API synchronization
* Manual management

Employee profiles should contain:

* Identity
* Organizational memberships
* Demographic data
* Employment details
* Hierarchy relationships
* Custom attributes

The platform must support project-specific employee attributes without schema changes.

---

# 9. Survey Engine

The Survey Engine is fully metadata-driven.

Components:

* Survey
* Pages
* Sections
* Questions
* Question Groups
* Validation Rules
* Branching Logic
* Scoring
* Localization
* Survey Versioning

Question Types include:

* Likert Scale
* NPS
* Ranking
* Matrix
* Single Choice
* Multiple Choice
* Short Text
* Long Text
* Numeric
* Date
* Image Selection
* Audio-enabled Questions
* File Upload (future)

---

# 10. Survey Distribution Engine

Distribution channels should be pluggable.

Supported:

* Email
* QR
* Kiosk
* PIN
* SMS
* Microsoft Teams
* Slack
* WhatsApp (future)
* Mobile Push (future)

Each campaign tracks:

* Sent
* Delivered
* Opened
* Started
* Completed
* Expired
* Failed

---

# 11. Anonymous Response Framework

Supports:

### Authenticated

Employee Login

### Semi Anonymous

Single-use employee token

### Fully Anonymous

Random PIN allocation

### Kiosk Mode

Random token generation

Duplicate prevention

Anonymous metadata tagging

---

# 12. Survey Builder

Features include:

* Drag-and-drop builder
* Question logic
* Conditional branching
* Dynamic pages
* Question libraries
* Survey templates
* Survey cloning
* Survey versioning
* Draft mode
* Preview mode

---

# 13. Analytics Engine

The Analytics Engine should calculate:

* eNPS
* Engagement Score
* Participation Rate
* Sentiment Score
* Response Trends
* Heatmaps
* Department Comparisons
* Benchmark Scores
* Historical Analysis

All analytics are filterable by any organizational or demographic dimension.

---

# 14. Dashboard Engine

Dashboards are widget-based rather than hardcoded.

Widgets include:

* KPI Cards
* Charts
* Heatmaps
* Trends
* Leaderboards
* Participation Maps
* Sentiment Clouds
* Response Distribution
* Action Tracking
* AI Insights

Projects can assemble custom dashboards for different roles.

---

# 15. Reporting Engine

The Reporting Engine generates outputs dynamically based on filters and templates.

Supported formats:

* Interactive dashboards
* PDF reports
* Excel exports
* PowerPoint summaries
* JSON APIs

Reports can be generated for any organizational level or demographic combination.

---

# 16. Action Planning Module

Enable organizations to convert survey findings into measurable improvement initiatives.

Features:

* Create action plans
* Assign owners
* Set due dates
* Track progress
* Link actions to survey themes
* Monitor completion status

---

# 17. AI Analytics Module

Provide intelligent insights beyond descriptive statistics.

Capabilities:

* Sentiment analysis of open-ended responses
* Theme extraction
* Keyword clustering
* Executive summaries
* Risk identification
* Suggested interventions
* Trend forecasting
* AI-generated recommendations

This complements Daash Global's consulting expertise rather than replacing it.

---

# 18. User Roles & Permissions

Implement Role-Based Access Control (RBAC) with configurable data access filters.

Example roles:

* Super Administrator
* Project Administrator
* HR Manager
* Executive
* Business Unit Head
* Department Manager
* Consultant (Daash Global)
* Viewer

Permissions should define both feature access and data visibility.

---

# 19. Project Configuration Framework

Every project defines its own:

* Branding
* Logo
* Theme
* Languages
* Organization hierarchy
* Employee schema
* Distribution channels
* Survey templates
* Dashboard layouts
* Report templates
* Notification rules
* Integrations
* Security policies

The objective is to maximize configuration and minimize custom development.

---

# 20. Database Design Principles

The database should be schema-flexible and metadata-driven, leveraging MongoDB Atlas.

Core principles:

* Configuration over code
* Dynamic schemas for project-specific attributes
* Generic organization tree
* Versioned surveys and reports
* Event/audit logging
* Separation of configuration, transactional, and analytical data

---

# 21. Technical Architecture

Recommended stack:

* **Frontend:** React 19+ (Vite, TypeScript, Tailwind CSS)
* **Backend:** Spring Boot 3.5+
* **Database:** MongoDB Atlas
* **Object Storage:** Firebase Storage / S3-compatible
* **Cache:** Redis
* **Authentication:** JWT, OAuth2, SSO
* **Reporting:** JasperReports / PDF generation service
* **AI Services:** OpenAI APIs (pluggable)
* **Deployment:** AWS ECS / EC2 with CDN support

---

# 22. Scalability Strategy

The platform should scale from:

* Small organizations (100 employees)
* Mid-sized enterprises (5,000 employees)
* Large enterprises (50,000+ employees)
* Multi-country organizations

Scalability considerations include asynchronous processing, caching, analytics snapshots, horizontal scaling, and configurable retention policies.

---

# 23. Future Roadmap

Potential modules built on the same engine:

* Employee Pulse Surveys
* Engagement Surveys
* eNPS
* Culture Assessments
* 360-Degree Feedback
* Exit Interviews
* Onboarding Feedback
* Training Evaluations
* Leadership Assessments
* Compliance Surveys
* Safety Surveys
* Customer Experience Surveys
* Vendor Assessments
* ESG Surveys

These modules should reuse the same core platform, differing only in configuration, templates, and analytics.
