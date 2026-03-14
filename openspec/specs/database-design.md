# 数据库设计规范

本文档定义了项目数据库设计的基本规范，包括表结构、命名、索引、事务等方面的准则。

---

## 1. 命名规范

### 1.1 数据库名

```
{project_name}_{module}
```

**示例**：
- `blog_user` - 用户模块
- `blog_order` - 订单模块
- `blog_pay` - 支付模块

### 1.2 表名

```
t_{entity_name}
```

**规则**：
- 使用小写字母
- 多个单词用下划线分隔
- 单词使用名词单数形式
- 必须添加 `t_` 前缀

**示例**：

| 业务实体 | 表名 |
|----------|------|
| 用户 | `t_user` |
| 用户角色 | `t_user_role` |
| 订单 | `t_order` |
| 订单明细 | `t_order_item` |
| 估值记录 | `t_valuation_record` |

### 1.3 字段名

```
{field_name}
```

**规则**：
- 使用小写字母
- 多个单词用下划线分隔
- 必须见名知意

**示例**：

| 字段含义 | 字段名 |
|----------|--------|
| 用户名 | `user_name` |
| 创建时间 | `gmt_create` |
| 修改时间 | `gmt_modified` |
| 是否删除 | `is_deleted` |

### 1.4 索引名

```
idx_{table_name}_{field_name}
uk_{table_name}_{field_name}   # 唯一索引
```

**示例**：
- `idx_t_user_phone` - 用户手机号索引
- `uk_t_user_user_name` - 用户名唯一索引

### 1.5 主键名

```
pk_{table_name}
```

---

## 2. 通用字段

### 2.1 必须字段

每张表必须包含以下字段：

```sql
CREATE TABLE t_example (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    gmt_create      DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    gmt_modified    DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted      TINYINT         NOT NULL    DEFAULT 0  COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';
```

### 2.2 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `gmt_create` | DATETIME | 创建时间 |
| `gmt_modified` | DATETIME | 修改时间，自动更新 |
| `is_deleted` | TINYINT | 逻辑删除标记 |

---

## 3. 数据类型选择

### 3.1 数值类型

| 数据类型 | 存储范围 | 使用场景 |
|----------|----------|----------|
| `TINYINT` | -128 ~ 127 | 状态码、布尔值 |
| `INT` | ±21 亿 | 普通整数 ID、计数 |
| `BIGINT` | ±922 京 | 主键、大数值 |
| `DECIMAL(p,s)` | 精确数值 | 金额（禁止使用 FLOAT/DOUBLE） |

**金额字段示例**：
```sql
amount      DECIMAL(18, 2)     NOT NULL    COMMENT '金额'
price       DECIMAL(10, 2)     NOT NULL    COMMENT '价格'
```

### 3.2 字符串类型

| 数据类型 | 长度 | 使用场景 |
|----------|------|----------|
| `CHAR(n)` | 固定长度 | 状态码、枚举值 |
| `VARCHAR(n)` | 可变长度 | 名称、描述（最大 65535） |
| `TEXT` | 大文本 | 文章内容、日志 |
| `JSON` | JSON | 扩展字段 |

**字符串字段示例**：
```sql
status      CHAR(1)         NOT NULL    DEFAULT '0'     COMMENT '状态: 0-禁用, 1-启用'
user_name   VARCHAR(50)     NOT NULL                COMMENT '用户名'
description VARCHAR(500)    NULL                     COMMENT '描述'
content     TEXT            NULL                     COMMENT '内容'
extra_info  JSON            NULL                     COMMENT '扩展信息'
```

### 3.3 日期类型

| 数据类型 | 格式 | 使用场景 |
|----------|------|----------|
| `DATETIME` | YYYY-MM-DD HH:MM:SS | 创建时间、修改时间 |
| `DATE` | YYYY-MM-DD | 生日、入职日期 |
| `TIMESTAMP` | 时间戳 | 需要时区的场景 |

**日期字段示例**：
```sql
birthday    DATE            NULL                     COMMENT '生日'
start_time  DATETIME        NOT NULL                COMMENT '开始时间'
```

---

## 4. 索引规范

### 4.1 索引选择

| 场景 | 索引类型 | 示例 |
|------|----------|------|
| 主键 | PRIMARY KEY | `id` |
| 唯一约束 | UNIQUE INDEX | `user_name` |
| 常用查询 | INDEX | `gmt_create` |
| 组合查询 | INDEX | `(user_id, status)` |

### 4.2 索引设计原则

1. **选择区分度高的字段**
2. **避免在频繁更新的字段上建索引**
3. **控制单表索引数量**（不超过 5 个）
4. **组合索引注意字段顺序**（区分度高的放前面）

### 4.3 索引示例

```sql
-- 用户表索引
ALTER TABLE t_user 
    ADD INDEX idx_user_phone (phone),
    ADD UNIQUE INDEX uk_user_user_name (user_name),
    ADD INDEX idx_user_status (status),
    ADD INDEX idx_user_gmt_create (gmt_create);

-- 订单表索引（组合索引）
ALTER TABLE t_order 
    ADD INDEX idx_order_user_status (user_id, status),
    ADD INDEX idx_order_gmt_create (gmt_create);
```

---

## 5. 事务规范

### 5.1 事务传播级别

| 传播行为 | 说明 | 使用场景 |
|----------|------|----------|
| `REQUIRED` | 当前有事务则加入，没有则新建 | 默认 |
| `REQUIRES_NEW` | 总是新建事务 | 记录日志 |
| `SUPPORTS` | 有事务则加入，没有则以非事务执行 | 查询 |
| `NOT_SUPPORTED` | 以非事务执行 | 异步操作 |

### 5.2 事务使用示例

```java
// 应用层控制事务
@Service
public class UserAppServiceImpl {
    
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserCommand command) {
        // 业务逻辑
    }
}
```

### 5.3 事务原则

1. **事务范围最小化**：只包含必要的数据库操作
2. **避免长事务**：控制事务执行时间
3. **读写分离**：查询使用 `@Transactional(readOnly = true)`

```java
// 查询使用只读事务
@Transactional(readOnly = true)
public UserDTO getUser(Long id) {
    return userRepository.byId(id);
}
```

---

## 6. 软删除与逻辑删除

### 6.1 逻辑删除

使用 `is_deleted` 字段实现逻辑删除：

```sql
ALTER TABLE t_user 
    ADD INDEX idx_user_deleted (is_deleted);

-- 查询时自动过滤已删除记录
SELECT * FROM t_user WHERE is_deleted = 0;
```

### 6.2 物理删除

谨慎使用物理删除，建议：
- 敏感数据：先归档再删除
- 日志数据：定期归档或清理
- 配置数据：标记为废弃，不物理删除

---

## 7. SQL 编写规范

### 7.1 SELECT 语句

```sql
-- 必须指定字段名，禁止使用 *
SELECT id, user_name, phone, gmt_create 
FROM t_user 
WHERE is_deleted = 0;

-- 使用别名
SELECT u.id, u.user_name, r.role_name 
FROM t_user u 
LEFT JOIN t_role r ON u.role_id = r.id 
WHERE u.is_deleted = 0;
```

### 7.2 INSERT 语句

```sql
-- 建议指定字段名
INSERT INTO t_user (user_name, phone, gmt_create, gmt_modified, is_deleted) 
VALUES ('张三', '13800138000', NOW(), NOW(), 0);

-- 批量插入
INSERT INTO t_user (user_name, phone, gmt_create, gmt_modified, is_deleted) 
VALUES 
    ('张三', '13800138000', NOW(), NOW(), 0),
    ('李四', '13900139000', NOW(), NOW(), 0);
```

### 7.3 UPDATE 语句

```sql
-- 必须添加 WHERE 条件
UPDATE t_user 
SET user_name = '新名字', gmt_modified = NOW() 
WHERE id = 1 AND is_deleted = 0;

-- 禁止不带条件的更新
-- UPDATE t_user SET status = 0;  -- 禁止！
```

### 7.4 DELETE 语句

```sql
-- 推荐使用逻辑删除
UPDATE t_user 
SET is_deleted = 1, gmt_modified = NOW() 
WHERE id = 1;

-- 必须添加 WHERE 条件
DELETE FROM t_user WHERE id = 1 AND is_deleted = 0;
```

---

## 8. 分表规范

### 8.1 分表策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| 哈希分表 | 按 ID 哈希 | 数据均匀分布 |
| 范围分表 | 按时间范围 | 历史数据分离 |
| 地域分表 | 按地区 | 区域业务 |

### 8.2 分表命名

```
t_order_2024         -- 按年分表
t_order_2024_01      -- 按月分表
t_order_00 ~ t_order_99  -- 哈希分表
```

---

## 9. 数据库配置

### 9.1 字符集

```sql
-- 表字符集
DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

-- 建议设置
-- utf8mb4 支持 emoji
-- utf8mb4_unicode_ci 排序更准确
```

### 9.2 存储引擎

```sql
-- 使用 InnoDB
ENGINE=InnoDB

-- 原因：支持事务、行级锁
```

---

## 10. 命名速查表

| 对象 | 命名规则 | 示例 |
|------|----------|------|
| 数据库 | `project_module` | `blog_user` |
| 表 | `t_{name}` | `t_user` |
| 主键 | `id` | `id` |
| 通用字段 | `gmt_create`, `gmt_modified`, `is_deleted` | `gmt_create` |
| 普通字段 | `word_word` | `user_name` |
| 索引 | `idx_{table}_{field}` | `idx_t_user_phone` |
| 外键 | `fk_{table}_{ref_table}` | `fk_t_order_user` |

---

## 11. 常见反模式

| 反模式 | 说明 | 正确做法 |
|----------|------|----------|
| 字段名使用大写 | 不同数据库兼容性问题 | 使用小写 |
| 不加索引 | 查询性能差 | 根据查询场景添加索引 |
| 使用 FLOAT/DOUBLE 存金额 | 精度丢失 | 使用 DECIMAL |
| 不加 WHERE 条件更新/删除 | 数据丢失 | 必须添加 WHERE 条件 |
| 使用 SELECT * | 性能问题 | 指定字段名 |
| 字段冗余 | 数据一致性风险 | 适当冗余，权衡利弊 |
