# Redis 7 部署教程（Ubuntu/Debian）

> 目标机器：192.168.238.134（你的Linux虚拟机）  
> 适用系统：Ubuntu 22.04 / Debian 11+  

---

## 1. 登录虚拟机

```bash
ssh root@192.168.238.134
# 或通过虚拟机管理软件的控制台登录
```

## 2. 安装 Redis

```bash
# 更新包列表
sudo apt update

# 安装 Redis（Ubuntu 22.04 默认源是 v6，装最新的用官方源）
sudo apt install -y redis-server
```

验证安装：

```bash
redis-cli --version
# 输出: redis-cli 6.x 或 7.x
```

## 3. 配置 Redis（关键步骤）

编辑配置文件：

```bash
sudo vi /etc/redis/redis.conf
```

按以下内容修改（用 `/` 搜索关键词定位）：

```ini
# ========== 网络 ==========

# 1. 允许远程连接（默认只绑 127.0.0.1）
# 找到 bind 127.0.0.1，改成：
bind 0.0.0.0

# 2. 保护模式关闭（允许非本地连接）
protected-mode no

# 3. 监听端口（默认即可）
port 6379


# ========== 内存（参考 Redis Skill） ==========

# 4. 必须设置最大内存！否则会吃光所有RAM
# 根据你的虚拟机内存来，假设分配了2G给VM，Redis给512M
maxmemory 512mb

# 5. 内存淘汰策略：allkeys-lru（适合纯缓存场景）
maxmemory-policy allkeys-lru


# ========== 持久化（参考 Redis Skill） ==========

# 6. RDB 快照（默认开启，保持即可）
save 900 1
save 300 10
save 60 10000

# 7. AOF 追加日志（推荐开启，数据更安全）
appendonly yes
appendfsync everysec


# ========== 安全 ==========

# 8. 设置密码（生产环境必须！）
# 找到 # requirepass foobared，取消注释并修改：
requirepass huixin2024redis
```

保存退出：`ESC` → `:wq`

## 4. 启动 Redis

```bash
# 重启服务使配置生效
sudo systemctl restart redis-server

# 设置开机自启
sudo systemctl enable redis-server

# 查看状态
sudo systemctl status redis-server
```

看到 `active (running)` 就成功了。

## 5. 开放防火墙

```bash
# 如果开了 ufw 防火墙
sudo ufw allow 6379/tcp

# 如果用的是 iptables
sudo iptables -A INPUT -p tcp --dport 6379 -j ACCEPT
```

## 6. 测试连接

```bash
# 本地测试
redis-cli
127.0.0.1:6379> AUTH huixin2024redis
OK
127.0.0.1:6379> PING
PONG
127.0.0.1:6379> SET test "hello"
OK
127.0.0.1:6379> GET test
"hello"
127.0.0.1:6379> EXIT
```

然后从你的 Windows 主机测试远程连接：

```powershell
# Windows 上安装 redis-cli（或使用 RedisInsight GUI）
redis-cli -h 192.168.238.134 -p 6379 -a huixin2024redis PING
```

## 7. 更新项目配置

Redis 配置了密码，需要更新 `application.yml`：

```yaml
spring:
  data:
    redis:
      host: 192.168.238.134
      port: 6379
      password: huixin2024redis    # ← 之前留空，现在填上
      database: 0
```

你项目的6个模块都需要改这一行。要我帮你批量改吗？

## 8. 验证 Redis 数据结构（预热测试）

```bash
redis-cli -h 192.168.238.134 -a huixin2024redis

# 测试 String INCR（阅读量）
INCR article:views:1
INCR article:views:1
GET article:views:1
# → "2"

# 测试 Set（点赞记录）
SADD article:like_users:1 1001
SADD article:like_users:1 1002
SISMEMBER article:like_users:1 1001
# → 1（已点赞）

# 测试 ZSet（热门排行）
ZADD article:hot:rank 10 "1"
ZADD article:hot:rank 5 "2"
ZINCRBY article:hot:rank 3 "1"
ZREVRANGE article:hot:rank 0 -1 WITHSCORES
# → 文章1排第一，文章2排第二

EXIT
```

## 9. 常见问题排查

| 问题 | 解决 |
|------|------|
| 连不上 6379 | `ss -tlnp \| grep 6379` 确认监听 0.0.0.0 |
| 连上但被拒 | 检查 `requirepass` 密码是否正确 |
| 内存不足 | `INFO memory` 查看，调低 `maxmemory` |
| 数据丢了 | 检查 `appendonly yes` 是否开启 |
| 连接慢 | 关闭 `protected-mode`，检查防火墙 |

## 10. 可选：安装 RedisInsight（GUI管理工具）

在 Windows 上下载 RedisInsight：https://redis.com/redis-enterprise/redis-insight/

连接配置：
- Host: `192.168.238.134`
- Port: `6379`
- Password: `huixin2024redis`

可视化查看 Key、执行命令、监控性能。
