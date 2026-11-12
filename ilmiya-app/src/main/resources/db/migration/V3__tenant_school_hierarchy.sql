-- Phase 1 hierarchy migration: Tenant (Yayasan / Foundation) and School hierarchy

CREATE TABLE IF NOT EXISTS core_tenants (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    legal_entity_number VARCHAR(100),
    registration_type VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    contact_email VARCHAR(150) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    logo_url VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX IF NOT EXISTS idx_core_tenants_status ON core_tenants(status);

-- Backfill core_tenants from existing core_schools so existing tenant_ids remain valid
INSERT INTO core_tenants (id, name, address, contact_email, contact_phone, status, created_at, updated_at)
SELECT tenant_id, name || ' Foundation', address, contact_email, contact_phone, status, created_at, updated_at
FROM core_schools
ON CONFLICT (id) DO NOTHING;

-- Drop UNIQUE constraint on core_schools(tenant_id) to allow multiple schools per tenant
ALTER TABLE core_schools DROP CONSTRAINT IF EXISTS core_schools_tenant_id_key;

-- Add foreign key constraint from core_schools to core_tenants
ALTER TABLE core_schools
    ADD CONSTRAINT fk_core_schools_tenant_id
    FOREIGN KEY (tenant_id) REFERENCES core_tenants(id) ON DELETE CASCADE;
