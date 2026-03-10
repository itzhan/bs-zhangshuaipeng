#!/bin/bash
# ============================================================
# 🐾 流浪宠物救助系统 - Docker 一键部署脚本
# ============================================================

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'
BOLD='\033[1m'

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║       🐾  流浪宠物救助系统 — Docker 部署            ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"
echo ""

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ 未检测到 Docker，请先安装 Docker Desktop${NC}"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Docker 未启动，请先启动 Docker Desktop${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker 已就绪${NC}"

# 构建并启动
echo ""
echo -e "${YELLOW}▶ 构建镜像并启动容器 (首次可能需要 5-10 分钟)...${NC}"
echo ""

docker compose up -d --build 2>&1

echo ""
echo -e "${GREEN}✅ 所有服务已启动！${NC}"
echo ""

# 等待后端就绪
echo -e "${YELLOW}⏳ 等待后端启动...${NC}"
for i in {1..30}; do
    if curl -sf http://localhost:8080/api/auth/info > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 后端已就绪${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${YELLOW}⚠️  后端可能还在启动中，请稍等...${NC}"
    fi
    sleep 2
done

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║               🎉 部署完成！                        ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
echo -e "${GREEN}║                                                      ║${NC}"
echo -e "${GREEN}║  ${BOLD}用户端:${NC}${GREEN}  http://localhost                           ║${NC}"
echo -e "${GREEN}║  ${BOLD}管理端:${NC}${GREEN}  http://localhost/admin                     ║${NC}"
echo -e "${GREEN}║  ${BOLD}后端API:${NC}${GREEN} http://localhost/api                       ║${NC}"
echo -e "${GREEN}║                                                      ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
echo -e "${GREEN}║  ${BOLD}管理员账号:${NC}${GREEN} admin / 123456                          ║${NC}"
echo -e "${GREEN}║  ${BOLD}普通用户:${NC}${GREEN}  user01 / 123456                          ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
echo -e "${GREEN}║                                                      ║${NC}"
echo -e "${GREEN}║  停止: docker compose down                           ║${NC}"
echo -e "${GREEN}║  日志: docker compose logs -f                        ║${NC}"
echo -e "${GREEN}║  重建: docker compose up -d --build                  ║${NC}"
echo -e "${GREEN}║                                                      ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"
echo ""
