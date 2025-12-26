package io.github.qwzhang01.wtool.domain;

/**
 * Functional interface for parameterized business logic execution.
 * Provides a callback mechanism for executing custom business logic
 * with a string parameter and boolean return value.
 *
 * @author avinzhang
 */
@FunctionalInterface
public interface CallFunction {
    /**
     * Executes the specific business logic.
     *
     * @param param the input parameter for the business logic
     * @return true if the execution is successful, false otherwise
     */
    boolean call(String param);
}
