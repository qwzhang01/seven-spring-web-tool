# Security Policy

## Supported Versions

We take security seriously and provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please report it responsibly.

### How to Report

**DO NOT** create a public issue for security vulnerabilities. Instead, please:

1. **Email Security Team**: Send an email to [avinzhang@tencent.com](mailto:avinzhang@tencent.com) with:
   - A clear description of the vulnerability
   - Steps to reproduce the issue
   - Potential impact assessment
   - Any suggested fixes or mitigation strategies

2. **Encryption**: For sensitive reports, you may encrypt your message using our PGP key:
   ```
   Key ID: [PGP_KEY_ID]
   Fingerprint: [PGP_FINGERPRINT]
   ```

### What to Include in Your Report

- Type of vulnerability (e.g., XSS, CSRF, injection, etc.)
- Affected component(s)
- Version number(s)
- Detailed reproduction steps
- Potential impact
- Any relevant logs or screenshots

## Response Timeline

We aim to respond to security reports within:

- **Initial Response**: 1 business day
- **Triage**: 3 business days
- **Fix Development**: Depending on severity, typically 1-4 weeks
- **Public Disclosure**: After fixes are released and users have had time to update

## Security Updates

### Patch Releases

Security fixes are typically released as patch versions (e.g., 1.0.1 → 1.0.2). We recommend:

- Always using the latest patch version
- Regularly updating dependencies
- Monitoring security advisories

### Dependency Security

This project depends on several libraries. We:

- Regularly update dependencies to address security issues
- Use Dependabot or similar tools to monitor vulnerabilities
- Conduct security reviews of major dependency updates

## Security Best Practices

### For Users

1. **Keep Updated**: Always use the latest version
2. **Dependency Scanning**: Regularly scan your dependencies for vulnerabilities
3. **Configuration**: Follow security best practices in your Spring Boot configuration
4. **Monitoring**: Implement proper logging and monitoring

### For Developers

1. **Input Validation**: Always validate and sanitize user input
2. **Authentication**: Implement proper authentication and authorization
3. **Encryption**: Use HTTPS and encrypt sensitive data
4. **Audit Logging**: Implement comprehensive audit trails

## Security Features

This library includes several built-in security features through integrated components:

### seven-shield Integration

- CSRF protection
- XSS prevention
- Input validation utilities
- Security headers configuration

### seven-data-security Integration

- Data encryption utilities
- Secure object serialization
- Reflection-based security controls

### seven-operating-record Integration

- Comprehensive audit logging
- Operation tracking
- Security event monitoring

## Known Security Considerations

### SSE (Server-Sent Events)

When using SSE functionality:
- Implement proper authentication for SSE connections
- Validate client IDs to prevent unauthorized access
- Use HTTPS in production environments
- Consider rate limiting for SSE endpoints

### Bean Operations

When using BeanUtil for object transformations:
- Validate input objects before processing
- Use appropriate access controls
- Consider data sensitivity when copying properties

## Third-Party Security

### Dependencies

We carefully select and monitor our dependencies:

- **Spring Boot**: Follows Spring Security best practices
- **MyBatis-Plus**: Regular security updates
- **Redis**: Secure configuration recommendations provided

### Security Scanning

We regularly:
- Scan dependencies for known vulnerabilities
- Review security advisories for all dependencies
- Update dependencies promptly when security issues are found

## Responsible Disclosure

We believe in responsible disclosure:

1. We will acknowledge your report promptly
2. We will keep you informed of our progress
3. We will credit you in the release notes (unless you prefer anonymity)
4. We will coordinate public disclosure timing

## Contact

For security-related questions or reports:

- **Primary Contact**: [avinzhang@tencent.com](mailto:avinzhang@tencent.com)
- **Backup Contact**: [GitHub Security Advisory](https://github.com/qwzhang01/seven-spring-web-tool/security/advisories)

## License

This security policy is licensed under the same terms as the project (Apache 2.0).