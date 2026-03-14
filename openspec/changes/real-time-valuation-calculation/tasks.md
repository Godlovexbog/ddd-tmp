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

- [ ] 9.1 在 infrastructure/db/model/ 创建 PO 类（继承 BaseModel，包含 is_deleted 字段）
- [ ] 9.2 在 infrastructure/db/mapper/ 创建 Mapper 接口
- [ ] 9.3 在 infrastructure/db/converter/ 创建 Converter 类（serialize/deserialize 方法）
- [ ] 9.4 在 infrastructure/db/repository/ 创建 Repository 实现（实现领域层接口，使用 @Repository）

## 10. 基础设施层 - 事件

- [ ] 10.1 创建 DomainEventPublisher 基础设施实现（如不存在）
- [ ] 10.2 创建 DomainEventRepository 基础设施实现（如不存在）

## 11. 应用层 - DTO/VO 模型（遵循 user-application.md）

- [ ] 11.1 在 application/query/model/valuation/ 创建 ValuationDTO
- [ ] 11.2 在 application/query/model/transaction/ 创建 TransactionDTO
- [ ] 11.3 在 application/query/model/delivery/ 创建 DeliveryDTO
- [ ] 11.4 如有需要，使用 @Sensitive 注解实现脱敏

## 12. 应用层 - 命令服务（遵循 user-application.md）

- [ ] 12.1 在 application/command/valuation/ 创建 ValuationCommand 接口
- [ ] 12.2 在 application/command/valuation/ 创建 CreateValuationCommand（实现 Command，带 @Valid 校验）
- [ ] 12.3 在 application/command/transaction/ 创建 TransactionCommand 接口
- [ ] 12.4 在 application/command/delivery/ 创建 DeliveryCommand 接口
- [ ] 12.5 在 application/command/impl/ 创建 AppService 实现类（使用 @Transactional）
- [ ] 12.6 使用 @RateLimiter 注解实现限流（如需要）

## 13. 应用层 - 查询服务

- [ ] 13.1 在 application/query/valuation/ 创建 ValuationQueryService 接口
- [ ] 13.2 在 application/query/transaction/ 创建 TransactionQueryService 接口
- [ ] 13.3 在 application/query/delivery/ 创建 DeliveryQueryService 接口
- [ ] 13.4 在 application/query/impl/ 创建 QueryService 实现类（@Transactional(readOnly = true)）

## 14. 应用层 - 实时估值计算

- [ ] 14.1 在 application/ 创建 ValuationCalculationAppService
- [ ] 14.2 实现计算逻辑：valuationAmount + transactionAmount + deliveryAmount

## 15. 交互层 - REST API（遵循 user-interaction.md 和 api-security.md）

- [ ] 15.1 在 interaction/api/ 创建 ValuationApi 控制器
- [ ] 15.2 在 interaction/api/ 创建 TransactionApi 控制器
- [ ] 15.3 在 interaction/api/ 创建 DeliveryApi 控制器
- [ ] 15.4 添加 @Valid 参数校验注解
- [ ] 15.5 添加幂等性控制（如 @Idempotent）
- [ ] 15.6 在 interaction/config/ 配置全局异常处理（如需要特殊处理）

## 16. Git 分支与提交（遵循 git-branch-management.md 和 git-commit规范.md）

- [x] 16.1 从 uat 创建特性分支 T_REAL_CALC_VAL
- [ ] 16.2 按规范格式提交：comment+作者+任务号+描述
- [ ] 16.3 合并顺序：T → dev → st → uat

## 17. 集成测试

- [ ] 17.1 测试估值记录 CRUD 操作
- [ ] 17.2 测试交易记录 CRUD 操作
- [ ] 17.3 测试交付记录 CRUD 操作
- [ ] 17.4 测试实时估值计算所有场景
- [ ] 17.5 测试 API 接口（包含参数校验测试）
- [ ] 17.6 测试幂等性

## 18. 文档

- [ ] 18.1 更新模块 README 文件
- [ ] 18.2 添加 API 文档注释
- [ ] 18.3 更新数据库设计文档
