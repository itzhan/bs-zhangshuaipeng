#!/bin/bash
# 流浪宠物救助系统 — 一键停止脚本

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$BASE_DIR/logs"

echo "=========================================="
echo "🛑 流浪宠物救助系统 — 停止中..."
echo "=========================================="

for svc in backend admin frontend; do
  PID_FILE="$LOG_DIR/$svc.pid"
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if kill -0 "$PID" 2>/dev/null; then
      kill "$PID" 2>/dev/null
      sleep 1
      # force kill if still running
      kill -0 "$PID" 2>/dev/null && kill -9 "$PID" 2>/dev/null
      echo "  ✅ $svc 已停止 (PID: $PID)"
    else
      echo "  ⚠️  $svc 进程不存在 (PID: $PID)"
    fi
    rm -f "$PID_FILE"
  else
    echo "  ⚠️  $svc PID 文件未找到"
  fi
done

# kill any remaining dev server processes on known ports
for port in 8080 9527 3000; do
  PID=$(lsof -ti:$port 2>/dev/null)
  if [ -n "$PID" ]; then
    kill "$PID" 2>/dev/null
    echo "  🔶 端口 $port 上的进程 (PID: $PID) 已终止"
  fi
done

echo ""
echo "✅ 全部服务已停止"
echo ""
