@echo off
chcp 65001 >nul
echo 🐾 正在停止所有容器...
docker compose down
echo ✅ 所有服务已停止
pause
