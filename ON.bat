@echo off
title SegundUM - ENCENDER
cd /d "%~dp0"
echo ==========================================
echo  SegundUM - Arrancando backend y frontend
echo ==========================================
powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\iniciar-app.ps1" -Rebuild
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Algo fallo al iniciar la aplicacion.
    pause
    exit /b %errorlevel%
)
echo.
echo Aplicacion en marcha. Backend: http://localhost:9000 - Frontend: http://localhost:5173
pause