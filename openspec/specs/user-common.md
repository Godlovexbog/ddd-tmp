# user-common（公共基础层）设计规范

本文档定义了 `user-common` 模块的设计规范，该模块为其他所有模块提供公共的基础设施。

---

## 1. 模块职责

`user-common` 是项目的公共基础层，负责定义：
- 领域标记接口（DDD 核心类型）
- 通用模型（结果模型、查询模型）
- 公共异常
- 工具类

**重要约束**：`user-common` 不能依赖其他任何业务模块（user-domain、user-application 等）

---

## 2. 包结构规范

```
user-common/src/main/java/net/zhaixing/blog/user/common/
├── domain/           # 领域标记接口
├── model/            # 通用模型
│   ├── result/       # 结果模型（Result, PageResult, Page, BaseResult, BaseModel）
│   └── query/        # 查询模型（PageQuery, KeywordQuery）
├── exception/        # 公共异常
└── util/             # 工具类
```

---

## 3. 领域标记接口规范

### 3.1 Marker（标记接口基类）

所有领域标记接口的父接口。

```java
public interface Marker {
}
```

**证据**：`user-common/.../domain/Marker.java:11`

### 3.2 Entity（实体接口）

具有唯一标识的对象，用于表示业务实体。

```java
public interface Entity extends Marker {
}
```

**命名规范**：无特殊命名要求，实现类使用业务名称

**证据**：`user-common/.../domain/Entity.java:11`

### 3.3 AggregateRoot（聚合根接口）

聚合根是聚合的管理入口，继承自 Entity。

```java
public interface AggregateRoot extends Marker {
}
```

**命名规范**：无特殊命名要求，实现类使用业务名称

**证据**：`user-common/.../domain/AggregateRoot.java:11`

### 3.4 ValueObject（值对象接口）

值对象通过属性值比较相等性。

```java
public interface ValueObject<T> extends Marker {
    boolean sameValueAs(T other);
}
```

**命名规范**：使用业务名称，如 `Address`、`Money`

**证据**：`user-common/.../domain/ValueObject.java:11-19`

### 3.5 Repository（仓储接口）

仓储是领域模型和存储之间的桥梁。

```java
public interface Repository<AGGREGATE, ID extends Serializable> {
    void delete(ID id);
    AGGREGATE byId(ID id);
    AGGREGATE save(AGGREGATE aggregate);
    default AGGREGATE saveAndFlush(AGGREGATE aggregate) {
        return aggregate;
    }
}
```

**命名规范**：`{业务名称}Repository`，如 `UserRepository`

**证据**：`user-common/.../domain/Repository.java:13-46`

### 3.6 DomainService（领域服务接口）

当领域逻辑无法放在实体或值对象中时使用。

```java
public interface DomainService extends Marker {
}
```

**命名规范**：`{业务名称}DomainService`，如 `UserDomainService`

**证据**：`user-common/.../domain/DomainService.java:11`

### 3.7 AppService（应用服务接口）

应用服务用于业务流程编排。

```java
public interface AppService extends Marker {
}
```

**命名规范**：`{业务名称}AppService`，如 `UserAppService`

**证据**：`user-common/.../domain/AppService.java:11`

### 3.8 AppQueryService（查询应用服务接口）

```java
public interface AppQueryService extends Marker {
}
```

**命名规范**：`{业务名称}AppQueryService`，如 `UserAppQueryService`

**证据**：`user-common/.../domain/AppQueryService.java:11`

### 3.9 Ability（能力接口）

能力是应用层的可复用业务逻辑封装。

```java
public interface Ability extends Marker {
}
```

**命名规范**：`{业务动作}Ability`，如 `UserCreateAbility`

**证据**：`user-common/.../domain/Ability.java:11`

### 3.10 Adapter（适配器接口）

用于外部服务调用的适配。

```java
public interface Adapter extends Marker {
}
```

**命名规范**：`{外部服务}Adapter`，如 `UnitAdapter`

**证据**：`user-common/.../domain/Adapter.java:11`

### 3.11 Command（命令接口）

用于封装命令对象。

```java
public interface Command extends Marker {
}
```

**命名规范**：`{业务动作}Command` 或 `{业务动作}AbilityCommand`

**证据**：`user-common/.../domain/Command.java:11`

---

## 4. 通用模型规范

### 4.1 结果模型

| 类名 | 职责 | 证据 |
|------|------|------|
| `Result<T>` | 通用返回结果 | `user-common/.../model/result/Result.java` |
| `PageResult<T>` | 分页返回结果 | `user-common/.../model/result/PageResult.java` |
| `Page<T>` | 分页数据 | `user-common/.../model/result/Page.java` |
| `BaseResult` | 结果状态码定义 | `user-common/.../model/result/BaseResult.java` |
| `BaseModel` | 基础模型（包含 id, gmtCreate, gmtModified） | `user-common/.../model/result/BaseModel.java` |

### 4.2 查询模型

| 类名 | 职责 | 证据 |
|------|------|------|
| `PageQuery` | 分页查询参数 | `user-common/.../model/query/PageQuery.java` |
| `KeywordQuery` | 关键词查询参数 | `user-common/.../model/query/KeywordQuery.java` |

---

## 5. 异常规范

### 5.1 ValidationException

参数校验异常。

**证据**：`user-common/.../exception/ValidationException.java`

### 5.2 ServiceException

业务服务异常。

**证据**：`user-common/.../exception/ServiceException.java`

---

## 6. 工具类规范

### 6.1 ValidationUtil

参数校验工具类。

**证据**：`user-common/.../util/ValidationUtil.java`

### 6.2 GsonUtil

JSON 序列化工具类。

**证据**：`user-common/.../util/GsonUtil.java`

---

## 7. 反模式清单

| 反模式 | 说明 | 正确做法 |
|--------|------|----------|
| 在 common 中引入业务模块依赖 | user-common 依赖其他业务模块 | user-common 应该是纯公共的，不含任何业务逻辑 |
| 在 common 中使用 @Autowired | 依赖注入不属于 common 职责 | common 只定义接口和模型，不涉及 Spring bean |

---

## 8. 新增规范要点

新增 `user-common` 类型时：
1. 创建 `domain/` 下的接口，继承 `Marker`
2. 使用 `@author`、`@version`、`@date` 文档注释
3. 不包含任何实现逻辑，只做标记
