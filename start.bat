@echo off
chcp 65001 >nul
REM ============================================================
REM 流浪宠物救助系统 - Windows 一键启动脚本 (Docker 版)
REM ============================================================

echo.
echo ╔══════════════════════════════════════════════════════╗
echo ║       🐾  流浪宠物救助系统 — Docker 启动中...       ║
echo ╚══════════════════════════════════════════════════════╝
echo.

REM ---- 检查 Docker ----
echo [0/3] 检查 Docker 环境...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker 未启动或未安装，请先安装并启动 Docker Desktop
    echo    下载地址: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo ✅ Docker 已就绪
echo.

REM ---- 构建镜像 ----
echo [1/3] 构建 Docker 镜像 (首次约5-10分钟)...
docker compose build
if %errorlevel% neq 0 (
    echo ❌ 镜像构建失败
    pause
    exit /b 1
)
echo ✅ 镜像构建完成
echo.

REM ---- 启动容器 ----
echo [2/3] 启动所有服务容器...
docker compose up -d
echo ✅ 容器已启动
echo.

REM ---- 等待后端 ----
echo [3/3] 等待后端服务就绪 (约30秒)...
set /a count=0
:waitloop
set /a count+=1
if %count% gtr 30 goto waitdone
curl -sf http://localhost:8080/api/auth/info >nul 2>&1
if %errorlevel% equ 0 goto waitdone
echo    ⏳ 等待中... (%count%/30)
timeout /t 2 /nobreak >nul
goto waitloop
:waitdone
echo ✅ 后端已就绪
echo.

REM ---- 完成信息 ----
echo ╔══════════════════════════════════════════════════════╗
echo ║          🎉 全部服务启动完成！                      ║
echo ╠══════════════════════════════════════════════════════╣
echo ║                                                      ║
echo ║  用户端:    http://localhost                          ║
echo ║  管理端:    http://localhost/admin                    ║
echo ║  后端 API:  http://localhost/api                      ║
echo ║                                                      ║
echo ╠══════════════════════════════════════════════════════╣
echo ║  管理员:   admin   / 123456                          ║
echo ║  工作人员: staff01 / 123456                          ║
echo ║  普通用户: user01  / 123456                          ║
echo ╠══════════════════════════════════════════════════════╣
echo ║                                                      ║
echo ║  查看日志: docker compose logs -f                    ║
echo ║  停止服务: docker compose down                       ║
echo ║  重新部署: docker compose up -d --build              ║
echo ║                                                      ║
echo ╚══════════════════════════════════════════════════════╝
echo.

echo 📺 以下为实时日志输出 (Ctrl+C 退出日志，服务不会停止)
echo.
docker compose logs -f
