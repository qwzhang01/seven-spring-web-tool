package io.github.qwzhang01.wtool.exception;

/**
 * Base64 decoding exception.
 * This exception is thrown when errors occur during Base64 string decoding operations,
 * such as invalid Base64 format or corrupted data.
 *
 * @author avinzhang
 */
public class Base64DecodeException extends RuntimeException {
    /**
     * Constructs a new Base64DecodeException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public Base64DecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}