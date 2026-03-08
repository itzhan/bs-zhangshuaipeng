-- ============================================================
-- 流浪宠物救助系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS pet_rescue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_rescue;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- -----------------------------------------------------------
-- 1. 用户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/STAFF/USER',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_role` (`role`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 宠物表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '宠物名称',
    `species` VARCHAR(20) NOT NULL COMMENT '物种：猫/狗/兔/鸟/其他',
    `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
    `age` VARCHAR(30) DEFAULT NULL COMMENT '年龄描述',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1公 2母',
    `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    `color` VARCHAR(30) DEFAULT NULL COMMENT '毛色',
    `health_status` VARCHAR(200) DEFAULT NULL COMMENT '健康状况',
    `is_sterilized` TINYINT DEFAULT 0 COMMENT '是否已绝育：0否 1是',
    `is_vaccinated` TINYINT DEFAULT 0 COMMENT '是否已接种疫苗：0否 1是',
    `description` TEXT COMMENT '详细描述',
    `photo` VARCHAR(500) DEFAULT NULL COMMENT '主图URL',
    `photos` VARCHAR(2000) DEFAULT NULL COMMENT '图片列表(JSON数组)',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '发现/所在地点',
    `rescue_date` DATE DEFAULT NULL COMMENT '救助日期',
    `station_id` BIGINT DEFAULT NULL COMMENT '所在救助站ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待救助 1救助中 2待领养 3已领养 4已归还',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_species` (`species`),
    INDEX `idx_status` (`status`),
    INDEX `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物表';

-- -----------------------------------------------------------
-- 3. 救助记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rescue_record`;
CREATE TABLE `rescue_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `pet_id` BIGINT DEFAULT NULL COMMENT '关联宠物ID（救助完成后关联）',
    `reporter_id` BIGINT NOT NULL COMMENT '报告人ID',
    `rescuer_id` BIGINT DEFAULT NULL COMMENT '救助人ID',
    `location` VARCHAR(200) NOT NULL COMMENT '发现地点',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `description` TEXT COMMENT '情况描述',
    `photo` VARCHAR(500) DEFAULT NULL COMMENT '现场照片',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已报告 1已出动 2救助中 3已完成',
    `rescue_date` DATETIME DEFAULT NULL COMMENT '出动时间',
    `finish_date` DATETIME DEFAULT NULL COMMENT '完成时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_pet_id` (`pet_id`),
    INDEX `idx_reporter_id` (`reporter_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助记录表';

-- -----------------------------------------------------------
-- 4. 领养申请表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `adoption_application`;
CREATE TABLE `adoption_application` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `user_id` BIGINT NOT NULL COMMENT '申请人ID',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '申请人真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '居住地址',
    `reason` TEXT COMMENT '领养理由',
    `experience` TEXT COMMENT '养宠经验',
    `living_condition` VARCHAR(200) DEFAULT NULL COMMENT '居住条件',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝 3已取消',
    `review_note` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_pet_id` (`pet_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养申请表';

-- -----------------------------------------------------------
-- 5. 失宠招领表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `lost_found`;
CREATE TABLE `lost_found` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '发布人ID',
    `type` TINYINT NOT NULL COMMENT '类型：1寻宠 2招领',
    `pet_name` VARCHAR(50) DEFAULT NULL COMMENT '宠物名称',
    `species` VARCHAR(20) DEFAULT NULL COMMENT '物种',
    `color` VARCHAR(30) DEFAULT NULL COMMENT '毛色特征',
    `description` TEXT COMMENT '详细描述',
    `photo` VARCHAR(500) DEFAULT NULL COMMENT '照片URL',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '丢失/发现地点',
    `lost_date` DATE DEFAULT NULL COMMENT '丢失/发现日期',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0进行中 1已找到 2已关闭',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='失宠招领表';

-- -----------------------------------------------------------
-- 6. 救助站表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rescue_station`;
CREATE TABLE `rescue_station` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '救助站名称',
    `address` VARCHAR(200) NOT NULL COMMENT '地址',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `capacity` INT NOT NULL DEFAULT 0 COMMENT '最大容量',
    `current_count` INT NOT NULL DEFAULT 0 COMMENT '当前数量',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `manager` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `description` TEXT COMMENT '简介',
    `photo` VARCHAR(500) DEFAULT NULL COMMENT '照片',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0已关闭 1运营中',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助站表';

-- -----------------------------------------------------------
-- 7. 物资表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `supply`;
CREATE TABLE `supply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '物资名称',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：食物/药品/用品/其他',
    `quantity` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `station_id` BIGINT NOT NULL COMMENT '所属救助站ID',
    `threshold` INT DEFAULT 10 COMMENT '预警阈值',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_station_id` (`station_id`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物资表';

-- -----------------------------------------------------------
-- 8. 志愿者排班表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `volunteer_schedule`;
CREATE TABLE `volunteer_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '志愿者用户ID',
    `station_id` BIGINT NOT NULL COMMENT '救助站ID',
    `schedule_date` DATE NOT NULL COMMENT '排班日期',
    `shift` VARCHAR(10) NOT NULL COMMENT '班次：早班/中班/晚班',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已排班 1已签到 2已完成 3缺席',
    `remark` VARCHAR(200) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_station_id` (`station_id`),
    INDEX `idx_date` (`schedule_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者排班表';

-- -----------------------------------------------------------
-- 9. 活动表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
    `description` TEXT COMMENT '活动描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `max_participants` INT DEFAULT 0 COMMENT '最大参与人数（0不限）',
    `current_participants` INT NOT NULL DEFAULT 0 COMMENT '当前报名人数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0未开始 1报名中 2进行中 3已结束 4已取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- -----------------------------------------------------------
-- 10. 活动报名表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已报名 1已签到 2已取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- -----------------------------------------------------------
-- 11. 评论表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：PET/ACTIVITY/LOST_FOUND',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `user_id` BIGINT NOT NULL COMMENT '评论人ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- -----------------------------------------------------------
-- 12. 收藏表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_pet` (`user_id`, `pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- -----------------------------------------------------------
-- 13. 通知表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(30) DEFAULT 'SYSTEM' COMMENT '类型：SYSTEM/ADOPTION/ACTIVITY/RESCUE',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- -----------------------------------------------------------
-- 14. 数据字典表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(50) NOT NULL COMMENT '字典类型',
    `label` VARCHAR(100) NOT NULL COMMENT '显示标签',
    `value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `remark` VARCHAR(200) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';
