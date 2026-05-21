# 慧芯博客 — Nacos 注册 & 配置中心部署

> 项目：慧芯博客（Huixin Blog）
> Spring Cloud Alibaba 2023.0.1.0 · Nacos 2.3.2
> 作者：李雪莹

---

## 一、在哪里跑

你已经在 CentOS 7 虚拟机（`192.168.238.134`）上用 Docker 跑着 Nacos 了，不用重新部署。

如果没有，一条命令：

```bash
docker run -d \
  --name huixin-nacos \
  --restart always \
  -p 8848:8848 \
  -p 9848:9848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.3.2
```

---

## 二、登录

浏览器打开：

```
http://192.168.238.134:8848/nacos
```

| 字段 | 值 |
|------|-----|
| 用户名 | `nacos` |
| 密码 | `nacos` |

> 进去之后改密码：**权限控制 → 用户列表 → admin → 修改密码**
> 
> 改成：`huixin2024#Nacos`（慧芯博客专属）

---

## 三、创建命名空间

> 为什么建命名空间？把慧芯博客的所有服务和配置隔离在一个独立空间里，不跟其他项目混在一起。

左侧菜单 → **命名空间** → **新建命名空间**

| 字段 | 值 |
|------|-----|
| 命名空间 ID | `huixin-blog` |
| 命名空间名 | `慧芯博客` |
| 描述 | 慧芯博客微服务 — 李雪莹 |

保存后，页面顶部命名空间下拉框里会多出"慧芯博客"，以后所有操作先切到这个空间。

---

## 四、创建共享配置

> 6 个微服务模块共用的数据库、Redis、MyBatis Plus 配置，不用每个模块的 application.yml 各写一份。

左侧菜单 → **配置管理** → **配置列表** → 切到"慧芯博客"命名空间 → **+** 新建配置

| 字段 | 值 |
|------|-----|
| **Data ID** | `huixin-blog-common.yaml` |
| **Group** | `HUIXIN_GROUP` |
| 配置格式 | `YAML` |
| 描述 | 慧芯博客公共配置 — 数据库 / Redis / ORM |

### 配置内容

```yaml
# ==========================================
#  慧芯博客（Huixin Blog）公共配置
#  共享范围：huixin-auth / huixin-user
#           huixin-article / huixin-comment
#           huixin-search / huixin-stats
# ==========================================

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/huixin_blog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
  data:
    redis:
      host: 192.168.238.134
      port: 6379
      password: huixin2024redis
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.huixin.common.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true

knife4j:
  enable: ${KNIFE4J_ENABLE:true}
  setting:
    language: zh_cn
```

填好后点击 **发布**。

---

## 五、修改项目的 bootstrap.yml

共享配置创建好后，所有微服务模块的 `bootstrap.yml` 里引用的 Data ID 和 Group 要跟 Nacos 上的一致。

每个模块（`huixin-auth`、`huixin-user`……）的 `bootstrap.yml` 改成：

```yaml
spring:
  application:
    name: huixin-auth        # 这里每个模块不同
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.238.134:8848
        namespace: huixin-blog
        group: HUIXIN_GROUP
      config:
        server-addr: 192.168.238.134:8848
        namespace: huixin-blog
        group: HUIXIN_GROUP
        file-extension: yaml
        refresh-enabled: true
        shared-configs:
          - data-id: huixin-blog-common.yaml
            group: HUIXIN_GROUP
            refresh: true
```

> 因为 Nacos 跑在 VM 而不是本地，`server-addr` 要从 `localhost:8848` 改成 `192.168.238.134:8848`

---

## 六、验证

全部微服务启动后，打开 Nacos 控制台 → **服务管理 → 服务列表**，切到"慧芯博客"命名空间，应看到 7 个服务：

```
huixin-gateway           ← API 网关 :8080
huixin-auth              ← 认证服务 :8081
huixin-user              ← 用户服务 :8082
huixin-article           ← 文章服务 :8083
huixin-comment           ← 评论服务 :8084
huixin-search            ← 搜索服务 :8085
huixin-stats             ← 统计服务 :8086
```

---

## 七、常见问题

| 现象 | 原因 | 解决 |
|------|------|------|
| 8848 连不上 | VM 防火墙没开 | `firewall-cmd --add-port=8848/tcp --permanent && firewall-cmd --reload` |
| 服务注册不上 | 命名空间 ID 或 Group 不匹配 | 确认 bootstrap.yml 里 `namespace: huixin-blog` + `group: HUIXIN_GROUP` |
| 共享配置拉不到 | Data ID 不匹配 | Nacos 上叫 `huixin-blog-common.yaml`，bootstrap.yml 里也叫这个 |
| Docker iptables 报错 | firewalld 清了 Docker 链 | `systemctl restart docker` |
| 登录后看不到服务 | 没切命名空间 | 顶部下拉框选"慧芯博客" |
