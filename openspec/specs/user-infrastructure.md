# user-infrastructure（基础设施层）设计规范

本文档定义了 `user-infrastructure` 模块的设计规范，该模块负责外部依赖实现、数据库访问、事件发布等基础设施功能。

---

## 1. 模块职责

`user-infrastructure` 是基础设施层，负责：
- 仓储接口实现
- 数据库访问（Mapper、PO）
- 对象转换（Converter）
- 领域事件发布和存储
- 外部服务适配器实现
- RPC 调用实现

**依赖约束**：基础设施层可以依赖领域层接口和 user-common，但不能依赖应用层。

---

## 2. 包结构规范

```
user-infrastructure/src/main/java/net/zhaixing/blog/user/infra/
├── db/                       # 数据库访问
│   ├── model/                # 持久化对象（PO）
│   │   └── {业务名称}PO.java
│   ├── mapper/               # MyBatis Mapper 接口
│   │   └── {业务名称}Mapper.java
│   ├── converter/           # 对象转换器
│   │   └── {业务名称}Converter.java
│   └── repository/           # 仓储实现
│       └── {业务名称}RepositoryImpl.java
├── event/                    # 领域事件
│   ├── DomainEventPublisherImpl.java
│   ├── DomainEventRepositoryImpl.java
│   └── NeedSaveEventHandlerAspect.java
├── adapter/                  # 适配器实现
│   └── {外部服务}AdapterImpl.java
├── rpc/                      # 外部 RPC 调用
│   ├── {外部服务}Api.java
│   └── {外部服务}DTO.java
└── config/                   # 配置类
```

---

## 3. 仓储实现（Repository Implementation）规范

### 3.1 接口实现

仓储实现实现领域层定义的仓储接口。

```java
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Resource
    private UserMapper userMapper;
    
    @Override
    public User byId(Long id) {
        UserPO user = userMapper.selectById(id);
        return UserConverter.deserialize(user);
    }
    
    @Override
    public User save(User user) {
        UserPO userPo = UserConverter.serialize(user);
        if (Objects.isNull(user.getId())) {
            userMapper.insert(userPo);
        } else {
            userMapper.updateById(userPo);
        }
        return UserConverter.deserialize(userPo);
    }
}
```

**命名规范**：`{业务名称}RepositoryImpl`

**证据**：`user-infrastructure/.../db/repository/UserRepositoryImpl.java:24`

### 3.2 注解使用

- 使用 `@Repository` 注解
- 可以使用 `@Resource` 或 `@Autowired` 注入 Mapper

---

## 4. 持久化对象（PO）规范

### 4.1 定义

PO（Persistent Object）对应数据库表结构。

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class UserPO extends BaseModel {
    private String userName;
    private String realName;
    private String phone;
    private Long unitId;
    private String roleIds;
}
```

**命名规范**：`{业务名称}PO`

**证据**：`user-infrastructure/.../db/model/UserPO.java:19`

### 4.2 注解使用

- 使用 `@TableName` 指定表名
- 继承 `BaseModel` 获取公共字段（id, gmtCreate, gmtModified）
- 使用 Lombok `@Data`、`@EqualsAndHashCode(callSuper = true)`

---

## 5. Mapper 规范

### 5.1 接口定义

使用 MyBatis Plus 的 Mapper 接口。

```java
public interface UserMapper extends BaseMapper<UserPO> {
    // 自定义查询方法
    Page<UserPO> userPage(Page<UserPO> page, @Param("query") KeywordQuery keywordQuery);
}
```

**命名规范**：`{业务名称}Mapper`

**证据**：`user-infrastructure/.../db/mapper/UserMapper.java`

---

## 6. 转换器（Converter）规范

### 6.1 定义

Converter 负责 PO 和领域模型之间的转换。

```java
public class UserConverter {
    public static User deserialize(UserPO po) {
        User user = User.builder()
                .id(po.getId())
                .userName(po.getUserName())
                .build();
        user.bindUnit(po.getUnitId());
        user.bindRole(po.getRoleIds());
        return user;
    }
    
    public static UserPO serialize(User user) {
        UserPO po = new UserPO();
        BeanUtils.copyProperties(user, po);
        po.setUnitId(user.getUnit().getId());
        return po;
    }
}
```

**命名规范**：`{业务名称}Converter`

**证据**：`user-infrastructure/.../db/converter/UserConverter.java:18`

### 6.2 方法命名

- `deserialize(PO)`: PO → 领域模型
- `serialize(领域模型)`: 领域模型 → PO

---

## 7. 适配器实现（Adapter Implementation）规范

### 7.1 接口

适配器接口定义在领域层，实现放在基础设施层。

```java
public interface UnitAdapter extends Adapter {
    UnitDTO byUnitId(Long unitId);
}
```

### 7.2 实现

```java
@Component
class UnitAdapterImpl implements UnitAdapter {
    @Override
    public UnitDTO byUnitId(Long unitId) {
        // 调用外部服务获取数据
        return new UnitDTO(10000L, "XX单位");
    }
}
```

**命名规范**：`{外部服务}AdapterImpl`

**证据**：`user-infrastructure/.../adapter/UnitAdapterImpl.java:16`

---

## 8. 领域事件实现规范

### 8.1 事件发布器

```java
@Component
public class DomainEventPublisherImpl implements DomainEventPublisher {
    @Override
    public <EVENT extends BaseDomainEvent<?>> void publish(EVENT event) {
        // 发布事件到消息队列
    }
    
    @Override
    public <EVENT extends BaseDomainEvent<?>> void publishAndSave(EVENT event) {
        // 发布并保存事件
    }
}
```

**证据**：`user-infrastructure/.../event/DomainEventPublisherImpl.java`

### 8.2 事件存储

```java
@Component
public class DomainEventRepositoryImpl implements DomainEventRepository {
    // 保存和查询事件
}
```

**证据**：`user-infrastructure/.../event/DomainEventRepositoryImpl.java`

---

## 9. 反模式清单

| 反模式 | 说明 | 正确做法 |
|--------|------|----------|
| 基础设施层包含业务逻辑 | 基础设施层只负责数据访问 | 业务逻辑应在领域层 |
| 直接在 Controller 使用 Mapper | 应通过应用层仓储 | 使用应用层服务 |
| PO 包含业务方法 | PO 只映射数据库字段 | 业务逻辑在领域模型 |

---

## 10. 调用关系约束

### 10.1 基础设施层可调用

- 领域层接口
- user-common
- 外部服务（RPC、消息队列）

### 10.2 基础设施层禁止调用

- 应用层
- 交互层

---

## 参考实现

- 仓储实现示例：`user-infrastructure/.../db/repository/UserRepositoryImpl.java`
- PO 示例：`user-infrastructure/.../db/model/UserPO.java`
- Converter 示例：`user-infrastructure/.../db/converter/UserConverter.java`
- 适配器示例：`user-infrastructure/.../adapter/UnitAdapterImpl.java`
