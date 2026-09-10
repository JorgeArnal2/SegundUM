$ErrorActionPreference = 'Stop'

$webDir = Join-Path $PSScriptRoot '..\DAWeb'
$logDir = Join-Path $PSScriptRoot 'logs'
New-Item -ItemType Directory -Path $logDir -Force | Out-Null
$logFile = Join-Path $logDir 'frontend.log'

if (Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "El frontend ya esta en marcha en http://localhost:5173"
    exit 0
}

Start-Process -FilePath 'npm.cmd' -ArgumentList 'run', 'dev' `
    -WorkingDirectory $webDir `
    -RedirectStandardOutput $logFile `
    -RedirectStandardError "$logFile.err" `
    -WindowStyle Hidden

for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 1
    if (Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue) {
        Write-Host "Frontend levantado en http://localhost:5173"
        Write-Host "Logs: $logFile"
        exit 0
    }
}

Write-Error "El frontend no arranco. Revisa $logFile"
exit 1
