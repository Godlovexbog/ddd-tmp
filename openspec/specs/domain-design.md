# 领域层设计规范

本文档定义了项目中领域层（Domain Layer）的设计规范，用于指导代码编写和存量代码重构。

---

## 1. 实体（Entity）设计规范

### 1.1 定义规范

实体必须实现 `Entity` 接口，该接口位于 `user-common` 模块的 `net.zhaixing.blog.user.common.domain.Entity`。

```java
// 正确示例
public class Role implements Entity {
    private Long id;
}

// 错误示例 - 不能直接 implements Entity 而不实现接口
public class User {  // 缺少 implements Entity
}
```

**证据**：
- `user-common/src/main/java/net/zhaixing/blog/user/common/domain/Entity.java:11` - `public interface Entity extends Marker`

### 1.2 注解使用

- 使用 Lombok `@Data` + `@AllArgsConstructor` 或 `@Builder`
- **禁止**在实体类中使用 `@Slf4j`（日志属于基础设施 concerns）

**证据**：
- `user-domain/src/main/java/net/zhaixing/blog/user/domain/aggregate/user/model/Role.java:17` - 正确使用 `@Data @AllArgsConstructor`
- `user-domain/src/main/java/net/zhaixing/blog/user/domain/aggregate/user/model/User.java:30` - **反模式**：使用了 `@Slf4j`

### 1.3 equals/hashCode

- 依赖 Lombok `@Data` 注解自动生成
- **禁止**手动重写 equals/hashCode（除非有特殊业务需求）

### 1.4 业务方法

实体可以包含业务方法，用于封装领域行为。

**证据**：
- `user-domain/.../model/User.java:88-92` - `bindRole(List<Long> roleIds)`
- `user-domain/.../model/User.java:113-115` - `bindAddress(String province, String city, String county)`

---

## 2. 聚合根（AggregateRoot）设计规范

### 2.1 定义规范

聚合根实现 `AggregateRoot` 接口，继承自 `Entity`。

```java
public class User implements AggregateRoot {
    // 聚合根定义
}
```

**证据**：
- `user-common/src/main/java/net/zhaixing/blog/user/common/domain/AggregateRoot.java:11` - `public interface AggregateRoot extends Marker`

### 2.2 唯一标识

聚合根必须有唯一标识（ID）字段。

**证据**：
- `user-domain/.../user/model/User.java:35` - `private Long id;`
- `user-domain/.../role/model/Role.java:26` - `private Long id;`

---

## 3. 值对象（Value Object）设计规范

### 3.1 定义规范

值对象实现 `ValueObject<T>` 接口。

```java
public class Address implements ValueObject<Address> {
    private String province;
    private String city;
    private String county;
}
```

**证据**：
- `user-domain/src/main/java/net/zhaixing/blog/user/domain/share/valueobject/Address.java:21`

### 3.2 相等性比较

必须实现 `sameValueAs(T other)` 方法，通过属性值比较相等性。

```java
@Override
public boolean sameValueAs(Address address) {
    return Objects.equals(this, address);
}
```

**证据**：
- `user-domain/.../valueobject/Address.java:44-46`

### 3.3 不可变性（推荐）

值对象应该设计为不可变的：
- 字段使用 `final` 修饰
- **不提供** `setter` 方法
- **不提供**无参构造函数（如果需要序列化，使用 `@NoArgsConstructor` + `@SuppressWarnings("unused")`）

**注意**：当前项目**未严格遵循**不可变设计，存在反模式。

### 3.4 校验（推荐）

值对象应该在构造函数中进行参数校验。

**注意**：当前项目**未实现**校验逻辑，存在改进空间。

---

## 4. 仓储接口（Repository）设计规范

### 4.1 定义规范

仓储接口继承 `Repository<AGGREGATE, ID>` 接口。

```java
public interface UserRepository extends Repository<User, Long> {
}
```

**证据**：
- `user-domain/.../user/repository/UserRepository.java:15`
- `user-common/.../Repository.java:13` - `public interface Repository<AGGREGATE, ID extends Serializable>`

### 4.2 基础方法

仓储接口继承以下基础方法：
- `void delete(ID id)` - 删除
- `AGGREGATE byId(ID id)` - 按ID查询
- `AGGREGATE save(AGGREGATE aggregate)` - 保存或更新
- `default AGGREGATE saveAndFlush(AGGREGATE aggregate)` - 保存并刷新

**证据**：
- `user-common/.../Repository.java:19-46`

### 4.3 自定义查询方法命名

自定义查询方法遵循以下命名规范：

| 查询类型 | 命名模式 | 示例 |
|---------|---------|------|
| 单条查询 | `by` + 字段名 | `byUserName(String userName)` |
| 列表查询 | `listBy` + 字段名复数 | `listByIds(List<Long> ids)` |
| 存在性检查 | `existBy` + 字段名 | `existByUserName(String userName)` |

**证据**：
- `user-domain/.../user/repository/UserRepository.java:22` - `User byUserName(String userName)`
- `user-domain/.../role/repository/RoleRepository.java:23` - `List<Role> listByIds(List<Long> ids)`

### 4.4 返回值类型

- 单条查询返回实体或聚合根
- 列表查询返回 `List<实体>`
- **禁止**返回 `void` 用于查询方法

---

## 5. 领域服务（Domain Service）设计规范

### 5.1 定义规范

领域服务实现领域逻辑，当实体方法无法封装时使用。

```java
public interface UserDomainService {
    // 领域服务接口
}
```

**证据**：
- `user-domain/.../user/service/UserDomainService.java`

### 5.2 实现规范

- 实现类放在 `impl` 包下
- 使用 `@Service` 注解（Spring 托管）

**证据**：
- `user-domain/.../user/service/impl/UserDomainServiceImpl.java`

---

## 6. 反模式清单

### 6.1 领域层反模式

| 反模式 | 位置 | 说明 |
|-------|------|------|
| 实体使用 @Slf4j | `User.java:30` | 日志属于基础设施层 |
| 值对象非不可变 | `Address.java` | 字段无 final，无校验 |

### 6.2 改进建议

1. **移除实体中的 @Slf4j**：日志应在应用层或基础设施层处理
2. **值对象添加校验**：在构造函数中进行参数校验
3. **值对象实现不可变**：使用 final 字段，移除 setter

---

## 附录：模块结构

```
user-common/           # 公共领域基础定义
  domain/
    Entity.java       # 实体接口
    ValueObject.java  # 值对象接口
    AggregateRoot.java # 聚合根接口
    Repository.java  # 仓储基接口

user-domain/           # 领域层实现
  aggregate/
    user/
      model/          # 实体、聚合根
      repository/     # 仓储接口
      service/         # 领域服务
    role/
      model/
      repository/
  share/
    valueobject/      # 共享值对象
```

---

## 参考实现

- 实体示例：`user-domain/.../model/Role.java`
- 聚合根示例：`user-domain/.../model/User.java`
- 值对象示例：`user-domain/.../valueobject/Address.java`
- 仓储接口示例：`user-domain/.../repository/UserRepository.java`
