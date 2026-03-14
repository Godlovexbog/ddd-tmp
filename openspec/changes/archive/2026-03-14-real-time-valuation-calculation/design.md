## 背景

当前项目采用 DDD（领域驱动设计）架构，包含 user-domain、user-application、user-infrastructure、user-interaction 等分层。用户提出需要实现实时预估计算功能，用于计算资产的当前预估价值。

**业务需求背景：**
- 用户需要基于最新交易和交付记录动态计算资产估值
- 估值计算涉及三个数据源：估值记录、交易记录、交互记录
- 交易记录和交互记录需要筛选"成功"状态且日期在估值日期之后

**技术约束：**
- 遵循现有 DDD 分层架构
- 使用 user-common 中定义的领域标记接口
- 参考现有 user 模块的代码结构和命名规范

## 目标 / 非目标

**目标：**
1. 实现估值记录（ValuationRecord）的完整 CRUD 功能
2. 实现交易记录（TransactionRecord）的完整 CRUD 功能
3. 实现交互记录的完整 CRUD 功能（现金交付记录、金融产品交付记录）
4. 实现实时估值金额计算服务：估值记录金额 + 估值日期后的成功交易金额 + 估值日期后的成功交付金额
5. 提供 REST API 暴露以上功能

**非目标：**
- 不实现估值算法的复杂优化（当前为简单累加）
- 不实现历史版本追溯功能
- 不实现估值记录的自动生成（仅手动创建）

## 技术决策

### 1. 模块结构设计
采用与 user 模块类似的独立模块结构：
- `valuation-domain`: 领域层，包含实体、值对象、仓储接口、领域服务
- `valuation-application`: 应用层，包含应用服务、命令对象、查询服务
- `valuation-infrastructure`: 基础设施层，包含仓储实现、数据库访问、事件发布
- `valuation-interaction`: 交互层，包含 REST API 控制器

### 2. 实体标识设计
所有实体实现统一标识策略：
- 使用 Long 类型作为主键
- 实现 Entity 接口
- 使用 Lombok @Data 注解

### 3. 交易状态枚举
交易记录和交付记录使用统一的成功状态标识：
- 使用 `TransactionStatusEnum` 枚举：SUCCESS, FAILED, PENDING

### 4. 估值计算策略
采用服务聚合模式：
- 创建 `ValuationCalculationService` 领域服务
- 计算公式：valuationAmount + transactionAmountAfterValuationDate + deliveryAmountAfterValuationDate

### 5. 仓储设计
遵循现有规范：
- 基础方法：byId, save, delete
- 自定义查询：byProjectId, listByDateRange

## 风险与权衡

| 风险 | 描述 | 缓解措施 |
|------|------|----------|
| 数据一致性 | 计算过程中数据可能发生变化 | 使用事务保证原子性 |
| 性能 | 大数据量计算可能较慢 | 后续可引入缓存优化 |
| 枚举扩展 | 交易状态可能需要更多类型 | 使用枚举可扩展设计 |

## 待解决问题

1. 交互记录的两种类型（现金交付、金融产品）是否需要抽象共同父类？
2. 估值计算是否需要支持多币种？
3. 是否需要保存计算历史记录？
