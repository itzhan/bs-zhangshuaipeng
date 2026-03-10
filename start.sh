#!/bin/bash
# ============================================================
# 流浪宠物救助系统 — 一键启动脚本 (Docker 版)
# ============================================================

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║       🐾  流浪宠物救助系统 — Docker 启动中...       ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"

# ============================================================
# 0. 检查 Docker 环境
# ============================================================
echo ""
echo -e "${CYAN}▶ [0/3] 检查 Docker 环境...${NC}"

if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ 未检测到 Docker，请先安装 Docker Desktop${NC}"
    echo -e "   下载地址: https://www.docker.com/products/docker-desktop"
    exit 1
fi

if ! docker info &> /dev/null 2>&1; then
    echo -e "${RED}❌ Docker 未启动，请先打开 Docker Desktop${NC}"
    exit 1
fi

echo -e "  ${GREEN}✅ Docker 已就绪${NC}"

# ============================================================
# 1. 构建镜像
# ============================================================
echo ""
echo -e "${CYAN}▶ [1/3] 构建 Docker 镜像 (首次约5-10分钟)...${NC}"

docker compose build 2>&1

echo -e "  ${GREEN}✅ 镜像构建完成${NC}"

# ============================================================
# 2. 启动所有容器
# ============================================================
echo ""
echo -e "${CYAN}▶ [2/3] 启动所有服务容器...${NC}"

docker compose up -d 2>&1

echo -e "  ${GREEN}✅ 容器已启动${NC}"

# ============================================================
# 3. 等待后端就绪
# ============================================================
echo ""
echo -e "${CYAN}▶ [3/3] 等待后端服务就绪...${NC}"
for i in $(seq 1 30); do
    if curl -sf http://localhost:8080/api/auth/info > /dev/null 2>&1; then
        echo -e "  ${GREEN}✅ 后端已就绪${NC}"
        break
    fi
    if [ "$i" -eq 30 ]; then
        echo -e "  ${YELLOW}⚠️  后端可能仍在启动，请稍等片刻...${NC}"
    fi
    printf "  ⏳ 等待中... (%d/30)\r" "$i"
    sleep 2
done

# ============================================================
# 启动完成 — 信息汇总
# ============================================================
echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║          🎉 全部服务启动完成！                      ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"

echo ""
echo -e "${BOLD}📝 访问地址:${NC}"
echo -e "  用户端:    ${CYAN}http://localhost${NC}"
echo -e "  管理端:    ${CYAN}http://localhost/admin${NC}"
echo -e "  后端 API:  ${CYAN}http://localhost/api${NC}"

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
echo -e "${BOLD}� Docker 常用命令:${NC}"
echo -e "  查看日志:  ${CYAN}docker compose logs -f${NC}"
echo -e "  停止服务:  ${CYAN}docker compose down${NC}"
echo -e "  重新部署:  ${CYAN}docker compose up -d --build${NC}"
echo -e "  清除数据:  ${CYAN}docker compose down -v${NC}"
echo ""

echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}📺 以下为实时日志输出 (Ctrl+C 退出日志，服务不会停止)${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo ""

docker compose logs -f
