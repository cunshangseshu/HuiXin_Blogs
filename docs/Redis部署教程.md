# Redis 6/7 部署教程（CentOS 7）

> 目标机器：192.168.238.134（CentOS 7 虚拟机）
> 适用系统：CentOS 7 / RHEL 7

---

## 1. 登录虚拟机

```bash
ssh root@192.168.238.134
```

## 2. 安装 Redis

CentOS 7 自带的 Redis 太旧（3.x），用 Remi 仓库装 Redis 6.x：

```bash
# 安装 EPEL 仓库
yum install -y epel-release

# 安装 Remi 仓库
yum install -y https://rpms.remirepo.net/enterprise/remi-release-7.rpm

# 启用 Remi 的 Redis 6 模块
yum-config-manager --enable remi

# 安装 Redis
yum install -y redis
```

验证：

```bash
redis-cli --version
# redis-cli 6.x
```

**想装 Redis 7？** 编译安装：

```bash
yum install -y gcc make wget
cd /tmp
wget https://download.redis.io/releases/redis-7.2.4.tar.gz
tar xzf redis-7.2.4.tar.gz
cd redis-7.2.4
make -j$(nproc)
make install PREFIX=/usr/local/redis
```

## 3. 配置 Redis（关键！）

```bash
vi /etc/redis.conf
```

按以下内容修改（用 `/` 搜索关键词）：

```ini
# ==== 网络 ====
bind 0.0.0.0
protected-mode no
port 6379

# ==== 内存（Redis Skill：必须设 maxmemory！）====
maxmemory 512mb
maxmemory-policy allkeys-lru

# ==== 持久化 ====
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec

# ==== 安全 ====
requirepass huixin2024redis
```

## 4. 启动 Redis

```bash
systemctl start redis
systemctl enable redis
systemctl status redis
```

## 5. 开放端口（CentOS 7 两道关）

```bash
# 5.1 firewalld
firewall-cmd --zone=public --add-port=6379/tcp --permanent
firewall-cmd --reload

# 5.2 SELinux（CentOS 7 最常见坑！连不上99%因为它）
setenforce 0                          # 临时关闭测试
# 永久关闭: vi /etc/selinux/config → SELINUX=disabled → 重启
```

## 6. 测试

```bash
# 本地测试
redis-cli
127.0.0.1:6379> AUTH huixin2024redis
127.0.0.1:6379> PING
PONG

# Windows 远程测试
.\redis-cli.exe -h 192.168.238.134 -p 6379 -a huixin2024redis PING
```

## 7. 预热（项目数据结构）

```bash
redis-cli -h 192.168.238.134 -a huixin2024redis

# 阅读量 INCR
INCR article:views:1
GET article:views:1
# → "1"

# 点赞 Set
SADD article:like_users:1 1001
SISMEMBER article:like_users:1 1001
# → 1

# 热门排行 ZSet
ZADD article:hot:rank 10 "1"
ZADD article:hot:rank 5 "2"
ZINCRBY article:hot:rank 3 "1"
ZREVRANGE article:hot:rank 0 -1 WITHSCORES

# 搜索热词
ZINCRBY search:hot_keywords 1 "Spring Boot"
ZREVRANGE search:hot_keywords 0 -1 WITHSCORES
```

## 8. 踩坑速查

| 现象 | 原因 | 解决 |
|------|------|------|
| Could not connect | 防火墙 | `firewall-cmd --add-port=6379/tcp --permanent` |
| telnet通但redis连不上 | SELinux | `setenforce 0` |
| DENIED protected mode | protected-mode没关 | 改 `protected-mode no` |
| NOAUTH | 需要密码 | `AUTH huixin2024redis` |
| OOM | 内存超限 | 调大 `maxmemory` |
| 重启数据丢了 | AOF没开 | 改 `appendonly yes` |

## 9. 部署完后

项目6个模块的 `application.yml` 中 Redis 密码需要从空改为 `huixin2024redis`。告诉我一声我来批量改。

## 10. GUI 工具

- RedisInsight: https://redis.com/redis-enterprise/redis-insight/
- Another Redis Desktop: https://github.com/qishibo/AnotherRedisDesktopManager
