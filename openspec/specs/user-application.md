# user-application（应用层）设计规范

本文档定义了 `user-application` 模块的设计规范，该模块负责业务流程编排、命令处理和查询服务。

---

## 1. 模块职责

`user-application` 是应用层，负责：
- 业务流程编排
- 命令处理（Command）
- 查询服务（Query）
- 能力封装（Ability）
- DTO 转换和组装

**依赖约束**：应用层可以依赖领域层和基础设施层，但不能被领域层依赖。

---

## 2. 包结构规范

```
user-application/src/main/java/net/zhaixing/blog/user/application/
├── command/                  # 命令服务（增删改）
│   ├── {业务名称}/           # 按业务模块分包
│   │   └── {业务名称}Command.java
│   └── impl/                 # 命令服务实现
├── query/                    # 查询服务
│   ├── {业务名称}QueryService.java
│   ├── model/                # 查询结果 DTO
│   │   └── {entity}DTO.java
│   │   └── {entity}VO.java
│   └── impl/                 # 查询服务实现
├── ability/                  # 能力封装
│   ├── share/                # 能力基类
│   │   └── BaseAbility.java
│   │   └── AbilityContext.java
│   └── {业务名称}/           # 按业务模块分包
│       └── {业务动作}Ability.java
└── factory/                  # 工厂（可选）
```

---

## 3. 命令服务（Command Service）规范

### 3.1 接口定义

命令服务接口继承 `AppService`，用于处理业务命令。

```java
public interface UserAppService extends AppService {
    void create(CreateUserAbilityCommand command);
    void updateUserName(UpdateUserCommand command);
    void delete(Long id);
}
```

**命名规范**：`{业务名称}AppService`

**证据**：`user-application/.../command/UserAppService.java:18`

### 3.2 实现类

使用 `@Service` 注解，添加 `@Slf4j`（应用层可以使用日志）。

```java
@Slf4j
@Service
public class UserAppServiceImpl implements UserAppService {
    @Resource
    UserRepository userRepository;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserName(UpdateUserCommand command) {
        // 业务流程编排
    }
}
```

**命名规范**：`{业务名称}AppServiceImpl`

**证据**：`user-application/.../command/impl/UserAppServiceImpl.java:31`

### 3.3 事务管理

- 命令方法使用 `@Transactional(rollbackFor = Exception.class)` 注解
- 事务边界应在应用层控制

**证据**：`user-application/.../command/impl/UserAppServiceImpl.java:47`

---

## 4. 命令对象（Command）规范

### 4.1 定义

命令对象实现 `Command` 接口，使用 `@Data` 注解。

```java
@Data
public class UpdateUserCommand implements Command {
    @NotNull(message = "{user.id.is.null}")
    private Long userId;
    
    @NotBlank(message = "{user.userName.is.blank}")
    private String userName;
}
```

**命名规范**：`{业务动作}Command` 或 `{业务动作}AbilityCommand`

**证据**：`user-application/.../command/user/UpdateUserCommand.java:18`

### 4.2 校验注解

- 使用 Jakarta Validation 注解（`@NotNull`, `@NotBlank`, `@Size` 等）
- 错误消息使用 `{key}` 格式，支持国际化

---

## 5. 查询服务（Query Service）规范

### 5.1 接口定义

查询服务接口继承 `AppQueryService`。

```java
public interface UserAppQueryService extends AppQueryService {
    Page<UserPageVO> userPage(KeywordQuery query);
    UserDTO detail(String userName);
}
```

**命名规范**：`{业务名称}AppQueryService`

**证据**：`user-application/.../query/UserAppQueryService.java:17`

### 5.2 实现类

```java
@Service
public class UserAppQueryServiceImpl implements UserAppQueryService {
    @Resource
    private UserMapper userMapper;
    
    @Override
    public Page<UserPageVO> userPage(KeywordQuery query) {
        // 查询逻辑
    }
}
```

**命名规范**：`{业务名称}AppQueryServiceImpl`

**证据**：`user-application/.../query/impl/UserAppQueryServiceImpl.java:35`

---

## 6. DTO/VO 规范

### 6.1 DTO（Data Transfer Object）

用于数据传输，通常包含完整的数据结构。

```java
@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String unitName;
    private List<RoleDTO> roles;
}
```

**命名规范**：`{业务名称}DTO`

**证据**：`user-application/.../query/model/user/dto/UserDTO.java`

### 6.2 VO（View Object）

用于视图展示，可能包含展示专用的字段转换。

```java
@Data
public class UserPageVO {
    private Long id;
    private String userName;
    private String unitName;
    private List<RoleDTO> roles;
}
```

**命名规范**：`{业务名称}PageVO`

**证据**：`user-application/.../query/model/user/dto/UserPageVO.java`

---

## 7. 能力（Ability）规范

### 7.1 基类

能力使用抽象基类 `BaseAbility`，提供模板方法模式。

```java
@Component
public abstract class BaseAbility<T, R> implements Ability {
    @Transactional(rollbackFor = Exception.class)
    public Result<R> executeAbility(T abilityCmd) {
        // 初始化上下文
        // 参数校验
        // 幂等性校验
        // 执行
    }
    
    public abstract void checkHandler(T abilityCmd);
    public abstract Result<R> checkIdempotent(T abilityCmd);
    public abstract Result<R> execute(T abilityCmd);
}
```

**证据**：`user-application/.../ability/share/BaseAbility.java:25-56`

### 7.2 能力实现

继承 `BaseAbility`，实现具体业务逻辑。

```java
@Service
public class UserCreateAbility extends BaseAbility<CreateUserAbilityCommand, Void> {
    @Override
    public void checkHandler(CreateUserAbilityCommand command) {
        // 参数校验
    }
    
    @Override
    public Result<Void> checkIdempotent(CreateUserAbilityCommand command) {
        // 幂等性校验
    }
    
    @Override
    public Result<Void> execute(CreateUserAbilityCommand command) {
        // 业务逻辑
    }
}
```

**命名规范**：`{业务动作}Ability`

**证据**：`user-application/.../ability/user/UserCreateAbility.java:33`

### 7.3 AbilityContext

使用 `AbilityContext` 在能力执行过程中传递数据。

```java
AbilityContext.putValue(ROLE_INFO_KEY, roles);
List<Role> roles = AbilityContext.getValue(ROLE_INFO_KEY);
AbilityContext.clearContext();
```

**证据**：`user-application/.../ability/user/UserCreateAbility.java:57`

---

## 8. 反模式清单

| 反模式 | 说明 | 正确做法 |
|--------|------|----------|
| 应用层直接注入 Mapper | 应该通过仓储访问数据 | 使用 Repository 接口 |
| 应用层包含领域逻辑 | 业务逻辑应在领域层 | 委托给领域服务或实体方法 |
| 使用 @Slf4j 在领域层 | 日志应在应用层或基础设施层 | 移除领域层的 @Slf4j |

---

## 9. 调用关系约束

### 9.1 应用层可调用

- 领域层：领域服务、仓储接口、领域事件发布器
- 基础设施层：仓储实现、适配器实现

### 9.2 应用层禁止调用

- 交互层（API 控制器）
- 基础设施层直接访问数据库（应通过仓储）

---

## 参考实现

- 命令服务示例：`user-application/.../command/UserAppServiceImpl.java`
- 查询服务示例：`user-application/.../query/UserAppQueryServiceImpl.java`
- 能力示例：`user-application/.../ability/user/UserCreateAbility.java`
