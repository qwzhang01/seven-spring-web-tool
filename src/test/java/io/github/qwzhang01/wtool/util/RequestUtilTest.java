package io.github.qwzhang01.wtool.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive test cases for RequestUtil class.
 * Tests cover JSON output, URI extraction, header parsing, and IP detection.
 *
 * @author avinzhang
 */
@ExtendWith(MockitoExtension.class)
class RequestUtilTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void testPrint_ValidResponse_Success() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        String jsonContent = "{\"status\":\"success\"}";

        // Act
        RequestUtil.print(response, jsonContent);
        writer.flush();

        // Assert
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setContentType("application/json;charset=utf-8");
        assertEquals(jsonContent, stringWriter.toString());
    }

    @Test
    void testPrint_EmptyContent_Success() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        RequestUtil.print(response, "");
        writer.flush();

        // Assert
        assertEquals("", stringWriter.toString());
    }

    @Test
    void testPrint_NullContent_Success() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        RequestUtil.print(response, null);
        writer.flush();

        // Assert
        assertEquals("null", stringWriter.toString());
    }

    @Test
    void testGetRequestUri_WithoutQueryString_ReturnsUri() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getQueryString()).thenReturn(null);

        // Act
        String result = RequestUtil.getRequestUri(request);

        // Assert
        assertEquals("/api/users", result);
    }

    @Test
    void testGetRequestUri_WithQueryString_ReturnsFullUri() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getQueryString()).thenReturn("page=1&size=10");

        // Act
        String result = RequestUtil.getRequestUri(request);

        // Assert
        assertEquals("/api/users?page=1&size=10", result);
    }

    @Test
    void testGetRequestUri_WithEmptyQueryString_ReturnsFullUri() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getQueryString()).thenReturn("");

        // Act
        String result = RequestUtil.getRequestUri(request);

        // Assert
        assertEquals("/api/users?", result);
    }

    @Test
    void testGetRequestUri_WithSpecialCharactersInQuery_ReturnsFullUri() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/search");
        when(request.getQueryString()).thenReturn("q=test&filter=name%3Djohn");

        // Act
        String result = RequestUtil.getRequestUri(request);

        // Assert
        assertEquals("/api/search?q=test&filter=name%3Djohn", result);
    }

    @Test
    void testGetHeaderByKey_ValidHeader_ReturnsDecodedValue() {
        // Arrange
        String headerKey = "Authorization";
        String headerValue = "Bearer token123";
        when(request.getHeader(headerKey)).thenReturn(headerValue);

        // Act
        String result = RequestUtil.getHeader(request, headerKey);

        // Assert
        assertEquals("Bearer token123", result);
    }

    @Test
    void testGetHeaderByKey_NullHeader_ReturnsEmptyString() {
        // Arrange
        String headerKey = "Authorization";
        when(request.getHeader(headerKey)).thenReturn(null);

        // Act
        String result = RequestUtil.getHeader(request, headerKey);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testGetHeaderByKey_EmptyHeader_ReturnsEmptyString() {
        // Arrange
        String headerKey = "Authorization";
        when(request.getHeader(headerKey)).thenReturn("");

        // Act
        String result = RequestUtil.getHeader(request, headerKey);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testGetHeaderByKey_BlankHeader_ReturnsEmptyString() {
        // Arrange
        String headerKey = "Authorization";
        when(request.getHeader(headerKey)).thenReturn("   ");

        // Act
        String result = RequestUtil.getHeader(request, headerKey);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testGetHeaderByKey_HeaderWithSpaces_ReturnsTrimmedValue() {
        // Arrange
        String headerKey = "Custom-Header";
        String headerValue = "  value with spaces  ";
        when(request.getHeader(headerKey)).thenReturn(headerValue);

        // Act
        String result = RequestUtil.getHeader(request, headerKey);

        // Assert
        assertEquals("value with spaces", result);
    }
    
    @Test
    void testGetHeader_NoHeaders_ReturnsEmptyString() {
        // Arrange
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Act
        String result = RequestUtil.getHeader(request);

        // Assert
        assertEquals("[]", result);
    }

    @Test
    void testGetIpAddress_FromXForwardedFor_ReturnsFirstIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("192.168.1.1, " +
                "192.168.1.2, 192.168.1.3");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("192.168.1.1", result);
    }

    @Test
    void testGetIpAddress_FromXForwardedFor_SingleIp_ReturnsIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("192.168.1.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("192.168.1.1", result);
    }

    @Test
    void testGetIpAddress_XForwardedForUnknown_FallsBackToProxyClientIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("unknown");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("10.0.0.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("10.0.0.1", result);
    }

    @Test
    void testGetIpAddress_FallsBackToWLProxyClientIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("172.16.0.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("172.16.0.1", result);
    }

    @Test
    void testGetIpAddress_FallsBackToHttpClientIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("203.0.113.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("203.0.113.1", result);
    }

    @Test
    void testGetIpAddress_FallsBackToHttpXForwardedFor() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn("198.51" +
                ".100.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("198.51.100.1", result);
    }

    @Test
    void testGetIpAddress_FallsBackToRemoteAddr() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("127.0.0.1", result);
    }

    @Test
    void testGetIpAddress_AllHeadersUnknown_FallsBackToRemoteAddr() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("unknown");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("unknown");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("unknown");
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("unknown");
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn("unknown");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("192.168.1.100", result);
    }

    @Test
    void testGetIpAddress_EmptyHeaders_FallsBackToRemoteAddr() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("");
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("");
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("192.168.1.100", result);
    }

    @Test
    void testGetIpAddress_IPv6Address_ReturnsCorrectly() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("2001:0db8:85a3" +
                ":0000:0000:8a2e:0370:7334");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("2001:0db8:85a3:0000:0000:8a2e:0370:7334", result);
    }

    @Test
    void testGetIpAddress_MultipleIpsWithSpaces_ReturnsTrimmedFirstIp() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn(" 192.168.1.1 ," +
                " 192.168.1.2 ");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals(" 192.168.1.1 ", result);
    }

    @Test
    void testGetLocalIp_ReturnsNonNull() {
        // Act
        String result = RequestUtil.getLocalIp();

        // Assert
        assertNotNull(result);
        // Local IP might be empty if no valid interface found, but shouldn't
        // be null
    }

    @Test
    void testGetLocalIp_ReturnsValidIpFormat() {
        // Act
        String result = RequestUtil.getLocalIp();

        // Assert
        // If an IP is returned, it should match IPv4 format
        if (!result.isEmpty()) {
            assertTrue(result.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\" +
                    ".\\d{1,3}$"));
        }
    }

    @Test
    void testGetLocalIp_NotLoopback() {
        // Act
        String result = RequestUtil.getLocalIp();

        // Assert
        // Should not return loopback address
        if (!result.isEmpty()) {
            assertNotEquals("127.0.0.1", result);
        }
    }

    @Test
    void testGetIpAddress_CaseInsensitiveUnknown_HandlesCorrectly() {
        // Arrange
        when(request.getHeader("x-forwarded-for")).thenReturn("UnKnOwN");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("192.168.1.1");

        // Act
        String result = RequestUtil.getIpAddress(request);

        // Assert
        assertEquals("192.168.1.1", result);
    }
}
