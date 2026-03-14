# 项目模块结构与调用关系约束

本文档描述了 DDD 项目各模块的职责、类组成及调用关系约束。

---

## 1. 模块架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                     user-start (启动模块)                        │
│                   负责应用启动和配置                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 user-interaction (交互层)                        │
│  ┌─────────────────────┐  ┌─────────────────────────────────┐ │
│  │      API控制器       │  │        事件处理器                 │ │
│  │   (UserApi)         │  │   (UserEventHandler)            │ │
│  └─────────┬───────────┘  └─────────────────────────────────┘ │
└────────────┼────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                 user-application (应用层)                        │
│  ┌───────────────────┐  ┌─────────────────┐  ┌───────────────┐  │
│  │  应用服务接口       │  │  查询服务接口    │  │   能力(Ability) │  │
│  │ (UserAppService)  │  │(UserAppQuery-  │  │(UserCreate-    │  │
│  │                   │  │   Service)     │  │   Ability)     │  │
│  └─────────┬─────────┘  └────────┬────────┘  └───────┬───────┘  │
└────────────┼─────────────────────┼───────────────────┼──────────┘
             │                     │                   │
             ▼                     ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                 user-domain (领域层)                             │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐  ┌───────┐ │
│  │  聚合根      │  │   领域服务     │  │   仓储接口   │  │适配器 │ │
│  │  (User)     │  │(UserDomain-   │  │(UserRepo-   │  │接口   │ │
│  │  (Role)    │  │   Service)    │  │   sitory)   │  │(Unit- │ │
│  │             │  │               │  │             │  │Adapter│ │
│  └─────────────┘  └──────────────┘  └─────────────┘  └───────┘ │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    领域事件                                   ││
│  │         (UserCreateEvent, UserUpdateEvent...)               ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
             │                                    ▲
             │                                    │
             ▼                                    │
┌───────────────────────────────────────────────────────────────┐
│                 user-infrastructure (基础设施层)                 │
│  ┌───────────────┐  ┌─────────────┐  ┌───────────────────────┐ │
│  │  仓储实现      │  │  适配器实现  │  │   领域事件发布/存储    │ │
│  │(UserRepo-     │  │(UnitAdapter │  │(DomainEventPublisher  │ │
│  │   sitoryImpl) │  │   Impl)     │  │   Impl)               │ │
│  └───────────────┘  └─────────────┘  └───────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │              数据库访问层 (Mapper, PO, Converter)           ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                 user-common (公共基础层)                         │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  领域标记接口: Entity, ValueObject, AggregateRoot,           ││
│  │               Repository, DomainService, Ability,           ││
│  │               Adapter, AppService, AppQueryService         ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │  命令/查询: Command, Query                                   ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │  公共模型: Page, PageQuery, KeywordQuery                     ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │  结果模型: Result, PageResult, BaseResult                   ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │  异常: ValidationException, ServiceException                ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 各模块职责与类说明

### 2.1 user-common（公共基础层）

**职责**：定义所有领域的公共接口、标记类型、通用模型

| 包路径 | 职责 | 核心类 |
|--------|------|--------|
| `domain/` | 领域标记接口 | `Entity`, `AggregateRoot`, `ValueObject`, `Repository`, `DomainService`, `Ability`, `Adapter`, `AppService`, `AppQueryService`, `Command` |
| `model/result/` | 通用结果模型 | `Result`, `PageResult`, `Page`, `BaseResult` |
| `model/query/` | 查询模型 | `PageQuery`, `KeywordQuery` |
| `exception/` | 公共异常 | `ValidationException`, `ServiceException` |
| `util/` | 工具类 | `ValidationUtil`, `GsonUtil` |

---

### 2.2 user-domain（领域层）

**职责**：业务领域逻辑、领域模型、仓储接口定义

| 包路径 | 职责 | 核心类 |
|--------|------|--------|
| `aggregate/*/model/` | 聚合根和实体 | `User`, `Role` (聚合根), `Unit`, `Role` (实体) |
| `aggregate/*/repository/` | 仓储接口 | `UserRepository`, `RoleRepository` |
| `aggregate/*/service/` | 领域服务 | `UserDomainService`, `UserDomainServiceImpl` |
| `aggregate/*/event/` | 领域事件 | `UserCreateEvent`, `UserUpdateEvent`, `UserDeleteEvent` |
| `share/event/` | 事件基础设施 | `DomainEventPublisher`, `DomainEventRepository`, `BaseDomainEvent` |
| `share/valueobject/` | 共享值对象 | `Address` |
| `adapter/` | 适配器接口 | `UnitAdapter` |
| `adapter/model/` | 适配器模型 | `UnitDTO` |

**领域层依赖**：仅依赖 `user-common`，不依赖其他任何模块

---

### 2.3 user-application（应用层）

**职责**：业务流程编排、命令处理、查询服务

| 包路径 | 职责 | 核心类 |
|--------|------|--------|
| `command/` | 命令服务 | `UserAppService`, `UserAppServiceImpl` |
| `command/user/` | 命令对象 | `UpdateUserCommand`, `CreateUserAbilityCommand` |
| `query/` | 查询服务 | `UserAppQueryService`, `UserAppQueryServiceImpl`, `RoleAppQueryService` |
| `query/model/` | 查询结果DTO | `UserDTO`, `UserPageVO`, `RoleDTO` |
| `ability/` | 能力封装 | `UserCreateAbility`, `BaseAbility`, `AbilityContext` |
| `ability/share/` | 能力基类 | `BaseAbility` |

---

### 2.4 user-infrastructure（基础设施层）

**职责**：外部依赖实现、仓储具体实现、事件发布、数据库访问

| 包路径 | 职责 | 核心类 |
|--------|------|--------|
| `db/repository/` | 仓储实现 | `UserRepositoryImpl`, `RoleRepositoryImpl` |
| `db/mapper/` | 数据库映射 | `UserMapper`, `RoleMapper` |
| `db/model/` | 持久化对象 | `UserPO`, `RolePO` |
| `db/converter/` | 对象转换 | `UserConverter`, `RoleConverter` |
| `event/` | 事件实现 | `DomainEventPublisherImpl`, `DomainEventRepositoryImpl`, `NeedSaveEventHandlerAspect` |
| `adapter/` | 适配器实现 | `UnitAdapterImpl` |
| `rpc/` | 外部RPC调用 | `UnitApi`, `UnitInfoDTO` |

---

### 2.5 user-interaction（交互层）

**职责**：HTTP API 入口、事件消费、异常处理

| 包路径 | 职责 | 核心类 |
|--------|------|--------|
| `api/` | REST 控制器 | `UserApi` |
| `event/handler/` | 事件消费者 | `UserEventHandler` |
| `config/` | 全局配置 | `GlobalExceptionHandler` |

---

### 2.6 user-api（API 定义层）

**职责**：微服务 HTTP 接口定义包

此模块目前为空，用于定义对外暴露的 API 接口

---

### 2.7 user-rpc（RPC 定义层）

**职责**：微服务间 RPC 调用定义

此模块目前为空，用于定义跨服务调用接口

---

### 2.8 user-start（启动模块）

**职责**：应用启动入口

包含 Spring Boot 启动类 `UserAppStart`

---

## 3. 调用关系约束

### 3.1 允许的依赖方向

```
user-common  ◄──────────────  user-domain
    ▲                         │
    │                         │
user-common  ◄──────────────  user-application
    ▲                         │
    │                         │
user-common  ◄──────────────  user-infrastructure
    ▲                         │
    │                         │
user-common  ◄──────────────  user-interaction
```

### 3.2 具体调用规则

| 调用方 | 可调用模块 | 禁止调用 |
|--------|-----------|----------|
| **interaction** | application | domain, infrastructure, common (除model/result) |
| **application** | domain (接口), infrastructure (实现), common | - |
| **domain** | common | application, infrastructure, interaction |
| **infrastructure** | common, domain (接口) | application |
| **common** | - | 无限制（基础模块） |

### 3.3 核心约束

1. **领域层独立**：领域层 (`user-domain`) **只能依赖** `user-common`，禁止依赖应用层和基础设施层

2. **依赖倒置**：应用层依赖领域层接口，基础设施层实现领域层接口

3. **跨层调用**：
   - 应用层可以调用领域层（领域服务、仓储接口）
   - 应用层可以调用基础设施层（仓储实现、适配器实现）
   - 领域层**不能**调用应用层或基础设施层

4. **交互层限制**：交互层只能调用应用层服务，不能直接访问领域层或基础设施层

---

## 4. 典型调用链示例

### 4.1 命令调用链

```
UserApi (interaction)
    │
    ▼
UserAppServiceImpl (application)
    │
    ├──▶ UserCreateAbility (application)
    │        │
    │        ├──▶ Validation (user-common)
    │        │
    │        ├──▶ RoleRepository (domain interface)
    │        │        │
    │        │        └──▶ RoleRepositoryImpl (infrastructure)
    │        │
    │        ├──▶ UserDomainService (domain)
    │        │
    │        ├──▶ UserRepository (domain interface)
    │        │        │
    │        │        └──▶ UserRepositoryImpl (infrastructure)
    │        │
    │        └──▶ DomainEventPublisher (domain interface)
    │                 │
    │                 └──▶ DomainEventPublisherImpl (infrastructure)
```

### 4.2 查询调用链

```
UserApi (interaction)
    │
    ▼
UserAppQueryServiceImpl (application)
    │
    ├──▶ UserMapper (infrastructure) - 直接查库
    │
    ├──▶ UnitAdapter (domain interface)
    │        │
    │        └──▶ UnitAdapterImpl (infrastructure)
    │
    └──▶ RoleAppQueryService (application)
              │
              └──▶ RoleMapper (infrastructure)
```

---

## 5. 包命名规范

| 层级 | 包名 | 示例 |
|------|------|------|
| 领域层 | `domain.aggregate.{aggregate}.{model|repository|service|event}` | `domain.aggregate.user.model.User` |
| 应用层 | `application.{command|query|ability}.{entity}` | `application.command.UserAppService` |
| 基础设施层 | `infra.{db|rpc|event|adapter}.{entity}` | `infra.db.repository.UserRepositoryImpl` |
| 交互层 | `interaction.{api|event|config}` | `interaction.api.UserApi` |

---

## 6. 反模式警示

| 反模式 | 位置 | 说明 |
|--------|------|------|
| 领域层使用 @Slf4j | `UserDomainServiceImpl.java:20` | 领域层不应有日志 |
| 应用层直接注入 Mapper | `UserAppQueryServiceImpl.java:37` | 应通过仓储访问 |
| 交互层调用仓储 | 无（符合规范） | 禁止直接访问 |
