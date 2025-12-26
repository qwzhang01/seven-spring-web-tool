package io.github.qwzhang01.wtool.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.qwzhang01.reflection.objectmapper.ObjectToMapConverter;
import io.github.qwzhang01.wtool.domain.CallCopy;
import io.github.qwzhang01.wtool.exception.BeanCopyException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for JavaBean operations including property copying,
 * list transformation, and object comparison.
 * Provides convenient methods for common bean manipulation tasks.
 *
 * @author avinzhang
 */
public class BeanUtil {
    /**
     * Safely compares two objects for equality, handling null values.
     *
     * @param object1 the first object to compare
     * @param object2 the second object to compare
     * @return true if both objects are equal or both are null, false otherwise
     */
    public static boolean equals(Object object1, Object object2) {
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 != null) {
            return object1.equals(object2);
        }
        return false;
    }

    /**
     * Copies properties from source object to target object.
     *
     * @param source the source object to copy from
     * @param target the target object to copy to
     * @param <S>    the type of the source object
     * @param <T>    the type of the target object
     */
    public static <S, T> void setProperties(S source, T target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * Creates a new instance of the target type and copies properties from the source object.
     *
     * @param source     the source object to copy from
     * @param targetType the class of the target object
     * @param <S>        the type of the source object
     * @param <T>        the type of the target object
     * @return a new instance of the target type with copied properties
     * @throws BeanCopyException if an error occurs during object creation or property copying
     */
    public static <S, T> T copyProperties(S source, Class<T> targetType) {
        try {
            Constructor<T> target = targetType.getConstructor();
            T result = target.newInstance();
            BeanUtils.copyProperties(source, result);
            return result;
        } catch (Exception e) {
            throw new BeanCopyException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Creates a new instance of the target type, copies properties from the source object,
     * and executes a callback for custom transformation logic.
     *
     * @param source     the source object to copy from
     * @param targetType the class of the target object
     * @param callback   callback function executed after property copying for custom transformations
     * @param <S>        the type of the source object
     * @param <T>        the type of the target object
     * @return a new instance of the target type with copied and transformed properties
     * @throws BeanCopyException if an error occurs during object creation or property copying
     */
    public static <S, T> T copyProperties(S source, Class<T> targetType,
                                          CallCopy<S, T> callback) {
        try {
            Constructor<T> target = targetType.getConstructor();
            T result = target.newInstance();
            BeanUtils.copyProperties(source, result);
            callback.call(source, result);
            return result;
        } catch (Exception e) {
            throw new BeanCopyException(e.getLocalizedMessage(), e);
        }
    }


    /**
     * Converts a collection of source objects to a list of target objects
     * by copying properties from each source object.
     *
     * @param collection the source collection
     * @param targetType the class of the target objects
     * @param <S>        the type of the source objects
     * @param <T>        the type of the target objects
     * @return a list of target objects with copied properties, or null if input is null
     */
    public static <S, T> List<T> copyToList(Collection<S> collection,
                                            Class<T> targetType) {
        if (null == collection) {
            return null;
        }
        if (collection.isEmpty()) {
            return new ArrayList<>(0);
        }

        return collection.stream().map((source) -> copyProperties(source,
                targetType)).collect(Collectors.toList());
    }

    /**
     * Converts a collection of source objects to a list of target objects
     * by copying properties and applying custom transformation logic.
     *
     * @param collection the source collection
     * @param targetType the class of the target objects
     * @param callback   callback function executed for each object after property copying
     * @param <T>        the type of the target objects
     * @param <S>        the type of the source objects
     * @return a list of target objects with copied and transformed properties, or null if input is null
     */
    public static <T, S> List<T> copyToList(Collection<S> collection,
                                            Class<T> targetType, CallCopy<S,
            T> callback) {
        if (null == collection) {
            return null;
        }
        if (collection.isEmpty()) {
            return new ArrayList<>(0);
        }

        return collection.stream().map((source) -> {
            T result = copyProperties(source, targetType);
            callback.call(source, result);
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * Converts a MyBatis-Plus Page of source objects to a Page of target objects
     * by copying properties from each record.
     *
     * @param page       the source page
     * @param targetType the class of the target objects
     * @param <S>        the type of the source objects
     * @param <T>        the type of the target objects
     * @return a page of target objects with copied properties
     */
    public static <S, T> Page<T> copyToPage(Page<S> page, Class<T> targetType) {
        Page<T> target = new Page<>();
        BeanUtils.copyProperties(page, target);
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            target.setRecords(copyToList(page.getRecords(), targetType));
        }
        return target;
    }

    /**
     * Converts a MyBatis-Plus Page of source objects to a Page of target objects
     * by copying properties and applying custom transformation logic.
     *
     * @param page       the source page
     * @param targetType the class of the target objects
     * @param callback   callback function executed for each record after property copying
     * @param <S>        the type of the source objects
     * @param <T>        the type of the target objects
     * @return a page of target objects with copied and transformed properties
     */
    public static <S, T> Page<T> copyToPage(Page<S> page, Class<T> targetType
            , CallCopy<S, T> callback) {
        Page<T> target = new Page<>();
        BeanUtils.copyProperties(page, target);
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            target.setRecords(copyToList(page.getRecords(), targetType,
                    callback));
        }
        return target;
    }

    /**
     * Removes elements from the list if the condition between the element
     * and its next element is not satisfied.
     *
     * @param list      the list to modify
     * @param condition the condition to test between consecutive elements
     * @param <T>       the type of elements in the list
     */
    public static <T> void removeIfNextFails(List<T> list,
                                             BiPredicate<T, T> condition) {
        if (list.size() < 2) {
            return;
        }

        IntStream.range(0, list.size() - 1).boxed().sorted(Comparator.reverseOrder()).filter(i -> !condition.test(list.get(i), list.get(i + 1))).forEach(i -> list.remove(i.intValue()));

    }

    /**
     * Converts an object to a Map representation.
     * The map keys are the object's property names, and values are the property values.
     *
     * @param obj the object to convert
     * @return a map representation of the object, or an empty map if obj is null
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        ObjectToMapConverter converter = new ObjectToMapConverter();
        return converter.toMap(obj);
    }
}