# Redis 7 部署教程（Docker · CentOS 7）

> 目标机器：192.168.238.134（CentOS 7 虚拟机）
> 部署方式：Docker 容器

---

## 1. 登录虚拟机

```bash
ssh root@192.168.238.134
```

## 2. 安装 Docker

```bash
# CentOS 7 安装 Docker（官方脚本，一步到位）
curl -fsSL https://get.docker.com | bash -s docker

# 启动 Docker
systemctl start docker
systemctl enable docker

# 验证
docker --version
# Docker version 2x.x.x ...
```

> 如果 `get.docker.com` 访问不了，用阿里云镜像：
> ```bash
> curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
> ```

## 3. 准备数据目录

```bash
mkdir -p /opt/redis/data
mkdir -p /opt/redis/logs
```

## 4. 启动 Redis 容器（一条命令搞定）

```bash
docker run -d \
  --name huixin-redis \
  --restart always \
  -p 6379:6379 \
  -v /opt/redis/data:/data \
  -v /opt/redis/logs:/logs \
  redis:7-alpine \
  redis-server \
    --requirepass huixin2024redis \
    --maxmemory 512mb \
    --maxmemory-policy allkeys-lru \
    --appendonly yes \
    --appendfsync everysec \
    --save 900 1 \
    --save 300 10 \
    --save 60 10000 \
    --loglevel notice \
    --logfile /logs/redis.log
```

> 参数说明：
> | 参数 | 含义 |
> |------|------|
> | `--name huixin-redis` | 容器名称 |
> | `--restart always` | 开机自启 / 崩溃重启 |
> | `-p 6379:6379` | 端口映射 |
> | `-v /opt/redis/data:/data` | 持久化数据挂载 |
> | `redis:7-alpine` | Redis 7 镜像（Alpine 版，极小体积） |

## 5. 常用管理命令

```bash
# 查看状态
docker ps | grep redis

# 查看日志
docker logs huixin-redis

# 进入 Redis CLI
docker exec -it huixin-redis redis-cli -a huixin2024redis

# 重启
docker restart huixin-redis

# 停止
docker stop huixin-redis

# 删除（数据在 /opt/redis/data 里不会丢）
docker rm -f huixin-redis
```

## 6. 开放端口

```bash
firewall-cmd --zone=public --add-port=6379/tcp --permanent
firewall-cmd --reload

# Docker 端口默认绕过 SELinux，无需额外配置
```

## 7. 测试

### VM 本地测试

```bash
docker exec -it huixin-redis redis-cli -a huixin2024redis PING
# PONG
```

### Windows 远程测试

```bash
# 方法1：Redis CLI
.\redis-cli.exe -h 192.168.238.134 -p 6379 -a huixin2024redis PING

# 方法2：telnet
telnet 192.168.238.134 6379
# 看到 Escape character is '^]' 即连通
```

## 8. 预热（验证项目数据结构）

```bash
docker exec -it huixin-redis redis-cli -a huixin2024redis
```

```redis
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

## 9. 踩坑速查

| 现象 | 原因 | 解决 |
|------|------|------|
| Could not connect | 防火墙没开 | `firewall-cmd --add-port=6379/tcp --permanent && firewall-cmd --reload` |
| NOAUTH | 需要密码 | 连接时加 `-a huixin2024redis` |
| Docker 未启动 | 服务没开 | `systemctl start docker` |
| 容器挂了 | 查看原因 | `docker logs huixin-redis` |

## 10. GUI 工具

- **RedisInsight**：https://redis.com/redis-enterprise/redis-insight/
- **Another Redis Desktop**：https://github.com/qishibo/AnotherRedisDesktopManager
