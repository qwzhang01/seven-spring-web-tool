# Seven Spring Web Tool

[![Maven Central](https://img.shields.io/badge/maven--central-v1.0.2-blue)](https://central.sonatype.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io/projects/spring-boot)

**Seven Spring Web Tool** 是一个全面的 Spring Boot 3 集成库，将多个强大的 **seven-*** 组件整合到一个统一的企业级 Web 应用开发工具包中。

[English](README.md) | 简体中文

## 🎯 项目特色

本库作为**统一集成中心**，无缝集成三个强大的安全和功能框架：

### 🔐 集成的 Seven-* 组件

| 组件 | 版本 | 用途 |
|-----------|---------|---------|
| **[seven-shield](https://github.com/qwzhang01/seven-shield)** | 1.0.1 | Web 应用安全防护层 |
| **[seven-operating-record](https://github.com/qwzhang01/seven-operating-record)** | 1.0.1 | 全面的操作日志和审计追踪系统 |
| **[seven-data-security](https://github.com/qwzhang01/seven-data-security)** | 1.2.17 | 高级数据安全和加密工具 |

只需在项目中添加这一个依赖，即可自动获得全部三个 seven-* 库以及丰富的 Web 工具集 - 在一个包中提供企业级安全、审计日志、数据保护和常用 Web 工具。

## ✨ 核心特性

### 🛡️ **集成安全与审计框架**
- **seven-shield**：防护常见 Web 漏洞
- **seven-operating-record**：自动操作追踪和审计日志
- **seven-data-security**：数据加密和安全对象操作

### 🔧 **生产级 Web 工具**
- **Bean 操作**：支持回调的高级对象属性复制
- **SSE 支持**：带连接管理的实时服务器推送事件
- **请求处理**：支持代理感知的 HTTP 请求解析和 IP 检测
- **字符串工具**：常用字符串操作和 UUID 生成
- **文件处理**：用于测试和编程式文件操作的 MultipartFile DTO

### ⚡ **开发者友好设计**
- 最少配置要求
- 支持函数式编程的回调接口
- 线程安全实现
- 兼容 MyBatis-Plus
- 完整的 Javadoc 文档

## 📋 环境要求

- **Java**：17 或更高版本
- **Spring Boot**：3.1.5 或更高版本
- **Maven**：3.6 或更高版本

## 🚀 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>io.github.qwzhang01</groupId>
    <artifactId>seven-spring-web-tool</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Gradle 依赖

```gradle
implementation 'io.github.qwzhang01:seven-spring-web-tool:1.0.2'
```

> **注意**：添加此依赖后，您将自动获得 **seven-shield**、**seven-operating-record** 和 **seven-data-security** 库的访问权限，无需单独添加。

## 📚 核心工具使用文档

### 1. Bean 工具类 (BeanUtil)

综合的 JavaBean 操作工具，集成 seven-data-security 实现高级对象操作。

#### 基本属性复制

```java
// 简单对象复制
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

// 带自定义转换回调的复制
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class, (source, target) -> {
    target.setFullName(source.getFirstName() + " " + source.getLastName());
    target.setAge(calculateAge(source.getBirthday()));
});
```

#### 列表转换

```java
// 将实体列表转换为 DTO 列表
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class);

// 带自定义转换的列表复制
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class, (source, target) -> {
    target.setFullName(source.getFirstName() + " " + source.getLastName());
});
```

#### MyBatis-Plus 分页转换

```java
// 转换分页结果
Page<User> userPage = userService.selectPage(page, wrapper);
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class);

// 带回调的分页转换
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class, (source, target) -> {
    target.setExtra("计算值");
});
```

#### 对象转 Map（集成 seven-data-security）

```java
// 使用高级反射将任意对象转换为 Map
Map<String, Object> map = BeanUtil.objectToMap(user);
```

### 2. SSE 工具类 (SseEmitterUtil)

生产级服务器推送事件实现，具有自动连接生命周期管理。

#### 创建 SSE 连接

```java
@GetMapping("/sse/connect")
public SseEmitter connect(@RequestParam String clientId) {
    return SseEmitterUtil.createEmitter(clientId, "连接已建立");
}
```

#### 发送消息给指定客户端

```java
boolean success = SseEmitterUtil.sendToClient("client123", "你好，客户端！");
```

#### 广播给所有客户端

```java
SseEmitterUtil.broadcast("系统通知：服务器将在 10 分钟后维护");
```

#### 关闭连接

```java
SseEmitterUtil.close("client123");
```

#### 前端示例

```javascript
const eventSource = new EventSource('/sse/connect?clientId=user123');

eventSource.addEventListener('message', (event) => {
    const data = JSON.parse(event.data);
    console.log('收到消息:', data);
});

eventSource.onerror = (error) => {
    console.error('SSE 连接错误:', error);
    eventSource.close();
};
```

### 3. 请求工具类 (RequestUtil)

HTTP 请求处理，支持代理检测和请求头解析。

#### 获取客户端 IP 地址（代理感知）

```java
// 自动通过代理请求头检测真实客户端 IP
String clientIp = RequestUtil.getIpAddress(request);
```

#### 获取完整请求 URI

```java
String fullUri = RequestUtil.getRequestUri(request);
// 返回：/api/users?page=1&size=10
```

#### 解析请求头

```java
// 获取指定请求头
String authToken = RequestUtil.getHeader(request, "Authorization");

// 获取所有请求头（排除 cookies）
String allHeaders = RequestUtil.getHeader(request);
```

#### 输出 JSON 响应

```java
RequestUtil.print(response, "{\"status\":\"success\",\"data\":\"结果\"}");
```

#### 获取本机 IP

```java
String localIp = RequestUtil.getLocalIp();
```

### 4. 字符串工具类 (StrUtil)

常用字符串操作，具有空值安全实现。

```java
// 检查字符串是否为空白（null、空字符串或仅包含空格）
boolean isBlank = StrUtil.isBlank("  ");  // true

// 获取字符串长度（空值安全）
int length = StrUtil.length("Hello");  // 5
int nullLength = StrUtil.length(null);  // 0

// 生成 UUID（小写，无连字符）
String uuid = StrUtil.uuidStr();  // "a1b2c3d4e5f6789..."

// Base64 字符串解码（支持数据 URI）
byte[] bytes = StrUtil.decodeBase64("SGVsbG8gV29ybGQ=");
// 也可以处理数据 URI，如 "data:image/png;base64,iVBORw0KG..."
byte[] imageBytes = StrUtil.decodeBase64("data:image/png;base64,iVBORw0KG...");
```

### 5. MultipartFile DTO

内存中的 MultipartFile 实现，用于测试和编程式文件处理。

```java
// 从字节数组创建
byte[] content = fileContent.getBytes();
MultipartFile file = new MultipartFileDto(
    "file",
    "document.pdf",
    "application/pdf",
    content
);

// 从 InputStream 创建
InputStream inputStream = new FileInputStream("file.txt");
MultipartFile file = new MultipartFileDto(
    "file",
    "file.txt",
    "text/plain",
    inputStream
);

// 在文件上传场景中使用
fileService.uploadFile(file);
```

### 6. 函数式接口

#### CallCopy - 对象转换回调

```java
@FunctionalInterface
public interface CallCopy<S, T> {
    void call(S source, T target);
}

// 使用示例
UserDTO dto = BeanUtil.copyProperties(user, UserDTO.class, 
    (source, target) -> {
        // 复制后的自定义转换逻辑
        target.setAge(calculateAge(source.getBirthday()));
        target.setDisplayName(source.getFirstName() + " " + source.getLastName());
    }
);
```

#### CallFunction - 业务逻辑回调

```java
@FunctionalInterface
public interface CallFunction {
    boolean call(String param);
}

// 使用示例
CallFunction validator = (param) -> {
    return param != null && param.matches("^[a-zA-Z0-9]+$");
};

boolean isValid = validator.call("user123");  // true
```

## 🔗 Seven-* 组件集成

### 集成工作原理

本库无缝集成三个 seven-* 组件：

1. **直接集成**：`BeanUtil.objectToMap()` 直接使用 `seven-data-security` 的反射工具
2. **传递依赖**：使用本库的应用程序自动继承 `seven-shield` 和 `seven-operating-record`
3. **统一配置**：通过 seven-* 组件的标准配置来配置所有安全和审计功能

### seven-shield 集成

`seven-shield` 提供 Web 应用安全功能。在 Spring Boot 应用中配置：

```yaml
# 配置示例（请参考 seven-shield 文档）
seven:
  shield:
    enabled: true
    csrf-protection: true
    xss-protection: true
```

### seven-operating-record 集成

`seven-operating-record` 提供自动操作日志记录。在服务层使用注解：

```java
@Service
public class UserService {
    
    @OperationLog(module = "用户管理", operation = "创建用户")
    public User createUser(UserDTO dto) {
        // seven-operating-record 自动记录此操作
        return userRepository.save(BeanUtil.copyProperties(dto, User.class));
    }
}
```

### seven-data-security 集成

`seven-data-security` 直接集成在 `BeanUtil` 中：

```java
// 自动使用 seven-data-security 的高级反射功能
Map<String, Object> userMap = BeanUtil.objectToMap(user);
```

## 🏗️ 架构设计

```
┌─────────────────────────────────────────────┐
│     您的 Spring Boot 应用程序                 │
│  - Controllers, Services, Repositories       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│     seven-spring-web-tool                    │
│  ┌─────────────────────────────────────┐    │
│  │  工具层                              │    │
│  │  - BeanUtil, RequestUtil            │    │
│  │  - SseEmitterUtil, StrUtil          │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│   集成的 Seven-* 组件                         │
│  ┌─────────────────────────────────────┐    │
│  │  seven-shield (v1.0.1)              │    │
│  │  → 安全防护                          │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  seven-operating-record (v1.0.1)    │    │
│  │  → 审计日志                          │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  seven-data-security (v1.2.17)      │    │
│  │  → 数据加密与安全                     │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│   Spring Boot 3 框架                         │
│   - Spring Web, Spring Core                 │
│   - MyBatis-Plus（可选）                     │
└─────────────────────────────────────────────┘
```

## 💡 使用场景

### 企业应用开发
- 单一依赖提供安全、审计日志和 Web 工具
- 使用预构建、生产测试的组件快速开发
- 微服务间的一致模式

### 实时应用
- 内置 SSE 支持服务器推送通知
- 实时数据流向 Web 客户端
- 实时更新和监控仪表板

### 多层架构
- 层间无缝 Bean 转换（Entity ↔ DTO ↔ VO）
- 使用 seven-operating-record 自动审计日志
- 使用 seven-data-security 安全数据处理

### API 网关和代理场景
- 通过多层代理的高级 IP 检测
- 请求头解析和操作
- 使用 seven-shield 进行安全过滤

## 🤝 贡献

欢迎贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m '添加某个很棒的功能'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 许可 - 详见 [LICENSE](LICENSE) 文件。

## 👨‍💻 作者

**avinzhang**

- GitHub: [@qwzhang01](https://github.com/qwzhang01)
- Email: avinzhang@tencent.com

## 🙏 致谢

特别感谢所有贡献者和 Spring Boot 社区！

## 📦 相关项目

本库集成并依赖以下项目：

- **[seven-shield](https://github.com/qwzhang01/seven-shield)** - Web 安全防护框架
- **[seven-operating-record](https://github.com/qwzhang01/seven-operating-record)** - 操作日志和审计追踪系统
- **[seven-data-security](https://github.com/qwzhang01/seven-data-security)** - 数据安全和加密工具

## 📝 更新日志

### v1.0.2 (2025-12-30)

- ✨ 增强所有类和方法的 Javadoc 注释
- 📚 改进文档，增加更详细的示例
- 🐛 修复 Base64DecodeException 类注释错误
- ✨ 添加支持数据 URI 的 Base64 解码工具
- 🔧 更好的代码文档，适合企业使用

### v1.0.1 (2025-12-30)

- 🎉 更新以强调 seven-* 组件集成
- ✨ 全面的英文和中文文档
- ✨ 所有类的增强 Javadoc 注释
- 🔧 企业应用的生产级工具

### v1.0.0 (2025-12-26)

- 🎉 初始版本发布
- ✨ 支持 MyBatis-Plus 的 Bean 工具
- ✨ 用于实时通信的 SSE 工具
- ✨ 带代理感知 IP 检测的请求工具
- ✨ 字符串工具和 MultipartFile DTO
- ✨ 用于回调的函数式接口

## 🔮 路线图

- [ ] seven-* 组件的增强集成示例
- [ ] 性能基准测试和优化
- [ ] 基于社区反馈的额外工具类
- [ ] 支持 Spring Boot 3.2+
- [ ] 全面的集成测试套件

---

⭐ 如果这个项目对你有帮助，请给个星标！您的支持意义重大！

**一个依赖，完整的企业解决方案。**
