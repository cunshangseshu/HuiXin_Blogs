# Nacos 本地部署与运行指南

> **更新说明：**
> 鉴于微服务架构带来的资源消耗，若将 Nacos 与其他依赖（如 Redis、MySQL）一起部署在低配的 Linux 虚拟机或 Docker 中，极易引发内存耗尽（OOM）。
> 为了保障本地开发的顺畅体验，“慧芯博客”决定全面采用 **“Nacos 本地化运行 + Redis 远程直连”** 的开发环境模式。本指南用于指导如何在本地开发机（Windows/Mac）启动轻量级 Nacos。

---

## 1. 下载 Nacos
1. 访问 Nacos 官方 GitHub Release 页面：[https://github.com/alibaba/nacos/releases](https://github.com/alibaba/nacos/releases)
2. 建议下载最新稳定版本（例如 Nacos `2.3.x` 或以上版本）的 `.zip` 压缩包。
3. 将下载的压缩包解压到了你的本地计算机（例如 `D:\Nacos\nacos`），注意路径中**不要有中文字符或空格**。

## 2. 启动 Nacos (单机模式)
Nacos 默认是集群启动模式，为了节省性能和快速启动，在本地开发环境我们需要以**单机（Standalone）模式**运行：

### Windows 用户：
1. 打开 `nacos/bin` 目录。
2. 在该目录下打开命令提示符（cmd）或 PowerShell。
3. 执行以下命令：
   ```cmd
   startup.cmd -m standalone
   ```
4. 如果出现类似 `Nacos started successfully in stand alone mode.` 的提示语，即代表本地服务注册中心已经成功上线。

### Mac / Linux 用户（仅作补充）：
```bash
sh startup.sh -m standalone
```

## 3. 登录 Nacos 控制台
1. 打开浏览器访问：[http://127.0.0.1:8848/nacos](http://127.0.0.1:8848/nacos)
2. 默认账号：`nacos`
3. 默认密码：`nacos`

## 4. 连接微服务
为了完全剥离繁冗的中心化配置（`bootstrap.yml`），目前“慧芯博客”的所有微服务均依赖服务自身的 `application.yml` 进行直指。

当你在 IDEA 中启动相关的业务模块（例：`huixin-article` 等），并在 Nacos 启动的状态下，
各个微服务将会自动根据 `server-addr: 127.0.0.1:8848` 寻址。
你可以进入 Nacos 控制台左侧的 **“服务管理 -> 服务列表”** 中，查看那些微服务是否已经成功注册上线。

### 常见启动报错补充
*   **报错：`nacos.core.auth.plugin.nacos.token.secret.key value is empty...`**
    *   **原因**：从 Nacos 2.2.0 版本开始，出于安全考虑，默认要求用户配置鉴权秘钥（即便不需要外网鉴权）。
    *   **解决方法（临时跑通）**：你可以在控制台直接输入一个长度大于 32 位的 Base64 字符串作为临时秘钥，例如输入：`U2VjcmV0S2V5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=` 然后回车。
    *   **彻底解决**：用记事本打开 `Nacos目录\conf\application.properties`，找到：
        ```properties
        nacos.core.auth.plugin.nacos.token.secret.key=
        ```
        将其修改为：
        ```properties
        nacos.core.auth.plugin.nacos.token.secret.key=U2VjcmV0S2V5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=
        ```
        保存后再重新跑 `startup.cmd -m standalone`，它就不会再要求你输入一遍了。
