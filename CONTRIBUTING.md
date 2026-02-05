# Contributing to Seven Spring Web Tool

Thank you for your interest in contributing to Seven Spring Web Tool! We welcome contributions from the community and are grateful for your help in making this project better.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Style Guidelines](#style-guidelines)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Enhancements](#suggesting-enhancements)

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before contributing.

## Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally
3. Set up the development environment (see below)
4. Create a branch for your contribution

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with the following information:

- Clear and descriptive title
- Steps to reproduce the bug
- Expected behavior
- Actual behavior
- Environment details (Java version, Spring Boot version, etc.)
- Any relevant logs or error messages

### Suggesting Enhancements

We welcome feature suggestions! Please include:

- Clear description of the proposed feature
- Use cases and examples
- Any relevant technical considerations

### Code Contributions

1. **Small fixes**: Direct pull requests are welcome for small bug fixes or documentation improvements
2. **New features**: Please open an issue first to discuss the feature before implementing

## Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

### Building the Project

```bash
# Clone the repository
git clone https://github.com/qwzhang01/seven-spring-web-tool.git
cd seven-spring-web-tool

# Build the project
mvn clean compile

# Run tests
mvn test

# Package the library
mvn package
```

### IDE Setup

We recommend using IntelliJ IDEA or Eclipse with the following plugins:

- Lombok Plugin (if using Lombok annotations)
- Checkstyle Plugin (for code style validation)

## Pull Request Process

1. **Fork and Branch**: Fork the repository and create a feature branch from `main`
2. **Code Quality**: Ensure your code follows our style guidelines
3. **Tests**: Add or update tests for your changes
4. **Documentation**: Update README.md if your changes affect functionality
5. **Commit Messages**: Use clear, descriptive commit messages
6. **Pull Request**: Open a PR with a clear description of changes

### Pull Request Checklist

- [ ] Code follows the project's style guidelines
- [ ] Tests pass (`mvn test`)
- [ ] Documentation is updated if necessary
- [ ] Changes are backward compatible (if applicable)
- [ ] New features include appropriate tests
- [ ] Code is properly formatted

## Style Guidelines

### Java Code Style

- Use 4 spaces for indentation (no tabs)
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods and classes
- Keep methods focused and concise

### Code Organization

- Place new utility classes in appropriate packages
- Follow existing patterns in the codebase
- Use dependency injection where appropriate
- Keep utility methods stateless when possible

### Testing Guidelines

- Write unit tests for new functionality
- Use descriptive test method names
- Follow Arrange-Act-Assert pattern
- Mock external dependencies appropriately

## Commit Message Format

Use the following format for commit messages:

```
<type>: <description>

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Example:
```
feat: add Redis-based SSE message broker

- Implement RedisSseMessageBroker for multi-instance support
- Add Redis configuration properties
- Update documentation with Redis setup instructions

Closes #123
```

## Review Process

- All pull requests will be reviewed by maintainers
- We aim to provide feedback within 3 business days
- Please be responsive to review comments
- We may request changes before merging

## Release Process

Releases are managed by project maintainers. After your contribution is merged:

1. Changes will be included in the next release
2. Release notes will credit contributors
3. The release will be published to Maven Central

## Getting Help

If you need help with your contribution:

- Check existing issues and documentation
- Ask questions in the issue comments
- Contact maintainers if needed

## Recognition

All contributors will be acknowledged in:

- Release notes
- Project documentation (if significant contribution)
- GitHub contributors list

Thank you for contributing to Seven Spring Web Tool!