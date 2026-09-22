# How-To: Database Migrations & Production Deployment

This guide walks through configuring PostgreSQL, running Flyway schema migrations, and deploying **Ilmiya** in a production environment with Java 25.

---

## Overview

Ilmiya is built to operate as a high-throughput, low-latency modular monolith:
- **Database Engine**: PostgreSQL 16+ with ACID compliance, JSONB support, and time-zone-aware timestamps (`TIMESTAMP WITH TIME ZONE`).
- **Migration Engine**: Flyway automates versioned DDL migrations located in `ilmiya-app/src/main/resources/db/migration/`.
- **Runtime Environment**: Java 25 with virtual threads and `ScopedValue` multi-tenancy context propagation.
- **Monitoring & Observability**: Spring Boot Actuator provides readiness, liveness, and operational metrics.

---

## Step 1: Provision PostgreSQL via Docker Compose

Create a `docker-compose.yml` file to run PostgreSQL locally or on staging:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:17-alpine
    container_name: ilmiya-postgres
    restart: always
    environment:
      POSTGRES_DB: ilmiya_db
      POSTGRES_USER: ilmiya_user
      POSTGRES_PASSWORD: ilmiya_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ilmiya_user -d ilmiya_db"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

Start PostgreSQL in the background:
```bash
docker compose up -d
```

Verify that PostgreSQL is healthy:
```bash
docker compose ps
```

---

## Step 2: Configure Environment Variables

Ilmiya utilizes externalized Spring Boot configuration. Configure the following environment variables in your deployment environment (systemd, Docker, Kubernetes, or `.env`):

| Variable Name | Default Value | Description |
|---------------|---------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/ilmiya_db` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `ilmiya_user` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `ilmiya_password` | Database password |
| `IAM_JWT_SECRET` | *(Random string)* | HMAC-SHA256 signing secret (min 256 bits) |
| `IAM_JWT_ACCESS_TOKEN_EXPIRATION` | `3600` | Access token lifespan in seconds (1 hour) |
| `IAM_JWT_REFRESH_TOKEN_EXPIRATION` | `604800` | Refresh token lifespan in seconds (7 days) |
| `SERVER_PORT` | `8080` | HTTP service port |

> [!CAUTION]
> In production environments, **always** generate a cryptographically strong 256-bit or 512-bit string for `IAM_JWT_SECRET`. Never use default secrets in production.

---

## Step 3: Run Flyway Database Migrations

Flyway automatically applies pending migrations upon application startup when `spring.flyway.enabled=true`.

### Migration File Layout
Migrations are located in `ilmiya-app/src/main/resources/db/migration/`:
- `V1__init_schema.sql`: Initializes baseline schema tracking table.
- `V2__phase_1_school_and_academic_schema.sql`: Provisions `core_schools`, `academic_years`, and `academic_terms`.

### Writing New Migrations
When adding new migrations:
1. Follow the naming format: `V{version}__{description}.sql` (double underscore after version).
2. Ensure all tables include `tenant_id VARCHAR(50) NOT NULL`.
3. Add an index on `tenant_id` for every table.
4. Keep migrations strictly append-only; never modify an already applied migration.

### Validating Migrations via Gradle
```bash
./gradlew :ilmiya-app:flywayInfo
```

---

## Step 4: Build & Run with Java 25

### Build Executable Archive
Build the production-ready fat JAR and execute unit/ArchUnit test suites:

```bash
./gradlew clean build -x test
./gradlew check
```

### Launch the Application
Run the packaged JAR:

```bash
java -jar ilmiya-app/build/libs/ilmiya-app-0.0.1-SNAPSHOT.jar
```

Or run directly via Gradle for development:
```bash
./gradlew :ilmiya-app:bootRun
```

---

## Step 5: Verify Health Probes & Swagger UI

Once the application has launched, verify operational health endpoints:

### 1. Actuator Liveness & Readiness Probes
```bash
curl -s http://localhost:8080/actuator/health | jq
```

**Expected Response (`200 OK`):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### 2. Swagger UI & OpenAPI Specification
- **Interactive Swagger UI**: Navigate to `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3 JSON Spec**: `http://localhost:8080/api-docs`

---

## Production Tuning & Checklist

- [ ] **Connection Pooling**: HikariCP default max pool size is 10. For high-concurrency production instances, tune `spring.datasource.hikari.maximum-pool-size=30` and `minimum-idle=10`.
- [ ] **Flyway Validation**: Maintain `spring.jpa.hibernate.ddl-auto=validate` to ensure JPA entity annotations match Flyway DDL definitions.
- [ ] **Reverse Proxy (Nginx / Cloudflare)**: Ensure `X-Forwarded-For`, `X-Forwarded-Proto`, and `X-Tenant-ID` headers are passed downstream to the Spring Boot instance.
- [ ] **CORS Settings**: Restrict `allowedOriginPatterns` in `SecurityConfig.kt` to authorized school subdomains instead of `*`.
