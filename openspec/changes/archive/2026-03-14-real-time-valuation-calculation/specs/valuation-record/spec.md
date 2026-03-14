## 新增需求

### 需求：估值记录 CRUD
系统 SHALL 提供估值记录的完整增删改查功能。

#### 场景：创建估值记录
- **当** 用户提交包含所有必填字段的有效估值记录时
- **则** 系统创建记录并返回成功

#### 场景：根据ID查询估值记录
- **当** 用户根据已存在的ID查询估值记录时
- **则** 系统返回完整的估值记录

#### 场景：根据项目ID查询估值记录
- **当** 用户根据项目ID查询估值记录时
- **则** 系统返回该项目的所有估值记录

#### 场景：更新估值记录
- **当** 用户更新已存在的估值记录时
- **则** 系统更新记录并返回成功

#### 场景：删除估值记录
- **当** 用户删除已存在的估值记录时
- **则** 系统删除记录并返回成功

### 需求：估值记录字段
估值记录 SHALL 包含以下字段：
- projectId: Long - 项目ID
- projectCode: String - 项目代码
- targetSource: String - 标的来源
- targetName: String - 标的名称
- targetAmount: BigDecimal - 标的金额
- valuationDate: LocalDate - 估值日期
- targetShare: BigDecimal - 标的份额
- accountCode: String - 账户代码
