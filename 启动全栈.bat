@echo off
chcp 65001 >nul
title 慧芯博客 - 全栈启动

cd /d "%~dp0"

echo.
echo   ╔══════════════════════════════════════════════════╗
echo   ║       ✨ 慧芯博客 Huixin Blog                    ║
echo   ║       全栈一键启动                                ║
echo   ╚══════════════════════════════════════════════════╝
echo.
echo   🔥 前端（热启动 HMR）：改代码自动刷新，开一次管一天
echo   ❄️  后端（冷启动）：改代码需要手动重启对应模块
echo.

REM ==================== 编译 ====================
echo [1/3] 编译后端模块...
call mvn compile -q
if %errorlevel% neq 0 (
    echo ❌ 编译失败，请检查代码
    pause
    exit /b 1
)
echo ✅ 编译通过

REM ==================== 前端 ====================
echo.
echo [2/3] 启动前端（端口 3000）...
if not exist "huixin-blog-web\node_modules\" (
    echo 首次运行，安装前端依赖...
    cd huixin-blog-web
    call npm install
    cd ..
)
start "慧芯博客-前端" cmd /c "cd /d %cd%\huixin-blog-web && title 慧芯博客-前端 :3000 && npm run dev"

REM ==================== 后端 ====================
echo.
echo [3/3] 启动后端微服务（每个服务独立窗口）...

REM 网关先启，业务模块后续自行启动
start "慧芯博客-网关 :8080" cmd /c "cd /d %cd% && title 慧芯博客-网关 :8080 && mvn spring-boot:run -pl huixin-gateway -q"
timeout /t 8 >nul

start "慧芯博客-认证 :8081" cmd /c "cd /d %cd% && title 慧芯博客-认证 :8081 && mvn spring-boot:run -pl huixin-auth -q"
timeout /t 5 >nul

start "慧芯博客-用户 :8082" cmd /c "cd /d %cd% && title 慧芯博客-用户 :8082 && mvn spring-boot:run -pl huixin-user -q"
timeout /t 3 >nul

start "慧芯博客-文章 :8083" cmd /c "cd /d %cd% && title 慧芯博客-文章 :8083 && mvn spring-boot:run -pl huixin-article -q"
timeout /t 3 >nul

start "慧芯博客-评论 :8084" cmd /c "cd /d %cd% && title 慧芯博客-评论 :8084 && mvn spring-boot:run -pl huixin-comment -q"
timeout /t 3 >nul

start "慧芯博客-搜索 :8085" cmd /c "cd /d %cd% && title 慧芯博客-搜索 :8085 && mvn spring-boot:run -pl huixin-search -q"
timeout /t 3 >nul

start "慧芯博客-统计 :8086" cmd /c "cd /d %cd% && title 慧芯博客-统计 :8086 && mvn spring-boot:run -pl huixin-stats -q"

echo.
echo   ╔══════════════════════════════════════════════════╗
echo   ║  启动完成！                                      ║
echo   ║                                                  ║
echo   ║  🌐 前端:  http://localhost:3000                  ║
echo   ║  🛡️  网关:  http://localhost:8080                  ║
echo   ║  📖 文档:  http://localhost:8081/doc.html          ║
echo   ║                                                  ║
echo   ║  🔥 前端热启动 — 改代码不用重启                   ║
echo   ║  ❄️  后端冷启动 — 改代码需手动重启对应窗口         ║
echo   ╚══════════════════════════════════════════════════╝
echo.
pause
