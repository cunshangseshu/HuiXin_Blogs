# Nacos 2.3 部署教程（Windows 本地 / Docker）

> 目标：Spring Cloud Alibaba 2023.0.1.0 配套 Nacos 2.3.x
> 项目配置：`localhost:8848`，命名空间 `huixin-blog`

---

## 方案 A：Windows 本地直接运行（推荐）

### 1. 下载

浏览器打开 https://github.com/alibaba/nacos/releases/tag/2.3.2

下载 `nacos-server-2.3.2.zip`

### 2. 解压

解压到项目同级目录：

```
D:\-OneDrive-\OneDrive\Desktop\JavaWeb的两个作业\nacos\
```

### 3. 启动

打开 **命令提示符**：

```cmd
cd "D:\-OneDrive-\OneDrive\Desktop\JavaWeb的两个作业\nacos\bin"
startup.cmd -m standalone
```

看到 `Nacos started successfully in standalone mode` 即成功。

### 4. 登录

浏览器打开 **http://localhost:8848/nacos**

| 字段 | 值 |
|------|-----|
| 用户名 | `nacos` |
| 密码 | `nacos` |

---

## 方案 B：Docker 部署（CentOS 7 @ 192.168.238.134）

### 1. 拉镜像

```bash
docker pull nacos/nacos-server:v2.3.2
```

### 2. 启动

```bash
docker run -d \
  --name nacos \
  --restart always \
  -p 8848:8848 \
  -p 9848:9848 \
  -e MODE=standalone \
  -e PREFER_HOST_MODE=hostname \
  nacos/nacos-server:v2.3.2
```

### 3. 开放端口

```bash
firewall-cmd --zone=public --add-port=8848/tcp --permanent
firewall-cmd --zone=public --add-port=9848/tcp --permanent
firewall-cmd --reload
```

### 4. 登录

浏览器打开 **http://192.168.238.134:8848/nacos**

| 字段 | 值 |
|------|-----|
| 用户名 | `nacos` |
| 密码 | `nacos` |

> ⚠️ 如果 Nacos 部署在 VM，项目里所有 `localhost:8848` 都要改成 `192.168.238.134:8848`

---

## 命名空间配置

登录 Nacos 控制台后：

### 1. 创建命名空间

左侧菜单 → **命名空间** → **新建命名空间**

| 字段 | 值 |
|------|-----|
| 命名空间名 | `huixin-blog` |
| 命名空间 ID | `huixin-blog` |
| 描述 | 慧芯博客微服务 |

### 2. 创建共享配置

左侧菜单 → **配置管理** → **配置列表** → 切换到 `huixin-blog` 命名空间 → **+** 新建配置

| 字段 | 值 |
|------|-----|
| Data ID | `common-config.yaml` |
| Group | `DEFAULT_GROUP` |
| 配置格式 | `YAML` |

内容：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/huixin_blog?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false
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

点击 **发布**。

---

## 验证

### 控制台

- 服务列表：`http://localhost:8848/nacos` → 服务管理 → 服务列表
- 配置列表：切换到 `huixin-blog` 命名空间查看 `common-config.yaml`

### 项目启动后检查

各微服务启动后，在 Nacos 控制台 **服务管理 → 服务列表** 中应看到：

```
huixin-gateway
huixin-auth
huixin-user
huixin-article
huixin-comment
huixin-search
huixin-stats
```

---

## 常见问题

| 现象 | 原因 | 解决 |
|------|------|------|
| 连不上 localhost:8848 | Nacos 没启动 | `startup.cmd -m standalone` |
| 服务注册不上 | 命名空间 ID 不匹配 | 确认 bootstrap.yml 中 `namespace: huixin-blog` |
| 共享配置拉不到 | Data ID 或 Group 不匹配 | 确认 `common-config.yaml` + `DEFAULT_GROUP` |
| 端口被占用 | 8848 已被使用 | `netstat -ano | findstr 8848` 查看占用 |
| Docker 启动报错 iptables | firewalld 清了 Docker 链 | `systemctl restart docker` |
