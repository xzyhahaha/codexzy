# codexzy

一个基于 Spring Boot 的个人打卡与备忘录管理系统。

## 当前状态

当前仓库只完成了项目初始化，业务功能还没有实现。

已完成内容：

- Spring Boot 3.2.0 Maven 工程骨架
- Java 包结构与资源目录初始化
- `application.yml` 基础配置
- 上传目录初始化
- 基础测试类
- `.gitignore` 初始化

待实现内容：

- 用户注册、登录、个人信息管理
- 打卡记录增删改查
- 日历视图与统计面板
- 备忘录分类与文件管理
- Spring Security 权限控制
- MyBatis-Plus 实体、Mapper、Service、Controller
- Thymeleaf 页面与静态资源
- 数据库建表脚本

## 技术栈

- Java 17
- Spring Boot 3.2.x
- Spring Security
- Thymeleaf
- MyBatis-Plus
- MySQL 8.x
- Maven
- Lombok

## 目录结构

```text
codexzy/
├─ src/main/java/com/codexzy
│  ├─ config
│  ├─ controller
│  ├─ dto
│  ├─ entity
│  ├─ mapper
│  ├─ service
│  ├─ util
│  └─ CodexzyApplication.java
├─ src/main/resources
│  ├─ application.yml
│  ├─ mapper
│  ├─ static
│  └─ templates
├─ src/test/java/com/codexzy
├─ uploads
└─ docs
```

## 初始化步骤

1. 安装 JDK 17 或更高版本。
2. 确认 `JAVA_HOME` 指向 JDK 17。
3. 确认 `mvn -v` 输出中的 Java 版本是 17 或更高。
4. 创建本地数据库 `codexzy`。
5. 按需修改 `src/main/resources/application.yml` 中的数据库账号密码。
6. 执行：

```bash
mvn clean compile
```

## 环境说明

当前这台机器上 Maven 已可用，但实际绑定的是 Java 8，因此现在直接编译会失败。

典型错误：

```text
Fatal error compiling: 无效的标记: --release
```

这不是项目 POM 的问题，而是 JDK 版本不符合 Spring Boot 3 的要求。

## Git 说明

当前远端仓库地址：

```text
https://github.com/xzyhahaha/codexzy.git
```

当前主分支名称：

```text
main
```

## 后续建议

建议按下面顺序继续实现：

1. 修正本机 JDK 17 环境
2. 初始化数据库脚本和实体类
3. 完成登录注册与安全配置
4. 完成打卡模块
5. 完成备忘录模块
6. 补齐页面模板和测试
