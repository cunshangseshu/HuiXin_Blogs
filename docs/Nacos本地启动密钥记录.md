# Nacos 本地启动鉴权密钥与凭证配置

> **更新时间：** 2026/05/24
> **背景说明：** 针对 Nacos 2.2.0+ 及其 3.x 版本服务端（Standalone 单机模式），在其首次启动或应用鉴权约束时，控制台会弹出拦截提示，要求强制提供一整套核心安全凭证，否则服务无法引导挂载。

为了确保开发体验一致，本项目已落实并确认以下三套定案凭证，供未来随时初始化本地 Nacos 服务环境时无缝复用。

---
## 0. 如何启动你的本地 Nacos 

在一切开始之前，你需要知道如何正确地在本地启动你的 Nacos 服务（单机开发模式）：

1. 打开你本地电脑上的 Nacos 文件夹。
2. 找到并双击进入 **`bin`** 目录（例如 `nacos/bin`）。
3. 在此文件夹的空白处，按住 `Shift` 键并点击鼠标右键，选择“在此处打开 PowerShell 窗口”；*或者*点击顶部文件夹路径栏，直接输入 `cmd` 后按回车。
4. 在弹出的黑色/蓝色命令行窗口中，输入并运行以下启动指代：
   ```cmd
   startup.cmd -m standalone
   ```
   *(注：必须要加上 `-m standalone`，否则由于 Nacos 默认是集群模式会启动报错！)*
5. 如果看到 `Nacos is starting` 字样，即代表启动序列执行中。在此刻如果有弹窗阻拦你的启动，请参考下方的第 1、第 2 章。

---


## 1. 定案密钥与身份对照表

遇到 CMD 拦截窗口输入需求时，请按照控制台的提问顺序精确贴入以下三组预设内容：

| 校验属性提示项 (Property) | 场景说明 | 本项目定案值输入 (Value) |
| :--- | :--- | :--- |
| `nacos.core.auth.plugin.nacos.token.secret.key` | **主鉴权 Secret Key**<br>限制大于 32 字节并要求 Base64 安全规范 | `SecretKey012345678901234567890123456789012345678901234567890123456789` |
| `nacos.core.auth.server.identity.key` | **集群身份标识 Key**<br>服务器节点互相验证的账名名称 | `huixinnacos` |
| `nacos.core.auth.server.identity.value` | **集群身份标识 Value**<br>服务器节点互相验证的账令值 | `huixin2024nacos` |

---

## 2. 永久固化配置教程（免命令行输入方案）

如果你觉得每次启动时都在弹窗中手动复制非常麻烦和耗时。可以按照如下思路，将上述我们在控制台录入的三组定案信息永远刻入到 Nacos 的原属文件中。

**操作路径与步骤：**
1. 请在你本地环境打开刚才我们下载并存放的 Nacos 目录树，进入配置专属目录：`nacos/conf/`。
2. 双击打开核心配置面板文件 **`application.properties`**（使用一般的记事本或 VSCode 均可）。
3. 查找并锁定关于 `auth.plugin` 及 `identity` 的如下三段声明，并将其等号身后的值填满（替换为你刚才亲自做出的定案）。

```properties
# 1. 替换 Token Secret 密钥
nacos.core.auth.plugin.nacos.token.secret.key=SecretKey012345678901234567890123456789012345678901234567890123456789

# 2. 补充 Identity Key 内部请求标头及 Value 匹配认证
nacos.core.auth.server.identity.key=huixinnacos
nacos.core.auth.server.identity.value=huixin2024nacos
```
4. 保存修改并关闭记事本。下一次你在主目录下执行 `startup.cmd -m standalone`，Nacos 就会直接识别这份“免签通行证”，实现纯享式的无感秒开。

---

## 3. Nacos 控制台登录凭证

当本地 Nacos 服务启动成功后，你可以通过浏览器访问控制台进行服务管理。出于项目统一规范，强烈建议在初始登录后，修改默认的认证信息。

- **控制台地址**：[http://127.0.0.1:8848/nacos](http://127.0.0.1:8848/nacos)
- **慧芯项目专属账号**：`huixin-nacos`
- **慧芯项目专属密码**：`huixin2024nacos`

*(注：如果你是全新解压的官方 Nacos，初始账号密码由系统默认提供均为 `nacos` / `nacos`。请在上方的控制台登录完毕后，前往左侧边栏底端的 `权限控制` -> `用户列表` 中，手动创建上述的慧芯专属账户并赋权，或直接将其作为日后的主力面板密钥)*
