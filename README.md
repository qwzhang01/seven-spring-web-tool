# Seven Spring Web Tool

[![Maven Central](https://img.shields.io/badge/maven--central-v1.0.0-blue)](https://central.sonatype.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io/projects/spring-boot)

基于 Spring Boot 3 的 Web 工具箱，提供常用的工具类和功能组件，简化 Spring Boot Web 应用开发。

## ✨ 特性

- 🔧 **Bean 工具** - 对象属性复制、列表转换、分页数据处理
- 📡 **SSE 支持** - Server-Sent Events 实时消息推送工具
- 📁 **文件处理** - MultipartFile 数据传输对象
- 🔤 **字符串工具** - 字符串验证、UUID 生成等常用操作
- 🎯 **函数式接口** - 回调机制支持自定义业务逻辑
- ⚡ **轻量级** - 最小依赖，易于集成

## 📋 环境要求

- Java 17+
- Spring Boot 3.1.5+
- Maven 3.6+

## 🚀 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>io.github.qwzhang01</groupId>
    <artifactId>seven-spring-web-tool</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle 依赖

```gradle
implementation 'io.github.qwzhang01:seven-spring-web-tool:1.0.0'
```

## 📚 核心功能

### 1. Bean 工具类 (BeanUtil)

提供对象属性复制、列表转换、分页数据处理等功能。

#### 基本属性复制

```java
// 单对象复制
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

// 带回调的属性复制
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class, (source, target) -> {
    // 自定义属性转换逻辑
    target.setFullName(source.getFirstName() + " " + source.getLastName());
});
```

#### 列表转换

```java
// 列表复制
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class);

// 带回调的列表复制
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class, (source, target) -> {
    target.setFullName(source.getFirstName() + " " + source.getLastName());
});
```

#### 分页数据转换

```java
// MyBatis-Plus 分页对象转换
Page<User> userPage = userService.selectPage(page, wrapper);
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class);

// 带回调的分页转换
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class, (source, target) -> {
    target.setExtra("some value");
});
```

#### 对象转 Map

```java
Map<String, Object> map = BeanUtil.objectToMap(user);
```

### 2. SSE 工具类 (SseEmitterUtil)

提供 Server-Sent Events 实时消息推送功能。

#### 创建 SSE 连接

```java
@GetMapping("/sse/connect")
public SseEmitter connect(@RequestParam String clientId) {
    return SseEmitterUtil.createEmitter(clientId, "连接成功");
}
```

#### 发送消息给指定客户端

```java
boolean success = SseEmitterUtil.sendToClient("client123", "Hello, Client!");
```

#### 广播消息给所有客户端

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

### 3. 字符串工具类 (StrUtil)

提供常用的字符串操作方法。

```java
// 检查字符串是否为空白
boolean blank = StrUtil.isBlank("  ");  // true

// 获取字符串长度
int length = StrUtil.length("Hello");  // 5

// 生成 UUID（无连字符，小写）
String uuid = StrUtil.uuidStr();  // "a1b2c3d4e5f6..."
```

### 4. MultipartFile DTO

提供 MultipartFile 的数据传输对象实现。

```java
// 从字节数组创建
byte[] content = fileContent.getBytes();
MultipartFile file = new MultipartFileDto(
    "file",
    "document.pdf",
    "application/pdf",
    content
);

// 从输入流创建
InputStream inputStream = new FileInputStream("file.txt");
MultipartFile file = new MultipartFileDto(
    "file",
    "file.txt",
    "text/plain",
    inputStream
);
```

### 5. 函数式接口

#### CallCopy - 对象复制回调

```java
@FunctionalInterface
public interface CallCopy<S, T> {
    void call(S source, T target);
}

// 使用示例
UserDTO dto = BeanUtil.copyProperties(user, UserDTO.class, 
    (source, target) -> {
        // 自定义转换逻辑
        target.setAge(calculateAge(source.getBirthday()));
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
    return param != null && param.length() > 0;
};
```

## 🔗 相关项目

此工具库依赖并集成了以下项目：

- [seven-shield](https://github.com/qwzhang01/seven-shield) - 防护组件
- [seven-operating-record](https://github.com/qwzhang01/seven-operating-record) - 操作记录组件
- [seven-data-security](https://github.com/qwzhang01/seven-data-security) - 数据安全组件

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

**avinzhang**

- GitHub: [@qwzhang01](https://github.com/qwzhang01)
- Email: avinzhang@tencent.com

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！

## 📝 更新日志

### v1.0.0 (2025-12-26)

- 🎉 初始版本发布
- ✨ 支持 Bean 工具类
- ✨ 支持 SSE 工具类
- ✨ 支持字符串工具类
- ✨ 支持 MultipartFile DTO
- ✨ 支持函数式接口

## 🔮 路线图

- [ ] 添加更多工具类
- [ ] 完善单元测试
- [ ] 添加性能优化
- [ ] 支持更多 Spring Boot 版本

---

如果这个项目对你有帮助，请给个 ⭐️ 支持一下！
