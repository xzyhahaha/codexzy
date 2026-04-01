# 打卡系统设计文档

## 项目概述

这是一个基于 Spring Boot 的个人打卡和备忘录管理系统，支持多用户使用。用户可以记录每天的学习打卡信息（包括文字和附件），管理个人备忘录文件（如简历、证书等），并查看打卡统计数据。

## 技术选型

### 后端技术栈
- **Spring Boot 3.x** - 核心框架
- **MyBatis-Plus** - 数据库 ORM 框架，简化 CRUD 操作
- **Spring Security** - 用户认证和授权
- **MySQL 8.x** - 关系型数据库
- **Thymeleaf** - 服务端模板引擎

### 前端技术栈
- **Bootstrap 5** - 响应式 UI 框架
- **jQuery** - JavaScript 库（简化 DOM 操作）
- **Bootstrap Icons** - 图标库

### 开发工具
- **Maven** - 项目构建工具
- **Lombok** - 简化 Java 代码

## 系统架构

### 项目结构
```
codexzy/
├── src/main/java/com/codexzy/
│   ├── controller/          # 控制器层
│   │   ├── AuthController.java          # 登录注册
│   │   ├── CheckInController.java       # 打卡管理
│   │   ├── MemoController.java          # 备忘录管理
│   │   └── UserController.java          # 用户信息
│   ├── service/             # 业务逻辑层
│   │   ├── UserService.java
│   │   ├── CheckInService.java
│   │   ├── MemoService.java
│   │   └── FileService.java
│   ├── mapper/              # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── CheckInMapper.java
│   │   ├── CheckInFileMapper.java
│   │   ├── MemoCategoryMapper.java
│   │   └── MemoFileMapper.java
│   ├── entity/              # 实体类
│   │   ├── User.java
│   │   ├── CheckIn.java
│   │   ├── CheckInFile.java
│   │   ├── MemoCategory.java
│   │   └── MemoFile.java
│   ├── dto/                 # 数据传输对象
│   │   ├── CheckInDTO.java
│   │   ├── CheckInStatDTO.java
│   │   └── MemoFileDTO.java
│   ├── config/              # 配置类
│   │   ├── SecurityConfig.java          # Spring Security 配置
│   │   ├── WebMvcConfig.java            # Web MVC 配置
│   │   └── MyBatisPlusConfig.java       # MyBatis-Plus 配置
│   └── util/                # 工具类
│       ├── FileUtil.java                # 文件处理工具
│       └── DateUtil.java                # 日期工具
├── src/main/resources/
│   ├── templates/           # Thymeleaf 模板
│   │   ├── login.html                   # 登录页面
│   │   ├── register.html                # 注册页面
│   │   ├── index.html                   # 首页/仪表盘
│   │   ├── checkin/
│   │   │   ├── list.html                # 打卡列表
│   │   │   ├── calendar.html            # 日历视图
│   │   │   └── form.html                # 新建/编辑打卡
│   │   ├── memo/
│   │   │   └── index.html               # 备忘录管理
│   │   └── user/
│   │       └── profile.html             # 个人设置
│   ├── static/              # 静态资源
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   ├── mapper/              # MyBatis XML 映射文件
│   └── application.yml      # 配置文件
└── uploads/                 # 文件上传目录
    ├── checkin/             # 打卡附件
    └── memo/                # 备忘录文件
```

### 分层架构说明

**Controller 层**
- 处理 HTTP 请求和响应
- 参数验证
- 返回视图或 JSON 数据

**Service 层**
- 核心业务逻辑
- 事务管理
- 调用 Mapper 层进行数据操作

**Mapper 层**
- 数据库访问
- 使用 MyBatis-Plus 简化 CRUD
- 复杂查询使用 XML 映射文件

**Entity 层**
- 对应数据库表结构
- 使用 Lombok 简化 getter/setter

## 数据库设计

### 用户表 (user)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL |
| password | VARCHAR(100) | 密码（加密） | NOT NULL |
| email | VARCHAR(100) | 邮箱 | NOT NULL |
| nickname | VARCHAR(50) | 昵称 | |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

**索引**
- PRIMARY KEY (id)
- UNIQUE KEY (username)

### 打卡记录表 (check_in)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL |
| title | VARCHAR(200) | 标题 | NOT NULL |
| content | TEXT | 内容 | |
| check_date | DATE | 打卡日期 | NOT NULL |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

**索引**
- PRIMARY KEY (id)
- INDEX (user_id, check_date)

### 打卡附件表 (check_in_file)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| check_in_id | BIGINT | 打卡记录ID | NOT NULL |
| file_name | VARCHAR(200) | 原始文件名 | NOT NULL |
| file_path | VARCHAR(500) | 存储路径 | NOT NULL |
| file_size | BIGINT | 文件大小（字节） | |
| file_type | VARCHAR(50) | 文件类型（MIME） | |
| create_time | DATETIME | 创建时间 | |

**索引**
- PRIMARY KEY (id)
- INDEX (check_in_id)

### 备忘录分类表 (memo_category)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL |
| category_name | VARCHAR(50) | 分类名称 | NOT NULL |
| sort_order | INT | 排序 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | |

**索引**
- PRIMARY KEY (id)
- INDEX (user_id)

### 备忘录文件表 (memo_file)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL |
| category_id | BIGINT | 分类ID | NOT NULL |
| file_name | VARCHAR(200) | 原始文件名 | NOT NULL |
| file_path | VARCHAR(500) | 存储路径 | NOT NULL |
| file_size | BIGINT | 文件大小 | |
| file_type | VARCHAR(50) | 文件类型 | |
| remark | VARCHAR(500) | 备注说明 | |
| create_time | DATETIME | 创建时间 | |

**索引**
- PRIMARY KEY (id)
- INDEX (user_id, category_id)

## 核心功能模块

### 1. 用户模块

**用户注册**
- 输入：用户名、密码、邮箱
- 验证：用户名唯一性检查
- 处理：密码使用 BCrypt 加密存储
- 输出：注册成功跳转到登录页

**用户登录**
- 使用 Spring Security 进行认证
- 登录成功创建 Session
- 记住我功能（可选）
- 登录失败提示错误信息

**个人信息管理**
- 查看个人信息
- 修改昵称、邮箱
- 修改密码（需要验证旧密码）

### 2. 打卡模块

**创建打卡记录**
- 填写标题和内容
- 可选上传多个附件
- 自动记录打卡日期（当天）
- 文件验证：类型和大小限制

**查看打卡列表**
- 分页显示（每页 20 条）
- 按日期倒序排列
- 显示标题、日期、附件数量
- 支持编辑和删除

**日历视图**
- 显示当月日历
- 标记有打卡的日期
- 点击日期查看当天的打卡记录
- 支持切换月份

**打卡统计**
- 总打卡天数
- 连续打卡天数
- 本月打卡次数
- 显示在首页仪表盘

**编辑打卡记录**
- 修改标题和内容
- 添加或删除附件
- 不能修改打卡日期

**删除打卡记录**
- 删除记录同时删除关联的附件文件
- 需要确认操作

### 3. 备忘录模块

**分类管理**
- 创建分类（如"简历"、"证书"、"项目文档"）
- 编辑分类名称
- 删除分类（需要先删除分类下的所有文件）
- 分类排序

**文件上传**
- 选择分类
- 上传文件
- 添加备注说明
- 文件验证

**文件列表**
- 按分类展示
- 显示文件名、大小、上传时间、备注
- 支持下载和删除
- 图片文件支持预览

**文件下载**
- 点击下载按钮
- 浏览器下载文件

**文件删除**
- 删除数据库记录
- 删除物理文件
- 需要确认操作

### 4. 文件管理

**文件上传处理**
- 文件类型验证（白名单）
  - 文档：pdf, doc, docx, xls, xlsx, ppt, pptx, txt
  - 图片：jpg, jpeg, png, gif, bmp
  - 压缩包：zip, rar
- 文件大小限制：单个文件最大 10MB
- 文件名处理：使用 UUID 重命名，避免冲突
- 存储路径：
  - 打卡附件：`uploads/checkin/{userId}/{yyyyMM}/{uuid}.{ext}`
  - 备忘录文件：`uploads/memo/{userId}/{categoryId}/{uuid}.{ext}`

**文件存储**
- 本地文件系统存储
- 按用户和日期分目录
- 数据库记录文件元信息

**文件下载**
- 权限检查：只能下载自己的文件
- 设置正确的 Content-Type
- 支持中文文件名

**文件删除**
- 先删除数据库记录
- 再删除物理文件
- 事务保证一致性

## 核心业务流程

### 用户注册登录流程
```
1. 用户访问系统 -> 未登录 -> 跳转到登录页
2. 点击注册 -> 填写用户名、密码、邮箱
3. 提交注册 -> 后端验证
   - 用户名是否已存在
   - 邮箱格式是否正确
   - 密码强度检查（可选）
4. 验证通过 -> 密码 BCrypt 加密 -> 保存到数据库
5. 注册成功 -> 跳转到登录页
6. 输入用户名和密码 -> 提交登录
7. Spring Security 验证 -> 创建 Session
8. 登录成功 -> 跳转到首页
```

### 打卡记录创建流程
```
1. 用户点击"新建打卡"
2. 填写标题和内容
3. 可选上传附件（支持多个文件）
4. 点击提交
5. 后端处理：
   - 验证标题不为空
   - 验证文件类型和大小
   - 保存打卡记录到数据库
   - 处理文件上传：
     * 生成 UUID 文件名
     * 保存到指定目录
     * 记录文件信息到数据库
6. 返回打卡列表页面
7. 显示成功提示
```

### 日历视图和统计计算
```
1. 用户访问日历页面
2. 查询当前用户指定月份的所有打卡记录
3. 在日历上标记有打卡的日期
4. 计算统计数据：
   - 总打卡天数：SELECT COUNT(DISTINCT check_date) FROM check_in WHERE user_id = ?
   - 连续打卡天数：从今天往前推，查询连续有记录的天数
   - 本月打卡次数：SELECT COUNT(*) FROM check_in WHERE user_id = ? AND MONTH(check_date) = ?
5. 显示统计结果
6. 用户点击日期 -> 显示当天的打卡详情
```

### 备忘录文件管理流程
```
1. 用户访问备忘录页面
2. 左侧显示分类列表，右侧显示文件列表
3. 创建分类：
   - 点击"新建分类"
   - 输入分类名称
   - 保存到数据库
4. 上传文件：
   - 选择分类
   - 选择文件
   - 输入备注（可选）
   - 提交上传
   - 后端处理文件并保存记录
5. 查看文件：
   - 点击分类 -> 显示该分类下的所有文件
   - 显示文件名、大小、上传时间、备注
6. 下载文件：
   - 点击下载按钮
   - 后端验证权限
   - 返回文件流
7. 删除文件：
   - 点击删除按钮
   - 确认操作
   - 删除数据库记录和物理文件
```

## 页面设计

### 响应式布局策略

**移动端（< 768px）**
- 单列布局
- 导航栏折叠成汉堡菜单
- 表格改为卡片式展示
- 按钮和表单元素适配触摸操作

**平板端（768px - 992px）**
- 两列布局
- 侧边栏可折叠
- 表格正常显示

**PC 端（> 992px）**
- 侧边栏导航 + 主内容区
- 表格完整显示所有列
- 更大的操作空间

### 主要页面

**登录页面 (login.html)**
- 居中的登录表单
- 用户名和密码输入框
- 记住我选项
- 登录按钮
- 注册链接

**注册页面 (register.html)**
- 居中的注册表单
- 用户名、密码、确认密码、邮箱输入框
- 注册按钮
- 返回登录链接

**首页/仪表盘 (index.html)**
- 顶部导航栏（用户信息、退出登录）
- 左侧菜单（打卡管理、备忘录、个人设置）
- 主内容区：
  - 打卡统计卡片（总天数、连续天数、本月次数）
  - 最近打卡记录列表
  - 快速操作按钮

**打卡列表页 (checkin/list.html)**
- 顶部：新建打卡按钮、搜索框
- 表格/卡片列表：
  - 标题
  - 打卡日期
  - 附件数量
  - 操作按钮（查看、编辑、删除）
- 底部分页

**打卡日历页 (checkin/calendar.html)**
- 月份切换按钮
- 日历网格
- 有打卡的日期高亮显示
- 点击日期显示详情弹窗

**新建/编辑打卡页 (checkin/form.html)**
- 标题输入框
- 内容文本域
- 文件上传区域（支持多文件）
- 已上传文件列表（编辑时）
- 保存和取消按钮

**备忘录管理页 (memo/index.html)**
- 左侧：分类列表
  - 新建分类按钮
  - 分类列表（可点击切换）
  - 编辑/删除分类按钮
- 右侧：文件列表
  - 上传文件按钮
  - 文件表格/卡片
  - 文件名、大小、上传时间、备注
  - 下载/删除按钮

**个人设置页 (user/profile.html)**
- 个人信息表单
- 昵称、邮箱输入框
- 修改密码区域
- 保存按钮

## 安全性设计

### 认证和授权
- 使用 Spring Security 进行用户认证
- 基于 Session 的认证方式
- 所有页面（除登录注册）都需要认证
- 用户只能访问和操作自己的数据

### 密码安全
- 使用 BCrypt 加密存储密码
- 密码强度要求（可选）：至少 6 位
- 登录失败次数限制（可选）

### 文件上传安全
- 文件类型白名单验证
- 检查文件扩展名和 MIME 类型
- 文件大小限制：单个文件最大 10MB
- 文件名使用 UUID 重命名，防止路径遍历攻击
- 上传目录不在 Web 根目录下

### CSRF 防护
- Spring Security 默认开启 CSRF 保护
- 所有表单包含 CSRF Token

### XSS 防护
- Thymeleaf 默认转义 HTML
- 用户输入内容在显示时自动转义

### SQL 注入防护
- 使用 MyBatis-Plus 的参数化查询
- 避免拼接 SQL 语句

### 权限控制
- 每个操作都验证用户身份
- 用户只能访问自己的数据
- 文件下载时检查文件所有权

## 性能优化

### 数据库优化
- 合理设计索引：
  - user 表：username 唯一索引
  - check_in 表：(user_id, check_date) 联合索引
  - memo_file 表：(user_id, category_id) 联合索引
- 分页查询减少数据量
- 使用连接池（HikariCP）

### 文件处理优化
- 文件上传使用流式处理，不占用过多内存
- 大文件下载使用分块传输
- 静态资源使用浏览器缓存

### 查询优化
- 打卡列表分页查询（每页 20 条）
- 日历视图只查询当月数据
- 统计数据可以考虑缓存（可选）

### 前端优化
- 使用 CDN 加载 Bootstrap 和 jQuery
- 图片懒加载（可选）
- 压缩 CSS 和 JS 文件

## 错误处理

### 全局异常处理
- 使用 @ControllerAdvice 统一处理异常
- 捕获常见异常：
  - 文件上传异常
  - 数据库异常
  - 权限异常
  - 参数验证异常
- 返回友好的错误提示页面或 JSON

### 具体错误场景

**文件上传失败**
- 文件过大：提示"文件大小不能超过 10MB"
- 文件类型不支持：提示"不支持的文件类型"
- 磁盘空间不足：提示"服务器存储空间不足"

**登录失败**
- 用户名不存在：提示"用户名或密码错误"
- 密码错误：提示"用户名或密码错误"
- 账号被禁用：提示"账号已被禁用"

**权限不足**
- 访问他人数据：跳转到 403 页面
- 未登录访问：跳转到登录页

**文件不存在**
- 下载时文件已被删除：提示"文件不存在"
- 显示默认图标或占位符

**数据库异常**
- 连接失败：提示"系统繁忙，请稍后再试"
- 数据重复：提示具体的错误信息

### 日志记录
- 使用 SLF4J + Logback
- 记录关键操作：
  - 用户登录/登出
  - 文件上传/下载/删除
  - 异常信息
- 日志级别：
  - INFO：正常操作
  - WARN：警告信息
  - ERROR：异常错误

## 测试策略

### 单元测试
- Service 层的核心业务逻辑
- 工具类方法
- 使用 JUnit 5 + Mockito
- 测试覆盖率目标：> 70%

**重点测试场景**
- 用户注册：用户名重复检查
- 密码加密：BCrypt 加密验证
- 打卡统计：连续打卡天数计算
- 文件上传：文件类型和大小验证
- 文件删除：数据库和物理文件同步删除

### 集成测试
- Controller 层的接口测试
- 使用 @SpringBootTest
- 测试完整的请求响应流程

**重点测试场景**
- 登录流程：从登录到访问受保护页面
- 打卡流程：创建、查看、编辑、删除
- 文件上传流程：上传、下载、删除

### 手动测试
- 页面功能测试
- 响应式布局测试（不同设备）
- 浏览器兼容性测试
- 用户体验测试

**测试清单**
- [ ] 用户注册和登录
- [ ] 创建打卡记录（带附件）
- [ ] 查看打卡列表和日历
- [ ] 编辑和删除打卡记录
- [ ] 创建备忘录分类
- [ ] 上传和下载备忘录文件
- [ ] 删除备忘录文件
- [ ] 移动端布局测试
- [ ] 文件上传限制测试
- [ ] 权限控制测试

## 部署方案

### 开发环境
- JDK 17+
- MySQL 8.0+
- Maven 3.6+
- IDE：IntelliJ IDEA

### 生产环境
- 服务器：Linux（推荐 Ubuntu/CentOS）
- 数据库：MySQL 8.0+
- 应用服务器：内置 Tomcat（Spring Boot）
- 反向代理：Nginx（可选）

### 部署步骤
1. 打包：`mvn clean package`
2. 上传 jar 包到服务器
3. 配置数据库连接
4. 创建 uploads 目录
5. 运行：`java -jar codexzy.jar`
6. 配置 Nginx 反向代理（可选）
7. 配置开机自启动（systemd）

### 配置文件
- 开发环境：application-dev.yml
- 生产环境：application-prod.yml
- 敏感信息使用环境变量

## 后续扩展

### 可选功能
- 打卡提醒（每天定时提醒）
- 打卡分享（生成分享链接）
- 数据导出（导出为 PDF 或 Excel）
- 主题切换（深色模式）
- 多语言支持
- 云存储集成（阿里云 OSS、七牛云）
- 移动端 APP（使用 WebView 或原生开发）

### 性能优化
- Redis 缓存（统计数据、用户信息）
- 数据库读写分离
- 文件存储迁移到云存储
- CDN 加速静态资源

### 功能增强
- 打卡标签系统
- 打卡搜索功能
- 打卡评论功能
- 好友系统（查看好友打卡）
- 打卡排行榜

## 总结

本系统采用 Spring Boot + Thymeleaf + Bootstrap 5 的技术栈，实现了一个功能完整的个人打卡和备忘录管理系统。系统架构清晰，分层合理，适合个人开发者快速开发和部署。通过响应式设计，系统可以在移动端和 PC 端都提供良好的用户体验。

系统的核心优势：
1. 技术栈简单，开发效率高
2. 响应式设计，移动端友好
3. 安全性设计完善
4. 易于部署和维护
5. 扩展性良好，便于后续功能增强
