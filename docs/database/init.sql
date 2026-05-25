-- ============================================================
-- 慧芯博客（Huixin Blog）数据库设计
-- 数据库名称：huixin_blog
-- 字符集：utf8mb4
-- 引擎：InnoDB
-- 设计日期：2026年5月20日
--
-- 设计原则：
--   1. 字段名顾名思义，使用下划线命名法
--   2. 所有表包含 id、create_time、update_time
--   3. 支持逻辑删除的表包含 is_deleted 字段
--   4. 遵循第三范式，适当反范式优化查询性能
--   5. 外键在应用层维护，数据库层仅做索引
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `huixin_blog`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `huixin_blog`;

-- ============================================================
-- 1. 用户表 (user)
-- 存储所有用户的基本信息，包括普通用户和博主
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID，主键自增',
    `username`          VARCHAR(50)     NOT NULL                 COMMENT '用户名，用于登录和显示，唯一',
    `password`          VARCHAR(255)    NOT NULL                 COMMENT '密码，BCrypt加密存储',
    `email`             VARCHAR(100)    DEFAULT NULL             COMMENT '用户邮箱，用于找回密码等',
    `nickname`          VARCHAR(50)     DEFAULT NULL             COMMENT '用户昵称，用于页面显示',
    `avatar_url`        VARCHAR(500)    DEFAULT NULL             COMMENT '头像图片URL',
    `bio`               VARCHAR(500)    DEFAULT NULL             COMMENT '个人简介/个性签名',
    `role_type`         TINYINT         NOT NULL DEFAULT 0       COMMENT '角色类型：0-普通用户，1-博主',
    `status`            TINYINT         NOT NULL DEFAULT 1       COMMENT '账号状态：0-禁用，1-正常',
    `last_login_time`   DATETIME        DEFAULT NULL             COMMENT '最后登录时间',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role_type` (`role_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 博主申请记录表 (blogger_apply)
-- 普通用户申请成为博主的审核记录
-- ============================================================
DROP TABLE IF EXISTS `blogger_apply`;
CREATE TABLE `blogger_apply` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '申请ID，主键自增',
    `user_id`           BIGINT          NOT NULL                 COMMENT '申请人用户ID',
    `apply_reason`      VARCHAR(500)    NOT NULL                 COMMENT '申请理由/自我介绍',
    `apply_status`      TINYINT         NOT NULL DEFAULT 0       COMMENT '审核状态：0-待审核，1-通过，2-拒绝',
    `review_comment`    VARCHAR(500)    DEFAULT NULL             COMMENT '审核意见/拒绝原因',
    `review_time`       DATETIME        DEFAULT NULL             COMMENT '审核时间',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_apply_status` (`apply_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博主申请记录表';

-- ============================================================
-- 3. 文章分类表 (category)
-- 文章分类，如：技术分享、生活随笔、前端开发、后端开发等
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID，主键自增',
    `category_name`     VARCHAR(50)     NOT NULL                 COMMENT '分类名称',
    `category_desc`     VARCHAR(200)    DEFAULT NULL             COMMENT '分类描述',
    `sort_order`        INT             NOT NULL DEFAULT 0       COMMENT '排序序号，越小越靠前',
    `article_count`     INT             NOT NULL DEFAULT 0       COMMENT '该分类下文章数量（冗余，方便查询）',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ============================================================
-- 4. 标签表 (tag)
-- 文章标签，支持多标签关联
-- ============================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '标签ID，主键自增',
    `tag_name`          VARCHAR(50)     NOT NULL                 COMMENT '标签名称',
    `tag_color`         VARCHAR(7)      DEFAULT '#6B7280'        COMMENT '标签展示颜色，十六进制色值',
    `article_count`     INT             NOT NULL DEFAULT 0       COMMENT '该标签下文章数量（冗余，方便查询）',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================================
-- 5. 文章表 (article)
-- 博客文章的核心数据表
-- ============================================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '文章ID，主键自增',
    `title`             VARCHAR(200)    NOT NULL                 COMMENT '文章标题',
    `summary`           VARCHAR(500)    DEFAULT NULL             COMMENT '文章摘要，用于列表展示',
    `content`           LONGTEXT        NOT NULL                 COMMENT '文章正文，Markdown格式存储',
    `content_html`      LONGTEXT        DEFAULT NULL             COMMENT '文章正文，HTML渲染结果（冗余，减少前端渲染压力）',
    `cover_image_url`   VARCHAR(500)    DEFAULT NULL             COMMENT '封面图片URL',
    `author_id`         BIGINT          NOT NULL                 COMMENT '作者用户ID',
    `category_id`       BIGINT          NOT NULL                 COMMENT '所属分类ID',
    `article_status`    TINYINT         NOT NULL DEFAULT 0       COMMENT '文章状态：0-草稿，1-已发布',
    `view_count`        INT             NOT NULL DEFAULT 0       COMMENT '阅读量（从Redis定期同步）',
    `like_count`        INT             NOT NULL DEFAULT 0       COMMENT '点赞数（从Redis定期同步）',
    `comment_count`     INT             NOT NULL DEFAULT 0       COMMENT '评论数（冗余，发布/删除评论时更新）',
    `is_top`            TINYINT         NOT NULL DEFAULT 0       COMMENT '是否置顶：0-否，1-是',
    `is_original`       TINYINT         NOT NULL DEFAULT 1       COMMENT '是否原创：0-转载，1-原创',
    `publish_time`      DATETIME        DEFAULT NULL             COMMENT '发布时间（首次发布时设置）',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_article_status` (`article_status`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_view_count` (`view_count`),
    KEY `idx_is_top_publish` (`is_top`, `publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================================
-- 6. 文章-标签关联表 (article_tag)
-- 文章与标签的多对多关联关系
-- ============================================================
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '关联ID，主键自增',
    `article_id`        BIGINT          NOT NULL                 COMMENT '文章ID',
    `tag_id`            BIGINT          NOT NULL                 COMMENT '标签ID',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- ============================================================
-- 7. 评论表 (comment)
-- 文章评论，支持二级回复（在一级评论下回复）
-- ============================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '评论ID，主键自增',
    `article_id`        BIGINT          NOT NULL                 COMMENT '所属文章ID',
    `user_id`           BIGINT          NOT NULL                 COMMENT '评论者用户ID',
    `parent_id`         BIGINT          DEFAULT NULL             COMMENT '父评论ID，NULL表示一级评论，非NULL表示回复某条评论',
    `reply_to_user_id`  BIGINT          DEFAULT NULL             COMMENT '被回复的用户ID，仅二级评论使用',
    `content`           TEXT            NOT NULL                 COMMENT '评论内容',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================================
-- 8. 点赞记录表 (like_record)
-- 用户对文章的点赞记录，用于判断用户是否已点赞
-- ============================================================
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '点赞记录ID，主键自增',
    `user_id`           BIGINT          NOT NULL                 COMMENT '点赞用户ID',
    `article_id`        BIGINT          NOT NULL                 COMMENT '被点赞文章ID',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- ============================================================
-- 9. 文章统计表 (article_stats)
-- 文章的各项统计数据，与Redis配合使用
-- ============================================================
DROP TABLE IF EXISTS `article_stats`;
CREATE TABLE `article_stats` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '统计记录ID，主键自增',
    `article_id`        BIGINT          NOT NULL                 COMMENT '文章ID',
    `total_view_count`  INT             NOT NULL DEFAULT 0       COMMENT '累计阅读量',
    `total_like_count`  INT             NOT NULL DEFAULT 0       COMMENT '累计点赞数',
    `total_comment_count` INT           NOT NULL DEFAULT 0       COMMENT '累计评论数',
    `daily_view_count`  INT             NOT NULL DEFAULT 0       COMMENT '今日阅读量',
    `daily_view_date`   DATE            DEFAULT NULL             COMMENT '今日阅读量统计日期',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章统计表';

-- ============================================================
-- 10. 系统配置表 (system_config)
-- 存储系统级别的配置项，如博客名称、公告等
-- ============================================================
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '配置ID，主键自增',
    `config_key`        VARCHAR(100)    NOT NULL                 COMMENT '配置键名',
    `config_value`      TEXT            NOT NULL                 COMMENT '配置值',
    `config_desc`       VARCHAR(500)    DEFAULT NULL             COMMENT '配置项描述',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`) VALUES
('blog_name', '慧芯博客', '博客名称'),
('blog_description', '记录思考，分享知识——慧芯博客，你的精神家园', '博客描述'),
('blog_announcement', '欢迎来到慧芯博客！这里是一个自由创作与知识分享的平台。', '博客公告'),
('enable_register', 'true', '是否开放注册'),
('article_page_size', '10', '文章列表每页显示数量');

-- 初始化默认分类
INSERT INTO `category` (`category_name`, `category_desc`, `sort_order`) VALUES
('技术分享', '技术文章、编程经验、开发心得', 1),
('生活随笔', '生活中的点滴感悟与记录', 2),
('前端开发', 'HTML、CSS、JavaScript、Vue等前端技术', 3),
('后端开发', 'Java、Spring、数据库等后端技术', 4),
('人工智能', '机器学习、深度学习、AI应用', 5);

-- 初始化默认标签
INSERT INTO `tag` (`tag_name`, `tag_color`) VALUES
('Java', '#B07219'),
('Spring Boot', '#6DB33F'),
('Vue.js', '#4FC08D'),
('MySQL', '#4479A1'),
('Redis', '#DC382D'),
('JavaScript', '#F7DF1E'),
('Docker', '#2496ED'),
('Python', '#3776AB'),
('Git', '#F05032'),
('Linux', '#FCC624');

-- ============================================================
-- 说明：
-- 1. MyBatis Plus 配置开启驼峰命名转换后，数据库字段 create_time
--    会自动映射为 Java 实体类的 createTime 属性
-- 2. 使用逻辑删除时，MyBatis Plus 配置：
--    mybatis-plus.global-config.db-config.logic-delete-field=is_deleted
--    mybatis-plus.global-config.db-config.logic-delete-value=1
--    mybatis-plus.global-config.db-config.logic-not-delete-value=0
-- 3. 阅读量和点赞数使用 Redis 缓存实时更新，定时同步到 MySQL
--    避免高并发下频繁写库
-- ============================================================


-- 利用原有的 blogger_apply 表，不用新建申请表。这完美契合你原有的结构！

-- 3. 创建全局站内信通知表 (因为你之前的 init.sql 确实没有这个)
CREATE TABLE IF NOT EXISTS `sys_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '接收者ID',
  `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息正文',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
  `type` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '类型: SYSTEM(系统通知), AUDIT(审批通知), WARN(违规警告)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户站内通知表';

-- 4. 创建 QPS / API 访问打点埋点统计表 (为未来挂载 MQ 做基建)
CREATE TABLE IF NOT EXISTS `sys_api_metrics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `api_path` VARCHAR(255) NOT NULL COMMENT 'API访问路径',
  `method` VARCHAR(10) NOT NULL COMMENT '请求方式',
  `response_time_ms` INT NOT NULL COMMENT '接口耗时(毫秒)',
  `client_ip` VARCHAR(50) DEFAULT NULL COMMENT '客户端IP',
  `status_code` INT NOT NULL COMMENT 'HTTP状态码',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_api_path` (`api_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='QPS及接口监控打点表';