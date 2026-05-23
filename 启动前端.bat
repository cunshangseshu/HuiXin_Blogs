@echo off
chcp 65001 >nul
title 慧芯博客 - 前端启动

cd /d "%~dp0huixin-blog-web"

echo.
echo   ╔══════════════════════════════════════╗
echo   ║     ✨ 慧芯博客 前端启动中...       ║
echo   ║     访问地址: http://localhost:3000  ║
echo   ╚══════════════════════════════════════╝
echo.

if not exist "node_modules\" (
    echo [1/2] 首次运行，安装依赖...
    call npm install
)

echo [2/2] 启动 Vite 开发服务器...
call npm run dev

pause
