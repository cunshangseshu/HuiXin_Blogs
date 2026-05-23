@echo off
title Huixin Blog - Startup
setlocal

cd /d "%~dp0"
set ROOT=%cd%
echo.
echo   ==========================================
echo     Huixin Blog - Starting...
echo   ==========================================
echo.
echo   [Frontend] Hot reload (HMR) - no restart needed
echo   [Backend]  Cold start - restart on code change
echo.

REM ============ Compile ============
echo [1/3] Compiling backend modules...
call mvn compile -q
if %errorlevel% neq 0 (
    echo [FAIL] Compile failed. Check your code.
    pause
    exit /b 1
)
echo [ OK ] Compile passed

REM ============ Frontend ============
echo.
echo [2/3] Starting Frontend (port 3000)...
if not exist "%ROOT%\huixin-blog-web\node_modules" (
    echo First run - installing dependencies...
    cd /d "%ROOT%\huixin-blog-web"
    call npm install
)
cd /d "%ROOT%\huixin-blog-web"
start "Huixin-Frontend-3000" cmd /c "title Huixin Frontend :3000 && npm run dev"

REM ============ Backend ============
echo.
echo [3/3] Starting Backend Services...

cd /d "%ROOT%"
start "Huixin-Gateway-8080"   cmd /c "title Huixin Gateway :8080   && mvn spring-boot:run -pl huixin-gateway -q"
timeout /t 8 >nul

start "Huixin-Auth-8081"      cmd /c "title Huixin Auth :8081      && mvn spring-boot:run -pl huixin-auth -q"
timeout /t 5 >nul

start "Huixin-User-8082"      cmd /c "title Huixin User :8082      && mvn spring-boot:run -pl huixin-user -q"
timeout /t 3 >nul

start "Huixin-Article-8083"   cmd /c "title Huixin Article :8083   && mvn spring-boot:run -pl huixin-article -q"
timeout /t 3 >nul

start "Huixin-Comment-8084"   cmd /c "title Huixin Comment :8084   && mvn spring-boot:run -pl huixin-comment -q"
timeout /t 3 >nul

start "Huixin-Search-8085"    cmd /c "title Huixin Search :8085    && mvn spring-boot:run -pl huixin-search -q"
timeout /t 3 >nul

start "Huixin-Stats-8086"     cmd /c "title Huixin Stats :8086     && mvn spring-boot:run -pl huixin-stats -q"

echo.
echo   ==========================================
echo     All services launched!
echo.
echo     Frontend : http://localhost:3000
echo     Gateway  : http://localhost:8080
echo     API Docs : http://localhost:8081/doc.html
echo.
echo     Close any CMD window to stop that service.
echo   ==========================================
echo.
pause
