# SQL 编写规范

本文档定义了项目数据库 SQL 的编写规范，包括表结构设计、SQL 编写、测试数据等方面的准则。

---

## 1. 数据库连接信息

| 环境 | Host | Port | Database | Username | Password |
|------|------|------|----------|-----------|----------|
| 开发 | 127.0.0.1 | 3306 | blog-user | root | laowei |

### JDBC 连接串
```yaml
url: jdbc:mysql://127.0.0.1:3306/blog-user?autoReconnect=true&autoReconnectForPools=true&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&rewriteBatchedStatements=true
```

---

## 2. SQL 文件组织

### 2.1 目录结构

```
db/
├── README.md                    # 数据库说明文档
├── t_valuation_record.sql       # 估值记录表
├── t_transaction_record.sql     # 交易记录表
├── t_cash_delivery_record.sql  # 现金交付记录表
├── t_xxx_table.sql           # 其他表
└── test_data.sql             # 测试数据
```

### 2.2 文件命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 表结构 | `t_{业务名称}.sql` | `t_user.sql` |
| 测试数据 | `test_data.sql` | `test_data.sql` |
| 初始化脚本 | `init_data.sql` | `init_data.sql` |
| 索引脚本 | `index_{表名}.sql` | `index_user.sql` |

---

## 3. 表结构 SQL 规范

### 3.1 表名规范

- 使用小写字母
- 多个单词用下划线分隔
- 必须添加 `t_` 前缀

**示例**：
```sql
CREATE TABLE t_valuation_record (...)
```

### 3.2 通用字段

每张表必须包含以下通用字段：

```sql
CREATE TABLE t_example (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    gmt_create      DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified    DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted      TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';
```

### 3.3 字段类型规范

| 数据 | MySQL 类型 | 示例 |
|------|------------|------|
| 主键 | BIGINT | `id BIGINT NOT NULL AUTO_INCREMENT` |
| 金额 | DECIMAL(18,2) | `amount DECIMAL(18,2)` |
| 状态 | CHAR(1) | `status CHAR(1) DEFAULT '0'` |
| 时间 | DATETIME | `gmt_create DATETIME` |
| 日期 | DATE | `valuation_date DATE` |

### 3.4 索引规范

```sql
-- 主键索引（必须）
PRIMARY KEY (id),

-- 业务查询索引
INDEX idx_{table}_{field} (field),

-- 唯一索引
UNIQUE INDEX uk_{table}_{field} (field),

-- 组合索引
INDEX idx_{table}_{field1}_{field2} (field1, field2)
```

### 3.5 表结构模板

```sql
-- {表中文名}
CREATE TABLE t_{业务名称} (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    {业务字段}...
    gmt_create      DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified    DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted      TINYINT         NOT NULL    DEFAULT 0       COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id),
    INDEX idx_{table}_{field} (field)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='{表中文名}';
```

---

## 4. 测试数据 SQL 规范

### 4.1 测试数据文件结构

```sql
-- =====================================================
-- {模块名}测试数据
-- 场景说明：
-- 1. {场景1描述}
-- 2. {场景2描述}
-- =====================================================

-- 1. {表1名}
INSERT INTO t_table1 (...) VALUES (...);

-- 2. {表2名}
INSERT INTO t_table2 (...) VALUES (...);

-- =====================================================
-- 验证查询
-- =====================================================

-- 查询表1数据
SELECT * FROM t_table1 WHERE is_deleted = 0;

-- 验证结果
SELECT ...;
```

### 4.2 测试数据要求

- 每条 INSERT 语句单独一行 VALUES
- 包含清晰的注释说明
- 包含验证查询语句

**示例**：

```sql
-- =====================================================
-- 估值计算模块测试数据
-- 场景：验证实时估值计算逻辑
-- 1. 估值记录金额 = 10000，估值日期 = 2024-01-01
-- 2. 成功交易 2000（2024-01-15）
-- 3. 成功交付 3000（2024-02-01）
-- 预期结果：10000 + 2000 + 3000 = 15000
-- =====================================================

-- 1. 估值记录
INSERT INTO t_valuation_record (project_id, project_code, target_amount, valuation_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 'P001', 10000.00, '2024-01-01', NOW(), NOW(), 0);

-- 2. 交易记录
INSERT INTO t_transaction_record (target_id, transaction_amount, transaction_status, transaction_date, gmt_create, gmt_modified, is_deleted) VALUES
(1, 2000.00, '1', '2024-01-15 10:00:00', NOW(), NOW(), 0);

-- =====================================================
-- 验证查询
-- =====================================================

-- 验证估值记录
SELECT * FROM t_valuation_record WHERE is_deleted = 0;

-- 验证计算结果
SELECT 
    (SELECT target_amount FROM t_valuation_record WHERE project_id = 1 LIMIT 1) +
    (SELECT COALESCE(SUM(transaction_amount), 0) FROM t_transaction_record WHERE transaction_date >= '2024-01-01' AND transaction_status = '1') as total;
```

---

## 5. SQL 执行规范

### 5.1 执行顺序

1. 先执行表结构脚本
2. 再执行测试数据脚本

### 5.2 执行命令

```bash
# 登录 MySQL
mysql -u root -p

# 选择数据库
USE blog-user;

# 执行表结构
SOURCE db/t_valuation_record.sql;
SOURCE db/t_transaction_record.sql;

# 执行测试数据
SOURCE db/test_data.sql;

# 验证数据
SELECT * FROM t_valuation_record;
```

### 5.3 常用验证 SQL

```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC t_valuation_record;

-- 查看索引
SHOW INDEX FROM t_valuation_record;

-- 查看数据
SELECT * FROM t_valuation_record WHERE is_deleted = 0;

-- 统计记录数
SELECT COUNT(*) FROM t_valuation_record WHERE is_deleted = 0;
```

---

## 6. 数据状态码规范

### 6.1 通用状态

| 状态码 | 含义 | 适用场景 |
|--------|------|----------|
| 0 | 否/禁用/处理中 | is_deleted, status |
| 1 | 是/启用/成功 | is_deleted, status |

### 6.2 交易/交付状态

| 状态码 | 含义 |
|--------|------|
| 0 | PENDING - 处理中 |
| 1 | SUCCESS - 成功 |
| 2 | FAILED - 失败 |

---

## 7. SQL 编写注意事项

### 7.1 禁止

- 禁止使用 `SELECT *`
- 禁止不带 WHERE 条件的 UPDATE/DELETE
- 禁止在生产环境执行全表删除

### 7.2 推荐

- 字段名小写下划线
- 关键字大写
- 缩进对齐
- 添加必要注释

---

## 8. 快速开始

### 8.1 执行测试数据

```bash
# 1. 登录 MySQL
mysql -u root -pl

# 2. 创建数据库（如需要）
CREATE DATABASE IF NOT EXISTS blog-user;
USE blog-user;

# 3. 执行表结构
SOURCE F:/code/java/ddd-tmp/db/t_valuation_record.sql;
SOURCE F:/code/java/ddd-tmp/db/t_transaction_record.sql;
SOURCE F:/code/java/ddd-tmp/db/t_cash_delivery_record.sql;
SOURCE F:/code/java/ddd-tmp/db/t_financial_product_delivery_record.sql;

# 4. 执行测试数据
SOURCE F:/code/java/ddd-tmp/db/test_data.sql;

# 5. 验证
SELECT * FROM t_valuation_record;
```

---

## 9. 相关规范

- 遵循 `database-design.md` 中的字段命名规范
- 遵循 `api-testing.md` 中的测试数据准备规范
