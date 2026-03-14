# 接口安全规范

本文档定义了项目 REST API 接口的安全规范，用于保障系统安全。

---

## 1. 认证与授权

### 1.1 身份认证

| 认证方式 | 说明 | 适用场景 |
|----------|------|----------|
| Token | JWT Token 认证 | 移动端、Web 端 |
| OAuth 2.0 | 第三方授权 | 第三方登录 |
| API Key | 接口密钥 | 服务间调用 |

### 1.2 接口权限控制

```java
// 示例：基于注解的权限控制
@PreAuthorize("hasRole('USER')")
@PostMapping("user")
public Result<Void> create(@RequestBody @Valid CreateUserCommand command) {
    // 仅登录用户可访问
}

// 示例：基于角色的控制
@PreAuthorize("hasAnyRole('ADMIN', 'USER_MANAGER')")
@DeleteMapping("/{id}")
public Result<Void> delete(@PathVariable Long id) {
    // 仅管理员或用户管理员可删除
}
```

### 1.3 敏感接口

敏感接口需要额外验证：

```java
// 示例：支付、提现等敏感操作需要二次验证
@PostMapping("withdraw")
@RequiresVerification(code = "SMS", scene = "WITHDRAW")  // 需要短信验证码
public Result<Void> withdraw(@RequestBody WithdrawCommand command) {
    // 提现操作
}
```

---

## 2. 参数校验

### 2.1 必须使用 @Valid

```java
@PostMapping("user")
public Result<Void> create(@RequestBody @Valid CreateUserCommand command) {
    // @Valid 必须添加，自动触发校验
}

// 命令对象定义校验规则
@Data
public class CreateUserCommand implements Command {
    @NotBlank(message = "{user.name.not.blank}")
    private String userName;
    
    @NotNull(message = "{user.age.not.null}")
    @Min(value = 18, message = "{user.age.min}")
    @Max(value = 100, message = "{user.age.max}")
    private Integer age;
    
    @Email(message = "{user.email.format}")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{user.phone.format}")
    private String phone;
}
```

### 2.2 常用校验注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@NotNull` | 不能为 null | 用户ID |
| `@NotBlank` | 不能为空字符串 | 用户名 |
| `@NotEmpty` | 不能为空集合 | 角色列表 |
| `@Min` / `@Max` | 数值范围 | 年龄 |
| `@Size` | 字符串/集合长度 | 密码 |
| `@Email` | 邮箱格式 | 邮箱 |
| `@Pattern` | 正则表达式 | 手机号 |
| `@DecimalMin` / `@DecimalMax` | BigDecimal 范围 | 金额 |

### 2.3 自定义校验

```java
// 自定义校验注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "{phone.format.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 校验实现
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.matches("^1[3-9]\\d{9}$");
    }
}
```

---

## 3. 敏感数据保护

### 3.1 脱敏处理

```java
// 示例：返回数据脱敏
@Data
public class UserDTO {
    private Long id;
    
    // 手机号脱敏：138****1234
    @Sensitive(type = SensitiveType.PHONE)
    private String phone;
    
    // 邮箱脱敏：a***@example.com
    @Sensitive(type = SensitiveType.EMAIL)
    private String email;
    
    // 身份证脱敏：310***********1234
    @Sensitive(type = SensitiveType.ID_CARD)
    private String idCard;
    
    // 银行卡脱敏：6222 **** **** 1234
    @Sensitive(type = SensitiveType.BANK_CARD)
    private String bankCard;
}
```

### 3.2 密码安全

```java
// 密码必须加密存储，使用 BCrypt
@Data
public class CreateUserCommand {
    @NotBlank
    @Length(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$")
    private String password;  // 明文传输，存储时加密
}

// 密码加密示例
public class PasswordEncoder {
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
```

---

## 4. 接口访问控制

### 4.1 限流策略

```java
// 示例：基于注解的限流
@RestController
@RequestMapping("/api/")
public class UserApi {
    
    // 每分钟最多 60 次请求
    @GetMapping("user/{id}")
    @RateLimiter(value = 60, timeUnit = TimeUnit.MINUTES)
    public Result<UserDTO> getUser(@PathVariable Long id) {
        // 查询用户
    }
    
    // 登录接口每分钟最多 10 次（防止暴力破解）
    @PostMapping("login")
    @RateLimiter(value = 10, timeUnit = TimeUnit.MINUTES)
    public Result<LoginResult> login(@RequestBody @Valid LoginCommand command) {
        // 登录逻辑
    }
}
```

### 4.2 IP 黑/白名单

```yaml
# application.yml 配置示例
security:
  ip:
    whitelist:
      - 10.0.0.0/8
      - 192.168.1.0/24
    blacklist:
      - 10.0.0.100
```

### 4.3 接口幂等性

```java
// 示例：基于 Token 的幂等性控制
@PostMapping("order")
@Idempotent(key = "#token", expire = 7200)  // 2小时内不重复处理
public Result<OrderDTO> createOrder(@RequestBody @Valid OrderCommand command,
                                     @RequestHeader("Idempotent-Token") String token) {
    // 创建订单
}
```

---

## 5. 日志与审计

### 5.1 访问日志

```java
// 自动记录访问日志
@Slf4j
@RestController
@RequestMapping("/api/")
public class UserApi {
    
    @GetMapping("user/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        log.info("[API] GET /api/user/{} - userId={}", id, getCurrentUserId());
        // 业务逻辑
    }
}
```

### 5.2 敏感操作审计

```java
// 示例：审计日志
@Service
public class AuditService {
    
    public void audit(String operation, String operator, String detail) {
        AuditLog log = AuditLog.builder()
                .operation(operation)
                .operator(operator)
                .detail(detail)
                .ip(getClientIp())
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}

// 使用示例
@DeleteMapping("/{id}")
public Result<Void> delete(@PathVariable Long id) {
    userRepository.delete(id);
    auditService.audit("DELETE_USER", getCurrentUserId(), "删除用户ID=" + id);
    return Result.ok();
}
```

---

## 6. HTTPS 配置

### 6.1 强制 HTTPS

```java
// 配置强制 HTTPS
@Configuration
public class SecurityConfig {
    
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(8443);
        factory.setScheme("https");
        factory.setSecure(true);
        return factory;
    }
}
```

### 6.2 证书配置

```yaml
# application.yml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: tomcat
```

---

## 7. CORS 跨域配置

### 7.1 允许的来源

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // 生产环境应指定域名
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

---

## 8. 常见安全漏洞防护

| 漏洞类型 | 防护措施 |
|----------|----------|
| SQL 注入 | 使用预编译语句（PreparedStatement） |
| XSS 攻击 | 输入转义、输出编码 |
| CSRF 攻击 | 使用 Token 验证 |
| 越权访问 | 接口权限校验、数据归属验证 |
| 暴力破解 | 限流、验证码、账号锁定 |
| 信息泄露 | 敏感数据脱敏、错误信息模糊化 |

---

## 9. 错误信息规范

### 9.1 避免泄露敏感信息

```java
// 错误示例：泄露敏感信息
{
    "code": "USER_NOT_FOUND",
    "message": "用户不存在，邮箱为 test@example.com"  // 泄露了邮箱
}

// 正确示例
{
    "code": "USER_NOT_FOUND",
    "message": "用户不存在"  // 不泄露具体信息
}
```

### 9.2 统一错误响应

```java
// 错误响应格式
{
    "code": "ERROR_CODE",
    "message": "错误描述",
    "data": null,
    "timestamp": "2024-01-01 12:00:00"
}
```

---

## 10. 接口版本管理

### 10.1 版本控制

```java
@RestController
@RequestMapping("/api/v1/")
public class UserApiV1 {
    // v1 版本接口
}

@RestController
@RequestMapping("/api/v2/")
public class UserApiV2 {
    // v2 版本接口
}
```

### 10.2 版本兼容性

- 保持旧版本兼容至少一个版本周期
- 新版本需要在前端适配后再废弃旧版本
