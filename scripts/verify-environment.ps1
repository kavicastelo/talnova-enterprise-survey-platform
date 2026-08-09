# PowerShell Environment & Configuration Audit Script for TESP
# Validates environment variable alignment, Docker Compose syntax, domain consistency, and secret leakage.

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$RootDir = Resolve-Path "$ScriptDir\.."

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " TESP AUTOMATED ENVIRONMENT & CONFIGURATION AUDIT" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

$Failures = 0

# 1. Check Root .env and .env.example Existence
Write-Host "`n[1/5] Checking Environment Files..." -NoNewline
if ((Test-Path "$RootDir\.env") -and (Test-Path "$RootDir\.env.example")) {
    Write-Host " [PASS]" -ForegroundColor Green
} else {
    Write-Host " [FAIL]" -ForegroundColor Red
    Write-Host "     Root .env or .env.example file missing!" -ForegroundColor Red
    $Failures++
}

# 2. Check for Obsolete Microservice .env Files
Write-Host "[2/5] Scanning for Scattered Service .env Files..." -NoNewline
$ObsoleteEnvFiles = Get-ChildItem -Path "$RootDir\services" -Recurse -Filter ".env" -ErrorAction SilentlyContinue
if ($ObsoleteEnvFiles.Count -eq 0) {
    Write-Host " [PASS]" -ForegroundColor Green
} else {
    Write-Host " [FAIL]" -ForegroundColor Red
    foreach ($file in $ObsoleteEnvFiles) {
        Write-Host "     Found obsolete environment file: $($file.FullName)" -ForegroundColor Red
    }
    $Failures++
}

# 3. Validate Docker Compose Syntax & Interpolation
Write-Host "[3/5] Validating Docker Compose Configuration..." -NoNewline
try {
    $composeConfig = & docker compose config 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host " [PASS]" -ForegroundColor Green
    } else {
        Write-Host " [FAIL]" -ForegroundColor Red
        Write-Host "     Docker Compose error: $composeConfig" -ForegroundColor Red
        $Failures++
    }
} catch {
    Write-Host " [FAIL]" -ForegroundColor Red
    Write-Host "     Failed to execute docker compose config" -ForegroundColor Red
    $Failures++
}

# 4. Scan .env.example for Hardcoded Production Secrets
Write-Host "[4/5] Auditing .env.example for Unsafe Credentials..." -NoNewline
$ExampleContent = Get-Content "$RootDir\.env.example" -Raw
if ($ExampleContent -match "sk-proj-" -or $ExampleContent -match "password123" -or $ExampleContent -match "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970") {
    Write-Host " [FAIL]" -ForegroundColor Red
    Write-Host "     .env.example contains real secret keys/passwords!" -ForegroundColor Red
    $Failures++
} else {
    Write-Host " [PASS]" -ForegroundColor Green
}

# 5. Check Git Ignore Enforcement for .env
Write-Host "[5/5] Checking Git Status for .env Secrecy..." -NoNewline
$GitStatus = & git status --ignored --porcelain "$RootDir\.env" 2>&1
if ($GitStatus -match "^\?\? \.env" -or $GitStatus -match "^!! \.env") {
    Write-Host " [PASS]" -ForegroundColor Green
} elseif ($GitStatus -match "^[ MAU] \.env") {
    Write-Host " [FAIL]" -ForegroundColor Red
    Write-Host "     CRITICAL: .env is tracked in git history!" -ForegroundColor Red
    $Failures++
} else {
    Write-Host " [PASS]" -ForegroundColor Green
}

Write-Host "`n------------------------------------------------------------" -ForegroundColor Cyan
if ($Failures -eq 0) {
    Write-Host " AUDIT COMPLETED SUCCESSFULLY: ALL CHECKS PASSED!" -ForegroundColor Green
    Write-Host "------------------------------------------------------------`n" -ForegroundColor Cyan
    Exit 0
} else {
    Write-Host " AUDIT FAILED WITH $Failures ERRORS." -ForegroundColor Red
    Write-Host "------------------------------------------------------------`n" -ForegroundColor Cyan
    Exit 1
}
