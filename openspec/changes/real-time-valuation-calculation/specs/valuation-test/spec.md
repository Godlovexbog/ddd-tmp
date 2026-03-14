## ADDED Requirements

### Requirement: ValuationCalculationService 单元测试
ValuationCalculationService 必须有完整的单元测试，覆盖所有业务场景。

#### Scenario: 无交易记录时返回基础估值金额
- **WHEN** 估值记录存在但无交易和交付记录
- **THEN** 返回估值记录的基础金额

#### Scenario: 成功交易时累加交易金额
- **WHEN** 存在成功的交易记录（交易日期 >= 估值日期）
- **THEN** 返回基础金额 + 成功交易金额

#### Scenario: 失败交易应被忽略
- **WHEN** 存在失败的交易记录
- **THEN** 失败交易金额不计入估值

#### Scenario: 处理中交易应被忽略
- **WHEN** 存在处理中的交易记录
- **THEN** 处理中交易金额不计入估值

#### Scenario: 估值日期之前交易应被忽略
- **WHEN** 交易日期早于估值日期
- **THEN** 该交易不计入估值

#### Scenario: 成功交付时累加交付金额
- **WHEN** 存在成功的现金交付或金融产品交付
- **THEN** 成功交付金额计入估值

#### Scenario: 失败交付应被忽略
- **WHEN** 存在失败的交付记录
- **THEN** 失败交付金额不计入估值

#### Scenario: 综合计算所有成功金额
- **WHEN** 估值记录、交易、交付都存在
- **THEN** 返回 基础金额 + 成功交易金额 + 成功交付金额

---

### Requirement: ValuationRecord 领域实体测试
ValuationRecord 实体必须正确保存和查询。

#### Scenario: 创建估值记录
- **WHEN** 调用 Repository 保存估值记录
- **THEN** 记录正确保存到数据库

#### Scenario: 根据 ID 查询估值记录
- **WHEN** 调用 Repository byId 方法
- **THEN** 返回正确的估值记录

#### Scenario: 逻辑删除估值记录
- **WHEN** 调用 delete 方法
- **THEN** is_deleted 标记为 1

---

### Requirement: TransactionRecord 领域实体测试
TransactionRecord 实体必须正确处理不同状态的交易。

#### Scenario: 查询成功交易
- **WHEN** 查询 transaction_status = '1' 的记录
- **THEN** 返回所有成功交易

#### Scenario: 过滤估值日期之前的交易
- **WHEN** 查询交易时指定估值日期
- **THEN** 只返回估值日期之后的交易

---

### Requirement: ValuationApi 集成测试
ValuationApi 接口必须正确处理请求和响应。

#### Scenario: 创建估值记录成功
- **WHEN** 发送有效的创建请求
- **THEN** 返回 200 和成功响应

#### Scenario: 参数校验失败
- **WHEN** 发送无效参数（如 projectId 为空）
- **THEN** 返回错误码和错误信息

#### Scenario: 查询实时估值成功
- **WHEN** 查询存在的估值记录
- **THEN** 返回计算后的实时估值金额

#### Scenario: 限流测试
- **WHEN** 请求超过限流阈值
- **THEN** 返回限流错误

---

### Requirement: 脱敏注解测试
敏感信息必须被正确脱敏处理。

#### Scenario: 账户信息脱敏
- **WHEN** 查询包含敏感字段的记录
- **THEN** 返回脱敏后的信息（如账号中间用 * 代替）
