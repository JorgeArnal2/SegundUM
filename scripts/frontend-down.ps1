$ErrorActionPreference = 'Stop'

$listener = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue
if (-not $listener) {
    Write-Host 'El frontend ya esta detenido.'
    exit 0
}

$procIds = $listener.OwningProcess | Sort-Object -Unique
foreach ($procId in $procIds) {
    Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
}

Start-Sleep -Seconds 1

if (Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue) {
    Write-Error 'No se pudo detener el proceso que escucha en el puerto 5173.'
    exit 1
}

Write-Host 'Frontend detenido.'
