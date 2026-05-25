# 慧芯博客（Huixin Blog）技术架构设计文档

> 文档版本：v1.1  
> 日期：2026年5月21日  
> 更新：安全加固 & Bug 修复记录

---

## 目录

1. [技术栈选型](#1-技术栈选型)
2. [系统架构设计](#2-系统架构设计)
3. [微服务模块划分](#3-微服务模块划分)
4. [项目工程结构](#4-项目工程结构)
5. [API设计规范](#5-api设计规范)
6. [数据库设计概述](#6-数据库设计概述)
7. [Redis缓存设计](#7-redis缓存设计)
8. [Spring Cloud组件设计](#8-spring-cloud组件设计)
9. [安全设计](#9-安全设计)
10. [前端技术方案](#10-前端技术方案)

---

## 1. 技术栈选型

### 1.1 技术栈总览

| 分层 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **前端框架** | Vue.js | 3.x | 渐进式JavaScript框架，组合式API |
| **UI框架** | Bootstrap | 5.x | 响应式CSS框架，栅格布局 |
| **前端构建** | Vite | 5.x | 新一代前端构建工具 |
| **后端框架** | Spring Boot | 3.2.x | Java微服务基础框架 |
| **微服务治理** | Spring Cloud | 2023.x | 微服务全家桶 |
| **持久层** | MyBatis Plus | 3.5.x | 增强版MyBatis，简化CRUD |
| **服务注册/配置** | Nacos | 2.3.x | 阿里巴巴注册中心+配置中心 |
| **API网关** | Spring Cloud Gateway | 4.x | 响应式网关，基于WebFlux |
| **远程调用** | OpenFeign + LoadBalancer | 4.x | 声明式HTTP客户端 + 负载均衡 |
| **缓存** | Redis | 7.x | 高性能KV缓存 |
| **关系数据库** | MySQL | 8.0 | 开源关系型数据库 |
| **API文档** | Knife4j | 4.x | Swagger增强UI |
| **认证授权** | JWT | 0.12.x | JSON Web Token |
| **对象存储** | 本地存储 / 七牛云 | — | 图片等静态资源 |
| **JDK** | OpenJDK | 17 LTS | 长期支持版本 |
| **构建工具** | Maven | 3.9.x | 项目构建与依赖管理 |
| **版本控制** | Git | 2.x | 分布式版本控制 |

### 1.2 选型理由

**前端选型理由：**

- **Vue.js 3**：渐进式框架，学习曲线平缓，组合式API提供更好的逻辑复用能力。相比React，模板语法更贴近传统HTML开发习惯
- **Bootstrap 5**：成熟的CSS框架，栅格系统使响应式布局简单高效，组件丰富，无需从零编写UI组件
- **Vite**：基于ES模块的构建工具，冷启动快，HMR热更新体验极佳

**后端选型理由：**

- **Spring Boot 3.2.x**：需要JDK17基线，与用户指定的JDK17完美匹配。相比2.x版本有显著的性能提升和更现代的API
- **Spring Cloud**：Spring生态的微服务解决方案，组件成熟稳定，与Spring Boot无缝集成
- **MyBatis Plus**：在MyBatis基础上提供开箱即用的CRUD、分页、逻辑删除等功能，大幅减少重复代码，同时保留SQL的灵活性
- **Nacos**：同时提供服务注册发现和配置管理，相比Eureka+Config组合更轻量，且支持CP+AP双模式
- **Spring Cloud Gateway**：基于WebFlux的响应式网关，性能优于Zuul，支持动态路由、限流、熔断等
- **Knife4j**：Swagger的增强UI，支持离线文档导出、全局参数配置、接口排序等，比原生Swagger更易用

---

## 2. 系统架构设计

### 2.1 整体架构图

系统采用前后端分离 + 微服务架构，分为以下层次：

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │    浏览器 (Chrome / Edge / Firefox)                   │    │
│  │    Vue.js 3 + Bootstrap 5 单页应用                    │    │
│  └─────────────────────────────────────────────────────┘    │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/HTTPS
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Gateway)                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  Spring Cloud Gateway (huixin-gateway)               │    │
│  │  - 路由转发 - 统一鉴权 - 限流 - 跨域处理 - 日志记录   │    │
│  └─────────────────────────────────────────────────────┘    │
└──────────────────────┬──────────────────────────────────────┘
                       │ 服务调用 (OpenFeign + LoadBalancer)
       ┌───────────────┼───────────────┬───────────────┐
       ▼               ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ 认证服务      │ │ 用户服务      │ │ 文章服务      │ │ 评论服务      │
│ huixin-auth  │ │ huixin-user  │ │huixin-article│ │huixin-comment│
│              │ │              │ │              │ │              │
│ - 登录/注册   │ │ - 个人信息    │ │ - 文章CRUD   │ │ - 评论CRUD   │
│ - Token签发  │ │ - 头像管理    │ │ - 分类管理   │ │ - 回复管理   │
│ - Token验证  │ │ - 博主申请    │ │ - 标签管理   │ │ - 评论审核   │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │               │               │               │
┌──────────────┐ ┌──────────────┐       │               │
│ 搜索服务      │ │ 统计服务      │       │               │
│huixin-search │ │ huixin-stats │       │               │
│              │ │              │       │               │
│ - 全文搜索   │ │ - 阅读量统计 │       │               │
│ - 搜索建议   │ │ - 点赞统计   │       │               │
└──────┬───────┘ └──────┬───────┘       │               │
       │               │               │               │
       └───────────────┼───────────────┼───────────────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   MySQL 8.0  │ │   Redis 7    │ │  Nacos 2.3   │
│  主数据库     │ │  缓存数据库   │ │  注册+配置    │
└──────────────┘ └──────────────┘ └──────────────┘
```

### 2.2 架构分层说明

| 层次 | 组件 | 职责 |
|------|------|------|
| **客户端层** | Vue.js SPA | 用户界面渲染、交互处理、路由管理 |
| **网关层** | Spring Cloud Gateway | 统一入口、路由转发、鉴权、限流、跨域 |
| **业务服务层** | 多个微服务 | 各业务逻辑独立部署、独立运行 |
| **服务治理层** | Nacos | 服务注册发现、配置统一管理 |
| **数据层** | MySQL + Redis | 数据持久化存储 + 高性能缓存 |

### 2.3 服务调用关系

```
客户端 → [Gateway:8080] → 路由转发
    ├── /api/auth/**    → huixin-auth (8081)
    ├── /api/user/**    → huixin-user (8082)
    ├── /api/article/** → huixin-article (8083)
    ├── /api/comment/** → huixin-comment (8084)
    ├── /api/search/**  → huixin-search (8085)
    └── /api/stats/**   → huixin-stats (8086)

服务间调用（OpenFeign + LoadBalancer）：
    huixin-article → huixin-user（获取作者信息）
    huixin-article → huixin-stats（更新/获取统计数据）
    huixin-comment → huixin-article（验证文章存在）
    huixin-comment → huixin-user（获取评论者信息）
    huixin-search  → huixin-article（同步文章数据）
```

---

## 3. 微服务模块划分

### 3.1 模块清单

| 模块名称 | 端口 | 职责描述 |
|----------|------|----------|
| **huixin-common** | — | 公共依赖模块：通用实体类、工具类、异常定义、统一返回结果 |
| **huixin-gateway** | 8080 | API网关：路由转发、JWT鉴权、跨域、限流 |
| **huixin-auth** | 8081 | 认证服务：注册、登录、Token签发与刷新、Token验证 |
| **huixin-user** | 8082 | 用户服务：用户信息管理、头像上传、博主申请 |
| **huixin-article** | 8083 | 文章服务：文章CRUD、分类管理、标签管理、封面图上传 |
| **huixin-comment** | 8084 | 评论服务：评论发布、回复、删除、评论列表 |
| **huixin-search** | 8085 | 搜索服务：文章全文检索、搜索建议、搜索历史 |
| **huixin-stats** | 8086 | 统计服务：阅读量、点赞数、热门排行、数据汇总 |

### 3.2 模块依赖关系

```
huixin-gateway
    ├── huixin-common

huixin-auth
    ├── huixin-common
    └── Feign → huixin-user

huixin-user
    ├── huixin-common

huixin-article
    ├── huixin-common
    └── Feign → huixin-user, huixin-stats

huixin-comment
    ├── huixin-common
    └── Feign → huixin-article, huixin-user

huixin-search
    ├── huixin-common
    └── Feign → huixin-article

huixin-stats
    ├── huixin-common
```

### 3.3 各模块核心表

| 模块 | 核心数据表 |
|------|-----------|
| huixin-auth | 无独立表（使用huixin-user的user表） |
| huixin-user | `user`、`blogger_apply` |
| huixin-article | `article`、`category`、`tag`、`article_tag` |
| huixin-comment | `comment` |
| huixin-search | 无独立表（查询article表） |
| huixin-stats | `article_stats`、`like_record`（或Redis缓存+定时同步） |

---

## 4. 项目工程结构

### 4.1 Maven多模块结构

```
huixin-blog/                          # 父工程
├── pom.xml                           # 父POM（依赖管理）
├── huixin-common/                    # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/huixin/common/
│       ├── entity/                   # 通用实体类
│       │   ├── User.java
│       │   ├── Article.java
│       │   ├── Category.java
│       │   ├── Tag.java
│       │   ├── Comment.java
│       │   └── ...
│       ├── dto/                      # 数据传输对象
│       │   ├── LoginDTO.java
│       │   ├── RegisterDTO.java
│       │   ├── ArticleDTO.java
│       │   └── ...
│       ├── vo/                       # 视图对象
│       │   ├── ResultVO.java         # 统一返回结果
│       │   ├── PageVO.java           # 分页结果
│       │   └── ...
│       ├── enums/                    # 枚举类
│       │   ├── ResultCode.java       # 响应状态码
│       │   └── ArticleStatus.java    # 文章状态
│       ├── exception/                # 异常定义
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       ├── utils/                    # 工具类
│       │   ├── JwtUtil.java
│       │   ├── RedisUtil.java
│       │   └── ...
│       └── config/                   # 通用配置
│           ├── MyBatisPlusConfig.java
│           ├── RedisConfig.java
│           └── Knife4jConfig.java
│
├── huixin-gateway/                   # 网关服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/gateway/
│       ├── GatewayApplication.java
│       ├── config/
│       │   └── CorsConfig.java       # 跨域配置
│       ├── filter/
│       │   ├── AuthGlobalFilter.java # 全局鉴权过滤器
│       │   └── RateLimitFilter.java  # 限流过滤器
│       └── resources/
│           └── application.yml
│
├── huixin-auth/                      # 认证服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/auth/
│       ├── AuthApplication.java
│       ├── controller/
│       │   └── AuthController.java
│       ├── service/
│       │   ├── AuthService.java
│       │   └── impl/AuthServiceImpl.java
│       ├── mapper/
│       │   └── UserMapper.java
│       └── resources/
│           └── application.yml
│
├── huixin-user/                      # 用户服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/user/
│       ├── UserApplication.java
│       ├── controller/
│       │   └── UserController.java
│       ├── service/
│       │   ├── UserService.java
│       │   └── impl/UserServiceImpl.java
│       ├── mapper/
│       │   ├── UserMapper.java
│       │   └── BloggerApplyMapper.java
│       └── resources/
│           └── application.yml
│
├── huixin-article/                   # 文章服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/article/
│       ├── ArticleApplication.java
│       ├── controller/
│       │   ├── ArticleController.java
│       │   ├── CategoryController.java
│       │   └── TagController.java
│       ├── service/
│       │   ├── ArticleService.java
│       │   ├── CategoryService.java
│       │   ├── TagService.java
│       │   └── impl/
│       ├── mapper/
│       │   ├── ArticleMapper.java
│       │   ├── CategoryMapper.java
│       │   ├── TagMapper.java
│       │   └── ArticleTagMapper.java
│       ├── feign/
│       │   ├── UserFeignClient.java
│       │   └── StatsFeignClient.java
│       └── resources/
│           └── application.yml
│
├── huixin-comment/                   # 评论服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/comment/
│       ├── CommentApplication.java
│       ├── controller/
│       │   └── CommentController.java
│       ├── service/
│       │   ├── CommentService.java
│       │   └── impl/CommentServiceImpl.java
│       ├── mapper/
│       │   └── CommentMapper.java
│       ├── feign/
│       │   ├── ArticleFeignClient.java
│       │   └── UserFeignClient.java
│       └── resources/
│           └── application.yml
│
├── huixin-search/                    # 搜索服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/search/
│       ├── SearchApplication.java
│       ├── controller/
│       │   └── SearchController.java
│       ├── service/
│       │   ├── SearchService.java
│       │   └── impl/SearchServiceImpl.java
│       ├── feign/
│       │   └── ArticleFeignClient.java
│       └── resources/
│           └── application.yml
│
├── huixin-stats/                     # 统计服务
│   ├── pom.xml
│   └── src/main/java/com/huixin/stats/
│       ├── StatsApplication.java
│       ├── controller/
│       │   └── StatsController.java
│       ├── service/
│       │   ├── StatsService.java
│       │   └── impl/StatsServiceImpl.java
│       ├── mapper/
│       │   ├── ArticleStatsMapper.java
│       │   └── LikeRecordMapper.java
│       └── resources/
│           └── application.yml
│
└── docs/                             # 项目文档
    ├── 需求分析文档-慧芯博客.md
    └── 技术架构设计文档-慧芯博客.md
```

### 4.2 父POM关键配置

```xml
<!-- Spring Boot -->
<spring-boot.version>3.2.5</spring-boot.version>
<!-- Spring Cloud -->
<spring-cloud.version>2023.0.1</spring-cloud.version>
<!-- MyBatis Plus -->
<mybatis-plus.version>3.5.5</mybatis-plus.version>
<!-- Nacos -->
<nacos.version>2023.0.1.0</nacos.version>
<!-- Knife4j -->
<knife4j.version>4.3.0</knife4j.version>
<!-- JWT -->
<jjwt.version>0.12.5</jjwt.version>
<!-- 公共模块版本 -->
<huixin.version>1.0.0</huixin.version>
```

---

## 5. API设计规范

### 5.1 统一返回格式

所有API接口统一使用以下JSON格式返回：

```
json
{
    "code": 200,           // 状态码：200成功，4xx客户端错误，5xx服务端错误
    "message": "操作成功",  // 提示信息
    "data": {},            // 业务数据（可为对象、数组、null）
    "timestamp": 1716172800000  // 时间戳
}
```
`
分页返回格式：`

```
json
{
    "code": 200,
    "message": "查询成功",
    "data": {
        "records": [],          // 数据列表
        "total": 100,           // 总记录数
        "size": 10,             // 每页大小
        "current": 1,           // 当前页
        "pages": 10             // 总页数
    },
    "timestamp": 1716172800000
}
```

### 5.2 RESTful API设计

| 资源 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | `/api/auth/register` | 用户注册 |
| 认证 | POST | `/api/auth/login` | 用户登录 |
| 认证 | POST | `/api/auth/refresh` | 刷新Token |
| 认证 | POST | `/api/auth/logout` | 登出 |
| 用户 | GET | `/api/user/info` | 获取当前用户信息 |
| 用户 | PUT | `/api/user/info` | 修改个人信息 |
| 用户 | PUT | `/api/user/password` | 修改密码 |
| 用户 | POST | `/api/user/avatar` | 上传头像 |
| 用户 | GET | `/api/user/{id}` | 获取用户公开信息 |
| 文章 | GET | `/api/article/list` | 文章列表（分页+筛选） |
| 文章 | GET | `/api/article/{id}` | 文章详情 |
| 文章 | POST | `/api/article` | 发布文章 |
| 文章 | PUT | `/api/article/{id}` | 编辑文章 |
| 文章 | DELETE | `/api/article/{id}` | 删除文章 |
| 分类 | GET | `/api/category/list` | 分类列表 |
| 标签 | GET | `/api/tag/list` | 标签列表 |
| 标签 | GET | `/api/tag/cloud` | 标签云 |
| 评论 | GET | `/api/comment/article/{id}` | 文章评论列表 |
| 评论 | POST | `/api/comment` | 发表评论 |
| 评论 | DELETE | `/api/comment/{id}` | 删除评论 |
| 点赞 | POST | `/api/like/article/{id}` | 点赞/取消点赞文章 |
| 搜索 | GET | `/api/search?keyword=xxx` | 文章搜索 |
| 统计 | GET | `/api/stats/article/{id}` | 文章统计数据 |
| 统计 | GET | `/api/stats/hot` | 热门文章排行 |

### 5.3 状态码规范

| 状态码 | 含义 |
|--------|------|
| 200 | 请求成功 |
| 400 | 参数校验失败 |
| 401 | 未登录或Token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 数据冲突（如用户名已存在） |
| 429 | 请求频率超限 |
| 500 | 服务器内部错误 |

### 5.4 网关路由映射

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证服务
        - id: huixin-auth
          uri: lb://huixin-auth
          predicates:
            - Path=/api/auth/**
        # 用户服务（需要鉴权）
        - id: huixin-user
          uri: lb://huixin-user
          predicates:
            - Path=/api/user/**
          filters:
            - AuthFilter
        # 文章服务（GET请求无需鉴权，POST/PUT/DELETE需要鉴权）
        - id: huixin-article
          uri: lb://huixin-article
          predicates:
            - Path=/api/article/**,/api/category/**,/api/tag/**
          filters:
            - AuthFilter
        # 评论服务
        - id: huixin-comment
          uri: lb://huixin-comment
          predicates:
            - Path=/api/comment/**
          filters:
            - AuthFilter
        # 搜索服务
        - id: huixin-search
          uri: lb://huixin-search
          predicates:
            - Path=/api/search/**
        # 统计服务
        - id: huixin-stats
          uri: lb://huixin-stats
          predicates:
            - Path=/api/stats/**
```

---

## 6. 数据库设计概述

### 6.1 数据库命名规范

- 数据库名：`huixin_blog`
- 字符集：`utf8mb4`，排序规则：`utf8mb4_unicode_ci`
- 表名：小写字母 + 下划线，单数形式（如 `user`、`article`、`comment`）
- 字段名：小写字母 + 下划线，顾名思义（如 `create_time`、`author_id`、`article_status`）
- 所有表包含 `id`（主键，自增）、`create_time`、`update_time` 字段
- 逻辑删除字段：`is_deleted`（0-未删除，1-已删除）

### 6.2 核心数据表清单

| 表名 | 所属模块 | 说明 |
|------|----------|------|
| `user` | huixin-user | 用户表 |
| `blogger_apply` | huixin-user | 博主申请记录表 |
| `article` | huixin-article | 文章表 |
| `category` | huixin-article | 分类表 |
| `tag` | huixin-article | 标签表 |
| `article_tag` | huixin-article | 文章-标签关联表 |
| `comment` | huixin-comment | 评论表 |
| `like_record` | huixin-stats | 点赞记录表 |
| `article_stats` | huixin-stats | 文章统计表 |

*注：详细的字段设计、索引设计、SQL语句将在《数据库设计文档》中单独提供。*

---

## 7. Redis缓存设计

### 7.1 缓存架构

```
┌─────────────────────────────────────────┐
│              应用服务层                   │
│  huixin-article / huixin-auth / ...     │
└───────────────┬─────────────────────────┘
                │ Spring Data Redis
                ▼
┌─────────────────────────────────────────┐
│              Redis 7                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│  │ 用户Token│ │文章阅读量│ │ 点赞记录 │ │
│  │ 缓存     │ │ 缓存     │ │ 缓存     │ │
│  └──────────┘ └──────────┘ └──────────┘ │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│  │ 热门文章 │ │ 分类标签 │ │ 限流计数 │ │
│  │ 排行缓存 │ │ 缓存     │ │ 器       │ │
│  └──────────┘ └──────────┘ └──────────┘ │
└─────────────────────────────────────────┘
```

### 7.2 Key设计规范

| 缓存场景 | Key格式 | 数据类型 | 过期时间 | 说明 |
|---------|---------|---------|---------|------|
| 用户Token | `token:{userId}` | String | 2小时 | JWT Blacklist备用 |
| 刷新Token | `refresh_token:{userId}` | String | 7天 | 刷新令牌 |
| 文章阅读量 | `article:views:{articleId}` | String (数值) | 永久 | 定时同步MySQL |
| 文章点赞数 | `article:likes:{articleId}` | String (数值) | 永久 | 定时同步MySQL |
| 用户点赞记录 | `user:likes:{userId}` | Set | 永久 | 存储已点赞文章ID集合 |
| 热门文章排行 | `article:hot:rank` | ZSet | 1小时 | score为热度分，定时计算 |
| 分类列表 | `category:list` | String (JSON) | 30分钟 | 分类数据变化少 |
| 标签列表 | `tag:list` | String (JSON) | 30分钟 | 标签数据变化少 |
| 搜索热词 | `search:hot_keywords` | ZSet | 24小时 | 搜索频次统计 |
| 接口限流 | `rate_limit:{ip}:{api}` | String | 滑动窗口 | Gateway层限流 |

### 7.3 缓存策略

**阅读量统计流程：**
```
用户访问文章 → 判断Redis中是否存在 article:views:{id}
    → 存在: INCR article:views:{id}
    → 不存在: 从MySQL加载阅读量 → 写入Redis → INCR
定时任务(每10分钟): Redis阅读量 → 批量同步MySQL
```

**点赞流程：**
```
用户点赞 → SADD user:likes:{userId} {articleId} → 判断返回
    → 1（未点过）: INCR article:likes:{articleId} → 点赞成功
    → 0（已点过）: SREM + DECR → 取消点赞
```

**Token管理：**
```
登录成功 → 生成JWT → Redis存refresh_token:{userId} → 返回access_token + refresh_token
请求鉴权 → 解析JWT → 验证签名+过期时间
Token刷新 → 验证refresh_token → 签发新access_token
登出 → 删除Redis中refresh_token → access_token加入黑名单(短期)
```

### 7.4 Redis配置要点

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: huixin2024
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

---

## 8. Spring Cloud组件设计

### 8.1 Nacos配置

**服务注册：** 所有微服务启动时自动向Nacos注册，网关通过服务名进行负载均衡路由。

```yaml
# 每个微服务的通用Nacos配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: huixin-blog
        group: DEFAULT_GROUP
      config:
        server-addr: localhost:8848
        namespace: huixin-blog
        group: DEFAULT_GROUP
        file-extension: yaml
```

**配置管理：** 使用Nacos作为统一配置中心，管理：
- 数据库连接配置
- Redis连接配置
- JWT密钥与过期时间
- 文件上传路径与大小限制
- 各微服务自定义配置

### 8.2 Spring Cloud Gateway配置

网关核心功能：
- **路由转发：** 根据路径前缀转发到对应微服务
- **统一鉴权：** 全局过滤器校验JWT Token（白名单路径除外）
- **跨域处理：** 统一配置CORS策略
- **限流：** 基于Redis的令牌桶算法限流
- **日志记录：** 请求/响应日志记录

### 8.3 OpenFeign + LoadBalancer配置

```java
// 示例：文章服务调用用户服务
@FeignClient(name = "huixin-user", path = "/api/user")
public interface UserFeignClient {
    
    @GetMapping("/{id}")
    ResultVO<UserVO> getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/batch")
    ResultVO<List<UserVO>> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
```

Feign配置：
- 连接超时：5秒
- 读取超时：10秒
- 重试策略：默认不重试（POST等非幂等请求）
- 日志级别：BASIC（生产）/ FULL（开发）

### 8.4 Knife4j配置

每个微服务独立配置Knife4j，网关统一聚合各服务文档地址：

```java
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("慧芯博客 - 认证服务API")
                .version("1.0")
                .description("用户注册、登录、Token管理相关接口"));
    }
}
```

访问方式：`http://localhost:8080/doc.html`（网关聚合）或各服务独立文档地址。

---

## 9. 安全设计

### 9.1 认证流程

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  客户端   │         │  Gateway │         │ Auth服务  │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │ POST /api/auth/login│                    │
     │─────────────────────►                    │
     │                     │  转发请求           │
     │                     │───────────────────►│
     │                     │                    │ 验证用户名密码
     │                     │                    │ 生成JWT Token
     │                     │  返回Token          │
     │                     │◄───────────────────│
     │  返回Token           │                    │
     │◄────────────────────│                    │
     │                     │                    │
     │ 后续请求 + Authorization头               │
     │─────────────────────►                    │
     │                     │  Gateway解析JWT     │
     │                     │  验证签名+过期时间   │
     │                     │  通过→放行 失败→401 │
     │                     │───────────────────►│
     │                     │                    │ 业务处理
```

### 9.2 JWT Token设计

**Payload结构：**
```json
{
    "sub": "用户ID",
    "username": "用户名",
    "role": "USER / BLOGGER",
    "iat": "签发时间",
    "exp": "过期时间"
}
```

**Token类型：**
- Access Token：有效期2小时，用于API鉴权
- Refresh Token：有效期7天，用于刷新Access Token

### 9.3 安全措施清单

- 密码BCrypt加密（强度10轮）
- JWT签名使用HMAC-SHA256
- 敏感配置（密钥、密码）使用Nacos配置加密或环境变量
- SQL注入防护（MyBatis Plus参数化查询）
- XSS过滤（前端输出转义，后端输入清洗）
- CORS白名单配置
- 文件上传类型和大小校验
- 接口频率限制（网关层）

---

## 10. 前端技术方案

### 10.1 前端项目结构

```
huixin-blog-web/
├── index.html
├── vite.config.js
├── package.json
├── public/
│   └── assets/
│       └── images/                # 背景图片、Logo等静态资源
└── src/
    ├── main.js                    # 入口文件
    ├── App.vue                    # 根组件
    ├── router/
    │   └── index.js               # 路由配置
    ├── api/                       # API接口封装
    │   ├── request.js             # Axios封装（拦截器、统一错误处理）
    │   ├── auth.js                # 认证相关API
    │   ├── article.js             # 文章相关API
    │   ├── comment.js             # 评论相关API
    │   └── user.js                # 用户相关API
    ├── store/                     # Pinia状态管理
    │   ├── user.js                # 用户状态
    │   └── app.js                 # 应用全局状态
    ├── views/                     # 页面组件
    │   ├── Home.vue               # 首页
    │   ├── ArticleDetail.vue      # 文章详情
    │   ├── ArticleEdit.vue        # 文章编辑/发布
    │   ├── Login.vue              # 登录
    │   ├── Register.vue           # 注册
    │   ├── UserCenter.vue         # 个人中心
    │   └── Search.vue             # 搜索结果
    ├── components/                # 通用组件
    │   ├── Navbar.vue             # 顶部导航栏
    │   ├── ArticleCard.vue        # 文章卡片
    │   ├── CommentSection.vue     # 评论区
    │   ├── TagCloud.vue           # 标签云
    │   ├── HotArticles.vue        # 热门文章
    │   ├── MarkdownEditor.vue     # Markdown编辑器
    │   ├── UserAvatar.vue         # 用户头像
    │   └── Pagination.vue         # 分页组件
    ├── styles/
    │   ├── global.css             # 全局样式
    │   ├── variables.css          # CSS变量（主题色等）
    │   └── chinese-theme.css      # 中国风主题样式
    └── utils/
        ├── auth.js                # Token存储与读取
        └── constants.js           # 常量定义
```

### 10.2 前端路由设计

| 路径 | 页面 | 权限 |
|------|------|------|
| `/` | 首页（文章信息流） | 公开 |
| `/article/:id` | 文章详情 | 公开 |
| `/category/:id` | 分类文章列表 | 公开 |
| `/tag/:name` | 标签文章列表 | 公开 |
| `/search` | 搜索结果 | 公开 |
| `/login` | 登录 | 未登录 |
| `/register` | 注册 | 未登录 |
| `/user/:id` | 用户主页 | 公开 |
| `/user/center` | 个人中心 | 登录 |
| `/article/new` | 发布文章 | 博主 |
| `/article/:id/edit` | 编辑文章 | 博主 |

### 10.3 UI设计要点

- **设计风格参考：** X (Twitter) 信息流式布局
- **整体色调：** 以中国传统色系为基础（如鸦青、月白、竹青等），结合现代扁平化设计
- **背景设计：** 采用中国风纹理/山水/水墨风格背景图片，非纯色背景
- **字体：** 标题可用"思源宋体"或"站酷文艺体"等中文字体，正文使用系统默认字体
- **Logo设计：** "慧芯博客"文字Logo，结合中国传统纹样元素
- **响应式：** Bootstrap 5 栅格系统，适配 PC 端（1920px/1366px）和平板（768px）

### 10.4 中国风背景方案

首页及各页面背景计划采用以下方案之一：
1. 淡雅水墨山水画背景（低透明度叠加）
2. 宣纸纹理背景
3. 中国传统纹样（云纹、回纹等）平铺
4. 古典窗棂/屏风元素装饰

---

*本文档为慧芯博客技术架构设计初版，后续开发过程中将持续完善和调整。*

---

## 附录：v1.1 安全加固 & Bug 修复记录（2026-05-21）

### 安全加固

| 类别 | 修复内容 |
|------|---------|
| JWT 密钥 | `JwtUtil` 支持通过环境变量 `JWT_SECRET` 或系统属性 `jwt.secret` 注入密钥，开发环境输出警告 |
| 网关头注入 | `AuthGlobalFilter` 入口处清除客户端可能伪造的 `X-User-Id`/`X-Username`/`X-Role` 请求头 |
| CORS 配置 | `allowedOriginPatterns` 从 `*` 限制为 `localhost:3000`，防止跨域凭证泄露 |
| 网关白名单 | 细化白名单规则：`/api/auth/logout` 需认证、`/api/comment/**` 公开读取、`/api/search` 无斜杠匹配 |
| SQL 日志 | 所有模块注释掉 `StdOutImpl` SQL 日志，避免生产环境泄露敏感数据 |
| Knife4j | 所有模块改为 `${KNIFE4J_ENABLE:true}`，可通过环境变量在生产环境关闭 |
| 密码校验 | 密码正则增加 `\S+` 排除空格；前端增加确认密码字段和客户端正则校验 |
| 头像 URL | 增加 `@NotBlank` + URL 格式验证 |
| 前端 Token | 删除不安全的客户端 JWT payload 解码，改为调用 `/api/user/info` 获取已验证用户信息 |

### 逻辑修复

| 模块 | 修复内容 |
|------|---------|
| common | BloggerApply 添加 `@TableField(fill=...)` 自动填充；ArticleTag/LikeRecord 添加设计说明 |
| auth | 注册增加 `DuplicateKeyException` 捕获；Token 刷新区分 user==null 和 status==0；登录改用 `LambdaUpdateWrapper` |
| user | 所有 Service 入口增加 userId null 检查；`updateById` → `LambdaUpdateWrapper`；`changePassword` 新旧比较改为明文 |
| article | **CRITICAL**: 补全 `buildArticleListVO` 截断的 builder 链；新增批量 `buildArticleListVOs` 消除 N+1；分类变更验证新分类存在；`deleteArticle` 清理 `article_tag` 关联；`listUserArticles` 过滤草稿；分页参数添加 `@Min/@Max`；分类/标签计数 SQL 增加负数保护 |
| comment | **CRITICAL**: 评论创建/删除同步文章 `comment_count`；文章作者可删除评论；批量获取用户信息消除 N+1；父评论校验同文章；级联删除子回复；轻量级文章存在性检查 |
| search+stats | **CRITICAL**: `toggleLike` 使用 Lua 脚本原子化消除竞态；移除不存在的 `@EnableFeignClients`/`@MapperScan`；`SearchController` 空关键词检查；删除 `StatsController` 死代码 |
| web | 标签筛选修复（tagId query param + watch）；退出登录先调 API 再清 Token；点赞/评论双重提交防护；编辑文章所有权校验；空状态提示；用户不存在提示 |

### 基础设施

| 类别 | 修复内容 |
|------|---------|
| Redis 部署文档 | 补充 `yum-utils` 安装、systemd 服务文件、PATH 配置；`protected-mode` 改为 yes；分离 yum/编译方案 |
| Git 提交 | 9 个语义化 commit，从底层到顶层分层记录 |

