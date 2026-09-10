@echo off
title SegundUM - APAGAR
cd /d "%~dp0"
echo ==========================================
echo  SegundUM - Deteniendo backend y frontend
echo ==========================================
powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\detener-app.ps1"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Algo fallo al detener la aplicacion.
    pause
    exit /b %errorlevel%
)
pause