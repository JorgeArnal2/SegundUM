$ErrorActionPreference = 'Stop'

$composeFile = Join-Path $PSScriptRoot '..\ArSo\docker-compose.yml'

if (-not (Test-Path -LiteralPath $composeFile)) {
    Write-Error "No se encuentra docker-compose.yml en $composeFile"
    exit 1
}

Write-Host "Parando backend (docker compose down)..."
docker compose -f $composeFile down
if ($LASTEXITCODE -ne 0) {
    Write-Error "docker compose fallo con codigo $LASTEXITCODE"
    exit $LASTEXITCODE
}

Write-Host "Backend detenido. Los datos se conservan (volumenes intactos)."
