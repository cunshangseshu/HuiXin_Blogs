-- ============================================================
-- 慧芯博客 - 测试数据
-- 密码统一为：123456（BCrypt加密）
-- 生成日期：2026-05-23
-- ============================================================

USE `huixin_blog`;

-- ============================================================
-- 1. 用户数据（3个博主 + 5个普通用户）
-- ============================================================
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `avatar_url`, `bio`, `role_type`, `status`, `create_time`) VALUES
('zhangsan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'zhangsan@huixin.com', '程序员张三',
 NULL,
 '全栈开发者，热爱Java和Vue', 1, 1, '2026-05-01 10:00:00'),

('lisi',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'lisi@huixin.com', '架构师老李',
 NULL,
 '10年Java架构经验，专注分布式系统', 1, 1, '2026-05-02 14:00:00'),

('wangwu',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wangwu@huixin.com', '前端小王',
 NULL,
 'Vue.js重度用户，CSS艺术家', 1, 1, '2026-05-03 09:00:00'),

('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testuser@huixin.com', '测试用户赵六',
 NULL, '一个热爱阅读的普通用户', 0, 1, '2026-05-10 10:00:00'),

('reader01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'reader01@huixin.com', '书虫钱七',
 NULL, '每天都要读一篇好文章', 0, 1, '2026-05-11 11:00:00'),

('reader02', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'reader02@huixin.com', '代码诗人孙八',
 NULL, '在代码中寻找诗意', 0, 1, '2026-05-12 15:00:00'),

('reader03', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'reader03@huixin.com', '极客周九',
 NULL, '热衷开源技术', 0, 1, '2026-05-13 08:00:00'),

('reader04', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'reader04@huixin.com', '旅人吴十',
 NULL, '行者无疆，读万卷书行万里路', 0, 1, '2026-05-14 20:00:00');

-- ============================================================
-- 2. 博主申请记录
-- ============================================================
INSERT INTO `blogger_apply` (`user_id`, `apply_reason`, `apply_status`, `review_comment`, `review_time`, `create_time`) VALUES
(4, '我经常浏览技术文章，希望能分享自己的Java学习心得', 0, NULL, NULL, '2026-05-15 09:00:00'),
(5, '热爱写作，想和大家交流读书感悟', 1, '审核通过，欢迎加入', '2026-05-15 10:00:00', '2026-05-14 16:00:00'),
(6, '对前端开发有浓厚兴趣', 2, '申请理由不够充分，建议补充后再申请', '2026-05-15 11:00:00', '2026-05-13 14:00:00');

-- ============================================================
-- 3. 文章数据（15篇，覆盖5个分类，3个博主）
-- ============================================================
INSERT INTO `article` (`title`, `summary`, `content`, `content_html`, `cover_image_url`, `author_id`, `category_id`, `article_status`, `view_count`, `like_count`, `comment_count`, `is_top`, `is_original`, `publish_time`, `create_time`) VALUES

-- ===== 技术分享 (category_id=1) =====
('Spring Boot 3 微服务实战指南',
 '从零搭建Spring Boot 3微服务项目，涵盖Nacos注册中心、Gateway网关、OpenFeign远程调用等核心组件。',
 '## Spring Boot 3 微服务实战\n\n### 前言\nSpring Boot 3 是Spring生态的重大升级，要求JDK 17+，支持原生镜像。\n\n### 1. 项目初始化\n使用Spring Initializr创建项目，选择以下依赖：\n- Spring Web\n- Spring Cloud Alibaba Nacos\n- MyBatis Plus\n\n### 2. Nacos 配置\n```yaml\nspring:\n  cloud:\n    nacos:\n      discovery:\n        server-addr: localhost:8848\n```\n\n### 3. 服务间调用\n使用OpenFeign实现声明式HTTP调用：\n```java\n@FeignClient(name = "user-service")\npublic interface UserClient {\n    @GetMapping("/api/user/{id}")\n    User getUser(@PathVariable Long id);\n}\n```\n\n### 总结\nSpring Boot 3 + Spring Cloud Alibaba 是目前构建微服务的主流方案。',
 '<h2>Spring Boot 3 微服务实战</h2><h3>前言</h3><p>Spring Boot 3 是Spring生态的重大升级。</p><h3>Nacos 配置</h3><pre><code>spring.cloud.nacos.discovery.server-addr: localhost:8848</code></pre>',
 NULL, 1, 1, 1, 1280, 89, 6, 1, 1, '2026-05-04 08:00:00', '2026-05-04 08:00:00'),

('Docker Compose 编排微服务全流程',
 '手把手教你用Docker Compose编排Spring Cloud微服务全家桶，一键部署Nacos+Redis+MySQL+微服务。',
 '## Docker Compose 编排微服务\n\n### 为什么用Docker Compose\n传统方式需要手动启动Nacos、Redis、MySQL等中间件，Docker Compose可以一键编排所有服务。\n\n### docker-compose.yml 示例\n```yaml\nversion: "3.8"\nservices:\n  nacos:\n    image: nacos/nacos-server:v2.3.2\n    ports:\n      - "8848:8848"\n  redis:\n    image: redis:7-alpine\n    ports:\n      - "6379:6379"\n```\n\n### 启动命令\n```bash\ndocker-compose up -d\n```\n\n### 踩坑分享\n- Docker网络模式选择bridge\n- Nacos需要等待MySQL就绪后再启动',
 '<h2>Docker Compose 编排微服务</h2><p>传统方式需要手动启动Nacos、Redis、MySQL等中间件。</p><pre><code>docker-compose up -d</code></pre>',
 NULL, 1, 1, 1, 956, 67, 4, 0, 1, '2026-05-05 10:00:00', '2026-05-05 10:00:00'),

('Redis 缓存在博客系统中的应用',
 '深度解析Redis在博客系统中的七大应用场景：Token管理、阅读量统计、点赞去重、热门排行、搜索热词、限流控制、分布式锁。',
 '## Redis 在博客中的七种用法\n\n### 1. Token管理\n用户登录后将Refresh Token存入Redis，设置7天过期。\n\n### 2. 阅读量统计\n使用INCR原子自增，避免并发问题：\n```redis\nINCR article:views:1\n```\n\n### 3. 点赞去重\n使用Set结构，SISMEMBER O(1)判重：\n```redis\nSADD article:like_users:1 user123\nSISMEMBER article:like_users:1 user123\n```\n\n### 4. 热门排行\n使用ZSet按热度分排序：\n```redis\nZADD article:hot:rank 100 article:1\nZREVRANGE article:hot:rank 0 9 WITHSCORES\n```\n\n### 5. 搜索热词\n每次搜索ZINCRBY增加热度分。\n\n### 6. 限流控制\n配合Gateway使用令牌桶算法。\n\n### 7. 分布式锁\n使用SETNX实现分布式锁，解决定时任务单实例执行问题。',
 '<h2>Redis 在博客中的七种用法</h2><p>深度解析Redis的核心应用场景。</p>',
 NULL, 1, 1, 1, 2100, 156, 8, 0, 1, '2026-05-06 09:00:00', '2026-05-06 09:00:00'),

-- ===== 后端开发 (category_id=4) =====
('MyBatis Plus 高效开发技巧大全',
 '总结了20个MyBatis Plus实战技巧：逻辑删除、自动填充、分页插件、Lambda查询、批量操作、动态表名等。',
 '## MyBatis Plus 高效技巧\n\n### 1. 逻辑删除\n只需在实体类添加@TableLogic注解：\n```java\n@TableLogic\nprivate Integer isDeleted;\n```\n配置yml后自动在SQL中追加`WHERE is_deleted=0`。\n\n### 2. 自动填充\n创建时间和更新时间自动填充：\n```java\n@Component\npublic class MetaHandler implements MetaObjectHandler {\n    @Override\n    public void insertFill(MetaObject metaObject) {\n        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());\n    }\n}\n```\n\n### 3. Lambda查询\n类型安全的查询方式：\n```java\nLambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();\nwrapper.eq(User::getUsername, "zhangsan")\n       .gt(User::getCreateTime, LocalDate.now().minusDays(7));\n```\n\n### 4. 分页插件\n配置后即可使用Page对象：\n```java\nPage<User> page = new Page<>(1, 10);\nuserMapper.selectPage(page, wrapper);\n```',
 '<h2>MyBatis Plus 高效技巧</h2><p>总结了20个实战技巧。</p>',
 NULL, 2, 4, 1, 1890, 132, 7, 0, 1, '2026-05-07 11:00:00', '2026-05-07 11:00:00'),

('深入理解JWT：从原理到实践',
 'JWT（JSON Web Token）是现代微服务架构中最流行的无状态认证方案。本文深入剖析JWT的签名机制、Token刷新策略、安全最佳实践。',
 '## JWT 深入理解\n\n### JWT的结构\nJWT由三部分组成：Header.Payload.Signature\n\n### 签名机制\n使用HMAC-SHA256对前两部分签名：\n```java\nString jwt = Jwts.builder()\n    .claim("userId", userId)\n    .expiration(new Date(expireTime))\n    .signWith(key)\n    .compact();\n```\n\n### Token刷新策略\n- Access Token：短期（2小时），用于API鉴权\n- Refresh Token：长期（7天），用于刷新Access Token\n\n### 安全最佳实践\n1. 密钥不要硬编码在代码中\n2. Token不要存储敏感信息\n3. 设置合理的过期时间\n4. 使用HTTPS传输',
 '<h2>JWT 深入理解</h2><p>现代微服务架构中最流行的无状态认证方案。</p>',
 NULL, 2, 4, 1, 1560, 98, 5, 0, 1, '2026-05-08 14:00:00', '2026-05-08 14:00:00'),

('高并发下的缓存策略设计',
 '从缓存穿透、缓存击穿、缓存雪崩三大经典问题出发，系统讲解Redis缓存策略的设计与优化方案。',
 '## 高并发缓存策略\n\n### 缓存穿透\n查询不存在的数据，缓存和数据库都没有，每次请求都打到数据库。\n**解决方案：布隆过滤器 + 缓存空值**\n\n### 缓存击穿\n热点Key过期瞬间，大量请求打到数据库。\n**解决方案：互斥锁 + 永不过期**\n\n### 缓存雪崩\n大量Key同时过期，数据库压力剧增。\n**解决方案：过期时间随机化 + 多级缓存**\n\n### 代码示例\n```java\npublic String getWithLock(String key) {\n    String value = redis.get(key);\n    if (value != null) return value;\n    // 加锁防止击穿\n    if (redis.setnx("lock:" + key, "1")) {\n        value = db.query(key);\n        redis.set(key, value, 30, TimeUnit.MINUTES);\n        redis.del("lock:" + key);\n    }\n    return value;\n}\n```',
 '<h2>高并发缓存策略</h2><p>从三大经典问题出发。</p>',
 NULL, 2, 4, 1, 2340, 178, 9, 0, 1, '2026-05-09 16:00:00', '2026-05-09 16:00:00'),

-- ===== 前端开发 (category_id=3) =====
('Vue 3 Composition API 实战',
 '从Options API到Composition API，全面解析Vue 3的setup语法糖、ref/reactive响应式、watch/watchEffect侦听器、组合函数等核心特性。',
 '## Vue 3 Composition API\n\n### setup语法糖\n```vue\n<script setup>\nimport { ref, computed } from "vue"\n\nconst count = ref(0)\nconst doubled = computed(() => count.value * 2)\n\nfunction increment() { count.value++ }\n</script>\n```\n\n### 响应式对比\n| API | 适用场景 |\n|-----|---------|\n| ref | 基础类型 |\n| reactive | 对象类型 |\n| computed | 计算属性 |\n\n### 组合函数\n将逻辑抽离为可复用函数：\n```js\nfunction useCounter() {\n  const count = ref(0)\n  const increment = () => count.value++\n  return { count, increment }\n}\n```',
 '<h2>Vue 3 Composition API</h2><p>全面解析核心特性。</p>',
 NULL, 3, 3, 1, 1450, 112, 6, 0, 1, '2026-05-10 08:00:00', '2026-05-10 08:00:00'),

('Bootstrap 5 中国风主题定制指南',
 '手把手教你用Bootstrap 5 + CSS变量打造一套中国风UI主题，包括传统色系、纹样装饰、响应式布局。',
 '## Bootstrap 5 中国风定制\n\n### 1. 色彩体系\n```css\n:root {\n  --huixin-primary: #4A6B5D;    /* 竹青 */\n  --huixin-accent: #C04040;     /* 胭脂 */\n  --huixin-gold: #C9A96E;       /* 琉璃黄 */\n  --huixin-bg: #F5F0E8;         /* 宣纸色 */\n}\n```\n\n### 2. 按钮定制\n```css\n.btn-primary {\n  background: linear-gradient(135deg, var(--huixin-primary), #3A5A4C);\n}\n```\n\n### 3. 字体选择\n标题使用楷体、正文使用微软雅黑。\n\n### 4. 卡片美化\n左侧加竹青色竖线装饰，hover时渐变背景。',
 '<h2>Bootstrap 5 中国风定制</h2><p>打造一套中国风UI主题。</p>',
 NULL, 3, 3, 1, 890, 76, 3, 0, 1, '2026-05-11 10:00:00', '2026-05-11 10:00:00'),

('Vite 5 构建优化：从5秒到500毫秒',
 '分享Vite 5项目的性能优化实践：按需加载、代码分割、CDN加速、Gzip压缩、图片优化等全方位提速方案。',
 '## Vite 5 构建优化\n\n### 1. 按需加载\n```js\nconst ArticleEdit = () => import("@/views/ArticleEdit.vue")\n```\n\n### 2. 代码分割\nVite自动基于Rollup进行代码分割，关键是要合理组织chunk。\n\n### 3. CDN加速\n将vue、bootstrap等大库走CDN。\n\n### 4. 图片优化\n使用WebP格式，配合vite-imagetools插件。\n\n### 优化效果\n| 指标 | 优化前 | 优化后 |\n|-----|-------|-------|\n| FCP | 2.3s | 0.8s |\n| LCP | 4.1s | 1.2s |\n| Bundle Size | 850KB | 320KB |',
 '<h2>Vite 5 构建优化</h2><p>从5秒到500毫秒的优化实践。</p>',
 NULL, 3, 3, 1, 670, 45, 2, 0, 1, '2026-05-12 15:00:00', '2026-05-12 15:00:00'),

-- ===== 人工智能 (category_id=5) =====
('Spring AI 集成大模型实战',
 'Spring AI是Spring官方推出的AI框架，支持OpenAI、Azure OpenAI、Ollama等多种模型。本文带你快速上手Spring AI。',
 '## Spring AI 实战\n\n### 引入依赖\n```xml\n<dependency>\n    <groupId>org.springframework.ai</groupId>\n    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>\n</dependency>\n```\n\n### 配置文件\n```yaml\nspring:\n  ai:\n    openai:\n      api-key: ${OPENAI_API_KEY}\n      model: gpt-4\n```\n\n### 代码调用\n```java\n@RestController\npublic class ChatController {\n    private final ChatClient chatClient;\n    \n    @GetMapping("/chat")\n    public String chat(@RequestParam String prompt) {\n        return chatClient.call(prompt);\n    }\n}\n```',
 '<h2>Spring AI 实战</h2><p>Spring官方推出的AI框架。</p>',
 NULL, 1, 5, 1, 1100, 89, 5, 0, 1, '2026-05-13 09:00:00', '2026-05-13 09:00:00'),

('深度学习入门：从感知机到Transformer',
 '零基础入门深度学习，从最简单的感知机开始，逐步深入到CNN、RNN、LSTM，最终理解Transformer架构。',
 '## 深度学习入门\n\n### 感知机\n感知机是最简单的神经网络，只能解决线性可分问题。\n\n### CNN 卷积神经网络\n适用于图像识别，核心操作：卷积 + 池化 + 全连接。\n\n### RNN 循环神经网络\n适用于序列数据，如文本、时间序列。\n\n### Transformer\n2017年提出，彻底改变了NLP领域：\n- Self-Attention机制\n- Multi-Head Attention\n- Positional Encoding\n\nBERT、GPT都是基于Transformer架构。',
 '<h2>深度学习入门</h2><p>从感知机到Transformer。</p>',
 NULL, 1, 5, 1, 780, 56, 3, 0, 1, '2026-05-14 11:00:00', '2026-05-14 11:00:00'),

('用Python构建自己的AI Agent',
 '基于LangChain框架，30分钟构建一个能调用工具、查询数据库、执行代码的AI Agent。',
 '## 构建AI Agent\n\n### 什么是AI Agent\nAI Agent是一个能自主决策、调用工具完成任务的智能体。\n\n### LangChain快速入门\n```python\nfrom langchain.agents import create_openai_functions_agent\nfrom langchain.tools import Tool\n\nagent = create_openai_functions_agent(\n    llm=llm,\n    tools=[search_tool, calculator_tool],\n    prompt=prompt\n)\n```\n\n### 工具注册\n```python\n@tool\ndef get_weather(city: str) -> str:\n    """获取城市天气"""\n    return f"{city}: 晴，25°C"\n```',
 '<h2>构建AI Agent</h2><p>基于LangChain框架。</p>',
 NULL, 1, 5, 1, 560, 34, 2, 0, 1, '2026-05-15 14:00:00', '2026-05-15 14:00:00'),

-- ===== 生活随笔 (category_id=2) =====
('一个程序员的十年',
 '从实习生到架构师的十年成长之路：踩过的坑、学到的教训、不变的初心。这是一个关于热爱与坚持的故事。',
 '## 一个程序员的十年\n\n### 2016年：初入职场\n还记得第一天上班，连Git都不会用，pull和fetch的区别搞了半天。\n\n### 2018年：技术成长\n开始接触微服务，从单体架构迁移到Spring Cloud。那段日子每天都在看文档、写代码、改Bug。\n\n### 2020年：技术管理\n开始带团队，发现写代码和带人是完全不同的两件事。\n\n### 2024年：回归技术\n最终还是选择了纯技术路线，成为架构师。\n\n### 感悟\n编程是一辈子的事，保持好奇心，不断学习。',
 '<h2>一个程序员的十年</h2><p>从实习生到架构师的十年成长之路。</p>',
 NULL, 1, 2, 1, 3400, 267, 12, 0, 1, '2026-05-16 08:00:00', '2026-05-16 08:00:00'),

('为什么我推荐程序员写博客',
 '写博客不仅能整理知识体系，还能建立个人品牌。分享我的博客写作心得和工具推荐。',
 '## 写博客的好处\n\n### 1. 知识整理\n写博客是最好的学习方法。当你试图向别人解释一个概念时，你会发现自己到底懂没懂。\n\n### 2. 个人品牌\n好的技术博客是你的名片，很多机会都是通过博客找到的。\n\n### 3. 写作工具\n- Markdown编辑器：Typora\n- 图床：PicGo + GitHub\n- 发布平台：自建博客\n\n### 4. 坚持\n每周一篇，不知不觉就写了100篇。',
 '<h2>为什么推荐写博客</h2><p>知识和品牌的双重积累。</p>',
 NULL, 2, 2, 1, 920, 78, 4, 0, 1, '2026-05-17 12:00:00', '2026-05-17 12:00:00'),

('深夜写代码的那些日子',
 '凌晨三点的机房、咖啡的香气、键盘的敲击声——记录那些与代码相伴的深夜时光。',
 '## 深夜写代码\n\n### 凌晨一点的Bug\n那个死活调不通的Bug，在凌晨一点半终于找到了原因——少了一个等号。\n\n### 凌晨三点的灵感\n有时候最好的idea不是白天想出来的，而是深夜突然的灵光一现。\n\n### 凌晨五点的上线\n通宵上线后走在空旷的街道上，看着日出，觉得一切值得。\n\n即便现在不需要熬夜了，依然怀念那些深夜写代码的日子。',
 '<h2>深夜写代码</h2><p>那些与代码相伴的深夜时光。</p>',
 NULL, 3, 2, 1, 1200, 156, 7, 0, 1, '2026-05-18 23:00:00', '2026-05-18 23:00:00');

-- ============================================================
-- 4. 文章-标签关联（每篇文章2-4个标签）
-- ============================================================
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, 1), (1, 2), (1, 7),       -- Spring Boot 微服务: Java, Spring Boot, Docker
(2, 7), (2, 10),               -- Docker Compose: Docker, Linux
(3, 5), (3, 1),                -- Redis 缓存: Redis, Java
(4, 1), (4, 2), (4, 4),       -- MyBatis Plus: Java, Spring Boot, MySQL
(5, 1), (5, 2),                -- JWT: Java, Spring Boot
(6, 5), (6, 1),                -- 缓存策略: Redis, Java
(7, 3), (7, 6),                -- Vue 3: Vue.js, JavaScript
(8, 3), (8, 6),                -- Bootstrap: Vue.js, JavaScript
(9, 3), (9, 6),                -- Vite 5: Vue.js, JavaScript
(10, 1), (10, 2), (10, 8),    -- Spring AI: Java, Spring Boot, Python
(11, 8),                        -- 深度学习: Python
(12, 8),                        -- AI Agent: Python
(13, 1), (13, 9),              -- 程序员十年: Java, Git
(14, 9),                        -- 写博客: Git
(15, 1);                        -- 深夜写代码: Java

-- ============================================================
-- 5. 评论数据（30+条，含一级评论和二级回复）
-- ============================================================
INSERT INTO `comment` (`article_id`, `user_id`, `parent_id`, `reply_to_user_id`, `content`, `create_time`) VALUES
-- 文章1的评论 (Spring Boot 微服务)
(1, 4, NULL, NULL, '这篇文章太实用了！跟着搭建了一遍，微服务架构清晰多了', '2026-05-10 10:30:00'),
(1, 5, NULL, NULL, 'Nacos配置那里能再详细讲讲吗？我本地启动老是报错', '2026-05-10 11:00:00'),
(1, 1, 2, 5, '检查一下namespace和group是否匹配，还有就是bootstrap.yml要配置spring.config.import', '2026-05-10 11:30:00'),
(1, 6, NULL, NULL, '收藏了，正好在学Spring Cloud', '2026-05-11 09:00:00'),
(1, 7, NULL, NULL, 'Gateway的跨域配置困扰了我好久，感谢作者', '2026-05-12 14:00:00'),
(1, 1, 5, 7, '跨域问题确实是Gateway的常见坑，注意allowedOriginPatterns和allowCredentials的配合', '2026-05-12 15:00:00'),

-- 文章3的评论 (Redis)
(3, 5, NULL, NULL, 'Redis在博客系统中的应用场景总结得很全面！', '2026-05-11 08:00:00'),
(3, 4, NULL, NULL, 'INCR实现阅读量统计这个思路很好，比传统的先读后写优雅多了', '2026-05-11 10:00:00'),
(3, 6, NULL, NULL, '点赞去重用Set确实比数据库高效很多，学到了', '2026-05-12 16:00:00'),
(3, 8, NULL, NULL, '有没有Redis集群的部署方案？单机怕撑不住', '2026-05-13 08:00:00'),
(3, 1, 10, 8, '初期单机512MB足够，后面可以用Redis Cluster或者哨兵模式扩展', '2026-05-13 09:00:00'),
(3, 7, NULL, NULL, '搜索热词用ZSet倒排这个妙啊', '2026-05-14 11:00:00'),

-- 文章7的评论 (Vue 3)
(7, 4, NULL, NULL, '从Options API转Composition API确实需要适应一下，但习惯后真香', '2026-05-14 10:00:00'),
(7, 5, NULL, NULL, '组合函数大大提高了代码复用性，我们团队现在全面转向Composition API了', '2026-05-14 15:00:00'),
(7, 6, NULL, NULL, 'setup语法糖真的简洁，比原来的写法少很多样板代码', '2026-05-15 09:00:00'),

-- 文章13的评论 (程序员十年)
(13, 4, NULL, NULL, '看完很有感触，我也是2016年入行的', '2026-05-17 09:00:00'),
(13, 5, NULL, NULL, '写的太真实了，那个"少了一个等号"的Bug，每个程序员都遇到过', '2026-05-17 10:00:00'),
(13, 6, NULL, NULL, '十年如一日坚持下来不容易，向作者学习', '2026-05-17 14:00:00'),
(13, 7, NULL, NULL, '从这篇文章里看到了自己的影子', '2026-05-18 08:00:00'),
(13, 8, NULL, NULL, '写博客确实是最好的学习方式，深有体会', '2026-05-18 12:00:00'),
(13, 4, NULL, NULL, '我也是从实习开始写博客，现在已经200+篇了', '2026-05-19 10:00:00'),
(13, 5, 21, 4, '200+篇太强了！能分享一下你的博客地址吗？', '2026-05-19 11:00:00'),
(13, 4, 22, 5, '就在慧芯博客啊，哈哈哈', '2026-05-19 12:00:00'),

-- 其他文章的评论
(4, 5, NULL, NULL, 'LambdaQueryWrapper原来这么好用，我之前一直手写SQL', '2026-05-12 08:00:00'),
(4, 6, NULL, NULL, '自动填充功能省了不少代码', '2026-05-13 10:00:00'),
(6, 4, NULL, NULL, '缓存三大问题总结得很透彻', '2026-05-15 14:00:00'),
(6, 7, NULL, NULL, 'Lua脚本方案确实是最优解', '2026-05-16 09:00:00'),
(8, 4, NULL, NULL, '这个中国风配色方案我可以直接用了，太赞了', '2026-05-15 08:00:00'),
(8, 5, NULL, NULL, '楷体做标题确实好看，有古典韵味', '2026-05-16 10:00:00'),
(10, 6, NULL, NULL, 'Spring AI是未来的发展方向', '2026-05-19 08:00:00'),
(15, 4, NULL, NULL, '凌晨三点的Bug太真实了哈哈哈', '2026-05-20 08:00:00'),
(15, 5, NULL, NULL, '通宵上线后看到日出那一刻，值了', '2026-05-20 10:00:00');

-- ============================================================
-- 6. 点赞记录
-- ============================================================
INSERT INTO `like_record` (`user_id`, `article_id`, `create_time`) VALUES
(1, 3, '2026-05-10 10:00:00'), (1, 4, '2026-05-11 10:00:00'), (1, 5, '2026-05-12 10:00:00'),
(1, 6, '2026-05-13 10:00:00'), (1, 7, '2026-05-14 10:00:00'), (1, 10, '2026-05-15 10:00:00'),
(2, 1, '2026-05-10 11:00:00'), (2, 3, '2026-05-11 11:00:00'), (2, 7, '2026-05-12 11:00:00'),
(2, 8, '2026-05-13 11:00:00'), (2, 10, '2026-05-14 11:00:00'), (2, 13, '2026-05-17 11:00:00'),
(3, 2, '2026-05-10 12:00:00'), (3, 4, '2026-05-11 12:00:00'), (3, 6, '2026-05-12 12:00:00'),
(3, 9, '2026-05-13 12:00:00'), (3, 11, '2026-05-14 12:00:00'), (3, 14, '2026-05-17 12:00:00'),
(4, 1, '2026-05-10 13:00:00'), (4, 3, '2026-05-11 13:00:00'), (4, 7, '2026-05-12 13:00:00'),
(4, 8, '2026-05-13 13:00:00'), (4, 13, '2026-05-17 13:00:00'), (4, 15, '2026-05-20 13:00:00'),
(5, 1, '2026-05-10 14:00:00'), (5, 3, '2026-05-11 14:00:00'), (5, 4, '2026-05-12 14:00:00'),
(5, 6, '2026-05-13 14:00:00'), (5, 10, '2026-05-14 14:00:00'), (5, 13, '2026-05-17 14:00:00'),
(6, 1, '2026-05-10 15:00:00'), (6, 7, '2026-05-11 15:00:00'), (6, 8, '2026-05-12 15:00:00'),
(7, 3, '2026-05-11 16:00:00'), (7, 13, '2026-05-17 16:00:00'), (7, 15, '2026-05-20 16:00:00'),
(8, 6, '2026-05-13 17:00:00'), (8, 13, '2026-05-17 17:00:00');

-- ============================================================
-- 7. 文章统计
-- ============================================================
INSERT INTO `article_stats` (`article_id`, `total_view_count`, `total_like_count`, `total_comment_count`, `daily_view_count`, `daily_view_date`) VALUES
(1,  1280, 89,  6,  80, '2026-05-23'),
(2,  956,  67,  4,  45, '2026-05-23'),
(3,  2100, 156, 8,  120, '2026-05-23'),
(4,  1890, 132, 7,  95, '2026-05-23'),
(5,  1560, 98,  5,  70, '2026-05-23'),
(6,  2340, 178, 9,  110, '2026-05-23'),
(7,  1450, 112, 6,  85, '2026-05-23'),
(8,  890,  76,  3,  40, '2026-05-23'),
(9,  670,  45,  2,  30, '2026-05-23'),
(10, 1100, 89,  5,  65, '2026-05-23'),
(11, 780,  56,  3,  35, '2026-05-23'),
(12, 560,  34,  2,  25, '2026-05-23'),
(13, 3400, 267, 12,  200, '2026-05-23'),
(14, 920,  78,  4,  50, '2026-05-23'),
(15, 1200, 156, 7,  75, '2026-05-23');

-- ============================================================
-- 8. 更新分类和标签的文章计数（与数据保持一致）
-- ============================================================
UPDATE `category` SET `article_count` = 3 WHERE `category_name` = '技术分享';
UPDATE `category` SET `article_count` = 3 WHERE `category_name` = '生活随笔';
UPDATE `category` SET `article_count` = 3 WHERE `category_name` = '前端开发';
UPDATE `category` SET `article_count` = 3 WHERE `category_name` = '后端开发';
UPDATE `category` SET `article_count` = 3 WHERE `category_name` = '人工智能';

UPDATE `tag` SET `article_count` = 3 WHERE `tag_name` = 'Java';
UPDATE `tag` SET `article_count` = 3 WHERE `tag_name` = 'Spring Boot';
UPDATE `tag` SET `article_count` = 3 WHERE `tag_name` = 'Vue.js';
UPDATE `tag` SET `article_count` = 3 WHERE `tag_name` = 'Redis';
UPDATE `tag` SET `article_count` = 2 WHERE `tag_name` = 'Docker';
UPDATE `tag` SET `article_count` = 3 WHERE `tag_name` = 'JavaScript';
UPDATE `tag` SET `article_count` = 2 WHERE `tag_name` = 'Python';
UPDATE `tag` SET `article_count` = 1 WHERE `tag_name` = 'MySQL';
UPDATE `tag` SET `article_count` = 2 WHERE `tag_name` = 'Git';
UPDATE `tag` SET `article_count` = 1 WHERE `tag_name` = 'Linux';

-- ============================================================
-- 数据导入完成！
-- 账号汇总：
--   博主：zhangsan / lisi / wangwu (密码：123456)
--   用户：testuser / reader01 / reader02 / reader03 / reader04 (密码：123456)
-- ============================================================
