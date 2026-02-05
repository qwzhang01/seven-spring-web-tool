# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- 

### Changed
- 

### Fixed
- 

### Deprecated
- 

### Removed
- 

### Security
- 

## [1.0.11] - 2026-02-05

### Changed
- Updated dependency versions
- Improved build configuration

## [1.0.5] - 2026-01-12

### Added
- **SSE Multi-Instance Support**: Refactored `SseEmitterUtil` to support multi-instance deployments
- Added `SseConnectionManager` for managing SSE connections with broker abstraction
- Added `SseMessageBroker` interface with `LocalSseMessageBroker` and `RedisSseMessageBroker` implementations
- Added `SseAutoConfiguration` for automatic Spring Boot integration
- Redis Pub/Sub support for cross-instance message routing

### Changed
- Backward compatible API - existing code works without changes
- Enhanced SSE connection management architecture

## [1.0.2] - 2025-12-30

### Added
- Enhanced Javadoc comments for all classes and methods
- Base64 decoding utility with data URI support

### Fixed
- Fixed Base64DecodeException class comment error

### Changed
- Improved documentation with more detailed examples

## [1.0.1] - 2025-12-30

### Added
- Comprehensive English documentation
- Enhanced Javadoc comments across all classes

### Changed
- Updated to emphasize seven-* components integration
- Production-ready utilities for enterprise applications

## [1.0.0] - 2025-12-26

### Added
- **Initial Release**: First public release of Seven Spring Web Tool
- Bean utility with MyBatis-Plus support
- SSE utility for real-time communications
- Request utility with proxy-aware IP detection
- String utility and MultipartFile DTO
- Functional interfaces for callbacks

### Features
- Integrated seven-shield security framework
- Integrated seven-operating-record audit system
- Integrated seven-data-security encryption utilities
- Comprehensive test suite
- Production-ready utilities for enterprise applications

---

## Versioning Policy

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality in a backward-compatible manner
- **PATCH** version for backward-compatible bug fixes

## Migration Guide

### From 1.0.0 to 1.0.5

No breaking changes. The SSE API remains fully backward compatible. New Redis-based SSE functionality is optional and can be enabled by adding Redis dependency.

### From Pre-1.0.0 versions

This is the first stable release. All APIs are considered stable and backward compatibility will be maintained in future 1.x releases.

## Dependency Updates

Regular dependency updates are performed to ensure security and compatibility:

- Spring Boot: 3.1.5+
- MyBatis-Plus: 3.5.11+
- Java: 17+

## Security Updates

Security-related updates are prioritized and released as patch versions when available.