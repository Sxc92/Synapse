package com.indigo.core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * @author 史偕成
 */
public class CollectionUtils {
    private CollectionUtils() { throw new IllegalStateException("Utility class"); }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    public static <T> List<T> collectionToList(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : new ArrayList<>(collection);
    }
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null) return Collections.emptyList();
        return Arrays.asList(array);
    }
    public static <T> Set<T> collectionToSet(Collection<T> collection) {
        return collection == null ? Collections.emptySet() : new HashSet<>(collection);
    }
    public static <T> Set<T> arrayToSet(T[] array) {
        if (array == null) return Collections.emptySet();
        return new HashSet<>(Arrays.asList(array));
    }
    public static <T, R> List<R> mapToList(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (collection == null || mapper == null) return Collections.emptyList();
        return collection.stream().map(mapper).collect(Collectors.toList());
    }
    public static <T> List<T> filterToList(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return Collections.emptyList();
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }
    public static <T> Set<T> filterToSet(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return Collections.emptySet();
        return collection.stream().filter(predicate).collect(Collectors.toSet());
    }

    public static <T> long count(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return 0L;
        return collection.stream().filter(predicate).count();
    }
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return false;
        return collection.stream().anyMatch(predicate);
    }
    public static <T> boolean allMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return false;
        return collection.stream().allMatch(predicate);
    }
    public static <T> boolean noneMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) return true;
        return collection.stream().noneMatch(predicate);
    }
    public static <T> List<T> distinct(Collection<T> collection) {
        if (collection == null) return Collections.emptyList();
        return collection.stream().distinct().collect(Collectors.toList());
    }
    public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
        if (collection == null || comparator == null) return Collections.emptyList();
        return collection.stream().sorted(comparator).collect(Collectors.toList());
    }
    public static boolean isMapEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    public static boolean isMapNotEmpty(Map<?, ?> map) {
        return !isMapEmpty(map);
    }
    public static <T> List<T> toList(T[] array) {
        if (array == null) return Collections.emptyList();
        return Arrays.asList(array);
    }
    public static <T> Set<T> toSet(T[] array) {
        if (array == null) return Collections.emptySet();
        return new HashSet<>(Arrays.asList(array));
    }
    public static <K, V> Map<K, V> toMap(List<K> keys, List<V> values) {
        if (keys == null || values == null) return Collections.emptyMap();
        Map<K, V> map = new HashMap<>();
        int size = Math.min(keys.size(), values.size());
        for (int i = 0; i < size; i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
    public static <T, K> Map<K, T> toMap(List<T> list, Function<? super T, ? extends K> keyMapper) {
        if (list == null || keyMapper == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }
    public static <T, K> Map<K, T> toMap(List<T> list, Function<? super T, ? extends K> keyMapper, BinaryOperator<T> mergeFunction) {
        if (list == null || keyMapper == null || mergeFunction == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity(), mergeFunction));
    }
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        if (list == null || keyMapper == null || valueMapper == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper, BinaryOperator<V> mergeFunction) {
        if (list == null || keyMapper == null || valueMapper == null || mergeFunction == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }
    public static <T> void add(List<T> list, T element) {
        if (list != null && element != null) list.add(element);
    }
    public static <T> void addAll(List<T> list, List<T> elements) {
        if (list != null && elements != null) list.addAll(elements);
    }
    public static <T> void remove(List<T> list, T element) {
        if (list != null && element != null) list.remove(element);
    }
    public static <T> void removeAll(List<T> list, List<T> elements) {
        if (list != null && elements != null) list.removeAll(elements);
    }
    public static <T> void retainAll(List<T> list, List<T> elements) {
        if (list != null && elements != null) list.retainAll(elements);
    }
    public static <T> void clear(List<T> list) {
        if (list != null) list.clear();
    }
    public static <T> Optional<T> find(List<T> list, Predicate<? super T> predicate) {
        if (list == null || predicate == null) return Optional.empty();
        return list.stream().filter(predicate).findFirst();
    }
    public static <T> List<T> findAll(List<T> list, Predicate<? super T> predicate) {
        if (list == null || predicate == null) return Collections.emptyList();
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
    public static <T> Optional<T> findLast(List<T> list) {
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(list.size() - 1));
    }
    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        if (list == null || predicate == null) return Collections.emptyList();
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
    public static <T> List<T> filterToNew(List<T> list, Predicate<? super T> predicate) {
        return filter(list, predicate);
    }
    public static <K, V> Map<K, V> filterMap(Map<K, V> map, BiFunction<? super K, ? super V, Boolean> predicate) {
        if (map == null || predicate == null) return Collections.emptyMap();
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.apply(entry.getKey(), entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    public static <K, V> Map<K, V> filterMapToNew(Map<K, V> map, BiFunction<? super K, ? super V, Boolean> predicate) {
        return filterMap(map, predicate);
    }
    public static <T, R> List<R> transform(List<T> list, Function<? super T, ? extends R> mapper) {
        if (list == null || mapper == null) return Collections.emptyList();
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    public static <T, R> List<R> transformToNew(List<T> list, Function<? super T, ? extends R> mapper) {
        return transform(list, mapper);
    }
    public static <K, V, R> Map<K, R> transformMapValues(Map<K, V> map, Function<? super V, ? extends R> mapper) {
        if (map == null || mapper == null) return Collections.emptyMap();
        Map<K, R> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.put(entry.getKey(), mapper.apply(entry.getValue()));
        }
        return result;
    }
    public static <K, V, R> Map<K, R> transformMapValuesToNew(Map<K, V> map, Function<? super V, ? extends R> mapper) {
        return transformMapValues(map, mapper);
    }
    public static <T, K> Map<K, Long> countBy(List<T> list, Function<? super T, ? extends K> classifier) {
        if (list == null || classifier == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.groupingBy(classifier, Collectors.counting()));
    }
    public static <T, K> Map<K, List<T>> groupBy(List<T> list, Function<? super T, ? extends K> classifier) {
        if (list == null || classifier == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.groupingBy(classifier));
    }
    public static <T, K, V> Map<K, List<V>> groupBy(List<T> list, Function<? super T, ? extends K> classifier, Function<? super T, ? extends V> valueMapper) {
        if (list == null || classifier == null || valueMapper == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.groupingBy(classifier, Collectors.mapping(valueMapper, Collectors.toList())));
    }
    public static <T, K, A, D> Map<K, D> groupBy(List<T> list, Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        if (list == null || classifier == null || downstream == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.groupingBy(classifier, downstream));
    }
    public static <T> List<T> toListFromArray(T[] array) {
        if (array == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(array);
    }
    public static <T> Set<T> toSetFromArray(T[] array) {
        if (array == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(array));
    }
    public static <T> List<T> toList(Collection<T> collection) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(collection);
    }
    public static <T> Set<T> toSet(Collection<T> collection) {
        if (collection == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(collection);
    }
    public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return null;
        }
        return collection.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * 查找集合中的第一个元素
     *
     * @param collection 要查找的集合
     * @param <T> 元素类型
     * @return 第一个元素的Optional包装
     */
    public static <T> Optional<T> findFirstOptional(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(collection.iterator().next());
    }

    /**
     * 查找集合中满足条件的第一个元素
     *
     * @param collection 要查找的集合
     * @param predicate 条件判断函数
     * @param <T> 元素类型
     * @return 满足条件的第一个元素的Optional包装
     */
    public static <T> Optional<T> findFirstOptional(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection == null || predicate == null) {
            return Optional.empty();
        }
        return collection.stream().filter(predicate).findFirst();
    }



} 