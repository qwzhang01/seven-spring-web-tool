package io.github.qwzhang01.wtool.util;

import io.github.qwzhang01.wtool.exception.Base64DecodeException;

import java.util.Base64;
import java.util.UUID;

/**
 * String utility class providing common string operations.
 * Includes methods for string validation, length checking, and UUID generation.
 *
 * @author avinzhang
 */
public class StrUtil {
    /**
     * Returns the length of the character sequence.
     *
     * @param cs the character sequence
     * @return the length of the sequence, or 0 if the sequence is null
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Checks if a character sequence is blank (null, empty, or contains only
     * whitespace).
     *
     * @param cs the character sequence to check
     * @return true if the sequence is blank, false otherwise
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a UUID string without hyphens in lowercase format.
     *
     * @return a UUID string with hyphens removed and converted to lowercase
     */
    public static String uuidStr() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * Decodes a Base64 encoded string to byte array.
     * This method automatically handles data URIs by removing the "base64," prefix if present.
     * For example, it can decode both "SGVsbG8=" and "data:image/png;base64,SGVsbG8=".
     *
     * @param base64 the Base64 encoded string to decode
     * @return the decoded byte array
     * @throws Base64DecodeException if the string is not valid Base64 or decoding fails
     */
    public static byte[] decodeBase64(String base64) {
        try {
            if (base64.contains("base64,")) {
                base64 = base64.substring(base64.indexOf("base64,") + 7);
            }
            return Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            throw new Base64DecodeException("Base64 decode error", e);
        }
    }
}