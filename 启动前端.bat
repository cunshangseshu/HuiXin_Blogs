@echo off
title Huixin Blog - Frontend
cd /d "%~dp0huixin-blog-web"

echo.
echo   ==========================================
echo     Huixin Blog - Frontend (port 3000)
echo     Hot reload: edit code freely
echo   ==========================================
echo.

if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
)

echo Starting Vite dev server...
npm run dev
pause
