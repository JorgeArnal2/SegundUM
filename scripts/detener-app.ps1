$ErrorActionPreference = 'Continue'

& (Join-Path $PSScriptRoot 'frontend-down.ps1')
& (Join-Path $PSScriptRoot 'backend-down.ps1')

Write-Host ''
Write-Host 'Aplicacion detenida (backend y frontend).'
