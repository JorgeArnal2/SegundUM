param(
    [switch]$Rebuild
)

$ErrorActionPreference = 'Stop'

$composeFile = Join-Path $PSScriptRoot '..\ArSo\docker-compose.yml'

if (-not (Test-Path -LiteralPath $composeFile)) {
    Write-Error "No se encuentra docker-compose.yml en $composeFile"
    exit 1
}

if ($Rebuild) {
    Write-Host "Reconstruyendo imagenes y levantando backend..."
    docker compose -f $composeFile up -d --build
} else {
    Write-Host "Levantando backend (docker compose up -d)..."
    docker compose -f $composeFile up -d
}
if ($LASTEXITCODE -ne 0) {
    Write-Error "docker compose fallo con codigo $LASTEXITCODE"
    exit $LASTEXITCODE
}

Start-Sleep -Seconds 5
docker compose -f $composeFile ps
Write-Host ""
Write-Host "Backend levantado. Punto de acceso: http://localhost:9000"
Write-Host "Usa -Rebuild solo si cambiaste codigo en ArSo."
