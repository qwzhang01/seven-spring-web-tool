package io.github.qwzhang01.wtool.exception;

/**
 * Bean copying exception.
 * This exception is thrown when errors occur during bean copying operations,
 * such as reflection errors, type conversion failures, or missing constructors.
 *
 * @author avinzhang
 */
public class BeanCopyException extends RuntimeException {
    /**
     * Constructs a new BeanCopyException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public BeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }
}