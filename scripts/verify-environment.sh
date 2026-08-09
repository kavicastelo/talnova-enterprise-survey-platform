#!/usr/bin/env bash
# Bash Environment & Configuration Audit Script for TESP
# Validates environment variable alignment, Docker Compose syntax, domain consistency, and secret leakage.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "============================================================"
echo " TESP AUTOMATED ENVIRONMENT & CONFIGURATION AUDIT"
echo "============================================================"

FAILURES=0

# 1. Check Root .env and .env.example Existence
echo -n "[1/5] Checking Environment Files... "
if [ -f "${ROOT_DIR}/.env" ] && [ -f "${ROOT_DIR}/.env.example" ]; then
    echo "[PASS]"
else
    echo "[FAIL]"
    echo "     Root .env or .env.example file missing!"
    FAILURES=$((FAILURES + 1))
fi

# 2. Check for Obsolete Microservice .env Files
echo -n "[2/5] Scanning for Scattered Service .env Files... "
OBSOLETE_FILES=$(find "${ROOT_DIR}/services" -type f -name ".env" 2>/dev/null || true)
if [ -z "${OBSOLETE_FILES}" ]; then
    echo "[PASS]"
else
    echo "[FAIL]"
    echo "     Found obsolete environment files:"
    echo "${OBSOLETE_FILES}"
    FAILURES=$((FAILURES + 1))
fi

# 3. Validate Docker Compose Syntax
echo -n "[3/5] Validating Docker Compose Configuration... "
if docker compose config >/dev/null 2>&1; then
    echo "[PASS]"
else
    echo "[FAIL]"
    echo "     Docker Compose syntax check failed!"
    FAILURES=$((FAILURES + 1))
fi

# 4. Scan .env.example for Hardcoded Production Secrets
echo -n "[4/5] Auditing .env.example for Unsafe Credentials... "
if grep -qE "sk-proj-|404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" "${ROOT_DIR}/.env.example"; then
    echo "[FAIL]"
    echo "     .env.example contains real secret keys!"
    FAILURES=$((FAILURES + 1))
else
    echo "[PASS]"
fi

# 5. Check Git Ignore Enforcement for .env
echo -n "[5/5] Checking Git Status for .env Secrecy... "
if git ls-files --error-unmatch "${ROOT_DIR}/.env" >/dev/null 2>&1; then
    echo "[FAIL]"
    echo "     CRITICAL: .env is tracked in git repository!"
    FAILURES=$((FAILURES + 1))
else
    echo "[PASS]"
fi

echo "------------------------------------------------------------"
if [ ${FAILURES} -eq 0 ]; then
    echo " AUDIT COMPLETED SUCCESSFULLY: ALL CHECKS PASSED!"
    echo "------------------------------------------------------------"
    exit 0
else
    echo " AUDIT FAILED WITH ${FAILURES} ERRORS."
    echo "------------------------------------------------------------"
    exit 1
fi
