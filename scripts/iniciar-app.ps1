param(
    [switch]$Rebuild
)

$ErrorActionPreference = 'Stop'

if (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue) {
    $mysqlServices = Get-Service -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -like '*mysql*' -and $_.Status -eq 'Running' }
    if ($mysqlServices) {
        try {
            $mysqlServices | ForEach-Object { Stop-Service -Name $_.Name -Force -ErrorAction Stop }
            Write-Host ('MySQL local detenido: ' + (($mysqlServices.Name | Sort-Object) -join ', '))
        } catch {
            Write-Warning 'El puerto 3306 esta ocupado por un servicio MySQL local.'
            Write-Host 'Ejecuta primero en PowerShell como administrador:'
            Write-Host '  Stop-Service MySQL, MySQL80'
            exit 1
        }
        Start-Sleep -Seconds 2
        if (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue) {
            Write-Error 'El puerto 3306 sigue ocupado tras detener el servicio MySQL.'
            exit 1
        }
    } else {
        Write-Error 'El puerto 3306 esta ocupado por un proceso que no es un servicio MySQL. No se puede levantar el backend.'
        exit 1
    }
}

if ($Rebuild) {
    & (Join-Path $PSScriptRoot 'backend-up.ps1') -Rebuild
} else {
    & (Join-Path $PSScriptRoot 'backend-up.ps1')
}
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& (Join-Path $PSScriptRoot 'frontend-up.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ''
Write-Host 'Aplicacion completa levantada:'
Write-Host '  Backend (pasarela): http://localhost:9000'
Write-Host '  Frontend (web):     http://localhost:5173'
