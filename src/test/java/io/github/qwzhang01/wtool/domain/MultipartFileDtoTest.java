package io.github.qwzhang01.wtool.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test cases for MultipartFileDto class.
 * Tests cover file creation from byte arrays and streams, file operations,
 * and edge cases.
 *
 * @author avinzhang
 */
class MultipartFileDtoTest {

    @TempDir
    Path tempDir;

    @Test
    void testConstructor_WithByteArray_Success() throws IOException {
        // Arrange
        byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
        String name = "file";
        String originalFilename = "test.txt";
        String contentType = "text/plain";

        // Act
        MultipartFileDto file = new MultipartFileDto(name, originalFilename,
                contentType, content);

        // Assert
        assertEquals(name, file.getName());
        assertEquals(originalFilename, file.getOriginalFilename());
        assertEquals(contentType, file.getContentType());
        assertArrayEquals(content, file.getBytes());
        assertEquals(content.length, file.getSize());
        assertFalse(file.isEmpty());
    }

    @Test
    void testConstructor_WithInputStream_Success() throws IOException {
        // Arrange
        String testContent = "Test content from stream";
        InputStream inputStream =
                new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));
        String name = "file";
        String originalFilename = "test.txt";
        String contentType = "text/plain";

        // Act
        MultipartFileDto file = new MultipartFileDto(name, originalFilename,
                contentType, inputStream);

        // Assert
        assertEquals(name, file.getName());
        assertEquals(originalFilename, file.getOriginalFilename());
        assertEquals(contentType, file.getContentType());
        assertEquals(testContent.length(), file.getSize());
        assertFalse(file.isEmpty());
    }

    @Test
    void testConstructor_WithNullName_ThrowsException() {
        // Arrange
        byte[] content = "Test".getBytes();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new MultipartFileDto(null, "test.txt", "text/plain", content));
    }

    @Test
    void testConstructor_WithEmptyName_ThrowsException() {
        // Arrange
        byte[] content = "Test".getBytes();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new MultipartFileDto("", "test.txt", "text/plain", content));
    }

    @Test
    void testConstructor_WithNullOriginalFilename_UsesEmpty() {
        // Arrange
        byte[] content = "Test".getBytes();

        // Act
        MultipartFileDto file = new MultipartFileDto("file", null, "text" +
                "/plain", content);

        // Assert
        assertEquals("", file.getOriginalFilename());
    }

    @Test
    void testConstructor_WithNullContent_UsesEmptyArray() throws IOException {
        // Act
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", (byte[]) null);

        // Assert
        assertNotNull(file.getBytes());
        assertEquals(0, file.getSize());
        assertTrue(file.isEmpty());
    }

    @Test
    void testGetName_ReturnsCorrectName() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("fieldName", "file.txt",
                "text/plain", "content".getBytes());

        // Act
        String name = file.getName();

        // Assert
        assertEquals("fieldName", name);
    }

    @Test
    void testGetOriginalFilename_ReturnsCorrectFilename() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "document.pdf",
                "application/pdf", "content".getBytes());

        // Act
        String filename = file.getOriginalFilename();

        // Assert
        assertEquals("document.pdf", filename);
    }

    @Test
    void testGetContentType_ReturnsCorrectType() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "image.jpg",
                "image/jpeg", "content".getBytes());

        // Act
        String contentType = file.getContentType();

        // Assert
        assertEquals("image/jpeg", contentType);
    }

    @Test
    void testGetContentType_NullContentType_ReturnsNull() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "test.txt", null
                , "content".getBytes());

        // Act
        String contentType = file.getContentType();

        // Assert
        assertNull(contentType);
    }

    @Test
    void testIsEmpty_EmptyContent_ReturnsTrue() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", new byte[0]);

        // Act
        boolean isEmpty = file.isEmpty();

        // Assert
        assertTrue(isEmpty);
    }

    @Test
    void testIsEmpty_NonEmptyContent_ReturnsFalse() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", "content".getBytes());

        // Act
        boolean isEmpty = file.isEmpty();

        // Assert
        assertFalse(isEmpty);
    }

    @Test
    void testGetSize_ReturnsCorrectSize() {
        // Arrange
        byte[] content =
                "This is test content".getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);

        // Act
        long size = file.getSize();

        // Assert
        assertEquals(content.length, size);
    }

    @Test
    void testGetSize_EmptyContent_ReturnsZero() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", new byte[0]);

        // Act
        long size = file.getSize();

        // Assert
        assertEquals(0, size);
    }

    @Test
    void testGetBytes_ReturnsCorrectBytes() throws IOException {
        // Arrange
        byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);

        // Act
        byte[] bytes = file.getBytes();

        // Assert
        assertArrayEquals(content, bytes);
    }

    @Test
    void testGetInputStream_ReturnsCorrectStream() throws IOException {
        // Arrange
        String testContent = "Test content for stream";
        byte[] content = testContent.getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);

        // Act
        InputStream inputStream = file.getInputStream();
        byte[] readBytes = inputStream.readAllBytes();

        // Assert
        assertArrayEquals(content, readBytes);
    }

    @Test
    void testTransferTo_CreatesFileWithCorrectContent() throws IOException {
        // Arrange
        String testContent = "Transfer test content";
        byte[] content = testContent.getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);
        File destFile = tempDir.resolve("transferred.txt").toFile();

        // Act
        file.transferTo(destFile);

        // Assert
        assertTrue(destFile.exists());
        byte[] fileContent = Files.readAllBytes(destFile.toPath());
        assertArrayEquals(content, fileContent);
    }

    @Test
    void testTransferTo_EmptyContent_CreatesEmptyFile() throws IOException {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "empty.txt",
                "text/plain", new byte[0]);
        File destFile = tempDir.resolve("empty.txt").toFile();

        // Act
        file.transferTo(destFile);

        // Assert
        assertTrue(destFile.exists());
        assertEquals(0, destFile.length());
    }

    @Test
    void testToStr_ReturnsJsonLikeString() {
        // Arrange
        byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);

        // Act
        String result = file.toStr();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"name\":\"file\""));
        assertTrue(result.contains("\"originalFilename\":\"test.txt\""));
        assertTrue(result.contains("\"contentType\":\"text/plain\""));
        assertTrue(result.contains("byte数组长度为:" + content.length));
    }

    @Test
    void testToStr_NullContent_ShowsZeroLength() {
        // Arrange
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", (byte[]) null);

        // Act
        String result = file.toStr();

        // Assert
        assertTrue(result.contains("byte数组长度为:0"));
    }

    @Test
    void testConstructor_LargeFile_Success() throws IOException {
        // Arrange - Create a large content (1MB)
        byte[] largeContent = new byte[1024 * 1024];
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        // Act
        MultipartFileDto file = new MultipartFileDto("file", "large.bin",
                "application/octet-stream", largeContent);

        // Assert
        assertEquals(largeContent.length, file.getSize());
        assertArrayEquals(largeContent, file.getBytes());
    }

    @Test
    void testConstructor_WithInputStream_LargeFile_Success() throws IOException {
        // Arrange - Create a large content (1MB)
        byte[] largeContent = new byte[1024 * 1024];
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(largeContent);

        // Act
        MultipartFileDto file = new MultipartFileDto("file", "large.bin",
                "application/octet-stream", inputStream);

        // Assert
        assertEquals(largeContent.length, file.getSize());
    }

    @Test
    void testGetInputStream_MultipleReads_Success() throws IOException {
        // Arrange
        String testContent = "Test content";
        byte[] content = testContent.getBytes(StandardCharsets.UTF_8);
        MultipartFileDto file = new MultipartFileDto("file", "test.txt",
                "text/plain", content);

        // Act - Read multiple times
        InputStream stream1 = file.getInputStream();
        byte[] read1 = stream1.readAllBytes();

        InputStream stream2 = file.getInputStream();
        byte[] read2 = stream2.readAllBytes();

        // Assert - Both reads should return the same content
        assertArrayEquals(content, read1);
        assertArrayEquals(content, read2);
    }

    @Test
    void testConstructor_BinaryContent_Success() throws IOException {
        // Arrange - Create binary content
        byte[] binaryContent = new byte[]{0x00, 0x01, 0x02, (byte) 0xFF,
                (byte) 0xFE};

        // Act
        MultipartFileDto file = new MultipartFileDto("file", "binary.dat",
                "application/octet-stream", binaryContent);

        // Assert
        assertEquals(5, file.getSize());
        assertArrayEquals(binaryContent, file.getBytes());
    }

    @Test
    void testConstructor_SpecialCharactersInFilename_Success() {
        // Arrange
        byte[] content = "Test".getBytes();
        String filename = "文件名_测试_123.txt";

        // Act
        MultipartFileDto file = new MultipartFileDto("file", filename, "text" +
                "/plain", content);

        // Assert
        assertEquals(filename, file.getOriginalFilename());
    }
}
