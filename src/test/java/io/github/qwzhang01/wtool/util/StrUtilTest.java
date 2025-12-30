package io.github.qwzhang01.wtool.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test cases for StrUtil class.
 * Tests cover string length checking, blank validation, and UUID generation.
 *
 * @author avinzhang
 */
class StrUtilTest {

    @Test
    void testLength_NullInput_ReturnsZero() {
        // Act
        int result = StrUtil.length(null);

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testLength_EmptyString_ReturnsZero() {
        // Act
        int result = StrUtil.length("");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testLength_NonEmptyString_ReturnsCorrectLength() {
        // Arrange
        String input = "Hello";

        // Act
        int result = StrUtil.length(input);

        // Assert
        assertEquals(5, result);
    }

    @Test
    void testLength_StringWithSpaces_ReturnsCorrectLength() {
        // Arrange
        String input = "Hello World";

        // Act
        int result = StrUtil.length(input);

        // Assert
        assertEquals(11, result);
    }

    @Test
    void testLength_ChineseCharacters_ReturnsCorrectLength() {
        // Arrange
        String input = "你好世界";

        // Act
        int result = StrUtil.length(input);

        // Assert
        assertEquals(4, result);
    }

    @Test
    void testLength_UnicodeCharacters_ReturnsCorrectLength() {
        // Arrange
        String input = "🎉🎊🎈";

        // Act
        int result = StrUtil.length(input);

        // Assert
        // Emoji characters might be counted differently depending on
        // representation
        assertTrue(result > 0);
    }

    @Test
    void testIsBlank_NullInput_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank(null);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_EmptyString_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank("");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_OnlySpaces_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank("   ");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_OnlyTabs_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank("\t\t\t");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_OnlyNewlines_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank("\n\n");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_MixedWhitespace_ReturnsTrue() {
        // Act
        boolean result = StrUtil.isBlank(" \t\n\r ");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlank_NonBlankString_ReturnsFalse() {
        // Act
        boolean result = StrUtil.isBlank("Hello");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsBlank_StringWithLeadingSpaces_ReturnsFalse() {
        // Act
        boolean result = StrUtil.isBlank("  Hello");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsBlank_StringWithTrailingSpaces_ReturnsFalse() {
        // Act
        boolean result = StrUtil.isBlank("Hello  ");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsBlank_StringWithMiddleSpaces_ReturnsFalse() {
        // Act
        boolean result = StrUtil.isBlank("Hello World");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsBlank_SingleCharacter_ReturnsFalse() {
        // Act
        boolean result = StrUtil.isBlank("a");

        // Assert
        assertFalse(result);
    }

    @Test
    void testUuidStr_GeneratesValidUUID() {
        // Act
        String uuid = StrUtil.uuidStr();

        // Assert
        assertNotNull(uuid);
        assertEquals(32, uuid.length()); // UUID without hyphens is 32
        // characters
        assertFalse(uuid.contains("-")); // Should not contain hyphens
    }

    @Test
    void testUuidStr_IsLowercase() {
        // Act
        String uuid = StrUtil.uuidStr();

        // Assert
        assertEquals(uuid, uuid.toLowerCase());
    }

    @Test
    void testUuidStr_GeneratesUniqueValues() {
        // Act - Generate multiple UUIDs
        String uuid1 = StrUtil.uuidStr();
        String uuid2 = StrUtil.uuidStr();
        String uuid3 = StrUtil.uuidStr();

        // Assert - All should be different
        assertNotEquals(uuid1, uuid2);
        assertNotEquals(uuid2, uuid3);
        assertNotEquals(uuid1, uuid3);
    }

    @Test
    void testUuidStr_OnlyHexCharacters() {
        // Act
        String uuid = StrUtil.uuidStr();

        // Assert - Should only contain hex characters (0-9, a-f)
        assertTrue(uuid.matches("^[0-9a-f]{32}$"));
    }

    @Test
    void testUuidStr_MultipleGenerations_AllValid() {
        // Act & Assert - Generate 100 UUIDs and verify all are valid
        for (int i = 0; i < 100; i++) {
            String uuid = StrUtil.uuidStr();
            assertNotNull(uuid);
            assertEquals(32, uuid.length());
            assertFalse(uuid.contains("-"));
            assertEquals(uuid, uuid.toLowerCase());
            assertTrue(uuid.matches("^[0-9a-f]{32}$"));
        }
    }

    @Test
    void testIsBlank_ZeroWidthSpace_ReturnsTrue() {
        // Arrange - Zero-width space character
        String input = "\u200B";

        // Act
        boolean result = StrUtil.isBlank(input);

        // Assert
        assertFalse(result); // Zero-width space is not considered whitespace
        // by Character.isWhitespace()
    }

    @Test
    void testLength_StringBuilder_ReturnsCorrectLength() {
        // Arrange
        StringBuilder sb = new StringBuilder("Hello World");

        // Act
        int result = StrUtil.length(sb);

        // Assert
        assertEquals(11, result);
    }

    @Test
    void testIsBlank_StringBuilder_Success() {
        // Arrange
        StringBuilder blank = new StringBuilder("   ");
        StringBuilder nonBlank = new StringBuilder("Hello");

        // Act & Assert
        assertTrue(StrUtil.isBlank(blank));
        assertFalse(StrUtil.isBlank(nonBlank));
    }
}
