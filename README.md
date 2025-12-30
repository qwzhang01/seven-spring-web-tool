# Seven Spring Web Tool

[![Maven Central](https://img.shields.io/badge/maven--central-v1.0.2-blue)](https://central.sonatype.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io/projects/spring-boot)

**Seven Spring Web Tool** is a comprehensive Spring Boot 3 integration library that brings together multiple powerful **seven-*** components into a unified toolkit for enterprise web application development.

## 🎯 What Makes This Library Special

This library serves as a **unified integration hub** that seamlessly combines three powerful security and functionality frameworks:

### 🔐 Integrated Seven-* Components

| Component | Version | Purpose |
|-----------|---------|---------|
| **[seven-shield](https://github.com/qwzhang01/seven-shield)** | 1.0.1 | Web application security and protection layer |
| **[seven-operating-record](https://github.com/qwzhang01/seven-operating-record)** | 1.0.1 | Comprehensive operation logging and audit trail system |
| **[seven-data-security](https://github.com/qwzhang01/seven-data-security)** | 1.2.17 | Advanced data security and encryption utilities |

By adding this single dependency to your project, you automatically gain access to all three seven-* libraries plus a rich set of web utilities - providing enterprise-grade security, audit logging, data protection, and common web utilities in one package.

## ✨ Key Features

### 🛡️ **Integrated Security & Audit Framework**
- **seven-shield**: Protection against common web vulnerabilities
- **seven-operating-record**: Automatic operation tracking and audit logging
- **seven-data-security**: Data encryption and secure object manipulation

### 🔧 **Production-Ready Web Utilities**
- **Bean Operations**: Advanced object property copying with callback support
- **SSE Support**: Real-time Server-Sent Events with connection management
- **Request Handling**: HTTP request parsing with proxy-aware IP detection
- **String Utilities**: Common string operations and UUID generation
- **File Handling**: MultipartFile DTO for testing and programmatic file operations

### ⚡ **Developer-Friendly Design**
- Minimal configuration required
- Functional programming support with callback interfaces
- Thread-safe implementations
- Compatible with MyBatis-Plus
- Comprehensive Javadoc documentation

## 📋 Requirements

- **Java**: 17 or higher
- **Spring Boot**: 3.1.5 or higher
- **Maven**: 3.6 or higher

## 🚀 Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.qwzhang01</groupId>
    <artifactId>seven-spring-web-tool</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Gradle Dependency

```gradle
implementation 'io.github.qwzhang01:seven-spring-web-tool:1.0.2'
```

> **Note**: By adding this dependency, you automatically get access to **seven-shield**, **seven-operating-record**, and **seven-data-security** libraries without needing to add them separately.

## 📚 Core Utilities Documentation

### 1. Bean Utility (BeanUtil)

Comprehensive JavaBean operations with seven-data-security integration for advanced object manipulation.

#### Basic Property Copying

```java
// Simple object copying
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

// Copying with custom transformation callback
UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class, (source, target) -> {
    target.setFullName(source.getFirstName() + " " + source.getLastName());
    target.setAge(calculateAge(source.getBirthday()));
});
```

#### List Transformations

```java
// Convert list of entities to DTOs
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class);

// Convert with custom transformation
List<UserDTO> userDTOs = BeanUtil.copyToList(users, UserDTO.class, (source, target) -> {
    target.setFullName(source.getFirstName() + " " + source.getLastName());
});
```

#### MyBatis-Plus Page Conversions

```java
// Convert pagination results
Page<User> userPage = userService.selectPage(page, wrapper);
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class);

// Convert with callback
Page<UserDTO> dtoPage = BeanUtil.copyToPage(userPage, UserDTO.class, (source, target) -> {
    target.setExtra("computed value");
});
```

#### Object to Map Conversion (seven-data-security integration)

```java
// Convert any object to Map using advanced reflection
Map<String, Object> map = BeanUtil.objectToMap(user);
```

### 2. SSE Utility (SseEmitterUtil)

Production-ready Server-Sent Events implementation with automatic connection lifecycle management.

#### Create SSE Connection

```java
@GetMapping("/sse/connect")
public SseEmitter connect(@RequestParam String clientId) {
    return SseEmitterUtil.createEmitter(clientId, "Connection established");
}
```

#### Send Message to Specific Client

```java
boolean success = SseEmitterUtil.sendToClient("client123", "Hello, Client!");
```

#### Broadcast to All Clients

```java
SseEmitterUtil.broadcast("System notification: Server maintenance in 10 minutes");
```

#### Close Connection

```java
SseEmitterUtil.close("client123");
```

#### Frontend Example

```javascript
const eventSource = new EventSource('/sse/connect?clientId=user123');

eventSource.addEventListener('message', (event) => {
    const data = JSON.parse(event.data);
    console.log('Received message:', data);
});

eventSource.onerror = (error) => {
    console.error('SSE connection error:', error);
    eventSource.close();
};
```

### 3. Request Utility (RequestUtil)

HTTP request handling with support for proxy detection and header parsing.

#### Get Client IP Address (Proxy-Aware)

```java
// Automatically detects real client IP through proxy headers
String clientIp = RequestUtil.getIpAddress(request);
```

#### Get Complete Request URI

```java
String fullUri = RequestUtil.getRequestUri(request);
// Returns: /api/users?page=1&size=10
```

#### Parse Request Headers

```java
// Get specific header
String authToken = RequestUtil.getHeader(request, "Authorization");

// Get all headers (excluding cookies)
String allHeaders = RequestUtil.getHeader(request);
```

#### Write JSON Response

```java
RequestUtil.print(response, "{\"status\":\"success\",\"data\":\"result\"}");
```

#### Get Local Machine IP

```java
String localIp = RequestUtil.getLocalIp();
```

### 4. String Utility (StrUtil)

Common string operations with null-safe implementations.

```java
// Check if string is blank (null, empty, or whitespace)
boolean isBlank = StrUtil.isBlank("  ");  // true

// Get string length (null-safe)
int length = StrUtil.length("Hello");  // 5
int nullLength = StrUtil.length(null);  // 0

// Generate UUID (lowercase, without hyphens)
String uuid = StrUtil.uuidStr();  // "a1b2c3d4e5f6789..."

// Decode Base64 string (supports data URIs)
byte[] bytes = StrUtil.decodeBase64("SGVsbG8gV29ybGQ=");
// Also handles data URIs like "data:image/png;base64,iVBORw0KG..."
byte[] imageBytes = StrUtil.decodeBase64("data:image/png;base64,iVBORw0KG...");
```

### 5. MultipartFile DTO

In-memory MultipartFile implementation for testing and programmatic file handling.

```java
// Create from byte array
byte[] content = fileContent.getBytes();
MultipartFile file = new MultipartFileDto(
    "file",
    "document.pdf",
    "application/pdf",
    content
);

// Create from InputStream
InputStream inputStream = new FileInputStream("file.txt");
MultipartFile file = new MultipartFileDto(
    "file",
    "file.txt",
    "text/plain",
    inputStream
);

// Use in file upload scenarios
fileService.uploadFile(file);
```

### 6. Functional Interfaces

#### CallCopy - Object Transformation Callback

```java
@FunctionalInterface
public interface CallCopy<S, T> {
    void call(S source, T target);
}

// Usage example
UserDTO dto = BeanUtil.copyProperties(user, UserDTO.class, 
    (source, target) -> {
        // Custom transformation logic after copying
        target.setAge(calculateAge(source.getBirthday()));
        target.setDisplayName(source.getFirstName() + " " + source.getLastName());
    }
);
```

#### CallFunction - Business Logic Callback

```java
@FunctionalInterface
public interface CallFunction {
    boolean call(String param);
}

// Usage example
CallFunction validator = (param) -> {
    return param != null && param.matches("^[a-zA-Z0-9]+$");
};

boolean isValid = validator.call("user123");  // true
```

## 🔗 Seven-* Component Integration

### How the Integration Works

This library integrates the three seven-* components seamlessly:

1. **Direct Integration**: `BeanUtil.objectToMap()` directly uses `seven-data-security`'s reflection utilities
2. **Transitive Dependencies**: Applications using this library automatically inherit `seven-shield` and `seven-operating-record`
3. **Unified Configuration**: Configure all security and audit features through the seven-* components' standard configuration

### seven-shield Integration

`seven-shield` provides web application security features. Configure it in your Spring Boot application:

```yaml
# Example configuration (refer to seven-shield documentation)
seven:
  shield:
    enabled: true
    csrf-protection: true
    xss-protection: true
```

### seven-operating-record Integration

`seven-operating-record` provides automatic operation logging. Use annotations in your service layer:

```java
@Service
public class UserService {
    
    @OperationLog(module = "User Management", operation = "Create User")
    public User createUser(UserDTO dto) {
        // seven-operating-record automatically records this operation
        return userRepository.save(BeanUtil.copyProperties(dto, User.class));
    }
}
```

### seven-data-security Integration

`seven-data-security` is directly integrated in `BeanUtil`:

```java
// Automatically uses seven-data-security's advanced reflection
Map<String, Object> userMap = BeanUtil.objectToMap(user);
```

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│     Your Spring Boot Application            │
│  - Controllers, Services, Repositories       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│     seven-spring-web-tool                    │
│  ┌─────────────────────────────────────┐    │
│  │  Utility Layer                      │    │
│  │  - BeanUtil, RequestUtil            │    │
│  │  - SseEmitterUtil, StrUtil          │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│   Integrated Seven-* Components              │
│  ┌─────────────────────────────────────┐    │
│  │  seven-shield (v1.0.1)              │    │
│  │  → Security & Protection            │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  seven-operating-record (v1.0.1)    │    │
│  │  → Audit Logging                    │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  seven-data-security (v1.2.17)      │    │
│  │  → Data Encryption & Security       │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│   Spring Boot 3 Framework                   │
│   - Spring Web, Spring Core                 │
│   - MyBatis-Plus (optional)                 │
└─────────────────────────────────────────────┘
```

## 💡 Use Cases

### Enterprise Application Development
- Single dependency provides security, audit logging, and web utilities
- Rapid development with pre-built, production-tested components
- Consistent patterns across microservices

### Real-Time Applications
- Built-in SSE support for server push notifications
- Real-time data streaming to web clients
- Live updates and monitoring dashboards

### Multi-Layer Architecture
- Seamless bean transformations between layers (Entity ↔ DTO ↔ VO)
- Automatic audit logging with seven-operating-record
- Secure data handling with seven-data-security

### API Gateway & Proxy Scenarios
- Advanced IP detection through multiple proxy layers
- Request header parsing and manipulation
- Security filtering with seven-shield

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**avinzhang**

- GitHub: [@qwzhang01](https://github.com/qwzhang01)
- Email: avinzhang@tencent.com

## 🙏 Acknowledgments

Special thanks to all contributors and the Spring Boot community!

## 📦 Related Projects

This library integrates and depends on the following projects:

- **[seven-shield](https://github.com/qwzhang01/seven-shield)** - Web security and protection framework
- **[seven-operating-record](https://github.com/qwzhang01/seven-operating-record)** - Operation logging and audit trail system
- **[seven-data-security](https://github.com/qwzhang01/seven-data-security)** - Data security and encryption utilities

## 📝 Changelog

### v1.0.2 (2025-12-30)

- ✨ Enhanced Javadoc comments for all classes and methods
- 📚 Improved documentation with more detailed examples
- 🐛 Fixed Base64DecodeException class comment error
- ✨ Added Base64 decoding utility with data URI support
- 🔧 Better code documentation for enterprise use

### v1.0.1 (2025-12-30)

- 🎉 Updated to emphasize seven-* components integration
- ✨ Comprehensive English documentation
- ✨ Enhanced Javadoc comments across all classes
- 🔧 Production-ready utilities for enterprise applications

### v1.0.0 (2025-12-26)

- 🎉 Initial release
- ✨ Bean utility with MyBatis-Plus support
- ✨ SSE utility for real-time communications
- ✨ Request utility with proxy-aware IP detection
- ✨ String utility and MultipartFile DTO
- ✨ Functional interfaces for callbacks

## 🔮 Roadmap

- [ ] Enhanced integration examples for seven-* components
- [ ] Performance benchmarks and optimization
- [ ] Additional utility classes based on community feedback
- [ ] Support for Spring Boot 3.2+
- [ ] Comprehensive integration testing suite

---

⭐ If this project helps you, please give it a star! Your support means a lot!

**One Dependency. Complete Enterprise Solution.**
