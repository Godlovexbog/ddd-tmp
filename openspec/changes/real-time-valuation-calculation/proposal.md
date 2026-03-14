## 变更原因

当前项目缺乏实时资产估值能力，用户无法获取基于最新交易和交付记录的动态估值金额。需要实现一个实时预估计算功能，整合估值记录、交易记录和交付记录，自动计算资产的当前预估价值。

## 变更内容

1. **新增估值记录模块**
   - 创建估值记录实体（ValuationRecord），包含项目id、项目code、标的来源、标的名称、标的金额（DECIMAL类型）、估值日期、标的份额、账户代码
   - 创建估值记录值对象（Money）用于金额封装
   - 实现估值记录的增删改查功能
   - 创建估值记录仓储接口和基础设施实现
   - 数据库表需包含通用字段：gmt_create, gmt_modified, is_deleted

2. **新增交易记录模块**
   - 创建交易记录实体（TransactionRecord），包含标的id、标的code、标的来源、交易份额、交易金额（DECIMAL类型）、交易状态枚举、交易时间
   - 创建交易状态枚举（TransactionStatusEnum）
   - 实现交易记录的增删改查功能
   - 创建交易记录仓储接口和基础设施实现
   - 数据库表需包含通用字段

3. **新增交互记录模块**
   - 创建现金交付记录实体（CashDeliveryRecord）
   - 创建金融产品交互记录实体（FinancialProductDeliveryRecord）
   - 交付金额使用 DECIMAL(18,2) 类型
   - 实现两种交互记录的增删改查功能

4. **新增实时估值计算能力**
   - 实现估值金额计算逻辑：估值记录金额 + 估值日期之后的成功交易记录金额 + 估值日期之后成功交付记录金额
   - 提供计算服务接口和实现

5. **新增相关API接口**
   - 估值记录 CRUD API（带参数 @Valid 校验）
   - 交易记录 CRUD API
   - 交互记录 CRUD API
   - 实时估值计算 API
   - 接口需考虑幂等性设计

## 能力清单

### 新增能力
- `valuation-record`: 估值记录管理，包含实体定义、值对象、仓储、CRUD API
- `transaction-record`: 交易记录管理，包含实体定义、枚举、仓储、CRUD API
- `delivery-record`: 交互记录管理，包含现金交付和金融产品交付两种记录类型
- `real-time-valuation`: 实时估值计算服务

### 修改能力
- （无）本次为全新功能模块

## 影响范围

- **新增模块**: valuation-domain, valuation-application, valuation-infrastructure, valuation-interaction
- **公共模块影响**: user-common 新增通用领域标记接口
- **API影响**: 新增 /api/valuation/*, /api/transaction/*, /api/delivery/* 端点
- **数据库影响**: 新增 4 张表（t_valuation_record, t_transaction_record, t_cash_delivery_record, t_financial_product_delivery_record）
- **Git分支**: 特性分支命名为 T_REAL_CALC_VAL
- **提交规范**: 按 git-commit规范.md 格式提交
