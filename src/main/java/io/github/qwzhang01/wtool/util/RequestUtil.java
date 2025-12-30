package io.github.qwzhang01.wtool.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


/**
 * Utility class for HTTP request processing and domain parsing.
 * Provides methods for handling JSON responses, extracting request information,
 * parsing headers, and detecting client IP addresses (including proxy scenarios).
 *
 * @author avinzhang
 */
public class RequestUtil {
    private static final Logger log =
            LoggerFactory.getLogger(RequestUtil.class);

    /**
     * Writes JSON content to the HTTP response.
     *
     * @param response    the HttpServletResponse object
     * @param respContent the JSON content to write
     */
    public static void print(HttpServletResponse response,
                             String respContent) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(respContent);
        } catch (IOException e) {
            log.error("Failed to write JSON response due to IO exception", e);
        }
    }

    /**
     * Gets the complete request URI including query parameters.
     *
     * @param request the HTTP request object
     * @return the complete request URI string
     */
    public static String getRequestUri(HttpServletRequest request) {
        if (request.getQueryString() == null) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + request.getQueryString();
    }

    /**
     * Retrieves a specific header value from the request and decodes it.
     *
     * @param request   the HTTP request object
     * @param headerKey the name of the header to retrieve
     * @return the decoded and trimmed header value, or empty string if not
     * found
     */
    public static String getHeader(HttpServletRequest request,
                                   String headerKey) {
        String headerValue = request.getHeader(headerKey);
        if (StrUtil.isBlank(headerValue)) {
            return "";
        }
        headerValue = URLDecoder.decode(headerValue, StandardCharsets.UTF_8);
        if (StrUtil.isBlank(headerValue)) {
            return "";
        }
        return headerValue.trim();
    }

    /**
     * Retrieves all request headers (excluding cookies) as a string.
     *
     * @param request the HTTP request object
     * @return a string representation of all headers
     */
    public static String getHeader(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            if ("cookie".equalsIgnoreCase(headerName)) {
                continue;
            }

            headers.set(headerName,
                    URLDecoder.decode(request.getHeader(headerName),
                            StandardCharsets.UTF_8));
        }
        return headers.toString();
    }

    /**
     * Gets the real client IP address, handling proxy scenarios.
     * This method checks various proxy headers to determine the original
     * client IP,
     * rather than using request.getRemoteAddr() which may return the proxy's
     * IP.
     * <p>
     * When behind multiple reverse proxies, X-Forwarded-For contains a
     * comma-separated
     * list of IPs. This method returns the first non-unknown valid IP address.
     * <p>
     * Example: X-Forwarded-For: 192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * Real client IP: 192.168.1.110
     *
     * @param request the HTTP request object
     * @return the real client IP address
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // When multiple proxies are involved, take the first non-unknown IP
        if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return ip;
    }

    /**
     * Gets the local machine's IP address.
     * Skips loopback (127.0.0.1), virtual interfaces, and disabled
     * interfaces.
     * Returns the first active IPv4 address found.
     *
     * @return the local machine's IP address, or empty string if not found
     * @throws UnsupportedOperationException if an error occurs while
     *                                       retrieving network interfaces
     */
    public static String getLocalIp() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                // Skip loopback (127.0.0.1), virtual interfaces, and disabled
                // interfaces
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Only return IPv4 addresses (remove !addr
                    // .isLoopbackAddress() condition for IPv6 support)
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        log.info("Network Interface: " + ni.getDisplayName());
                        log.info("IP Address: " + addr.getHostAddress());
                        return addr.getHostAddress();
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            throw new UnsupportedOperationException("Failed to retrieve local machine IP address", e);
        }
    }
}