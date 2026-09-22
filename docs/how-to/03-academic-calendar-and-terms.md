# How-To: Academic Calendar & Term Lifecycle Configuration

This guide walks through configuring academic years, appending terms/semesters, and managing the lifecycle transitions from planning to active operations in **Ilmiya**.

---

## Overview

The academic calendar establishes the foundational temporal boundary for classes, timetables, attendance, examinations, and fee invoicing.

### Domain Aggregate Model
- **`AcademicYear` (Aggregate Root)**: Defines the overarching academic cycle (e.g., `2026/2027`).
- **`AcademicTerm` (Child Entity)**: Partitions the year into distinct instruction and assessment cycles (e.g., `Semester 1`, `Semester 2`).
- **Lifecycle States**:
  - `PLANNED`: Calendar created, terms can be scheduled and configured.
  - `ACTIVE`: Currently in-session; classes, attendance, and grading are actively linked to this cycle.
  - `ARCHIVED`: Past academic period preserved immutably for historical record keeping.

### Invariants Enforced by Domain Core
1. **Single Active Year**: A tenant can have at most **one** active academic year at any given time. Activating a new year automatically archives the previously active year.
2. **Single Active Term**: An academic year can have at most **one** active term at a time.
3. **Boundary Enclosure**: All academic terms must have start and end dates strictly within the enclosing academic year's date range.
4. **Chronological Validity**: Start dates must strictly precede end dates.

---

## Step 1: Create an Academic Year

Create an academic year in `PLANNED` state by sending a `POST` request to `/api/v1/academic-years`:

```bash
curl -i -X POST http://localhost:8080/api/v1/academic-years \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2026/2027",
    "startDate": "2026-07-13",
    "endDate": "2027-06-25"
  }'
```

**Expected Response (`201 Created`):**
```json
{
  "id": "ay-4d9f1c8a-7b3e",
  "tenantId": "sma-nusantara-1",
  "name": "2026/2027",
  "startDate": "2026-07-13",
  "endDate": "2027-06-25",
  "status": "PLANNED",
  "terms": [],
  "createdAt": "2026-09-22T09:15:00Z",
  "updatedAt": "2026-09-22T09:15:00Z"
}
```

---

## Step 2: Add Academic Terms (Semesters / Quarters)

Next, add terms to the created academic year using its ID (`ay-4d9f1c8a-7b3e`).

### Add Semester 1 (Odd Term / Ganjil)
```bash
curl -i -X POST http://localhost:8080/api/v1/academic-years/ay-4d9f1c8a-7b3e/terms \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Semester Ganjil 2026",
    "type": "SEMESTER_1",
    "displayName": "Semester 1 (Ganjil)",
    "startDate": "2026-07-13",
    "endDate": "2026-12-18"
  }'
```

### Add Semester 2 (Even Term / Genap)
```bash
curl -i -X POST http://localhost:8080/api/v1/academic-years/ay-4d9f1c8a-7b3e/terms \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Semester Genap 2027",
    "type": "SEMESTER_2",
    "displayName": "Semester 2 (Genap)",
    "startDate": "2027-01-04",
    "endDate": "2027-06-25"
  }'
```

> [!TIP]
> Ilmiya supports multiple term layout conventions:
> - **Semester**: `SEMESTER_1`, `SEMESTER_2`
> - **Trimester**: `TRIMESTER_1`, `TRIMESTER_2`, `TRIMESTER_3`
> - **Quarter**: `QUARTER_1`, `QUARTER_2`, `QUARTER_3`, `QUARTER_4`
> - **Custom**: `CUSTOM` (for special intensive sessions or pesantren summer terms)

---

## Step 3: Activate the Academic Year

When the school session begins, activate the academic year via `PUT /api/v1/academic-years/{id}/activate`:

```bash
curl -i -X PUT http://localhost:8080/api/v1/academic-years/ay-4d9f1c8a-7b3e/activate \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1"
```

**Expected Behavior:**
1. Aggregate status transitions from `PLANNED` $\rightarrow$ `ACTIVE`.
2. Any previously `ACTIVE` academic year under `sma-nusantara-1` is automatically marked `ARCHIVED`.
3. An `AcademicYearActivatedEvent` domain event is published internally.

---

## Step 4: Activate the Current Term

Activate the ongoing term within the active academic year:

```bash
curl -i -X PUT http://localhost:8080/api/v1/academic-years/ay-4d9f1c8a-7b3e/terms/term-sem1-uuid/activate \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1"
```

**Expected Response (`200 OK`):**
```json
{
  "id": "term-sem1-uuid",
  "name": "Semester Ganjil 2026",
  "type": "SEMESTER_1",
  "displayName": "Semester 1 (Ganjil)",
  "startDate": "2026-07-13",
  "endDate": "2026-12-18",
  "status": "ACTIVE"
}
```

---

## Step 5: Query Active Calendar

Downstream modules (Timetable, Attendance, Gradebook) query the currently active academic calendar:

```bash
curl -s -X GET http://localhost:8080/api/v1/academic-years/active \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
  -H "X-Tenant-ID: sma-nusantara-1" | jq
```

**Expected Response (`200 OK`):**
```json
{
  "id": "ay-4d9f1c8a-7b3e",
  "tenantId": "sma-nusantara-1",
  "name": "2026/2027",
  "startDate": "2026-07-13",
  "endDate": "2027-06-25",
  "status": "ACTIVE",
  "terms": [
    {
      "id": "term-sem1-uuid",
      "name": "Semester Ganjil 2026",
      "type": "SEMESTER_1",
      "displayName": "Semester 1 (Ganjil)",
      "startDate": "2026-07-13",
      "endDate": "2026-12-18",
      "status": "ACTIVE"
    },
    {
      "id": "term-sem2-uuid",
      "name": "Semester Genap 2027",
      "type": "SEMESTER_2",
      "displayName": "Semester 2 (Genap)",
      "startDate": "2027-01-04",
      "endDate": "2027-06-25",
      "status": "PLANNED"
    }
  ],
  "createdAt": "2026-09-22T09:15:00Z",
  "updatedAt": "2026-09-22T09:20:00Z"
}
```

---

## Step 6: Verify Database Integrity

Inspect the database state in PostgreSQL:

```sql
SELECT id, tenant_id, name, start_date, end_date, status 
FROM academic_years 
WHERE tenant_id = 'sma-nusantara-1';

SELECT id, academic_year_id, name, term_type, start_date, end_date, status 
FROM academic_terms 
ORDER BY start_date ASC;
```

---

## Troubleshooting & Domain Validations

| Error | Condition | Fix |
|-------|-----------|-----|
| `400 Bad Request: Start date must be before end date` | `startDate >= endDate` during year or term creation. | Correct the date ranges. |
| `400 Bad Request: Term dates must fall within academic year` | Term `startDate` is before year `startDate` or term `endDate` is after year `endDate`. | Adjust term boundaries to fall within the year range. |
| `409 Conflict: Academic year with name ... already exists` | Duplicate academic year name under the same tenant. | Academic year names must be unique per tenant (e.g. `2026/2027`). |
