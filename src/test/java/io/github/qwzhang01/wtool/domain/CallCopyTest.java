package io.github.qwzhang01.wtool.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for CallCopy functional interface.
 * Tests cover lambda expressions, method references, and callback execution.
 *
 * @author avinzhang
 */
class CallCopyTest {

    @Test
    void testCallCopy_SimpleLambda_Success() {
        // Arrange
        Source source = new Source("test", 100);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            tgt.setName(src.getName());
            tgt.setValue(src.getValue());
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals(source.getName(), target.getName());
        assertEquals(source.getValue(), target.getValue());
    }

    @Test
    void testCallCopy_WithComputation_Success() {
        // Arrange
        Source source = new Source("test", 100);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            tgt.setComputed(src.getName().toUpperCase() + "_" + src.getValue());
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals("TEST_100", target.getComputed());
    }

    @Test
    void testCallCopy_MultipleOperations_Success() {
        // Arrange
        Source source = new Source("hello", 42);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            tgt.setName(src.getName().toUpperCase());
            tgt.setValue(src.getValue() * 2);
            tgt.setComputed("Processed: " + src.getName());
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals("HELLO", target.getName());
        assertEquals(84, target.getValue());
        assertEquals("Processed: hello", target.getComputed());
    }

    @Test
    void testCallCopy_EmptyLambda_NoException() {
        // Arrange
        Source source = new Source("test", 100);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            // Do nothing
        };

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> callback.call(source, target));
    }

    @Test
    void testCallCopy_ConditionalLogic_Success() {
        // Arrange
        Source source = new Source("test", 50);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            if (src.getValue() > 30) {
                tgt.setComputed("High value");
            } else {
                tgt.setComputed("Low value");
            }
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals("High value", target.getComputed());
    }

    @Test
    void testCallCopy_ChainedCallbacks_Success() {
        // Arrange
        Source source = new Source("test", 100);
        Target target = new Target();

        CallCopy<Source, Target> callback1 =
                (src, tgt) -> tgt.setName(src.getName());
        CallCopy<Source, Target> callback2 =
                (src, tgt) -> tgt.setValue(src.getValue());
        CallCopy<Source, Target> callback3 = (src, tgt) -> tgt.setComputed(
                "Done");

        // Act
        callback1.call(source, target);
        callback2.call(source, target);
        callback3.call(source, target);

        // Assert
        assertEquals("test", target.getName());
        assertEquals(100, target.getValue());
        assertEquals("Done", target.getComputed());
    }

    @Test
    void testCallCopy_NullHandling_Success() {
        // Arrange
        Source source = new Source(null, 0);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            tgt.setName(src.getName() != null ? src.getName() : "default");
            tgt.setValue(src.getValue());
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals("default", target.getName());
        assertEquals(0, target.getValue());
    }

    @Test
    void testCallCopy_FunctionalInterfaceAnnotation_Success() {
        // Verify that CallCopy can be used as functional interface
        // Arrange
        CallCopy<String, StringBuilder> callback =
                (src, tgt) -> tgt.append(src);

        // Act
        StringBuilder sb = new StringBuilder();
        callback.call("Hello", sb);

        // Assert
        assertEquals("Hello", sb.toString());
    }

    @Test
    void testCallCopy_GenericTypes_Success() {
        // Test with different generic types
        // Arrange
        CallCopy<Integer, String[]> callback = (src, tgt) -> tgt[0] =
                String.valueOf(src);
        String[] target = new String[1];

        // Act
        callback.call(42, target);

        // Assert
        assertEquals("42", target[0]);
    }

    @Test
    void testCallCopy_ComplexTransformation_Success() {
        // Arrange
        Source source = new Source("Product Name", 1500);
        Target target = new Target();

        CallCopy<Source, Target> callback = (src, tgt) -> {
            // Complex business logic
            String formattedName = src.getName().toLowerCase().replace(" ",
                    "_");
            int discountedValue = (int) (src.getValue() * 0.9);
            String summary = String.format("%s: $%d (10%% off)",
                    formattedName, discountedValue);

            tgt.setName(formattedName);
            tgt.setValue(discountedValue);
            tgt.setComputed(summary);
        };

        // Act
        callback.call(source, target);

        // Assert
        assertEquals("product_name", target.getName());
        assertEquals(1350, target.getValue());
        assertTrue(target.getComputed().contains("product_name"));
        assertTrue(target.getComputed().contains("$1350"));
    }

    static class Source {
        private String name;
        private int value;

        public Source(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    static class Target {
        private String name;
        private int value;
        private String computed;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getComputed() {
            return computed;
        }

        public void setComputed(String computed) {
            this.computed = computed;
        }
    }
}
