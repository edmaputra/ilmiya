-- Phase 1 schema migration: School institutions, operational settings, academic years and terms

CREATE TABLE IF NOT EXISTS core_schools (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    institution_code VARCHAR(50) NOT NULL UNIQUE,
    government_reg_number VARCHAR(50),
    registration_type VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    contact_email VARCHAR(150) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    logo_url VARCHAR(500),
    accreditation_status VARCHAR(50) NOT NULL DEFAULT 'UNACCREDITED',
    accreditation_body VARCHAR(100),
    accreditation_valid_until DATE,
    working_days VARCHAR(100) NOT NULL DEFAULT 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
    grading_preference VARCHAR(50) NOT NULL DEFAULT 'NUMERIC_0_100',
    term_layout VARCHAR(50) NOT NULL DEFAULT 'SEMESTER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX IF NOT EXISTS idx_core_schools_tenant_id ON core_schools(tenant_id);
CREATE INDEX IF NOT EXISTS idx_core_schools_institution_code ON core_schools(institution_code);

CREATE TABLE IF NOT EXISTS academic_years (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT uq_academic_years_tenant_name UNIQUE (tenant_id, name)
);

CREATE INDEX IF NOT EXISTS idx_academic_years_tenant_status ON academic_years(tenant_id, status);

CREATE TABLE IF NOT EXISTS academic_terms (
    id VARCHAR(36) PRIMARY KEY,
    academic_year_id VARCHAR(36) NOT NULL REFERENCES academic_years(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    term_type VARCHAR(50) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED'
);

CREATE INDEX IF NOT EXISTS idx_academic_terms_year_id ON academic_terms(academic_year_id);
