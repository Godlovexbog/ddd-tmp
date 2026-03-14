# Git 提交规范

本文档定义了项目的 Git 提交信息规范，用于统一提交信息格式，便于追溯和管理。

---

## 1. 提交格式

```
{type}+{author}+{issue}+{description}
```

### 1.1 格式说明

| 字段 | 说明 | 示例 |
|------|------|------|
| `type` | 提交类型 | `comment`、`feat`、`fix`、`docs` |
| `author` | 提交人 | `五老峰`、`zhangsan` |
| `issue` | 任务/卡片编号 | `1234`、`TASK-5678` |
| `description` | 提交描述 | `初始化记录`、`添加用户模块` |

### 1.2 示例

```
comment+五老峰+1234+初始化记录
feat+zhangsan+TASK-5678+新增用户登录功能
fix+lishi+9876+修复列表查询分页问题
docs+wanger+DOC-001+更新接口文档
```

---

## 2. 提交类型（type）

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| `comment` | 注释/说明 | 日常提交、初始化、常规功能开发 |
| `feat` | 新功能 | 新增功能模块 |
| `fix` | 修复 | Bug 修复 |
| `docs` | 文档 | 文档更新 |
| `style` | 格式 | 代码格式调整，不影响功能 |
| `refactor` | 重构 | 代码重构 |
| `perf` | 性能 | 性能优化 |
| `test` | 测试 | 测试相关 |
| `chore` | 构建 | 构建过程、辅助工具变动 |

---

## 3. 提交人规范

### 3.1 命名规则

使用中文姓名全拼或英文名，全小写。

**示例**：

| 姓名 | 提交人 |
|------|--------|
| 五老峰 | `wulaofeng` |
| 张三 | `zhangsan` |
| 李四 | `lishi` |
| 王二 | `wanger` |

### 3.2 注意事项

- 一个项目内保持一致的命名风格
- 建议使用姓名全拼，避免重名
- 团队统一提交人名称

---

## 4. 任务编号规范

### 4.1 编号来源

任务编号来源于项目管理工具（如 Jira、TAPD、Trello等）。

**示例**：

| 来源 | 格式示例 |
|------|----------|
| Jira | `PROJ-1234`、`BUG-567` |
| TAPD | `TASK-9876`、`BUG-123` |
| 内部系统 | `1234`、`5678` |
| 特性卡片 | `T_REAL_CALC_VAL` |

### 4.2 无任务编号

如果任务没有编号，使用 `0` 或 `NOISSUE` 占位。

```
feat+zhangsan+0+添加新功能
fix+lishi+NOISSUE+修复临时问题
```

---

## 5. 描述规范

### 5.1 命名规则

- 使用中文描述
- 简洁明了，不超过 20 个字
- 描述本次提交的核心内容
- 使用动词开头，如：添加、新增、修复、优化、更新

### 5.2 示例

| 场景 | 描述 |
|------|------|
| 初始化项目 | 初始化项目结构 |
| 添加功能 | 新增用户管理模块 |
| 添加接口 | 添加用户查询接口 |
| 修复问题 | 修复分页查询问题 |
| 优化性能 | 优化列表查询性能 |
| 更新文档 | 更新接口文档 |

---

## 6. 完整示例

### 6.1 常规开发

```
# 新增功能
feat+五老峰+1234+新增用户管理模块
feat+zhangsan+TASK-1001+新增订单查询接口

# Bug修复
fix+lishi+BUG-888+修复登录超时问题

# 常规提交
comment+wanger+0+初始化估值模块代码
comment+五老峰+1234+添加估值计算逻辑
```

### 6.2 多任务提交

如果一次提交包含多个任务，使用主任务编号：

```
feat+五老峰+1234+新增用户模块及接口
```

---

## 7. Git 配置

### 7.1 设置提交模板

```bash
# 创建提交模板文件
cat > ~/.git-commit-template << EOF
comment+{author}+{issue}+{description}
EOF

# 配置 Git 使用模板
git config --global commit.template ~/.git-commit-template
```

### 7.2 设置默认提交人

```bash
# 设置提交人
git config user.name "五老峰"
git config user.email "wulaofeng@example.com"
```

### 7.3 使用 Commit Hook（可选）

创建 `prepare-commit-msg` hook 自动填充模板：

```bash
# .git/hooks/prepare-commit-msg
#!/bin/sh
COMMIT_MSG_FILE=$1
COMMIT_SOURCE=$2
SHA1=$3

if [ -z "$COMMIT_SOURCE" ]; then
    echo "comment+++" > $COMMIT_MSG_FILE
fi
```

---

## 8. 规范检查

### 8.1 手动检查

提交前检查格式：

```bash
# 查看最近提交
git log --oneline

# 格式示例
comment+五老峰+1234+初始化记录
feat+zhangsan+TASK-1001+新增用户登录功能
```

### 8.2 自动检查（可选）

使用 Commitlint 进行格式校验：

```javascript
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [2, 'always', ['comment', 'feat', 'fix', 'docs', 'style', 'refactor', 'perf', 'test', 'chore']],
    'subject-format': [2, 'always', '^[a-z]+[\\u4e00-\\u9fa5]+.+'],
  }
}
```

---

## 9. 常见问题

### Q1: 提交信息写错了怎么办？

```bash
# 修改最后一次提交
git commit --amend -m "comment+五老峰+1234+正确的描述"
```

### Q2: 任务编号不确定怎么办？

先使用 `0` 占位，后续补充：

```
comment+五老峰+0+新增功能（任务编号待补充）
```

### Q3: 提交类型不确定怎么办？

优先使用 `comment`，这是最通用的类型。

---

## 10. 速查表

```
{type}+{author}+{issue}+{description}

类型: comment, feat, fix, docs, style, refactor, perf, test, chore
作者: 姓名全拼（小写）
编号: 任务编号 / 0 / NOISSUE
描述: 中文描述，简洁明了
```
