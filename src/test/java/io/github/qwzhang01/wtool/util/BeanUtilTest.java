package io.github.qwzhang01.wtool.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.qwzhang01.wtool.exception.BeanCopyException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test cases for BeanUtil class.
 * Tests cover property copying, list transformations, page conversions,
 * object comparisons, and Map conversions.
 *
 * @author avinzhang
 */
class BeanUtilTest {

    @Test
    void testSetProperties_Success() {
        // Arrange
        SourceUser source = new SourceUser(1L, "john", "john@example.com", 25);
        TargetUserDTO target = new TargetUserDTO();

        // Act
        BeanUtil.setProperties(source, target);

        // Assert
        assertEquals(source.getId(), target.getId());
        assertEquals(source.getUsername(), target.getUsername());
        assertEquals(source.getEmail(), target.getEmail());
        assertEquals(source.getAge(), target.getAge());
    }

    @Test
    void testCopyProperties_WithoutCallback_Success() {
        // Arrange
        SourceUser source = new SourceUser(1L, "john", "john@example.com", 25);

        // Act
        TargetUserDTO result = BeanUtil.copyProperties(source,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(source.getId(), result.getId());
        assertEquals(source.getUsername(), result.getUsername());
        assertEquals(source.getEmail(), result.getEmail());
        assertEquals(source.getAge(), result.getAge());
        assertNull(result.getFullName());
    }

    @Test
    void testCopyProperties_WithCallback_Success() {
        // Arrange
        SourceUser source = new SourceUser(1L, "john", "john@example.com", 25);

        // Act
        TargetUserDTO result = BeanUtil.copyProperties(source,
                TargetUserDTO.class,
                (src, tgt) -> tgt.setFullName(src.getUsername() + "_fullname"));

        // Assert
        assertNotNull(result);
        assertEquals(source.getId(), result.getId());
        assertEquals(source.getUsername(), result.getUsername());
        assertEquals("john_fullname", result.getFullName());
    }

    @Test
    void testCopyProperties_NoDefaultConstructor_ThrowsException() {
        // Arrange
        SourceUser source = new SourceUser(1L, "john", "john@example.com", 25);

        // Act & Assert
        assertThrows(BeanCopyException.class, () ->
                BeanUtil.copyProperties(source, NoDefaultConstructor.class));
    }

    @Test
    void testCopyToList_NullInput_ReturnsNull() {
        // Act
        List<TargetUserDTO> result = BeanUtil.copyToList(null,
                TargetUserDTO.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testCopyToList_EmptyInput_ReturnsEmptyList() {
        // Arrange
        List<SourceUser> emptyList = new ArrayList<>();

        // Act
        List<TargetUserDTO> result = BeanUtil.copyToList(emptyList,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCopyToList_WithoutCallback_Success() {
        // Arrange
        List<SourceUser> sourceList = Arrays.asList(
                new SourceUser(1L, "john", "john@example.com", 25),
                new SourceUser(2L, "jane", "jane@example.com", 30),
                new SourceUser(3L, "bob", "bob@example.com", 35)
        );

        // Act
        List<TargetUserDTO> result = BeanUtil.copyToList(sourceList,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("john", result.get(0).getUsername());
        assertEquals("jane", result.get(1).getUsername());
        assertEquals("bob", result.get(2).getUsername());
    }

    @Test
    void testCopyToList_WithCallback_Success() {
        // Arrange
        List<SourceUser> sourceList = Arrays.asList(
                new SourceUser(1L, "john", "john@example.com", 25),
                new SourceUser(2L, "jane", "jane@example.com", 30)
        );

        // Act
        List<TargetUserDTO> result = BeanUtil.copyToList(sourceList,
                TargetUserDTO.class,
                (src, tgt) -> tgt.setFullName(src.getUsername().toUpperCase()));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("JOHN", result.get(0).getFullName());
        assertEquals("JANE", result.get(1).getFullName());
    }

    @Test
    void testCopyToPage_WithoutCallback_Success() {
        // Arrange
        Page<SourceUser> sourcePage = new Page<>(1, 10);
        sourcePage.setTotal(2);
        sourcePage.setRecords(Arrays.asList(
                new SourceUser(1L, "john", "john@example.com", 25),
                new SourceUser(2L, "jane", "jane@example.com", 30)
        ));

        // Act
        Page<TargetUserDTO> result = BeanUtil.copyToPage(sourcePage,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRecords().size());
        assertEquals("john", result.getRecords().get(0).getUsername());
        assertEquals("jane", result.getRecords().get(1).getUsername());
    }

    @Test
    void testCopyToPage_WithCallback_Success() {
        // Arrange
        Page<SourceUser> sourcePage = new Page<>(1, 10);
        sourcePage.setTotal(2);
        sourcePage.setRecords(Arrays.asList(
                new SourceUser(1L, "john", "john@example.com", 25),
                new SourceUser(2L, "jane", "jane@example.com", 30)
        ));

        // Act
        Page<TargetUserDTO> result = BeanUtil.copyToPage(sourcePage,
                TargetUserDTO.class,
                (src, tgt) -> tgt.setFullName("Mr/Ms " + src.getUsername()));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals("Mr/Ms john", result.getRecords().get(0).getFullName());
        assertEquals("Mr/Ms jane", result.getRecords().get(1).getFullName());
    }

    @Test
    void testCopyToPage_EmptyRecords_Success() {
        // Arrange
        Page<SourceUser> sourcePage = new Page<>(1, 10);
        sourcePage.setTotal(0);
        sourcePage.setRecords(new ArrayList<>());

        // Act
        Page<TargetUserDTO> result = BeanUtil.copyToPage(sourcePage,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    void testCopyToPage_NullRecords_Success() {
        // Arrange
        Page<SourceUser> sourcePage = new Page<>(1, 10);
        sourcePage.setTotal(0);
        sourcePage.setRecords(null);

        // Act
        Page<TargetUserDTO> result = BeanUtil.copyToPage(sourcePage,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertNull(result.getRecords());
    }

    @Test
    void testRemoveIfNextFails_Success() {
        // Arrange
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 4, 5, 7, 10));

        // Act - Remove elements where next is not consecutive
        BeanUtil.removeIfNextFails(list, (a, b) -> b - a == 1);

        // Assert
        assertEquals(Arrays.asList(10), list);
    }

    @Test
    void testRemoveIfNextFails_SingleElement_NoChange() {
        // Arrange
        List<Integer> list = new ArrayList<>(Collections.singletonList(1));

        // Act
        BeanUtil.removeIfNextFails(list, (a, b) -> b - a == 1);

        // Assert
        assertEquals(Collections.singletonList(1), list);
    }

    @Test
    void testRemoveIfNextFails_EmptyList_NoChange() {
        // Arrange
        List<Integer> list = new ArrayList<>();

        // Act
        BeanUtil.removeIfNextFails(list, (a, b) -> true);

        // Assert
        assertTrue(list.isEmpty());
    }

    @Test
    void testEquals_BothNull_ReturnsTrue() {
        // Act & Assert
        assertTrue(BeanUtil.equals(null, null));
    }

    @Test
    void testEquals_FirstNull_ReturnsFalse() {
        // Act & Assert
        assertFalse(BeanUtil.equals(null, "test"));
    }

    @Test
    void testEquals_SecondNull_ReturnsFalse() {
        // Act & Assert
        assertFalse(BeanUtil.equals("test", null));
    }

    @Test
    void testEquals_SameValue_ReturnsTrue() {
        // Act & Assert
        assertTrue(BeanUtil.equals("test", "test"));
        assertTrue(BeanUtil.equals(123, 123));
    }

    @Test
    void testEquals_DifferentValue_ReturnsFalse() {
        // Act & Assert
        assertFalse(BeanUtil.equals("test1", "test2"));
        assertFalse(BeanUtil.equals(123, 456));
    }

    @Test
    void testListEquals_BothNullOrEmpty_ReturnsTrue() {
        // Act & Assert
        assertTrue(BeanUtil.listEquals(null, null));
        assertTrue(BeanUtil.listEquals(null, new ArrayList<>()));
        assertTrue(BeanUtil.listEquals(new ArrayList<>(), null));
        assertTrue(BeanUtil.listEquals(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void testListEquals_OneEmptyOneNot_ReturnsFalse() {
        // Arrange
        List<String> list1 = new ArrayList<>();
        List<String> list2 = Arrays.asList("a", "b");

        // Act & Assert
        assertFalse(BeanUtil.listEquals(list1, list2));
        assertFalse(BeanUtil.listEquals(list2, list1));
    }

    @Test
    void testListEquals_DifferentSizes_ReturnsFalse() {
        // Arrange
        List<String> list1 = Arrays.asList("a", "b");
        List<String> list2 = Arrays.asList("a", "b", "c");

        // Act & Assert
        assertFalse(BeanUtil.listEquals(list1, list2));
    }

    @Test
    void testListEquals_SameElementsSameOrder_ReturnsTrue() {
        // Arrange
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("a", "b", "c");

        // Act & Assert
        assertTrue(BeanUtil.listEquals(list1, list2));
    }

    @Test
    void testListEquals_SameElementsDifferentOrder_ReturnsTrue() {
        // Arrange
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("c", "a", "b");

        // Act & Assert
        assertTrue(BeanUtil.listEquals(list1, list2));
    }

    @Test
    void testListEquals_DifferentElements_ReturnsFalse() {
        // Arrange
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("a", "b", "d");

        // Act & Assert
        assertFalse(BeanUtil.listEquals(list1, list2));
    }

    @Test
    void testObjectToMap_NullInput_ReturnsEmptyMap() {
        // Act
        Map<String, Object> result = BeanUtil.objectToMap(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testObjectToMap_ValidObject_ReturnsMap() {
        // Arrange
        SourceUser user = new SourceUser(1L, "john", "john@example.com", 25);

        // Act
        Map<String, Object> result = BeanUtil.objectToMap(user);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Note: The actual keys depend on seven-data-security implementation
        // Basic validation that conversion occurred
        assertTrue(result.size() > 0);
    }

    @Test
    void testCopyProperties_PartialMatch_Success() {
        // Arrange - Source has more properties than target
        SourceUser source = new SourceUser(1L, "john", "john@example.com", 25);

        // Act
        TargetUserDTO result = BeanUtil.copyProperties(source,
                TargetUserDTO.class);

        // Assert - Only matching properties are copied
        assertNotNull(result);
        assertEquals(source.getId(), result.getId());
        assertEquals(source.getUsername(), result.getUsername());
    }

    @Test
    void testCopyToList_LargeList_Success() {
        // Arrange - Test with larger dataset
        List<SourceUser> sourceList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            sourceList.add(new SourceUser((long) i, "user" + i, "user" + i +
                    "@example.com", 20 + i % 50));
        }

        // Act
        List<TargetUserDTO> result = BeanUtil.copyToList(sourceList,
                TargetUserDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals(1000, result.size());
        assertEquals("user0", result.get(0).getUsername());
        assertEquals("user999", result.get(999).getUsername());
    }

    // Test domain classes
    static class SourceUser {
        private Long id;
        private String username;
        private String email;
        private Integer age;

        public SourceUser() {
        }

        public SourceUser(Long id, String username, String email, Integer age) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.age = age;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class TargetUserDTO {
        private Long id;
        private String username;
        private String email;
        private Integer age;
        private String fullName;

        public TargetUserDTO() {
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }

    static class NoDefaultConstructor {
        private String name;

        public NoDefaultConstructor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
