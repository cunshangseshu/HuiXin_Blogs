# Redis 6/7 部署教程（CentOS 7）

> 目标机器：192.168.238.134（CentOS 7 虚拟机）
> 适用系统：CentOS 7 / RHEL 7

---

## 1. 登录虚拟机

```bash
ssh root@192.168.238.134
```

## 2. 安装 Redis（二选一）

### 方案 A：yum 安装 Redis 6（推荐，简单稳定）

```bash
# 安装 EPEL 仓库和工具
yum install -y epel-release yum-utils

# 安装 Remi 仓库
yum install -y https://rpms.remirepo.net/enterprise/remi-release-7.rpm

# 启用 Remi 仓库
yum-config-manager --enable remi

# 安装 Redis
yum --enablerepo=remi install -y redis
```

验证：

```bash
redis-server --version
# Redis server v=6.x ...
```

### 方案 B：编译安装 Redis 7（需要最新版时使用）

```bash
yum install -y gcc make wget
cd /tmp
wget https://download.redis.io/releases/redis-7.2.4.tar.gz
tar xzf redis-7.2.4.tar.gz
cd redis-7.2.4
make -j$(nproc)
make install PREFIX=/usr/local/redis

# 复制配置文件到标准位置
cp /tmp/redis-7.2.4/redis.conf /etc/redis.conf

# 添加 PATH
echo 'export PATH=$PATH:/usr/local/redis/bin' >> ~/.bashrc
source ~/.bashrc

# 创建 systemd 服务文件
cat > /etc/systemd/system/redis.service << 'EOF'
[Unit]
Description=Redis Server
After=network.target

[Service]
Type=forking
ExecStart=/usr/local/redis/bin/redis-server /etc/redis.conf
ExecStop=/usr/local/redis/bin/redis-cli -a huixin2024redis shutdown
Restart=always

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
```

## 3. 配置 Redis（关键！）

```bash
vi /etc/redis.conf
```

按以下内容修改（用 `/` 搜索关键词）：

```ini
# ==== 网络 ====
bind 0.0.0.0
# 有 requirepass 时外部可连接，无密码时仅本地可连；设 yes 更安全
protected-mode yes
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
# 注意：关闭 SELinux 会降低系统安全性，仅建议开发 VM 这样做
# 生产环境应使用：semanage port -a -t redis_port_t -p tcp 6379
```

## 6. 测试

### 本地测试

```bash
redis-cli
127.0.0.1:6379> AUTH huixin2024redis
127.0.0.1:6379> PING
PONG
```

### Windows 远程测试

```bash
# 方法1：如果有 Redis CLI（下载 https://github.com/tporadowski/redis/releases）
.\redis-cli.exe -h 192.168.238.134 -p 6379 -a huixin2024redis PING

# 方法2：用 telnet 测试端口连通性
telnet 192.168.238.134 6379
# 看到 Escape character is '^]' 即连通，Ctrl+] 退出
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
| DENIED protected mode | 有密码但 protect-mode=yes 且没 bind 本地 | 连接时加 `-a huixin2024redis` 或设 `bind 127.0.0.1` |
| NOAUTH | 需要密码 | `AUTH huixin2024redis` |
| OOM | 内存超限 | 调大 `maxmemory` |
| 重启数据丢了 | AOF没开 | 改 `appendonly yes` |

## 9. 部署完后

项目6个模块的 `application.yml` 中 Redis 密码需要从空改为 `huixin2024redis`。告诉我一声我来批量改。

## 10. GUI 工具

- RedisInsight: https://redis.com/redis-enterprise/redis-insight/
- Another Redis Desktop: https://github.com/qishibo/AnotherRedisDesktopManager
