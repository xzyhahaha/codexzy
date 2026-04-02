# 打卡系统实现计划

> **给智能体工作者：** 必需的子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 来逐任务实现此计划。步骤使用复选框（`- [ ]`）语法进行跟踪。

**目标：** 构建一个基于 Spring Boot 的多用户打卡和备忘录管理系统，支持文件上传、日历视图和统计功能。

**架构：** 采用传统的 MVC 三层架构（Controller-Service-Mapper），使用 Thymeleaf 服务端渲染，Bootstrap 5 响应式前端，MyBatis-Plus 简化数据库操作，Spring Security 处理用户认证。

**技术栈：** Spring Boot 3.x, MyBatis-Plus, Spring Security, MySQL 8.x, Thymeleaf, Bootstrap 5, Lombok

---

## 任务 1：项目初始化和基础配置

**文件：**
- 创建：`pom.xml`
- 创建：`src/main/resources/application.yml`
- 创建：`src/main/java/com/codexzy/CodexzyApplication.java`

- [ ] **步骤 1：创建 Maven 项目配置文件**

创建 `pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.codexzy</groupId>
    <artifactId>codexzy</artifactId>
    <version>1.0.0</version>
    <name>codexzy</name>
    <description>打卡和备忘录管理系统</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Thymeleaf -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- Spring Boot Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Spring Security Test -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **步骤 2：创建应用配置文件**

创建 `src/main/resources/application.yml`：

```yaml
spring:
  application:
    name: codexzy

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/codexzy?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root

  # Thymeleaf 配置
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html
    encoding: UTF-8
    mode: HTML

  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

  # Security 配置
  security:
    user:
      name: admin
      password: admin

# MyBatis-Plus 配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:mapper/*.xml

# 文件上传路径
file:
  upload-dir: uploads

# 服务器配置
server:
  port: 8080
```

- [ ] **步骤 3：创建 Spring Boot 主类**

创建 `src/main/java/com/codexzy/CodexzyApplication.java`：

```java
package com.codexzy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 打卡系统主启动类
 */
@SpringBootApplication
@MapperScan("com.codexzy.mapper")
public class CodexzyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexzyApplication.java, args);
    }
}
```

- [ ] **步骤 4：创建目录结构**

运行命令：
```bash
mkdir -p src/main/java/com/codexzy/{controller,service,mapper,entity,dto,config,util}
mkdir -p src/main/resources/{templates/{checkin,memo,user},static/{css,js,images},mapper}
mkdir -p src/test/java/com/codexzy
mkdir -p uploads/{checkin,memo}
```

- [ ] **步骤 5：验证项目结构**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 6：提交初始化代码**

```bash
git add pom.xml src/main/resources/application.yml src/main/java/com/codexzy/CodexzyApplication.java
git commit -m "feat: 初始化 Spring Boot 项目

- 添加 Maven 依赖配置
- 配置数据源和 MyBatis-Plus
- 配置 Thymeleaf 和文件上传
- 创建主启动类和目录结构

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 2：数据库表结构

**文件：**
- 创建：`src/main/resources/db/schema.sql`

- [ ] **步骤 1：创建数据库初始化脚本**

创建 `src/main/resources/db/schema.sql`：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS codexzy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE codexzy;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS `check_in` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `check_date` DATE NOT NULL COMMENT '打卡日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- 打卡附件表
CREATE TABLE IF NOT EXISTS `check_in_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `check_in_id` BIGINT NOT NULL COMMENT '打卡记录ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '原始文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型（MIME）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_check_in_id` (`check_in_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡附件表';

-- 备忘录分类表
CREATE TABLE IF NOT EXISTS `memo_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备忘录分类表';

-- 备忘录文件表
CREATE TABLE IF NOT EXISTS `memo_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '原始文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_category` (`user_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备忘录文件表';
```

- [ ] **步骤 2：执行数据库脚本**

运行命令：
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

预期输出：成功创建数据库和表

- [ ] **步骤 3：验证表结构**

运行命令：
```bash
mysql -u root -p -e "USE codexzy; SHOW TABLES;"
```

预期输出：显示 5 张表（user, check_in, check_in_file, memo_category, memo_file）

- [ ] **步骤 4：提交数据库脚本**

```bash
git add src/main/resources/db/schema.sql
git commit -m "feat: 添加数据库表结构

- 创建用户表
- 创建打卡记录表和附件表
- 创建备忘录分类表和文件表
- 添加索引优化查询性能

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 3：实体类（Entity）

**文件：**
- 创建：`src/main/java/com/codexzy/entity/User.java`
- 创建：`src/main/java/com/codexzy/entity/CheckIn.java`
- 创建：`src/main/java/com/codexzy/entity/CheckInFile.java`
- 创建：`src/main/java/com/codexzy/entity/MemoCategory.java`
- 创建：`src/main/java/com/codexzy/entity/MemoFile.java`

- [ ] **步骤 1：创建用户实体类**

创建 `src/main/java/com/codexzy/entity/User.java`：

```java
package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

- [ ] **步骤 2：创建打卡记录实体类**

创建 `src/main/java/com/codexzy/entity/CheckIn.java`：

```java
package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 */
@Data
@TableName("check_in")
public class CheckIn {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 打卡日期
     */
    private LocalDate checkDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

- [ ] **步骤 3：创建打卡附件实体类**

创建 `src/main/java/com/codexzy/entity/CheckInFile.java`：

```java
package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打卡附件实体类
 */
@Data
@TableName("check_in_file")
public class CheckInFile {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 打卡记录ID
     */
    private Long checkInId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME）
     */
    private String fileType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
```

- [ ] **步骤 4：创建备忘录分类实体类**

创建 `src/main/java/com/codexzy/entity/MemoCategory.java`：

```java
package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备忘录分类实体类
 */
@Data
@TableName("memo_category")
public class MemoCategory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
```

- [ ] **步骤 5：创建备忘录文件实体类**

创建 `src/main/java/com/codexzy/entity/MemoFile.java`：

```java
package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备忘录文件实体类
 */
@Data
@TableName("memo_file")
public class MemoFile {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
```

- [ ] **步骤 6：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 7：提交实体类**

```bash
git add src/main/java/com/codexzy/entity/
git commit -m "feat: 添加实体类

- 创建用户实体类
- 创建打卡记录和附件实体类
- 创建备忘录分类和文件实体类
- 使用 Lombok 简化代码

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 4：Mapper 接口

**文件：**
- 创建：`src/main/java/com/codexzy/mapper/UserMapper.java`
- 创建：`src/main/java/com/codexzy/mapper/CheckInMapper.java`
- 创建：`src/main/java/com/codexzy/mapper/CheckInFileMapper.java`
- 创建：`src/main/java/com/codexzy/mapper/MemoCategoryMapper.java`
- 创建：`src/main/java/com/codexzy/mapper/MemoFileMapper.java`

- [ ] **步骤 1：创建用户 Mapper**

创建 `src/main/java/com/codexzy/mapper/UserMapper.java`：

```java
package com.codexzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codexzy.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

- [ ] **步骤 2：创建打卡记录 Mapper**

创建 `src/main/java/com/codexzy/mapper/CheckInMapper.java`：

```java
package com.codexzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codexzy.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡记录 Mapper 接口
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}
```

- [ ] **步骤 3：创建打卡附件 Mapper**

创建 `src/main/java/com/codexzy/mapper/CheckInFileMapper.java`：

```java
package com.codexzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codexzy.entity.CheckInFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡附件 Mapper 接口
 */
@Mapper
public interface CheckInFileMapper extends BaseMapper<CheckInFile> {
}
```

- [ ] **步骤 4：创建备忘录分类 Mapper**

创建 `src/main/java/com/codexzy/mapper/MemoCategoryMapper.java`：

```java
package com.codexzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codexzy.entity.MemoCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备忘录分类 Mapper 接口
 */
@Mapper
public interface MemoCategoryMapper extends BaseMapper<MemoCategory> {
}
```

- [ ] **步骤 5：创建备忘录文件 Mapper**

创建 `src/main/java/com/codexzy/mapper/MemoFileMapper.java`：

```java
package com.codexzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codexzy.entity.MemoFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备忘录文件 Mapper 接口
 */
@Mapper
public interface MemoFileMapper extends BaseMapper<MemoFile> {
}
```

- [ ] **步骤 6：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 7：提交 Mapper 接口**

```bash
git add src/main/java/com/codexzy/mapper/
git commit -m "feat: 添加 Mapper 接口

- 创建用户 Mapper
- 创建打卡记录和附件 Mapper
- 创建备忘录分类和文件 Mapper
- 继承 MyBatis-Plus BaseMapper

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 5：工具类

**文件：**
- 创建：`src/main/java/com/codexzy/util/FileUtil.java`

- [ ] **步骤 1：创建文件工具类**

创建 `src/main/java/com/codexzy/util/FileUtil.java`：

```java
package com.codexzy.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtil {

    /**
     * 允许上传的文件类型
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",
            "jpg", "jpeg", "png", "gif", "bmp",
            "zip", "rar"
    );

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 验证文件类型和大小
     *
     * @param file 上传的文件
     * @return 是否通过验证
     */
    public static boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = getFileExtension(originalFilename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return UUID 文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 保存文件到指定目录
     *
     * @param file      上传的文件
     * @param directory 目标目录
     * @param filename  文件名
     * @return 文件相对路径
     * @throws IOException IO 异常
     */
    public static String saveFile(MultipartFile file, String directory, String filename) throws IOException {
        // 创建目录
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 保存文件
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的字符串
     */
    public static String formatFileSize(Long size) {
        if (size == null || size == 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double fileSize = size;

        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交工具类**

```bash
git add src/main/java/com/codexzy/util/FileUtil.java
git commit -m "feat: 添加文件处理工具类

- 文件类型和大小验证
- 生成唯一文件名
- 文件保存和删除
- 文件大小格式化

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 6：DTO 类

**文件：**
- 创建：`src/main/java/com/codexzy/dto/CheckInDTO.java`
- 创建：`src/main/java/com/codexzy/dto/CheckInStatDTO.java`

- [ ] **步骤 1：创建打卡 DTO**

创建 `src/main/java/com/codexzy/dto/CheckInDTO.java`：

```java
package com.codexzy.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡记录 DTO
 */
@Data
public class CheckInDTO {

    /**
     * 打卡记录ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 打卡日期
     */
    private LocalDate checkDate;

    /**
     * 附件列表
     */
    private List<FileInfo> files;

    /**
     * 附件信息
     */
    @Data
    public static class FileInfo {
        private Long id;
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String fileType;
    }
}
```

- [ ] **步骤 2：创建打卡统计 DTO**

创建 `src/main/java/com/codexzy/dto/CheckInStatDTO.java`：

```java
package com.codexzy.dto;

import lombok.Data;

/**
 * 打卡统计 DTO
 */
@Data
public class CheckInStatDTO {

    /**
     * 总打卡天数
     */
    private Long totalDays;

    /**
     * 连续打卡天数
     */
    private Integer continuousDays;

    /**
     * 本月打卡次数
     */
    private Long monthCount;
}
```

- [ ] **步骤 3：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 4：提交 DTO 类**

```bash
git add src/main/java/com/codexzy/dto/
git commit -m "feat: 添加 DTO 类

- 创建打卡记录 DTO
- 创建打卡统计 DTO
- 用于数据传输和展示

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 7：Service 层 - 用户服务

**文件：**
- 创建：`src/main/java/com/codexzy/service/UserService.java`
- 创建：`src/test/java/com/codexzy/service/UserServiceTest.java`

- [ ] **步骤 1：编写用户服务测试（TDD）**

创建 `src/test/java/com/codexzy/service/UserServiceTest.java`：

```java
package com.codexzy.service;

import com.codexzy.entity.User;
import com.codexzy.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 */
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegister() {
        // 测试用户注册
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setNickname("测试用户");

        User registered = userService.register(user);

        assertNotNull(registered.getId());
        assertEquals("testuser", registered.getUsername());
        assertTrue(passwordEncoder.matches("password123", registered.getPassword()));
        assertEquals("test@example.com", registered.getEmail());
    }

    @Test
    void testRegisterDuplicateUsername() {
        // 测试重复用户名注册
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password123");
        user1.setEmail("user1@example.com");
        userService.register(user1);

        User user2 = new User();
        user2.setUsername("duplicate");
        user2.setPassword("password456");
        user2.setEmail("user2@example.com");

        assertThrows(RuntimeException.class, () -> userService.register(user2));
    }

    @Test
    void testFindByUsername() {
        // 测试根据用户名查找用户
        User user = new User();
        user.setUsername("findme");
        user.setPassword("password123");
        user.setEmail("findme@example.com");
        userService.register(user);

        User found = userService.findByUsername("findme");

        assertNotNull(found);
        assertEquals("findme", found.getUsername());
    }
}
```

- [ ] **步骤 2：运行测试确认失败**

运行命令：
```bash
mvn test -Dtest=UserServiceTest
```

预期输出：`FAILURE` - UserService 类不存在

- [ ] **步骤 3：实现用户服务**

创建 `src/main/java/com/codexzy/service/UserService.java`：

```java
package com.codexzy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.User;
import com.codexzy.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册后的用户
     */
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        User existingUser = findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存用户
        userMapper.insert(user);
        return user;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否更新成功
     */
    @Transactional
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }
}
```

- [ ] **步骤 4：运行测试确认通过**

运行命令：
```bash
mvn test -Dtest=UserServiceTest
```

预期输出：`SUCCESS` - 所有测试通过

- [ ] **步骤 5：提交用户服务**

```bash
git add src/main/java/com/codexzy/service/UserService.java src/test/java/com/codexzy/service/UserServiceTest.java
git commit -m "feat: 实现用户服务

- 用户注册功能（密码加密）
- 用户名重复检查
- 根据用户名和ID查找用户
- 更新用户信息
- 添加单元测试

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 8：Service 层 - 打卡服务

**文件：**
- 创建：`src/main/java/com/codexzy/service/CheckInService.java`

- [ ] **步骤 1：实现打卡服务**

创建 `src/main/java/com/codexzy/service/CheckInService.java`：

```java
package com.codexzy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codexzy.dto.CheckInDTO;
import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.entity.CheckInFile;
import com.codexzy.mapper.CheckInFileMapper;
import com.codexzy.mapper.CheckInMapper;
import com.codexzy.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 打卡服务类
 */
@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInMapper checkInMapper;
    private final CheckInFileMapper checkInFileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 创建打卡记录
     *
     * @param checkIn 打卡记录
     * @param files   附件列表
     * @return 打卡记录ID
     */
    @Transactional
    public Long createCheckIn(CheckIn checkIn, List<MultipartFile> files) throws IOException {
        // 设置打卡日期为当天
        checkIn.setCheckDate(LocalDate.now());

        // 保存打卡记录
        checkInMapper.insert(checkIn);

        // 处理附件
        if (files != null && !files.isEmpty()) {
            saveFiles(checkIn.getId(), checkIn.getUserId(), files);
        }

        return checkIn.getId();
    }

    /**
     * 保存附件
     *
     * @param checkInId 打卡记录ID
     * @param userId    用户ID
     * @param files     文件列表
     */
    private void saveFiles(Long checkInId, Long userId, List<MultipartFile> files) throws IOException {
        String yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String directory = uploadDir + "/checkin/" + userId + "/" + yearMonth;

        for (MultipartFile file : files) {
            if (file.isEmpty() || !FileUtil.validateFile(file)) {
                continue;
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = FileUtil.generateUniqueFilename(originalFilename);
            String filePath = FileUtil.saveFile(file, directory, uniqueFilename);

            CheckInFile checkInFile = new CheckInFile();
            checkInFile.setCheckInId(checkInId);
            checkInFile.setFileName(originalFilename);
            checkInFile.setFilePath(filePath);
            checkInFile.setFileSize(file.getSize());
            checkInFile.setFileType(file.getContentType());

            checkInFileMapper.insert(checkInFile);
        }
    }

    /**
     * 分页查询打卡记录
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public IPage<CheckIn> getCheckInList(Long userId, int pageNum, int pageSize) {
        Page<CheckIn> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
                .orderByDesc(CheckIn::getCheckDate);
        return checkInMapper.selectPage(page, wrapper);
    }

    /**
     * 获取打卡记录详情（包含附件）
     *
     * @param checkInId 打卡记录ID
     * @return 打卡记录 DTO
     */
    public CheckInDTO getCheckInDetail(Long checkInId) {
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null) {
            return null;
        }

        CheckInDTO dto = new CheckInDTO();
        dto.setId(checkIn.getId());
        dto.setTitle(checkIn.getTitle());
        dto.setContent(checkIn.getContent());
        dto.setCheckDate(checkIn.getCheckDate());

        // 查询附件
        LambdaQueryWrapper<CheckInFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInFile::getCheckInId, checkInId);
        List<CheckInFile> files = checkInFileMapper.selectList(wrapper);

        List<CheckInDTO.FileInfo> fileInfos = files.stream().map(file -> {
            CheckInDTO.FileInfo info = new CheckInDTO.FileInfo();
            info.setId(file.getId());
            info.setFileName(file.getFileName());
            info.setFilePath(file.getFilePath());
            info.setFileSize(file.getFileSize());
            info.setFileType(file.getFileType());
            return info;
        }).collect(Collectors.toList());

        dto.setFiles(fileInfos);
        return dto;
    }

    /**
     * 更新打卡记录
     *
     * @param checkIn 打卡记录
     * @return 是否更新成功
     */
    @Transactional
    public boolean updateCheckIn(CheckIn checkIn) {
        return checkInMapper.updateById(checkIn) > 0;
    }

    /**
     * 删除打卡记录（包括附件）
     *
     * @param checkInId 打卡记录ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteCheckIn(Long checkInId) {
        // 删除附件文件
        LambdaQueryWrapper<CheckInFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInFile::getCheckInId, checkInId);
        List<CheckInFile> files = checkInFileMapper.selectList(wrapper);

        for (CheckInFile file : files) {
            FileUtil.deleteFile(file.getFilePath());
        }

        // 删除附件记录
        checkInFileMapper.delete(wrapper);

        // 删除打卡记录
        return checkInMapper.deleteById(checkInId) > 0;
    }

    /**
     * 获取打卡统计
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    public CheckInStatDTO getCheckInStat(Long userId) {
        CheckInStatDTO stat = new CheckInStatDTO();

        // 总打卡天数
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId);
        Long totalDays = checkInMapper.selectCount(wrapper);
        stat.setTotalDays(totalDays);

        // 本月打卡次数
        LocalDate now = LocalDate.now();
        LambdaQueryWrapper<CheckIn> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(CheckIn::getUserId, userId)
                .ge(CheckIn::getCheckDate, now.withDayOfMonth(1))
                .le(CheckIn::getCheckDate, now.withDayOfMonth(now.lengthOfMonth()));
        Long monthCount = checkInMapper.selectCount(monthWrapper);
        stat.setMonthCount(monthCount);

        // 连续打卡天数
        int continuousDays = calculateContinuousDays(userId);
        stat.setContinuousDays(continuousDays);

        return stat;
    }

    /**
     * 计算连续打卡天数
     *
     * @param userId 用户ID
     * @return 连续天数
     */
    private int calculateContinuousDays(Long userId) {
        LocalDate today = LocalDate.now();
        int days = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);
            LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CheckIn::getUserId, userId)
                    .eq(CheckIn::getCheckDate, checkDate);
            Long count = checkInMapper.selectCount(wrapper);

            if (count > 0) {
                days++;
            } else {
                break;
            }
        }

        return days;
    }

    /**
     * 获取指定月份的打卡日期列表
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 打卡日期列表
     */
    public List<LocalDate> getCheckInDates(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
                .ge(CheckIn::getCheckDate, startDate)
                .le(CheckIn::getCheckDate, endDate);

        List<CheckIn> checkIns = checkInMapper.selectList(wrapper);
        return checkIns.stream()
                .map(CheckIn::getCheckDate)
                .distinct()
                .collect(Collectors.toList());
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交打卡服务**

```bash
git add src/main/java/com/codexzy/service/CheckInService.java
git commit -m "feat: 实现打卡服务

- 创建打卡记录（支持附件上传）
- 分页查询打卡列表
- 获取打卡详情
- 更新和删除打卡记录
- 打卡统计（总天数、连续天数、本月次数）
- 获取月份打卡日期列表

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 9：Service 层 - 备忘录服务

**文件：**
- 创建：`src/main/java/com/codexzy/service/MemoService.java`

- [ ] **步骤 1：实现备忘录服务**

创建 `src/main/java/com/codexzy/service/MemoService.java`：

```java
package com.codexzy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.MemoCategory;
import com.codexzy.entity.MemoFile;
import com.codexzy.mapper.MemoCategoryMapper;
import com.codexzy.mapper.MemoFileMapper;
import com.codexzy.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 备忘录服务类
 */
@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoCategoryMapper memoCategoryMapper;
    private final MemoFileMapper memoFileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 分类ID
     */
    @Transactional
    public Long createCategory(MemoCategory category) {
        memoCategoryMapper.insert(category);
        return category.getId();
    }

    /**
     * 获取用户的所有分类
     *
     * @param userId 用户ID
     * @return 分类列表
     */
    public List<MemoCategory> getCategoriesByUserId(Long userId) {
        LambdaQueryWrapper<MemoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemoCategory::getUserId, userId)
                .orderByAsc(MemoCategory::getSortOrder);
        return memoCategoryMapper.selectList(wrapper);
    }

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 是否更新成功
     */
    @Transactional
    public boolean updateCategory(MemoCategory category) {
        return memoCategoryMapper.updateById(category) > 0;
    }

    /**
     * 删除分类（需要先删除分类下的所有文件）
     *
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        // 检查分类下是否有文件
        LambdaQueryWrapper<MemoFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemoFile::getCategoryId, categoryId);
        Long count = memoFileMapper.selectCount(wrapper);

        if (count > 0) {
            throw new RuntimeException("分类下还有文件，无法删除");
        }

        return memoCategoryMapper.deleteById(categoryId) > 0;
    }

    /**
     * 上传文件到指定分类
     *
     * @param userId     用户ID
     * @param categoryId 分类ID
     * @param file       文件
     * @param remark     备注
     * @return 文件ID
     */
    @Transactional
    public Long uploadFile(Long userId, Long categoryId, MultipartFile file, String remark) throws IOException {
        if (!FileUtil.validateFile(file)) {
            throw new RuntimeException("文件类型或大小不符合要求");
        }

        String directory = uploadDir + "/memo/" + userId + "/" + categoryId;
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = FileUtil.generateUniqueFilename(originalFilename);
        String filePath = FileUtil.saveFile(file, directory, uniqueFilename);

        MemoFile memoFile = new MemoFile();
        memoFile.setUserId(userId);
        memoFile.setCategoryId(categoryId);
        memoFile.setFileName(originalFilename);
        memoFile.setFilePath(filePath);
        memoFile.setFileSize(file.getSize());
        memoFile.setFileType(file.getContentType());
        memoFile.setRemark(remark);

        memoFileMapper.insert(memoFile);
        return memoFile.getId();
    }

    /**
     * 获取分类下的所有文件
     *
     * @param categoryId 分类ID
     * @return 文件列表
     */
    public List<MemoFile> getFilesByCategoryId(Long categoryId) {
        LambdaQueryWrapper<MemoFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemoFile::getCategoryId, categoryId)
                .orderByDesc(MemoFile::getCreateTime);
        return memoFileMapper.selectList(wrapper);
    }

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    public MemoFile getFileById(Long fileId) {
        return memoFileMapper.selectById(fileId);
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteFile(Long fileId) {
        MemoFile file = memoFileMapper.selectById(fileId);
        if (file == null) {
            return false;
        }

        // 删除物理文件
        FileUtil.deleteFile(file.getFilePath());

        // 删除数据库记录
        return memoFileMapper.deleteById(fileId) > 0;
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交备忘录服务**

```bash
git add src/main/java/com/codexzy/service/MemoService.java
git commit -m "feat: 实现备忘录服务

- 创建、更新、删除分类
- 获取用户的所有分类
- 上传文件到指定分类
- 获取分类下的所有文件
- 删除文件（同时删除物理文件）

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 10：Spring Security 配置

**文件：**
- 创建：`src/main/java/com/codexzy/config/SecurityConfig.java`
- 创建：`src/main/java/com/codexzy/service/CustomUserDetailsService.java`

- [ ] **步骤 1：创建自定义 UserDetailsService**

创建 `src/main/java/com/codexzy/service/CustomUserDetailsService.java`：

```java
package com.codexzy.service;

import com.codexzy.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 自定义用户详情服务
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }
}
```

- [ ] **步骤 2：创建 Security 配置类**

创建 `src/main/java/com/codexzy/config/SecurityConfig.java`：

```java
package com.codexzy.config;

import com.codexzy.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- [ ] **步骤 3：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 4：提交 Security 配置**

```bash
git add src/main/java/com/codexzy/config/SecurityConfig.java src/main/java/com/codexzy/service/CustomUserDetailsService.java
git commit -m "feat: 配置 Spring Security

- 自定义 UserDetailsService
- 配置登录和登出
- 配置密码加密器（BCrypt）
- 配置静态资源和注册页面无需认证

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 11：Controller 层 - 认证控制器

**文件：**
- 创建：`src/main/java/com/codexzy/controller/AuthController.java`

- [ ] **步骤 1：创建认证控制器**

创建 `src/main/java/com/codexzy/controller/AuthController.java`：

```java
package com.codexzy.controller;

import com.codexzy.entity.User;
import com.codexzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 认证控制器
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String register(User user, RedirectAttributes redirectAttributes) {
        try {
            userService.register(user);
            redirectAttributes.addFlashAttribute("message", "注册成功，请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交认证控制器**

```bash
git add src/main/java/com/codexzy/controller/AuthController.java
git commit -m "feat: 实现认证控制器

- 登录页面
- 注册页面
- 处理注册请求

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 12：全局异常处理和配置

**文件：**
- 创建：`src/main/java/com/codexzy/config/GlobalExceptionHandler.java`
- 创建：`src/main/java/com/codexzy/config/WebMvcConfig.java`

- [ ] **步骤 1：创建全局异常处理器**

创建 `src/main/java/com/codexzy/config/GlobalExceptionHandler.java`：

```java
package com.codexzy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        log.error("文件上传大小超限", e);
        redirectAttributes.addFlashAttribute("error", "文件大小不能超过 10MB");
        return "redirect:/";
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, RedirectAttributes redirectAttributes) {
        log.error("运行时异常", e);
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/";
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        log.error("系统异常", e);
        redirectAttributes.addFlashAttribute("error", "系统繁忙，请稍后再试");
        return "redirect:/";
    }
}
```

- [ ] **步骤 2：创建 Web MVC 配置**

创建 `src/main/java/com/codexzy/config/WebMvcConfig.java`：

```java
package com.codexzy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
```

- [ ] **步骤 3：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 4：提交配置类**

```bash
git add src/main/java/com/codexzy/config/GlobalExceptionHandler.java src/main/java/com/codexzy/config/WebMvcConfig.java
git commit -m "feat: 添加全局异常处理和 Web MVC 配置

- 全局异常处理器（文件上传、运行时异常等）
- Web MVC 配置（文件访问路径）
- 统一错误提示

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 13：Controller 层 - 首页控制器

**文件：**
- 创建：`src/main/java/com/codexzy/controller/IndexController.java`

- [ ] **步骤 1：创建首页控制器**

创建 `src/main/java/com/codexzy/controller/IndexController.java`：

```java
package com.codexzy.controller;

import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.entity.User;
import com.codexzy.service.CheckInService;
import com.codexzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页控制器
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserService userService;
    private final CheckInService checkInService;

    /**
     * 首页/仪表盘
     */
    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // 获取打卡统计
        CheckInStatDTO stat = checkInService.getCheckInStat(user.getId());
        model.addAttribute("stat", stat);

        // 获取最近 5 条打卡记录
        List<CheckIn> recentCheckIns = checkInService.getCheckInList(user.getId(), 1, 5).getRecords();
        model.addAttribute("recentCheckIns", recentCheckIns);

        model.addAttribute("user", user);
        return "index";
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交首页控制器**

```bash
git add src/main/java/com/codexzy/controller/IndexController.java
git commit -m "feat: 实现首页控制器

- 展示打卡统计数据
- 展示最近打卡记录
- 获取当前登录用户信息

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 14：Controller 层 - 打卡控制器

**文件：**
- 创建：`src/main/java/com/codexzy/controller/CheckInController.java`

- [ ] **步骤 1：创建打卡控制器**

创建 `src/main/java/com/codexzy/controller/CheckInController.java`：

```java
package com.codexzy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codexzy.dto.CheckInDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.entity.User;
import com.codexzy.service.CheckInService;
import com.codexzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡控制器
 */
@Controller
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final UserService userService;
    private final CheckInService checkInService;

    /**
     * 打卡列表页面
     */
    @GetMapping("/list")
    public String list(Authentication authentication,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {
        User user = userService.findByUsername(authentication.getName());
        IPage<CheckIn> checkInPage = checkInService.getCheckInList(user.getId(), page, 20);

        model.addAttribute("page", checkInPage);
        return "checkin/list";
    }

    /**
     * 打卡日历页面
     */
    @GetMapping("/calendar")
    public String calendar(Authentication authentication,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           Model model) {
        User user = userService.findByUsername(authentication.getName());

        LocalDate now = LocalDate.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        List<LocalDate> checkInDates = checkInService.getCheckInDates(user.getId(), currentYear, currentMonth);

        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);
        model.addAttribute("checkInDates", checkInDates);
        return "checkin/calendar";
    }

    /**
     * 新建打卡页面
     */
    @GetMapping("/new")
    public String newCheckIn() {
        return "checkin/form";
    }

    /**
     * 创建打卡记录
     */
    @PostMapping("/create")
    public String create(Authentication authentication,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(required = false) List<MultipartFile> files,
                         RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName());

            CheckIn checkIn = new CheckIn();
            checkIn.setUserId(user.getId());
            checkIn.setTitle(title);
            checkIn.setContent(content);

            checkInService.createCheckIn(checkIn, files);

            redirectAttributes.addFlashAttribute("message", "打卡成功");
            return "redirect:/checkin/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "打卡失败: " + e.getMessage());
            return "redirect:/checkin/new";
        }
    }

    /**
     * 编辑打卡页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CheckInDTO checkIn = checkInService.getCheckInDetail(id);
        model.addAttribute("checkIn", checkIn);
        return "checkin/form";
    }

    /**
     * 更新打卡记录
     */
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         RedirectAttributes redirectAttributes) {
        try {
            CheckIn checkIn = new CheckIn();
            checkIn.setId(id);
            checkIn.setTitle(title);
            checkIn.setContent(content);

            checkInService.updateCheckIn(checkIn);

            redirectAttributes.addFlashAttribute("message", "更新成功");
            return "redirect:/checkin/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失败: " + e.getMessage());
            return "redirect:/checkin/edit/" + id;
        }
    }

    /**
     * 删除打卡记录
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            checkInService.deleteCheckIn(id);
            redirectAttributes.addFlashAttribute("message", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/checkin/list";
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交打卡控制器**

```bash
git add src/main/java/com/codexzy/controller/CheckInController.java
git commit -m "feat: 实现打卡控制器

- 打卡列表（分页）
- 打卡日历视图
- 创建打卡记录（支持附件）
- 编辑和删除打卡记录

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 15：Controller 层 - 备忘录控制器

**文件：**
- 创建：`src/main/java/com/codexzy/controller/MemoController.java`

- [ ] **步骤 1：创建备忘录控制器**

创建 `src/main/java/com/codexzy/controller/MemoController.java`：

```java
package com.codexzy.controller;

import com.codexzy.entity.MemoCategory;
import com.codexzy.entity.MemoFile;
import com.codexzy.entity.User;
import com.codexzy.service.MemoService;
import com.codexzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 备忘录控制器
 */
@Controller
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {

    private final UserService userService;
    private final MemoService memoService;

    /**
     * 备忘录管理页面
     */
    @GetMapping
    public String index(Authentication authentication,
                        @RequestParam(required = false) Long categoryId,
                        Model model) {
        User user = userService.findByUsername(authentication.getName());

        // 获取所有分类
        List<MemoCategory> categories = memoService.getCategoriesByUserId(user.getId());
        model.addAttribute("categories", categories);

        // 如果指定了分类，获取该分类下的文件
        if (categoryId != null) {
            List<MemoFile> files = memoService.getFilesByCategoryId(categoryId);
            model.addAttribute("files", files);
            model.addAttribute("currentCategoryId", categoryId);
        }

        return "memo/index";
    }

    /**
     * 创建分类
     */
    @PostMapping("/category/create")
    public String createCategory(Authentication authentication,
                                 @RequestParam String categoryName,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName());

            MemoCategory category = new MemoCategory();
            category.setUserId(user.getId());
            category.setCategoryName(categoryName);
            category.setSortOrder(0);

            memoService.createCategory(category);

            redirectAttributes.addFlashAttribute("message", "分类创建成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "创建失败: " + e.getMessage());
        }
        return "redirect:/memo";
    }

    /**
     * 删除分类
     */
    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memoService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "分类删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/memo";
    }

    /**
     * 上传文件
     */
    @PostMapping("/file/upload")
    public String uploadFile(Authentication authentication,
                             @RequestParam Long categoryId,
                             @RequestParam MultipartFile file,
                             @RequestParam(required = false) String remark,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName());
            memoService.uploadFile(user.getId(), categoryId, file, remark);

            redirectAttributes.addFlashAttribute("message", "文件上传成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "上传失败: " + e.getMessage());
        }
        return "redirect:/memo?categoryId=" + categoryId;
    }

    /**
     * 下载文件
     */
    @GetMapping("/file/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            MemoFile memoFile = memoService.getFileById(id);
            if (memoFile == null) {
                return ResponseEntity.notFound().build();
            }

            File file = new File(memoFile.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);
            String encodedFilename = URLEncoder.encode(memoFile.getFileName(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除文件
     */
    @PostMapping("/file/delete/{id}")
    public String deleteFile(@PathVariable Long id,
                             @RequestParam Long categoryId,
                             RedirectAttributes redirectAttributes) {
        try {
            memoService.deleteFile(id);
            redirectAttributes.addFlashAttribute("message", "文件删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/memo?categoryId=" + categoryId;
    }
}
```

- [ ] **步骤 2：编译验证**

运行命令：
```bash
mvn clean compile
```

预期输出：`BUILD SUCCESS`

- [ ] **步骤 3：提交备忘录控制器**

```bash
git add src/main/java/com/codexzy/controller/MemoController.java
git commit -m "feat: 实现备忘录控制器

- 备忘录管理页面
- 创建和删除分类
- 上传文件到指定分类
- 下载和删除文件

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 16：前端页面（简化实现）

**说明：** 由于前端页面代码量很大，这里提供核心页面的基本框架。实际执行时需要完善样式和交互。

**文件：**
- 创建：`src/main/resources/templates/login.html`
- 创建：`src/main/resources/templates/register.html`
- 创建：`src/main/resources/templates/index.html`
- 创建：`src/main/resources/templates/checkin/list.html`
- 创建：`src/main/resources/templates/checkin/calendar.html`
- 创建：`src/main/resources/templates/checkin/form.html`
- 创建：`src/main/resources/templates/memo/index.html`

- [ ] **步骤 1：创建登录页面**

创建基本的登录页面，使用 Bootstrap 5 样式。

- [ ] **步骤 2：创建注册页面**

创建基本的注册页面，包含用户名、密码、邮箱输入框。

- [ ] **步骤 3：创建首页**

创建首页仪表盘，展示打卡统计和最近打卡记录。

- [ ] **步骤 4：创建打卡相关页面**

创建打卡列表、日历视图、表单页面。

- [ ] **步骤 5：创建备忘录页面**

创建备忘录管理页面，左侧分类列表，右侧文件列表。

- [ ] **步骤 6：测试所有页面**

运行应用并测试所有页面的功能和响应式布局。

- [ ] **步骤 7：提交前端页面**

```bash
git add src/main/resources/templates/
git commit -m "feat: 添加所有前端页面

- 登录和注册页面
- 首页仪表盘
- 打卡列表、日历、表单页面
- 备忘录管理页面
- 使用 Bootstrap 5 响应式设计

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务 17：最终测试和部署

**文件：**
- 创建：`README.md`

- [ ] **步骤 1：运行完整测试**

运行命令：
```bash
mvn clean test
```

预期输出：所有测试通过

- [ ] **步骤 2：启动应用**

运行命令：
```bash
mvn spring-boot:run
```

预期输出：应用成功启动在 http://localhost:8080

- [ ] **步骤 3：手动测试所有功能**

测试清单：
- 用户注册和登录
- 创建打卡记录（带附件）
- 查看打卡列表和日历
- 编辑和删除打卡记录
- 创建备忘录分类
- 上传和下载备忘录文件
- 删除备忘录文件
- 移动端布局测试

- [ ] **步骤 4：创建 README 文档**

创建 `README.md`，包含项目介绍、技术栈、安装步骤、使用说明。

- [ ] **步骤 5：最终提交**

```bash
git add README.md
git commit -m "docs: 添加 README 文档

- 项目介绍
- 技术栈说明
- 安装和运行步骤
- 功能特性列表

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

- [ ] **步骤 6：推送到远程仓库**

运行命令：
```bash
git push -u origin master
```

---

## 总结

本实现计划涵盖了打卡系统的完整功能：

1. **项目初始化**：Maven 配置、Spring Boot 启动类、目录结构
2. **数据库设计**：5 张表的 SQL 脚本
3. **实体类**：User、CheckIn、CheckInFile、MemoCategory、MemoFile
4. **Mapper 接口**：MyBatis-Plus 数据访问层
5. **工具类**：文件处理工具
6. **DTO 类**：数据传输对象
7. **Service 层**：用户、打卡、备忘录业务逻辑（包含单元测试）
8. **Security 配置**：用户认证和授权
9. **Controller 层**：认证、首页、打卡、备忘录控制器
10. **全局配置**：异常处理、Web MVC 配置
11. **前端页面**：Thymeleaf + Bootstrap 5 响应式界面
12. **测试和部署**：完整的功能测试和文档

**技术栈**：Spring Boot 3.x, MyBatis-Plus, Spring Security, MySQL 8.x, Thymeleaf, Bootstrap 5

**开发原则**：TDD（测试驱动开发）、DRY（不重复）、YAGNI（只实现需要的功能）、频繁提交

**后续扩展**：可以参考设计文档中的"后续扩展"章节，添加打卡提醒、数据导出、云存储等功能。
