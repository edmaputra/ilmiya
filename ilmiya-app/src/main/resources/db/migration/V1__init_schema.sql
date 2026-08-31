-- Initial schema migration baseline for ilmiya
-- Schema tables will be defined per bounded context (core, academic, student, attendance, hr, exam, finance)

CREATE TABLE IF NOT EXISTS schema_baseline (
    id VARCHAR(50) PRIMARY KEY,
    initialized_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO schema_baseline (id) VALUES ('BASELINE_V1') ON CONFLICT DO NOTHING;
