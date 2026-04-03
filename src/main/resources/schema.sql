CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `check_in` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT DEFAULT NULL,
    `check_date` DATE NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_check_in_user_date` (`user_id`, `check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `check_in_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `check_in_id` BIGINT NOT NULL,
    `file_name` VARCHAR(200) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_size` BIGINT DEFAULT NULL,
    `file_type` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_check_in_file_check_in_id` (`check_in_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `memo_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `category_name` VARCHAR(50) NOT NULL,
    `sort_order` INT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_memo_category_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `memo_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `file_name` VARCHAR(200) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_size` BIGINT DEFAULT NULL,
    `file_type` VARCHAR(50) DEFAULT NULL,
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_memo_file_user_category` (`user_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `business_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `report_code` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_business_account_user_id` (`user_id`),
    UNIQUE KEY `uk_business_account_report_code` (`report_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `business_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `owner_user_id` BIGINT NOT NULL,
    `reporter_user_id` BIGINT NOT NULL,
    `record_type` VARCHAR(20) NOT NULL,
    `record_status` VARCHAR(40) NOT NULL DEFAULT 'UNINVENTORIED',
    `occurred_at` DATETIME NOT NULL,
    `product_name` VARCHAR(200) NOT NULL,
    `quantity` INT NOT NULL,
    `cost_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `fixed_return_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `profit_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `sold_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_business_record_owner_time` (`owner_user_id`, `occurred_at`),
    KEY `idx_business_record_owner_reporter` (`owner_user_id`, `reporter_user_id`),
    KEY `idx_business_record_reporter` (`reporter_user_id`),
    KEY `idx_business_record_owner_type` (`owner_user_id`, `record_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 兼容旧库：仅在缺少 record_status 时补充该列
SET @business_record_record_status_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'business_record'
      AND COLUMN_NAME = 'record_status'
);

SET @business_record_record_status_sql := IF(
    @business_record_record_status_exists = 0,
    'ALTER TABLE `business_record` ADD COLUMN `record_status` VARCHAR(40) NOT NULL DEFAULT ''UNINVENTORIED'' AFTER `record_type`',
    'SELECT 1'
);

PREPARE business_record_record_status_stmt FROM @business_record_record_status_sql;
EXECUTE business_record_record_status_stmt;
DEALLOCATE PREPARE business_record_record_status_stmt;

CREATE TABLE IF NOT EXISTS `business_reporter_note` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `owner_user_id` BIGINT NOT NULL,
    `reporter_user_id` BIGINT NOT NULL,
    `note_name` VARCHAR(100) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_business_reporter_note_owner_reporter` (`owner_user_id`, `reporter_user_id`),
    KEY `idx_business_reporter_note_owner` (`owner_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
