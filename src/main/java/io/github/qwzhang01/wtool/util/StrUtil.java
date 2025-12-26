package io.github.qwzhang01.wtool.util;

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
     * Checks if a character sequence is blank (null, empty, or contains only whitespace).
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
}
