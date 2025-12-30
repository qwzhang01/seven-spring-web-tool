package io.github.qwzhang01.wtool.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for BeanCopyException class.
 * Tests exception creation, message handling, and cause wrapping.
 *
 * @author avinzhang
 */
class BeanCopyExceptionTest {

    @Test
    void testConstructor_WithMessageAndCause_Success() {
        // Arrange
        String message = "Bean copy failed";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructor_WithNullMessage_Success() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");

        // Act
        BeanCopyException exception = new BeanCopyException(null, cause);

        // Assert
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructor_WithNullCause_Success() {
        // Arrange
        String message = "Bean copy failed";

        // Act
        BeanCopyException exception = new BeanCopyException(message, null);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_WithBothNull_Success() {
        // Act
        BeanCopyException exception = new BeanCopyException(null, null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testException_IsRuntimeException() {
        // Arrange
        BeanCopyException exception = new BeanCopyException("Test",
                new Exception());

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testException_CanBeThrown() {
        // Arrange
        String message = "Test exception";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // Act & Assert
        BeanCopyException exception = assertThrows(BeanCopyException.class,
                () -> {
                    throw new BeanCopyException(message, cause);
                });

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testException_WithNestedCause_Success() {
        // Arrange
        Throwable rootCause = new IllegalStateException("Root cause");
        Throwable intermediateCause = new RuntimeException("Intermediate",
                rootCause);
        String message = "Bean copy failed";

        // Act
        BeanCopyException exception = new BeanCopyException(message,
                intermediateCause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    void testException_MessagePreservation_Success() {
        // Arrange
        String detailedMessage = "Failed to copy properties from User to " +
                "UserDTO: No such field 'nonExistentField'";
        Throwable cause = new NoSuchFieldException("nonExistentField");

        // Act
        BeanCopyException exception = new BeanCopyException(detailedMessage,
                cause);

        // Assert
        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains("UserDTO"));
        assertTrue(exception.getMessage().contains("nonExistentField"));
    }

    @Test
    void testException_StackTrace_Captured() {
        // Arrange
        String message = "Test exception";
        Throwable cause = new RuntimeException("Cause");

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testException_WithReflectionException_Success() {
        // Arrange
        String message = "Failed to instantiate target class";
        Throwable cause = new InstantiationException("Cannot create instance");

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof InstantiationException);
    }

    @Test
    void testException_WithIllegalAccessException_Success() {
        // Arrange
        String message = "Cannot access constructor";
        Throwable cause = new IllegalAccessException("Private constructor");

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalAccessException);
    }

    @Test
    void testException_LongMessage_Success() {
        // Arrange
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Error detail ").append(i).append(". ");
        }
        Throwable cause = new Exception("Root cause");

        // Act
        BeanCopyException exception =
                new BeanCopyException(longMessage.toString(), cause);

        // Assert
        assertEquals(longMessage.toString(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testException_SpecialCharactersInMessage_Success() {
        // Arrange
        String message = "Failed: @#$%^&*() with special chars: <>, [], {}, " +
                "\"quotes\"";
        Throwable cause = new RuntimeException();

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testException_UnicodeInMessage_Success() {
        // Arrange
        String message = "复制失败: 对象转换错误 🚫";
        Throwable cause = new RuntimeException();

        // Act
        BeanCopyException exception = new BeanCopyException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testException_CatchAndRethrow_Success() {
        // Act & Assert
        Exception caught = assertThrows(BeanCopyException.class, () -> {
            try {
                throw new IllegalArgumentException("Original exception");
            } catch (IllegalArgumentException e) {
                throw new BeanCopyException("Wrapped exception", e);
            }
        });

        assertTrue(caught instanceof BeanCopyException);
        assertTrue(caught.getCause() instanceof IllegalArgumentException);
        assertEquals("Original exception", caught.getCause().getMessage());
    }

    @Test
    void testException_MultipleWrappingLevels_Success() {
        // Arrange
        Throwable level1 = new NullPointerException("Level 1");
        Throwable level2 = new IllegalStateException("Level 2", level1);
        Throwable level3 = new RuntimeException("Level 3", level2);

        // Act
        BeanCopyException exception = new BeanCopyException("Bean copy " +
                "failed", level3);

        // Assert
        assertEquals("Bean copy failed", exception.getMessage());
        assertEquals("Level 3", exception.getCause().getMessage());
        assertEquals("Level 2", exception.getCause().getCause().getMessage());
        assertEquals("Level 1",
                exception.getCause().getCause().getCause().getMessage());
    }
}
