#!/usr/bin/env bash
#
# Helper script to initialize local HashiCorp Vault secrets and Consul KV configuration for Ilmiya.
#
# Usage:
#   ./scripts/init-vault-consul.sh
#
# Environment variables:
#   VAULT_ADDR          - Vault server URL (default: http://localhost:8200)
#   VAULT_TOKEN         - Vault authentication token (default: root)
#   CONSUL_HTTP_ADDR    - Consul agent HTTP URL (default: http://localhost:8500)
#   POSTGRES_PASSWORD   - PostgreSQL password to store in Vault (default: ilmiya_password)
#   IAM_JWT_SECRET      - Secret key for JWT signing to store in Vault
#

set -euo pipefail

VAULT_ADDR="${VAULT_ADDR:-http://localhost:8200}"
VAULT_TOKEN="${VAULT_TOKEN:-root}"
CONSUL_HTTP_ADDR="${CONSUL_HTTP_ADDR:-http://localhost:8500}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-ilmiya_password}"
IAM_JWT_SECRET="${IAM_JWT_SECRET:-super-secret-jwt-signing-key-for-ilmiya-school-platform-which-must-be-at-least-256-bits!}"

echo "=========================================================="
echo "Initializing local HashiCorp Vault & Consul for Ilmiya"
echo "  Vault URL:  ${VAULT_ADDR}"
echo "  Consul URL: ${CONSUL_HTTP_ADDR}"
echo "=========================================================="

# Check Vault health
echo -n "Checking Vault connectivity... "
if curl -s -f "${VAULT_ADDR}/v1/sys/health" > /dev/null 2>&1; then
    echo "OK (Active)"
elif curl -s "${VAULT_ADDR}/v1/sys/health" | grep -q '"initialized":true'; then
    echo "OK (Initialized)"
else
    echo "FAILED"
    echo "Error: Cannot connect to Vault at ${VAULT_ADDR}. Is the Vault service running?"
    exit 1
fi

# Check Consul health
echo -n "Checking Consul connectivity... "
if curl -s -f "${CONSUL_HTTP_ADDR}/v1/status/leader" > /dev/null 2>&1; then
    echo "OK"
else
    echo "FAILED"
    echo "Error: Cannot connect to Consul at ${CONSUL_HTTP_ADDR}. Is the Consul service running?"
    exit 1
fi

# 1. Seed Vault Secrets (KV v2 path: secret/data/ilmiya-backend)
echo "Writing secrets to Vault at secret/data/ilmiya-backend..."
VAULT_PAYLOAD=$(cat <<EOF
{
  "data": {
    "spring.datasource.password": "${POSTGRES_PASSWORD}",
    "iam.jwt.secret": "${IAM_JWT_SECRET}"
  }
}
EOF
)

VAULT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "${VAULT_ADDR}/v1/secret/data/ilmiya-backend" \
  -H "X-Vault-Token: ${VAULT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "${VAULT_PAYLOAD}")

VAULT_HTTP_CODE=$(echo "${VAULT_RESPONSE}" | tail -n1)
if [ "${VAULT_HTTP_CODE}" -ge 200 ] && [ "${VAULT_HTTP_CODE}" -lt 300 ]; then
    echo "Vault secrets successfully seeded!"
else
    echo "Failed to seed Vault secrets (HTTP ${VAULT_HTTP_CODE}):"
    echo "${VAULT_RESPONSE}"
    exit 1
fi

# 2. Seed Consul KV Configuration (Path: config/ilmiya-backend/data)
echo "Writing configuration to Consul KV at config/ilmiya-backend/data..."
CONSUL_YAML=$(cat <<'EOF'
logging:
  level:
    root: INFO
    io.github.edmaputra.ilmiya: DEBUG
ilmiya:
  seed:
    enabled: true
EOF
)

CONSUL_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "${CONSUL_HTTP_ADDR}/v1/kv/config/ilmiya-backend/data" \
  --data-binary "${CONSUL_YAML}")

CONSUL_HTTP_CODE=$(echo "${CONSUL_RESPONSE}" | tail -n1)
if [ "${CONSUL_HTTP_CODE}" -ge 200 ] && [ "${CONSUL_HTTP_CODE}" -lt 300 ]; then
    echo "Consul KV configuration successfully seeded!"
else
    echo "Failed to seed Consul KV (HTTP ${CONSUL_HTTP_CODE}):"
    echo "${CONSUL_RESPONSE}"
    exit 1
fi

echo "=========================================================="
echo "Initialization complete!"
echo "  Consul UI: ${CONSUL_HTTP_ADDR}/ui/"
echo "  Vault UI:  ${VAULT_ADDR}/ui/ (Token: ${VAULT_TOKEN})"
echo "=========================================================="
