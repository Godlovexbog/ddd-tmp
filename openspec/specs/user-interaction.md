# user-interaction（交互层）设计规范

本文档定义了 `user-interaction` 模块的设计规范，该模块负责对外提供 HTTP 接口、事件消费和全局配置。

---

## 1. 模块职责

`user-interaction` 是交互层（也称接口层），负责：
- HTTP REST API 控制器
- 事件消费者
- 全局异常处理
- 请求/响应拦截

**依赖约束**：交互层只能依赖应用层服务，不能直接访问领域层或基础设施层。

---

## 2. 包结构规范

```
user-interaction/src/main/java/net/zhaixing/blog/user/interaction/
├── api/                      # REST API 控制器
│   └── {业务名称}Api.java
├── event/                    # 事件处理
│   └── handler/
│       └── {业务}EventHandler.java
├── mq/                       # 消息队列（可选）
└── config/                   # 全局配置
    ├── GlobalExceptionHandler.java
    └── 其他配置类
```

---

## 3. REST API 控制器规范

### 3.1 定义

使用 `@RestController` 和 `@RequestMapping` 注解。

```java
@RestController
@RequestMapping("/api/blog/")
public class UserApi {
    @Resource
    UserAppService userApplicationService;
    
    @Resource
    UserAppQueryService userQueryApplicationService;
    
    @PostMapping("user")
    public Result<Void> create(@RequestBody @Valid CreateUserAbilityCommand command) {
        userApplicationService.create(command);
        return Result.ok(BaseResult.INSERT_SUCCESS);
    }
    
    @GetMapping("user")
    public PageResult<UserPageVO> query(KeywordQuery query) {
        Page<UserPageVO> users = userQueryApplicationService.userPage(query);
        return PageResult.ok(users);
    }
}
```

**命名规范**：`{业务名称}Api`

**证据**：`user-interaction/.../api/UserApi.java:28`

### 3.2 注解使用

- `@RestController`：标记为 REST 控制器
- `@RequestMapping`：定义基础路径
- `@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping`：HTTP 方法映射
- `@PathVariable`：路径参数
- `@RequestBody`：请求体绑定
- `@Valid`：参数校验

### 3.3 依赖注入

- 注入应用层服务接口（`UserAppService`、`UserAppQueryService`）
- **禁止**直接注入仓储或 Mapper

---

## 4. 全局异常处理规范

### 4.1 定义

使用 `@RestControllerAdvice` 注解。

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public Object handle(ValidationException ex) {
        String errorMessage = messageSource.getMessage(ex.getErrorCode(), ex.getMessage(), ex.getParams());
        return Result.error(ex.getErrorCode(), errorMessage);
    }
    
    @ExceptionHandler(ServiceException.class)
    public Object handle(ServiceException ex) {
        return Result.error(ex.getErrorCode(), ex.getMessage());
    }
    
    @ExceptionHandler(Throwable.class)
    public Object handle(Throwable ex) {
        log.error("全局异常", ex);
        return Result.error(BaseResult.SYSTEM_ERROR);
    }
}
```

**命名规范**：`GlobalExceptionHandler`

**证据**：`user-interaction/.../config/GlobalExceptionHandler.java:33`

### 4.2 异常处理类型

| 异常类型 | 处理方式 |
|---------|----------|
| `ValidationException` | 返回校验错误消息 |
| `ServiceException` | 返回业务错误 |
| `ConstraintViolationException` | 返回参数校验错误 |
| `MethodArgumentNotValidException` | 返回请求参数错误 |
| `Throwable` | 捕获系统异常，记录日志并返回系统错误 |

---

## 5. 事件消费者规范

### 5.1 定义

处理领域事件或消息队列事件。

```java
@Component
public class UserEventHandler {
    @EventListener
    public void handleUserCreateEvent(UserCreateEvent event) {
        // 处理用户创建事件
    }
}
```

**命名规范**：`{业务}EventHandler`

---

## 6. 返回结果规范

### 6.1 单条结果

```java
Result<Void> create(@RequestBody @Valid CreateUserAbilityCommand command) {
    return Result.ok(BaseResult.INSERT_SUCCESS);
}
```

### 6.2 分页结果

```java
PageResult<UserPageVO> query(KeywordQuery query) {
    Page<UserPageVO> users = userQueryApplicationService.userPage(query);
    return PageResult.ok(users);
}
```

---

## 7. 反模式清单

| 反模式 | 说明 | 正确做法 |
|--------|------|----------|
| 交互层直接注入仓储 | 应通过应用层服务 | 注入应用层接口 |
| 交互层包含业务逻辑 | 只做请求转发 | 调用应用层方法 |
| 交互层使用 @Transactional | 事务应在应用层 | 移除事务注解 |

---

## 8. 调用关系约束

### 8.1 交互层可调用

- 应用层服务接口

### 8.2 交互层禁止调用

- 领域层（实体、领域服务、仓储）
- 基础设施层（仓储实现、Mapper）

---

## 9. API 路径规范

### 9.1 命名约定

| 操作 | 方法 | 路径示例 |
|------|------|----------|
| 创建 | POST | `/api/{module}/{entity}` |
| 更新 | PUT | `/api/{module}/{entity}` |
| 删除 | DELETE | `/api/{module}/{entity}/{id}` |
| 查询单条 | GET | `/api/{module}/{entity}/{id}` |
| 查询列表 | GET | `/api/{module}/{entity}` |

---

## 参考实现

- API 控制器示例：`user-interaction/.../api/UserApi.java`
- 全局异常处理示例：`user-interaction/.../config/GlobalExceptionHandler.java`
