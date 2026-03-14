# 编译构建规范

本文档定义项目的编译构建规范。

---

## 1. 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8.0_461+ | Java 开发工具包 |
| Maven | 3.6.0+ | 项目构建工具 |
| MySQL | 5.7+ | 数据库 |

### JDK 安装配置

1. 下载 JDK 8：https://download.oracle.com/java/8u471/jdk-8u471-windows-x64.exe
2. 安装到：`C:\Program Files\Java\jdk1.8.0_471`
3. 配置环境变量：
   - 系统变量 `JAVA_HOME` = `C:\Program Files\Java\jdk1.8.0_471`
   - 系统变量 `Path` 添加 `%JAVA_HOME%\bin`

---

## 2. Maven 配置

### settings.xml 配置

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
</mirrors>

<profiles>
    <profile>
        <id>jdk-1.8</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <jdk>1.8</jdk>
        </activation>
        <properties>
            <maven.compiler.source>1.8</maven.compiler.source>
            <maven.compiler.target>1.8</maven.compiler.target>
            <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
        </properties>
    </profile>
</profiles>
```

---

## 3. 项目编译

### 3.1 编译命令

```bash
# 编译所有模块
mvn clean compile

# 跳过测试编译
mvn clean compile -DskipTests

# 安装到本地仓库
mvn clean install -DskipTests
```

### 3.2 模块编译顺序

项目采用多模块结构，编译顺序：

```
user-api           (基础API定义)
    ↓
user-common        (通用工具和注解)
    ↓
user-domain        (领域层)
    ↓
user-infrastructure (基础设施层)
    ↓
user-application   (应用层)
    ↓
user-interaction   (交互层)
    ↓
valuation-domain   (估值领域层)
    ↓
valuation-infrastructure (估值基础设施层)
    ↓
valuation-application (估值应用层)
    ↓
valuation-interaction (估值交互层)
    ↓
user-start         (启动模块)
```

---

## 4. 测试执行

### 4.1 运行测试

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=ValuationApiTest

# 生成测试报告
mvn test -DskipTests=false
```

### 4.2 测试覆盖率

| 模块 | 最低覆盖率 |
|------|-----------|
| 核心业务 Service | 80% |
| 领域服务 | 90% |
| 工具类 | 100% |

---

## 5. 打包部署

### 5.1 打包

```bash
# 打包为 JAR
mvn clean package -DskipTests

# 打包并安装到本地仓库
mvn clean install -DskipTests
```

### 5.2 启动

```bash
# 运行打包后的 JAR
java -jar user-start/target/user-start-1.0.0.jar

# 或使用 Maven 运行
mvn spring-boot:run -pl user-start
```

---

## 6. 常见问题

### 6.1 编译错误：No compiler is provided

**原因**：只安装了 JRE，没有安装 JDK

**解决**：安装 JDK 并配置 JAVA_HOME

### 6.2 依赖下载失败

**解决**：配置 Maven 阿里云镜像（见上文）

### 6.3 本地仓库依赖缺失

**解决**：执行 `mvn clean install -DskipTests` 安装所有模块

---

## 7. 验证命令

```bash
# 验证 Java 环境
java -version
javac -version

# 验证 Maven
mvn -version

# 验证项目结构
mvn help:effective-pom
```
