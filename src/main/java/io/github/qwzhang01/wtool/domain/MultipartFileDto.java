package io.github.qwzhang01.wtool.domain;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Data Transfer Object implementation of Spring's MultipartFile interface.
 * This class provides an in-memory representation of a multipart file,
 * useful for testing and programmatic file handling without actual file uploads.
 *
 * @author avinzhang
 */
public class MultipartFileDto implements MultipartFile {

    private final String name;
    private final byte[] content;
    private final String originalFilename;
    private final String contentType;

    /**
     * Creates a new MultipartFileDto with the given byte array content.
     *
     * @param name             the name of the file parameter
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType      the MIME type of the file (e.g., "image/jpeg")
     * @param content          the binary content of the file as byte array
     */
    public MultipartFileDto(String name, String originalFilename,
                            String contentType, byte[] content) {
        Assert.hasLength(name, "Name must not be null");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename
                : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
    }

    /**
     * Creates a new MultipartFileDto with content read from an InputStream.
     *
     * @param name             the name of the file parameter
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType      the MIME type of the file (e.g., "application/pdf")
     * @param contentStream    the input stream containing the file content
     * @throws IOException if an error occurs while reading from the stream
     */
    public MultipartFileDto(String name, String originalFilename,
                            String contentType, InputStream contentStream)
            throws IOException {
        this(name, originalFilename, contentType,
                FileCopyUtils.copyToByteArray(contentStream));
    }

    /**
     * Converts the MultipartFileDto to a JSON-like string representation.
     * The byte array content is omitted, showing only its length for brevity.
     *
     * @return a string representation in JSON format
     */
    public String toStr() {
        Integer length =
                Optional.ofNullable(content).map(t -> t.length).orElse(0);
        return "{\"name\":\"" + name + "\",\"content\":\"省略打印byte数组内容，当前byte" +
                "数组长度为:" + length + "\",\"originalFilename\":\"" + originalFilename + "\",\"contentType\":\"" + contentType + "\"}\n";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException,
            IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
}
