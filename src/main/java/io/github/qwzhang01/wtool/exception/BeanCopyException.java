package io.github.qwzhang01.wtool.exception;

/**
 * Bean copying exception.
 * This exception is thrown when errors occur during bean copying operations.
 *
 * @author avinzhang
 */
public class BeanCopyException extends RuntimeException {
    public BeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }
}