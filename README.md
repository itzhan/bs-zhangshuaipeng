# 基于 Spring Boot 的流浪宠物救助系统

## 项目概述

本系统旨在为流浪动物救助提供全方位的信息化管理平台，包含后台管理端和用户端两个前端应用，配合 Spring Boot 后端 API 服务。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus + JWT + MySQL |
| 管理端 | soybean-admin (Vue 3 + Naive UI + Pinia) |
| 用户端 | Vue 3 + Naive UI + Pinia + Vue Router |
| 数据库 | MySQL 8.0 |

## 项目结构

```
张帅鹏/
├── backend/          # Spring Boot 后端
│   ├── src/main/java/com/rescue/petrescue/
│   │   ├── controller/   # 6 个控制器
│   │   ├── service/      # 6 个服务类
│   │   ├── mapper/       # 14 个 Mapper 接口
│   │   ├── entity/       # 14 个实体类
│   │   ├── config/       # Security/MyBatis/Web 配置
│   │   ├── common/       # 统一响应/异常处理
│   │   └── util/         # JWT/文件上传工具
│   └── pom.xml
├── admin/            # soybean-admin 管理端
│   ├── src/views/        # 9 个管理页面
│   ├── src/service/api/  # API 服务
│   └── .env              # 环境配置
├── frontend/         # Vue 3 用户端
│   ├── src/views/        # 10 个用户页面
│   ├── src/api/          # Axios API 封装
│   ├── src/store/        # Pinia 状态管理
│   └── src/router/       # 路由配置
├── sql/              # 数据库脚本
│   ├── init.sql          # 14 张表 DDL
│   └── data.sql          # 测试数据
├── start.sh          # 一键启动
└── stop.sh           # 一键停止
```

## 环境要求

- **JDK** 17+
- **Maven** 3.8+
- **Node.js** 18+ & npm
- **pnpm** (管理端)
- **MySQL** 8.0

## 部署步骤

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE pet_rescue DEFAULT CHARSET utf8mb4;"

# 导入表结构和测试数据
mysql -u root -p pet_rescue < sql/init.sql
mysql -u root -p pet_rescue < sql/data.sql
```

### 2. 修改后端配置

编辑 `backend/src/main/resources/application.yml`，配置数据库连接信息。

### 3. 一键启动

```bash
bash start.sh
```

### 4. 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:8080/api | RESTful API |
| 管理端 | http://localhost:9527 | 后台管理界面 |
| 用户端 | http://localhost:3000 | 用户前端界面 |

### 5. 停止服务

```bash
bash stop.sh
```

## 功能模块

### 管理端（9 个功能页面）
- 🐾 **宠物管理** — CRUD + 状态流转
- 🚑 **救助记录** — 创建 + 状态追踪（待处理→处理中→已完成）
- 💛 **领养审核** — 申请列表 + 通过/拒绝审核
- 🏥 **救助站管理** — CRUD + 容量使用率
- 📦 **物资管理** — CRUD + 库存预警
- 📅 **活动管理** — CRUD + 报名统计
- 👤 **用户管理** — 列表 + 角色 + 状态切换
- 💬 **评论管理** — 列表 + 删除
- 📖 **数据字典** — 类型/标签/值管理

### 用户端（10 个页面）
- 🏠 **首页** — 英雄区域 + 数据统计 + 推荐宠物 + 近期活动
- 🐾 **宠物列表** — 多条件搜索 + 卡片网格 + 分页
- 📋 **宠物详情** — 大图 + 元数据 + 领养申请弹窗 + 收藏
- 🆘 **求助中心** — 救助信息提交 + 历史记录
- 🔍 **寻宠招领** — 寻宠/招领信息发布和浏览
- 💝 **爱心活动** — 活动列表 + 在线报名
- 🏥 **救助站** — 站点信息 + 容量进度条
- 👤 **个人中心** — 信息/领养记录/收藏/通知 Tab
- 🔐 **登录/注册** — Token 认证

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user1 | 123456 |

> 具体账号信息以 `sql/data.sql` 中的测试数据为准。
