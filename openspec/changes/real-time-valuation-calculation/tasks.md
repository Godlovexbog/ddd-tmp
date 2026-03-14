## 1. 模块结构搭建

- [x] 1.1 创建 valuation-domain 模块及 pom.xml
- [x] 1.2 创建 valuation-application 模块及 pom.xml
- [x] 1.3 创建 valuation-infrastructure 模块及 pom.xml
- [x] 1.4 创建 valuation-interaction 模块及 pom.xml
- [x] 1.5 在根 pom.xml 中配置模块依赖

## 2. 数据库设计（遵循 database-design.md）

- [x] 2.1 在 db/ 创建 valuation_record 表 SQL（包含通用字段：id, gmt_create, gmt_modified, is_deleted）
- [x] 2.2 在 db/ 创建 transaction_record 表 SQL（包含通用字段）
- [x] 2.3 在 db/ 创建 cash_delivery_record 表 SQL（包含通用字段）
- [x] 2.4 在 db/ 创建 financial_product_delivery_record 表 SQL（包含通用字段）
- [x] 2.5 金额字段使用 DECIMAL(18,2) 类型
- [x] 2.6 表名使用 t_ 前缀，如 t_valuation_record

## 3. 领域层 - 通用枚举

- [x] 3.1 在 domain/share/enums/ 创建 TransactionStatusEnum 枚举（SUCCESS, FAILED, PENDING）
- [x] 3.2 在 domain/share/enums/ 创建 DeliveryStatusEnum 枚举（可复用 TransactionStatusEnum）

## 4. 领域层 - 值对象（遵循 domain-design.md）

- [x] 4.1 在 domain/share/valueobject/ 创建 Money 值对象（实现 ValueObject 接口）
- [x] 4.2 实现 sameValueAs 方法
- [x] 4.3 金额字段使用 final 修饰（不可变设计）

## 5. 领域层 - 估值记录

- [x] 5.1 在 domain/aggregate/valuation/model/ 创建 ValuationRecord 实体（实现 Entity 或 AggregateRoot）
- [x] 5.2 在 domain/aggregate/valuation/repository/ 创建 ValuationRecordRepository 接口
- [x] 5.3 （可选）创建估值记录领域事件

## 6. 领域层 - 交易记录

- [x] 6.1 在 domain/aggregate/transaction/model/ 创建 TransactionRecord 实体
- [x] 6.2 在 domain/aggregate/transaction/repository/ 创建 TransactionRecordRepository 接口

## 7. 领域层 - 交付记录

- [x] 7.1 在 domain/aggregate/delivery/model/ 创建 CashDeliveryRecord 实体
- [x] 7.2 在 domain/aggregate/delivery/model/ 创建 FinancialProductDeliveryRecord 实体
- [x] 7.3 在 domain/aggregate/delivery/repository/ 创建 DeliveryRecordRepository 接口

## 8. 领域层 - 实时估值服务

- [x] 8.1 在 domain/aggregate/valuation/service/ 创建 ValuationCalculationService 接口
- [x] 8.2 在 domain/aggregate/valuation/service/impl/ 创建 ValuationCalculationServiceImpl 实现

## 9. 基础设施层 - 数据库访问

- [x] 9.1 在 infrastructure/db/model/ 创建 PO 类（继承 BaseModel，包含 is_deleted 字段）
- [x] 9.2 在 infrastructure/db/mapper/ 创建 Mapper 接口
- [x] 9.3 在 infrastructure/db/converter/ 创建 Converter 类（serialize/deserialize 方法）
- [x] 9.4 在 infrastructure/db/repository/ 创建 Repository 实现（实现领域层接口，使用 @Repository）

## 10. 基础设施层 - 事件

- [x] 10.1 创建 DomainEventPublisher 基础设施实现（如不存在）
- [x] 10.2 创建 DomainEventRepository 基础设施实现（如不存在）

## 11. 应用层 - DTO/VO 模型（遵循 user-application.md）

- [x] 11.1 在 application/query/model/valuation/ 创建 ValuationDTO
- [x] 11.2 在 application/query/model/transaction/ 创建 TransactionDTO
- [x] 11.3 在 application/query/model/delivery/ 创建 DeliveryDTO
- [x] 11.4 如有需要，使用 @Sensitive 注解实现脱敏

## 12. 应用层 - 命令服务（遵循 user-application.md）

- [x] 12.1 在 application/command/valuation/ 创建 ValuationCommand 接口
- [x] 12.2 在 application/command/valuation/ 创建 CreateValuationCommand（实现 Command，带 @Valid 校验）
- [x] 12.3 在 application/command/transaction/ 创建 TransactionCommand 接口
- [x] 12.4 在 application/command/delivery/ 创建 DeliveryCommand 接口
- [x] 12.5 在 application/command/impl/ 创建 AppService 实现类（使用 @Transactional）
- [x] 12.6 使用 @RateLimiter 注解实现限流（如需要）

## 13. 应用层 - 查询服务

- [x] 13.1 在 application/query/valuation/ 创建 ValuationQueryService 接口
- [x] 13.2 在 application/query/transaction/ 创建 TransactionQueryService 接口
- [x] 13.3 在 application/query/delivery/ 创建 DeliveryQueryService 接口
- [x] 13.4 在 application/query/impl/ 创建 QueryService 实现类（@Transactional(readOnly = true)）

## 14. 应用层 - 实时估值计算

- [x] 14.1 在 application/ 创建 ValuationCalculationAppService
- [x] 14.2 实现计算逻辑：valuationAmount + transactionAmount + deliveryAmount

## 15. 交互层 - REST API（遵循 user-interaction.md 和 api-security.md）

- [x] 15.1 在 interaction/api/ 创建 ValuationApi 控制器
- [ ] 15.2 在 interaction/api/ 创建 TransactionApi 控制器
- [ ] 15.3 在 interaction/api/ 创建 DeliveryApi 控制器
- [x] 15.4 添加 @Valid 参数校验注解
- [ ] 15.5 添加幂等性控制（如 @Idempotent）
- [ ] 15.6 在 interaction/config/ 配置全局异常处理（如需要特殊处理）

## 16. Git 分支与提交（遵循 git-branch-management.md 和 git-commit规范.md）

- [x] 16.1 从 uat 创建特性分支 T_REAL_CALC_VAL
- [x] 16.2 按规范格式提交：comment+作者+任务号+描述
- [ ] 16.3 合并顺序：T → dev → st → uat

## 17. 集成测试（遵循 api-testing.md）

### 17.1 单元测试

- [ ] 17.1.1 在 test/valuation/domain/service/ 创建 ValuationCalculationServiceTest
- [ ] 17.1.2 测试计算逻辑：无交易无交付
- [ ] 17.1.3 测试计算逻辑：含成功交易
- [ ] 17.1.4 测试计算逻辑：含成功交付
- [ ] 17.1.5 测试计算逻辑：含所有组件
- [ ] 17.1.6 测试计算逻辑：忽略失败交易
- [ ] 17.1.7 测试计算逻辑：忽略估值日期之前交易
- [ ] 17.1.8 测试计算逻辑：空估值记录返回0

### 17.2 应用服务测试

- [ ] 17.2.1 在 test/valuation/application/command/ 创建 ValuationAppServiceTest
- [ ] 17.2.2 测试创建估值记录
- [ ] 17.2.3 测试创建交易记录
- [ ] 17.2.4 测试创建交付记录（现金）
- [ ] 17.2.5 测试创建交付记录（金融产品）
- [ ] 17.2.6 测试删除记录

### 17.3 查询服务测试

- [ ] 17.3.1 在 test/valuation/application/query/ 创建 ValuationQueryServiceTest
- [ ] 17.3.2 测试按ID查询
- [ ] 17.3.3 测试按项目ID查询
- [ ] 17.3.4 测试按项目代码查询

### 17.4 API 集成测试

- [ ] 17.4.1 在 test/valuation/interaction/api/ 创建 ValuationApiTest
- [ ] 17.4.2 测试创建估值 - 成功场景
- [ ] 17.4.3 测试创建估值 - 参数校验失败（projectId为空）
- [ ] 17.4.4 测试创建估值 - 参数校验失败（targetAmount为负数）
- [ ] 17.4.5 测试查询估值 - 记录存在
- [ ] 17.4.6 测试查询估值 - 记录不存在
- [ ] 17.4.7 测试删除估值 - 成功
- [ ] 17.4.8 测试计算估值 - 按项目ID
- [ ] 17.4.9 测试计算估值 - 按项目代码

### 17.5 限流测试

- [ ] 17.5.1 测试超出限流阈值返回错误

### 17.6 事务测试

- [ ] 17.6.1 测试异常时事务回滚

## 18. 文档

- [x] 18.1 更新模块 README 文件
- [x] 18.2 添加 API 文档注释
- [ ] 18.3 更新数据库设计文档
