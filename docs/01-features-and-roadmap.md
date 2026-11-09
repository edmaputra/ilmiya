# Features and Roadmap

## Overview

**Ilmiya** is an open-source, multi-tenant School Management System (SMS) and Educational ERP designed for primary, secondary, vocational, and Islamic educational institutions (SD, SMP, SMA, SMK, MI, MTs, MA, and Pesantren).

Built with **Kotlin 2.3**, **Spring Boot 4.1.2**, and **Java 25**, Ilmiya follows **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)**. It uses a **shared-database, tenant-discriminator** multi-tenancy model powered by Java 25 `ScopedValue` execution contexts.

---

## Target Market

### Primary Users
- **School Administrators & Principals**: Institutional governance, academic years, operational oversight.
- **Yayasan / School Board Management**: Multi-school oversight, institutional performance, executive reporting.
- **Teachers & Educators**: Subject delivery, timetable schedules, daily/period attendance, grading, report cards.
- **Students**: Class schedules, learning resources, assignments, exam results, and attendance records.
- **Parents / Guardians**: Student progress tracking, attendance alerts, fee invoices (SPP), payment history.
- **Finance Officers / Bursars (Tata Usaha Keuangan)**: Fee structures, student billings, fee collection, payment reconciliation, receipts.
- **School HR & Administrative Staff**: Staff records, teacher allocations, leave requests, payroll processing.

### Target Segments

| Segment | Status | Notes |
|---------|--------|-------|
| Primary & Junior High (SD, SMP / MI, MTs) | 🎯 Core MVP | Homeroom-based attendance, semester report cards, basic fee structures (SPP) |
| Senior High & Vocational (SMA, SMK / MA) | 🎯 Core MVP | Department/major tracks, subject-level period attendance, internships, specialized grading |
| Islamic Boarding Schools (Pesantren) | 🎯 Core MVP | Dual curriculum (National + Kitab/Diniyyah), boarding/dormitory fee integration |
| Private & International Schools | 📋 Planned | Custom grading criteria (IB/Cambridge), bilingual reports, multi-tier tuition fees |
| Tutoring Centers & Test Prep (Bimbel) | 📋 Planned | Course/session packages, batch-based enrollment, flexible billing |
| Higher Education (Universities / Poltek) | ⏳ Long-Term | Complex SKS credit semester systems, faculty research grants, elective add/drop |

### Not Targeted (Near-Term)
- **Higher Education Credit Accumulation (SKS)**: University-style semester credit units, prerequisite chains, and student study plans (KRS).
- **In-House Video Conferencing Hosting**: Integrates with Zoom, Google Meet, or Microsoft Teams instead of hosting video infrastructure.
- **Hardware-Level Biometric Firmware**: Relies on standard webhook/REST endpoints or CSV imports for RFID/fingerprint terminal sync rather than proprietary device drivers.
- **Multi-Currency Global Operations**: Primary currency is Indonesian Rupiah (IDR); single-currency per tenant in early releases.

---

## Architecture & Platform Pillars

### 1. Modular Monolith with Hexagonal Architecture
Dependencies strictly flow **inward**. The domain core is framework-agnostic pure Kotlin with zero annotations from Spring or JPA.

```
Driving Adapters (Web / REST Controllers)     Driven Adapters (Spring Data JPA / DB / Messaging)
                 \                                      /
                  \                                    /
                   -->   Domain Model & Ports (Pure Kotlin)  <---
                                      ^
                                      |
                       Application Use Cases (Services)
                                      ^
                                      |
                        ilmiya-app (Wiring & Starters)
```

### 2. Multi-Tenancy Architecture
- **Model**: Shared application, shared database, tenant discriminator column (`tenant_id`).
- **Context Propagation**: Java 25 `ScopedValue` through `TenantContext` ensures fail-fast, zero-leak thread scoping without mutable `ThreadLocal` leaks.
- **Isolation Enforcement**: All tenant-aware persistence queries automatically bind the current `TenantId`.
- **Tenant-Scoped Sequences**: Invoices, admission numbers (NIS/NISN), and receipt IDs are partitioned per tenant.

### 3. Append-Only Audit Trail
- Platform capability tracking all business state mutations via `Auditable` and `DomainEvent`.
- Captures tenant ID, actor identity, action type, correlation ID, and payload snapshot.
- Historical audit records are immutable and append-only.

---

## Module Layout & Bounded Contexts

The system is partitioned into 10 decoupled Gradle modules:

| Module | Responsibility & Bounded Context |
|--------|----------------------------------|
| `ilmiya-common` | **Shared Kernel**: Pure Kotlin domain primitives (`TenantId`, `EntityId`, `TenantAware`, `Auditable`, `AggregateRoot`), `TenantContext` (`ScopedValue`), API envelopes (`ApiResponse`, `PageResponse`), and base domain exceptions. |
| `ilmiya-core` | **IAM & Institutional Core**: School/tenant profile, authentication, Role-Based Access Control (RBAC), user accounts, institutional settings, audit trail persistence. |
| `ilmiya-academic` | **Curriculum & Academic Structure**: Academic years, terms/semesters, courses, grade levels, classes/batches, subjects, subject allocations, and student promotions. |
| `ilmiya-student` | **Student Lifecycle**: Student registration/admissions (PPDB), student master profiles, demographic data, guardian links, student categories, student status (Active, Transferred, Graduated, Expelled). |
| `ilmiya-attendance` | **Attendance Tracking**: Student daily attendance, subject/period-wise attendance, staff attendance, leave justifications, absence summaries, and attendance alert dispatch. |
| `ilmiya-timetable` | **Timetable & Institutional Calendar**: Class timings, period definitions, timetable scheduling, room allocations, teacher conflict prevention, holidays, and school calendar events. |
| `ilmiya-exam` | **Examinations & Grading**: Exam periods (UTS, UAS, Daily Tests), assessment components, customizable grading scales, marksheet recording, and automated report card (e-Rapor) generation. |
| `ilmiya-finance` | **Fee Management & Bursar**: Fee categories (SPP, uang pangkal, seragam, buku), fee schedules, student fee discounts/scholarships, invoice generation, payment processing, receipts, and cash journal summaries. |
| `ilmiya-hr` | **Human Resources & Payroll**: Staff/teacher master records, departments, designations, staff leave requests & approvals, salary component configuration, and monthly payroll processing. |
| `ilmiya-app` | **Composition Root**: Spring Boot 4.1.2 executable launcher, transactional use-case decorators, Flyway database migrations, OpenAPI 3 (Swagger UI) integration, and production configuration. |

---

## Implemented Features

### Phase 0: Project Setup & Architecture Foundation (Complete)
- **Java 25 + Kotlin 2.3.21** toolchain configuration with strict `-Xjsr305=strict` null-safety.
- **Spring Boot 4.1.2** modular monolith setup with clean boundary separation.
- **PostgreSQL + Flyway** migration engine initialized with baseline tracking (`V1__init_schema.sql`).
- **Framework-Agnostic Kernel (`ilmiya-common`)**:
  - `TenantId` typed value object and `TenantAware` marker.
  - `TenantContext` powered by Java 25 `ScopedValue` with `withTenant` / `runWithTenant` scoping.
  - `EntityId<T>` type-safe entity identifiers.
  - `AggregateRoot<ID>` with domain event registration and retrieval.
  - `Auditable` contract for structured mutation audits.
  - Consistent API response envelopes: `ApiResponse<T>` and `PageResponse<T>`.
  - Base domain exceptions: `DomainException`, `MissingTenantContextException`, `ResourceNotFoundException`.
- **OpenAPI 3 / Swagger UI** configuration (`/swagger-ui.html`, `/api-docs`).
- **Comprehensive Unit Tests** for tenant context scoping and API response formats.
- **Coding & Architectural Governance Rules** established in `.agents/rules/`.

---

## Product Roadmap & Phases

### Phase 1: Core IAM, Tenant Management & Academic Year (In Progress)

#### Tenant & School Profile
- Multi-tenant school registration and provisioning.
- Institutional profile: School name, NPSN (Nomor Pokok Sekolah Nasional), address, contact info, school logo, accreditation status.
- School operational settings (working days, grading format preference, term layout).

#### Identity & Access Management (IAM)
- User identity management with tenant scoping.
- Standard roles: `SUPERADMIN` (Platform), `SCHOOL_ADMIN`, `PRINCIPAL`, `TEACHER`, `BURSAR` (Finance), `HR_STAFF`, `STUDENT`, `PARENT`.
- Granular permission matrix mapped to use-case ports.
- Password encryption (Argon2 / BCrypt) and secure session / JWT token management.

#### Academic Year & Term Configuration
- Academic Year master (e.g., 2026/2027) with active/archived statuses.
- Academic Terms / Semesters (Ganjil & Genap) with start and end date boundaries.
- Academic year rollover rules.

---

### Phase 2: Academic Curriculum & Student Lifecycle

#### Academic Structure (`ilmiya-academic`)
- Course and grade level definitions (e.g., Grade 1 to Grade 12 / Kelas 1 - 12).
- Batch / Class section management (e.g., "Kelas 7-A", "Kelas 10-IPA-1").
- Subject master catalog (General, Religious, Vocational, Elective).
- Subject allocation per class and teacher assignment.
- Academic promotion and class progression workflow at term end.

#### Student Lifecycle (`ilmiya-student`)
- Student registration & New Student Admission (PPDB / Penerimaan Peserta Didik Baru).
- Student master profile: NIS, NISN, Full Name, Gender, Birth Info, Religion, Blood Type, Address.
- Student category classification (Reguler, Beasiswa, Asrama).
- Parent & Guardian management: Father, Mother, Guardian profiles with NIK, occupation, phone, email.
- Student-Guardian mapping (Primary contact, emergency contact).
- Student status transitions: Enrolled → Active → On-Leave → Graduated / Transferred / Withdrawn.
- Student document attachments (birth certificate, previous report cards, family card / KK).

---

### Phase 3: Timetable Scheduling & Attendance System

#### Institutional Calendar & Timetable (`ilmiya-timetable`)
- Class timing sets (e.g., Regular Days vs. Friday schedules).
- Period definitions (slot duration, prayer/break times, flag ceremony periods).
- Timetable matrix per class batch and per teacher.
- Clash and conflict detection (prevent assigning the same teacher/room to multiple concurrent classes).
- Institutional calendar: Public holidays, school holidays, exam weeks, and special event markers.

#### Student & Staff Attendance (`ilmiya-attendance`)
- Daily attendance mode (homeroom teacher marks attendance once daily).
- Period/Subject attendance mode (teachers record attendance per teaching slot).
- Attendance statuses: Present (Hadir), Sick with letter (Sakit), Permitted (Izin), Unexcused Absence (Alpa).
- Teacher & staff daily attendance check-in/check-out.
- Monthly attendance summary and percentage calculation per student.
- Automatic absence alert generation for parent notifications.

---

### Phase 4: Examination, Assessment & Report Cards

#### Examination Management (`ilmiya-exam`)
- Exam sessions: Formative assessments, Midterm Exams (PTS/UTS), Final Semester Exams (PAS/PAT), Practical Exams.
- Assessment components and weightages (e.g., Homework 20%, Quiz 20%, Midterm 30%, Final 30%).
- Grading schemes:
  - Letter grade scale (A, B, C, D, E) with customizable score thresholds.
  - Indonesian National Curriculum standards (Kurikulum Merdeka learning achievements & K13 KKM thresholds).
  - Descriptive qualitative remarks per subject.
- Marksheet entry by assigned subject teachers with validation.
- Grade submission and approval workflow (Teacher submit → Homeroom/Curriculum review → Principal lock).

#### Report Card (e-Rapor) Generation
- Auto-compilation of academic scores, attendance records, extracurricular activities, and teacher notes.
- Indonesian national standard report card format (Rapor Siswa).
- Batch PDF export for student report cards ready for printing/signing.

---

### Phase 5: School Finance, Fee Management & Invoicing

#### Fee Structure & Tuition Setup (`ilmiya-finance`)
- Fee category setup: Recurring tuition (SPP Bulanan), One-time admission fee (Uang Pangkal/Gedung), Activity fees, Uniforms, Books, Examination fees.
- Batch-based or student-category-based fee assignment.
- Sibling discounts, scholarship waivers (Beasiswa Prestasi / KIP), and financial aid adjustments.

#### Billing & Payments
- Automated monthly invoice generation for active students.
- Payment recording: Cash over the counter, bank transfer verification, or payment gateway integration.
- Partial payment and installment handling.
- Automated payment receipts with unique receipt numbers.
- Outstanding fee tracking (Daftar Tunggakan SPP) with aging reports.
- Daily cash collection log and financial transaction ledger for the bursar.

---

### Phase 6: Human Resources & Payroll

#### Staff Master Data (`ilmiya-hr`)
- Employee profiles: Permanent teachers (GTY/PNS), contract teachers (GTT/Honorer), administrative staff.
- Department, designation, and employment status tracking.
- Educational background, certifications (Sertifikasi Guru), and NUPTK/NIP tracking.
- Staff leave management: Leave balance, leave application, approval workflow by principal/HR.

#### School Payroll Processing
- Indonesian school salary components:
  - Base Salary (Gaji Pokok).
  - Allowances: Functional/Teaching allowance (Tunjangan Jabatan/Mengajar), Homeroom allowance (Tunjangan Wali Kelas), Attendance allowance (Tunjangan Kehadiran).
  - Deductions: Loan/Kasbon deductions, absence penalties, social security/BPJS.
- Monthly payroll batch calculation based on attendance and teaching hours.
- Payslip generation (printable and viewable online).
- Disbursement summary reports.

---

### Phase 7: Self-Service Portals & Communication Gateway

#### Guardian & Student Portal
- Responsive web portal for parents and guardians.
- Real-time overview of student's attendance records.
- Examination scores and downloadable digital report cards.
- Fee bills, upcoming SPP dues, payment history, and receipt downloads.
- School circulars, announcements, and academic calendar viewer.

#### Communication Gateway
- Automated notification triggers for:
  - Unexcused absence alerts sent to parents immediately after morning roll call.
  - Monthly fee billing reminders and payment confirmation receipts.
  - Important school announcements and exam schedules.
- Multi-channel delivery connectors: WhatsApp Gateway (e.g. Baileys / Waha / commercial API), Telegram Bot, and Email (SMTP).

---

### Phase 8: Compliance, Analytics & Platform Extensions

#### National Education Compliance (Dapodik & EMIS)
- Dapodik (Kemendikbudristek) export-ready data format.
- EMIS (Kementerian Agama) export-ready data format for Madrasah and Pesantren.
- Student NISN and teacher NUPTK validation checks.

#### Executive Dashboards & Analytics
- Principal & Yayasan dashboard: Enrollment trends, student retention, revenue collection vs. outstanding arrears.
- Academic performance analytics: Grade distributions per subject/class, teacher workload metrics.
- Attendance trends: Chronic absenteeism identification and early warning indicators.

#### Disaster Recovery & Tenant Management
- Automated encrypted database backups per tenant.
- School data import/export utilities (Excel/CSV templates for students, teachers, grades).
- Tenant onboarding wizard with pre-configured Indonesian school templates (SD, SMP, SMA, SMK, MI, MTs, MA, Pesantren).

---

## Non-Functional & Security Standards

- **Strict Tenant Isolation**: Zero cross-tenant data leakage guaranteed at domain and persistence layers using `TenantContext` and mandatory `tenant_id` database indices.
- **Role-Based Access Control (RBAC)**: Fine-grained method security and use-case port level checks adhering to the Principle of Least Privilege.
- **Auditability & Traceability**: All mutations to sensitive entities (grades, payments, admissions) publish immutable domain events storing actor identity and timestamp.
- **Idempotency & Data Integrity**: Transactional decorators ensure atomic operations across use-cases with strong validation against invalid transitions.
- **Modern Observability**: Health checks, JVM metrics via Spring Boot Actuator, and structured JSON logs with tenant correlation context.
