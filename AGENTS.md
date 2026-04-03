# AGENTS.md

## 说明

* 本仓库是一个基于 Spring Boot 3.2 和 Java 17 的打卡与备忘录管理系统。
* 技术栈主要包括 Spring MVC、Thymeleaf、Spring Security、MyBatis-Plus、MySQL 8。
* 修改时必须优先做小范围、可验证、与现有结构一致的变更，禁止大范围重构。

---

## 工作流程（必须遵循）

1. 先分析问题和相关代码，不要直接修改
2. 明确影响范围（controller / service / mapper / entity）
3. 优先做最小改动实现需求
4. 修改后进行编译验证
5. 如涉及数据库或接口，补充必要说明

---

## 语言要求

* 与本项目相关的说明、总结、评审意见默认使用中文
* 新增代码注释默认使用中文，除非是标准协议字段或必须英文
* 编写文档默认中文
* 面向用户提示文案保持现有中文语境

---

## 技术栈

* Java 17
* Maven
* Spring Boot 3.2.0
* MyBatis-Plus 3.5.5
* MySQL 8
* Thymeleaf
* Lombok

---

## 目录结构

* `src/main/java/com/codexzy`

  * `config`：Spring 配置
  * `controller`：MVC 控制器
  * `dto`：请求响应 DTO
  * `entity`：数据库实体
  * `mapper`：MyBatis-Plus Mapper
  * `service`、`service/impl`：业务逻辑
  * `util`：工具类
* `src/main/resources`

  * `application.yml`
  * `schema.sql`
  * `templates`
  * `static`
* `src/test/java`
* `uploads`
* `target`（禁止修改）

---

## 构建与运行

* 打包：`mvn clean package -DskipTests`
* 快速编译：`mvn -q -DskipTests compile`
* 运行：`mvn spring-boot:run`
* 默认端口：8081

---

## 数据库约束

* 数据源配置：`application.yml`
* 建表脚本：`schema.sql`
* 已开启自动初始化（spring.sql.init.mode=always）
* 必须提前创建数据库 `codexzy`
* 表结构变更必须同步更新 `schema.sql`
* 保证建表脚本具备幂等性

---

## 开发约定

* 保持现有分层结构，不新增无必要层
* 优先复用现有代码
* 不修改对外接口返回结构
* 不引入新框架或依赖
* 命名风格保持一致（Java: PascalCase / DB: snake_case）

---

## SQL 与 MyBatis 规则

* 禁止使用 `select *`
* 大表查询必须带条件
* 优先考虑索引命中
* 禁止全表 update / delete
* MyBatis-Plus 优先使用已有方法
* 修改 mapper 时同步检查 XML / 注解一致性

---

## 异常与健壮性

* 外部输入必须校验
* 避免空指针（null 判断）
* 涉及写操作要考虑事务
* 关键操作增加日志

---

## 测试与验证

* 常用命令：

  * `mvn -q -DskipTests compile`
  * `mvn test`
* 涉及页面或安全：

  * 检查 Controller / 模板 / CSRF
* 涉及数据库：

  * 验证 schema.sql 是否正常执行

---

## 改动约束

* 每次只修改必要文件
* 不做无关重构
* 不同时大规模修改多个层
* 不为了“优化”重写已有逻辑

---

## 变更安全

* 不硬编码密钥、账号
* 不随意修改路由和模板
* 修改安全逻辑需检查权限与CSRF
* 上传逻辑必须兼容 `file.upload-dir`

---

## 高风险操作（必须确认）

* 删除文件
* 批量替换
* 修改数据库结构
* 修改配置文件
* 执行 git 操作

---

## 完成标准（Definition of Done）

只有满足以下条件才算完成：

1. 代码可编译通过（mvn compile）
2. 无明显逻辑错误或空指针风险
3. 修改范围最小
4. 与现有风格一致
5. 涉及数据库时 schema.sql 已同步

---

## 遇到不确定情况

* 优先查看：

  * pom.xml
  * application.yml
  * schema.sql
* 保持最小 diff
* 不确定时先说明再修改
