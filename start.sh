#!/bin/bash
# ============================================================
# 流浪宠物救助系统 — 一键启动脚本
# 启动后端(Spring Boot) + 管理端(soybean-admin) + 用户端(Vue 3)
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$LOG_DIR"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'

# ============================================================
# 端口清理函数：若端口被占用则杀掉占用进程
# ============================================================
kill_port() {
  local port=$1
  local name=$2
  local pids=$(lsof -ti:$port 2>/dev/null)
  if [ -n "$pids" ]; then
    echo -e "  ${YELLOW}⚠️  端口 $port ($name) 被占用，正在清理...${NC}"
    for pid in $pids; do
      kill -9 "$pid" 2>/dev/null
      echo -e "  ${RED}   ✖ 已杀掉进程 PID: $pid${NC}"
    done
    sleep 1
  fi
}

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║       🐾  流浪宠物救助系统 — 启动中...              ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"

# ============================================================
# 0. 清理被占用的端口
# ============================================================
echo ""
echo -e "${CYAN}▶ [0/3] 检查并清理端口...${NC}"
kill_port 8080 "后端"
kill_port 9527 "管理端"
kill_port 3000 "用户端"
echo -e "  ${GREEN}✅ 端口检查完成${NC}"

# ============================================================
# 1. 启动后端
# ============================================================
echo ""
echo -e "${CYAN}▶ [1/3] 启动后端服务 (Spring Boot :8080)...${NC}"
cd "$BASE_DIR/backend"
if [ -f "target/pet-rescue-1.0.0.jar" ]; then
  nohup java -jar target/pet-rescue-1.0.0.jar > "$LOG_DIR/backend.log" 2>&1 &
  echo $! > "$LOG_DIR/backend.pid"
  echo -e "  ${GREEN}✅ 后端已启动${NC} (PID: $(cat $LOG_DIR/backend.pid))"
else
  echo -e "  ${YELLOW}⚠️  JAR 未找到，正在编译打包...${NC}"
  mvn clean package -DskipTests > "$LOG_DIR/backend-build.log" 2>&1
  if [ $? -eq 0 ]; then
    nohup java -jar target/pet-rescue-1.0.0.jar > "$LOG_DIR/backend.log" 2>&1 &
    echo $! > "$LOG_DIR/backend.pid"
    echo -e "  ${GREEN}✅ 后端编译并启动成功${NC} (PID: $(cat $LOG_DIR/backend.pid))"
  else
    echo -e "  ${RED}❌ 后端编译失败${NC}，请查看 $LOG_DIR/backend-build.log"
  fi
fi

echo -e "  ${YELLOW}⏳ 等待后端启动 (约10秒)...${NC}"
sleep 10

# ============================================================
# 2. 启动管理端
# ============================================================
echo ""
echo -e "${CYAN}▶ [2/3] 启动管理端 (soybean-admin :9527)...${NC}"
cd "$BASE_DIR/admin"
if [ ! -d "node_modules" ]; then
  echo -e "  ${YELLOW}📦 首次运行，安装依赖...${NC}"
  pnpm install > "$LOG_DIR/admin-install.log" 2>&1
fi
nohup pnpm dev > "$LOG_DIR/admin.log" 2>&1 &
echo $! > "$LOG_DIR/admin.pid"
echo -e "  ${GREEN}✅ 管理端已启动${NC} (PID: $(cat $LOG_DIR/admin.pid))"

# ============================================================
# 3. 启动用户端
# ============================================================
echo ""
echo -e "${CYAN}▶ [3/3] 启动用户端 (Vue 3 :3000)...${NC}"
cd "$BASE_DIR/frontend"
if [ ! -d "node_modules" ]; then
  echo -e "  ${YELLOW}📦 首次运行，安装依赖...${NC}"
  npm install > "$LOG_DIR/frontend-install.log" 2>&1
fi
nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
echo $! > "$LOG_DIR/frontend.pid"
echo -e "  ${GREEN}✅ 用户端已启动${NC} (PID: $(cat $LOG_DIR/frontend.pid))"

# ============================================================
# 启动完成 — 信息汇总
# ============================================================
echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║          🎉 全部服务启动完成！                      ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"

echo ""
echo -e "${BOLD}📝 服务地址:${NC}"
echo -e "  后端 API:  ${CYAN}http://localhost:8080/api${NC}"
echo -e "  管理端:    ${CYAN}http://localhost:9527${NC}"
echo -e "  用户端:    ${CYAN}http://localhost:3000${NC}"

echo ""
echo -e "${BOLD}🔑 测试账号 (密码统一为: ${YELLOW}123456${NC}${BOLD}):${NC}"
echo -e "  ┌──────────┬──────────┬──────────┬────────────┐"
echo -e "  │ ${BOLD}角色${NC}     │ ${BOLD}用户名${NC}   │ ${BOLD}密码${NC}     │ ${BOLD}姓名${NC}       │"
echo -e "  ├──────────┼──────────┼──────────┼────────────┤"
echo -e "  │ ${RED}管理员${NC}   │ admin    │ 123456   │ 系统管理员 │"
echo -e "  ├──────────┼──────────┼──────────┼────────────┤"
echo -e "  │ ${YELLOW}工作人员${NC} │ staff01  │ 123456   │ 王志远     │"
echo -e "  │ ${YELLOW}工作人员${NC} │ staff02  │ 123456   │ 李秀芳     │"
echo -e "  │ ${YELLOW}工作人员${NC} │ staff03  │ 123456   │ 赵建国     │"
echo -e "  ├──────────┼──────────┼──────────┼────────────┤"
echo -e "  │ ${GREEN}普通用户${NC} │ user01   │ 123456   │ 张小明     │"
echo -e "  │ ${GREEN}普通用户${NC} │ user02   │ 123456   │ 刘文静     │"
echo -e "  │ ${GREEN}普通用户${NC} │ user03   │ 123456   │ 陈浩然     │"
echo -e "  │ ${GREEN}普通用户${NC} │ user04   │ 123456   │ 王丽华     │"
echo -e "  │ ${GREEN}普通用户${NC} │ user05   │ 123456   │ 孙伟       │"
echo -e "  └──────────┴──────────┴──────────┴────────────┘"

echo ""
echo -e "${BOLD}📄 日志目录:${NC}   $LOG_DIR/"
echo -e "${BOLD}🛑 停止服务:${NC}   bash stop.sh"
echo ""
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}📺 以下为实时日志输出 (Ctrl+C 退出日志查看，服务不会停止)${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo ""

# 实时显示所有日志
tail -f "$LOG_DIR/backend.log" "$LOG_DIR/admin.log" "$LOG_DIR/frontend.log" 2>/dev/null
