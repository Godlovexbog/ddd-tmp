# 接口测试规范

本文档定义了项目 REST API 接口的测试规范，确保接口质量。

---

## 1. 测试分层

### 1.1 测试金字塔

```
         ▲
        /│\        端到端测试 (E2E)
       / │ \       少量，重点场景
      /  │  \
     /───┼───\     集成测试 (Integration)
    /    │    \    中等，核心业务流程
   /_____|_____\   单元测试 (Unit)
  数量最多，最细粒度
```

### 1.2 各层测试职责

| 层级 | 测试范围 | 测试对象 |
|------|----------|----------|
| 单元测试 | 单个类/方法 | Service、Domain Service |
| 集成测试 | 多个类协作 | Repository、Controller |
| E2E测试 | 完整业务流程 | API 接口 |

---

## 2. 单元测试规范

### 2.1 测试结构

使用 AAA 模式（Arrange-Act-Assert）：

```java
@SpringBootTest
class UserDomainServiceTest {

    @Test
    void should_calculate_valuation_when_success_transactions() {
        // Arrange: 准备测试数据
        ValuationRecord record = ValuationRecord.builder()
                .targetAmount(new BigDecimal("10000"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();
        
        // Act: 执行测试
        BigDecimal result = service.calculate(record);
        
        // Assert: 验证结果
        assertThat(result).isEqualByComparingTo(new BigDecimal("10000"));
    }
}
```

### 2.2 命名规范

```
类名: {被测试类名}Test
方法名: should_{场景}_{预期行为}
```

**示例**：
- `ValuationCalculationServiceTest`
- `should_calculate_valuation_with_no_transactions_returns_base_amount`

### 2.3 测试覆盖率

| 类型 | 最低覆盖率 |
|------|-----------|
| 核心业务 Service | 80% |
| 领域服务 | 90% |
| 工具类 | 100% |

---

## 3. 集成测试规范

### 3.1 Controller 测试

使用 `@WebMvcTest` 或 `@SpringBootTest`：

```java
@SpringBootTest
@AutoConfigureMockMvc
class ValuationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_200_when_create_valuation_success() throws Exception {
        // 请求体
        String requestBody = """
            {
                "projectId": 1,
                "projectCode": "P001",
                "targetAmount": 10000.00,
                "valuationDate": "2024-01-01"
            }
            """;
        
        mockMvc.perform(post("/api/valuation/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
```

### 3.2 事务测试

确保事务正确回滚：

```java
@Test
void should_rollback_when_exception_occurs() {
    assertThatThrownBy(() -> service.createInvalidRecord())
            .isInstanceOf(Exception.class);
    
    // 验证数据未保存
    assertThat(repository.count()).isEqualTo(0);
}
```

---

## 4. API 测试场景

### 4.1 成功场景

```java
@Test
void should_return_success_when_create_valuation() {
    // 正常参数
    CreateValuationCommand command = new CreateValuationCommand();
    command.setProjectId(1L);
    command.setProjectCode("P001");
    command.setTargetAmount(new BigDecimal("10000"));
    command.setValuationDate(LocalDate.now());
    
    Result<Void> result = api.create(command);
    
    assertThat(result.getCode()).isEqualTo(0);
}
```

### 4.2 参数校验失败

```java
@Test
void should_return_error_when_project_id_is_null() {
    CreateValuationCommand command = new CreateValuationCommand();
    // projectId 为空
    
    Result<Void> result = api.create(command);
    
    assertThat(result.getCode()).isNotEqualTo(0);
    assertThat(result.getMessage()).contains("project.id.not.null");
}
```

### 4.3 业务异常

```java
@Test
void should_return_error_when_project_not_found() {
    when(repository.byId(999L)).thenReturn(null);
    
    Result<ValuationDTO> result = api.getById(999L);
    
    assertThat(result.getCode()).isNotEqualTo(0);
}
```

### 4.4 限流测试

```java
@Test
void should_return_error_when_exceed_rate_limit() {
    // 连续请求超过限流阈值
    for (int i = 0; i < 61; i++) {
        Result<Void> result = api.create(command);
        if (i >= 60) {
            assertThat(result.getCode()).isNotEqualTo(0);
        }
    }
}
```

### 4.5 计算逻辑测试

```java
@Test
void should_calculate_correct_amount_with_all_components() {
    // 给定：估值记录 10000，估值日期 2024-01-01
    ValuationRecord record = createValuationRecord("10000", "2024-01-01");
    
    // 给定：成功交易 2000（2024-01-15）
    addTransaction("2000", "2024-01-15", TransactionStatusEnum.SUCCESS);
    
    // 给定：成功现金交付 3000（2024-02-01）
    addCashDelivery("3000", "2024-02-01", TransactionStatusEnum.SUCCESS);
    
    // 当：计算估值
    BigDecimal result = service.calculate(record);
    
    // 那么：结果 = 10000 + 2000 + 3000 = 15000
    assertThat(result).isEqualTo(new BigDecimal("15000"));
}
```

---

## 5. 测试数据管理

### 5.1 测试数据准备

```java
@Test
void test_case() {
    // 使用测试数据 builder
    ValuationRecord testRecord = TestDataBuilder.valuation()
            .projectId(1L)
            .targetAmount(new BigDecimal("10000"))
            .build();
    
    repository.save(testRecord);
    
    // 测试执行...
}
```

### 5.2 测试数据清理

```java
@AfterEach
void tearDown() {
    repository.deleteAll();
}
```

---

## 6. Mock 使用规范

### 6.1 外部依赖 Mock

```java
@MockBean
private UnitAdapter unitAdapter;

@BeforeEach
void setUp() {
    when(unitAdapter.byUnitId(anyLong()))
            .thenReturn(new UnitDTO(1L, "测试单位"));
}
```

---

## 7. 测试代码组织

### 7.1 目录结构

```
src/test/java/
└── net/zhaixing/blog/
    └── valuation/
        ├── domain/
        │   └── service/
        │       └── ValuationCalculationServiceTest.java
        ├── application/
        │   └── command/
        │       └── ValuationAppServiceTest.java
        └── interaction/
            └── api/
                └── ValuationApiTest.java
```

### 7.2 测试类命名

| 测试类型 | 命名规则 |
|----------|----------|
| 单元测试 | {类名}Test |
| 集成测试 | {类名}IntegrationTest |
| E2E测试 | {类名}E2ETest |

---

## 8. 持续集成

### 8.1 Maven 配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

### 8.2 CI 检查项

- [ ] 单元测试通过
- [ ] 覆盖率达标
- [ ] 无新增警告
- [ ] 代码格式检查通过

---

## 9. 常见测试反模式

| 反模式 | 说明 | 正确做法 |
|--------|------|----------|
| 测试包含多个断言 | 难以定位问题 | 每个测试一个场景 |
| Mock 过多 | 测试与实现耦合 | 优先使用真实对象 |
| 测试无清理 | 数据污染 | 使用 @AfterEach 清理 |
| 忽略异常测试 | 隐藏 bug | 必须测试异常场景 |

---

## 10. 测试用例模板

```java
package net.zhaixing.blog.valuation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

/**
 * {模块名}测试
 *
 * @author JanYork
 */
@SpringBootTest
class {类名}Test {

    @Autowired
    private {被测试类} {被测试对象};

    /**
     * 场景描述
     */
    @Test
    void should_{场景}_{预期结果}() {
        // Arrange
        
        // Act
        {执行方法};
        
        // Assert
        assertThat({结果}).{匹配器};
    }
}
```
