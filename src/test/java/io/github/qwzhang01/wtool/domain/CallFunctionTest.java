package io.github.qwzhang01.wtool.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for CallFunction functional interface.
 * Tests cover validation logic, business rules, and callback execution.
 *
 * @author avinzhang
 */
class CallFunctionTest {

    @Test
    void testCallFunction_SimpleLambda_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> true;

        // Act
        boolean result = function.call("test");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_SimpleLambda_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> false;

        // Act
        boolean result = function.call("test");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_NullCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null;

        // Act
        boolean result = function.call("test");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_NullCheck_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null;

        // Act
        boolean result = function.call(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_EmptyCheck_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && !param.isEmpty();

        // Act
        boolean result = function.call("");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_EmptyCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && !param.isEmpty();

        // Act
        boolean result = function.call("content");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_LengthValidation_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && param.length() >= 5;

        // Act
        boolean result = function.call("hello");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_LengthValidation_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && param.length() >= 5;

        // Act
        boolean result = function.call("hi");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_PatternMatching_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && param.matches(
                "^[a-zA-Z0-9]+$");

        // Act
        boolean result = function.call("user123");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_PatternMatching_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && param.matches(
                "^[a-zA-Z0-9]+$");

        // Act
        boolean result = function.call("user@123");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_EmailValidation_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) ->
                param != null && param.matches("^[A-Za-z0-9+_.-]+@(.+)$");

        // Act
        boolean result = function.call("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_EmailValidation_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) ->
                param != null && param.matches("^[A-Za-z0-9+_.-]+@(.+)$");

        // Act
        boolean result = function.call("invalid-email");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_ContainsCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && param.contains(
                "test");

        // Act
        boolean result = function.call("this is a test");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_ContainsCheck_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && param.contains(
                "test");

        // Act
        boolean result = function.call("no match here");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_StartsWithCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && param.startsWith(
                "prefix");

        // Act
        boolean result = function.call("prefix_value");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_StartsWithCheck_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && param.startsWith(
                "prefix");

        // Act
        boolean result = function.call("value_prefix");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_EndsWithCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> param != null && param.endsWith(
                ".txt");

        // Act
        boolean result = function.call("document.txt");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_EndsWithCheck_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> param != null && param.endsWith(
                ".txt");

        // Act
        boolean result = function.call("document.pdf");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_ComplexValidation_ReturnsTrue() {
        // Arrange - Username must be 3-20 chars, alphanumeric with underscore
        CallFunction function = (param) -> {
            if (param == null) return false;
            if (param.length() < 3 || param.length() > 20) return false;
            return param.matches("^[a-zA-Z0-9_]+$");
        };

        // Act
        boolean result = function.call("valid_user123");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_ComplexValidation_ReturnsFalse() {
        // Arrange - Username must be 3-20 chars, alphanumeric with underscore
        CallFunction function = (param) -> {
            if (param == null) return false;
            if (param.length() < 3 || param.length() > 20) return false;
            return param.matches("^[a-zA-Z0-9_]+$");
        };

        // Act
        boolean result = function.call("ab"); // Too short

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_NumericValidation_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> {
            if (param == null) return false;
            try {
                int value = Integer.parseInt(param);
                return value >= 0 && value <= 100;
            } catch (NumberFormatException e) {
                return false;
            }
        };

        // Act
        boolean result = function.call("50");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_NumericValidation_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> {
            if (param == null) return false;
            try {
                int value = Integer.parseInt(param);
                return value >= 0 && value <= 100;
            } catch (NumberFormatException e) {
                return false;
            }
        };

        // Act
        boolean result = function.call("150"); // Out of range

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_CaseInsensitiveCheck_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) ->
                param != null && param.equalsIgnoreCase("admin");

        // Act
        boolean result = function.call("ADMIN");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_MultipleConditions_ReturnsTrue() {
        // Arrange
        CallFunction function = (param) -> {
            if (param == null) return false;
            return param.length() > 5 &&
                    param.contains("@") &&
                    param.endsWith(".com");
        };

        // Act
        boolean result = function.call("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_MultipleConditions_ReturnsFalse() {
        // Arrange
        CallFunction function = (param) -> {
            if (param == null) return false;
            return param.length() > 5 &&
                    param.contains("@") &&
                    param.endsWith(".com");
        };

        // Act
        boolean result = function.call("test@example.org"); // Doesn't end
        // with .com

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_WhitespaceCheck_ReturnsTrue() {
        // Arrange
        CallFunction function =
                (param) -> param != null && !param.trim().isEmpty();

        // Act
        boolean result = function.call("content");

        // Assert
        assertTrue(result);
    }

    @Test
    void testCallFunction_WhitespaceCheck_ReturnsFalse() {
        // Arrange
        CallFunction function =
                (param) -> param != null && !param.trim().isEmpty();

        // Act
        boolean result = function.call("   ");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCallFunction_FunctionalComposition_Success() {
        // Arrange - Can be composed with other logic
        CallFunction validator1 =
                (param) -> param != null && param.length() > 3;
        CallFunction validator2 = (param) -> param != null && param.matches(
                "^[a-z]+$");

        String testParam = "hello";

        // Act
        boolean result1 = validator1.call(testParam);
        boolean result2 = validator2.call(testParam);
        boolean combinedResult = result1 && result2;

        // Assert
        assertTrue(combinedResult);
    }
}
