package com.indigo.core.utils;

import com.indigo.core.exception.MapException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Map 工具类
 * 提供常用的 Map 操作工具方法
 * 
 * @author 史偕成
 * @date 2025/04/24 22:30
 **/
@Slf4j
public class MapUtils {

    private MapUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return CollectionUtils.isEmpty(map);
    }

    /**
     * 判断 Map 是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 创建 HashMap
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 创建 HashMap 并添加初始键值对
     */
    public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 创建 HashMap 并添加初始键值对
     */
    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
        return new HashMap<>(map);
    }

    /**
     * 创建 LinkedHashMap
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * 创建 LinkedHashMap 并添加初始键值对
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(K key, V value) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 创建 LinkedHashMap 并添加初始键值对
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<K, V> map) {
        return new LinkedHashMap<>(map);
    }

    /**
     * 创建 TreeMap
     */
    public static <K extends Comparable<K>, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * 创建 TreeMap 并添加初始键值对
     */
    public static <K extends Comparable<K>, V> TreeMap<K, V> newTreeMap(K key, V value) {
        TreeMap<K, V> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 创建 TreeMap 并添加初始键值对
     */
    public static <K extends Comparable<K>, V> TreeMap<K, V> newTreeMap(Map<K, V> map) {
        return new TreeMap<>(map);
    }

    /**
     * 获取 Map 中的值，如果不存在则返回默认值
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        return map != null ? map.getOrDefault(key, defaultValue) : defaultValue;
    }

    /**
     * 获取 Map 中的值，如果不存在则抛出异常
     */
    public static <K, V> V getRequired(Map<K, V> map, K key) {
        if (map == null) {
            throw new MapException("Map cannot be null");
        }
        V value = map.get(key);
        if (value == null) {
            throw new MapException("Key not found: " + key);
        }
        return value;
    }

    /**
     * 获取 Map 中的值，如果不存在则返回 null
     */
    public static <K, V> V get(Map<K, V> map, K key) {
        return map != null ? map.get(key) : null;
    }

    /**
     * 获取 Map 中的值，如果不存在则返回 Optional.empty()
     */
    public static <K, V> Optional<V> getOptional(Map<K, V> map, K key) {
        return map != null ? Optional.ofNullable(map.get(key)) : Optional.empty();
    }

    /**
     * 获取 Map 中的所有键
     */
    public static <K, V> Set<K> keys(Map<K, V> map) {
        return map != null ? map.keySet() : Collections.emptySet();
    }

    /**
     * 获取 Map 中的所有值
     */
    public static <K, V> Collection<V> values(Map<K, V> map) {
        return map != null ? map.values() : Collections.emptyList();
    }

    /**
     * 获取 Map 中的所有键值对
     */
    public static <K, V> Set<Map.Entry<K, V>> entries(Map<K, V> map) {
        return map != null ? map.entrySet() : Collections.emptySet();
    }

    /**
     * 将 Map 转换为 List
     */
    public static <K, V> List<V> toList(Map<K, V> map) {
        return map != null ? new ArrayList<>(map.values()) : Collections.emptyList();
    }

    /**
     * 将 Map 转换为 List，并应用转换函数
     */
    public static <K, V, R> List<R> toList(Map<K, V> map, Function<V, R> mapper) {
        if (map == null) {
            return Collections.emptyList();
        }
        return map.values().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 将 Map 转换为 Set
     */
    public static <K, V> Set<V> toSet(Map<K, V> map) {
        return map != null ? new HashSet<>(map.values()) : Collections.emptySet();
    }

    /**
     * 将 Map 转换为 Set，并应用转换函数
     */
    public static <K, V, R> Set<R> toSet(Map<K, V> map, Function<V, R> mapper) {
        if (map == null) {
            return Collections.emptySet();
        }
        return map.values().stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    /**
     * 将 Map 转换为另一个 Map，应用键值转换函数
     */
    public static <K1, V1, K2, V2> Map<K2, V2> transform(
            Map<K1, V1> map,
            Function<K1, K2> keyMapper,
            Function<V1, V2> valueMapper) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> keyMapper.apply(e.getKey()),
                        e -> valueMapper.apply(e.getValue())
                ));
    }

    /**
     * 将 Map 转换为另一个 Map，应用键值转换函数，处理键冲突
     */
    public static <K1, V1, K2, V2> Map<K2, V2> transform(
            Map<K1, V1> map,
            Function<K1, K2> keyMapper,
            Function<V1, V2> valueMapper,
            BiFunction<V2, V2, V2> mergeFunction) {
        if (map == null) {
            return Collections.emptyMap();
        }
        BinaryOperator<V2> binaryOperator = mergeFunction::apply;
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> keyMapper.apply(e.getKey()),
                        e -> valueMapper.apply(e.getValue()),
                        binaryOperator
                ));
    }

    /**
     * 过滤 Map，保留满足条件的键值对
     */
    public static <K, V> Map<K, V> filter(Map<K, V> map, Predicate<Map.Entry<K, V>> predicate) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .filter(predicate)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * 过滤 Map，保留满足条件的键值对，处理键冲突
     */
    public static <K, V> Map<K, V> filter(
            Map<K, V> map,
            Predicate<Map.Entry<K, V>> predicate,
            BiFunction<V, V, V> mergeFunction) {
        if (map == null) {
            return Collections.emptyMap();
        }
        BinaryOperator<V> binaryOperator = mergeFunction::apply;
        return map.entrySet().stream()
                .filter(predicate)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        binaryOperator
                ));
    }

    /**
     * 过滤 Map 的键，保留满足条件的键值对
     */
    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, Predicate<K> predicate) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * 过滤 Map 的值，保留满足条件的键值对
     */
    public static <K, V> Map<K, V> filterValues(Map<K, V> map, Predicate<V> predicate) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * 合并两个 Map
     */
    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        if (map1 == null) {
            return map2 != null ? new HashMap<>(map2) : Collections.emptyMap();
        }
        if (map2 == null) {
            return new HashMap<>(map1);
        }
        Map<K, V> result = new HashMap<>(map1);
        result.putAll(map2);
        return result;
    }

    /**
     * 合并两个 Map，处理键冲突
     */
    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2, BiFunction<V, V, V> mergeFunction) {
        if (map1 == null) {
            return map2 != null ? new HashMap<>(map2) : Collections.emptyMap();
        }
        if (map2 == null) {
            return new HashMap<>(map1);
        }
        Map<K, V> result = new HashMap<>(map1);
        map2.forEach((key, value) -> result.merge(key, value, mergeFunction));
        return result;
    }

    /**
     * 合并多个 Map
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mergeAll(Map<K, V>... maps) {
        if (maps == null) return Collections.emptyMap();
        Map<K, V> result = new HashMap<>();
        for (Map<K, V> map : maps) {
            if (map != null) {
                result.putAll(map);
            }
        }
        return result;
    }

    /**
     * 合并多个 Map,使用指定的合并函数处理键冲突
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mergeAllWith(BiFunction<? super V, ? super V, ? extends V> mergeFunction, Map<K, V>... maps) {
        if (mergeFunction == null) throw new NullPointerException("mergeFunction 不能为空");
        if (maps == null) return Collections.emptyMap();
        Map<K, V> result = new HashMap<>();
        for (Map<K, V> map : maps) {
            if (map != null) {
                map.forEach((key, value) -> result.merge(key, value, mergeFunction));
            }
        }
        return result;
    }

    /**
     * 反转 Map 的键值对,值冲突时合并为 List
     */
    public static <K, V> Map<V, List<K>> invert(Map<K, V> map) {
        if (map == null) return Collections.emptyMap();
        Map<V, List<K>> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }
        return result;
    }

    /**
     * 反转 Map 的键值对,使用指定的转换函数和合并函数
     */
    public static <K1, V1, K2, V2> Map<V2, K2> invert(
            Map<K1, V1> map,
            Function<? super V1, ? extends V2> valueMapper,
            Function<? super K1, ? extends K2> keyMapper,
            BinaryOperator<K2> mergeFunction) {
        if (map == null || valueMapper == null || keyMapper == null || mergeFunction == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> valueMapper.apply(e.getValue()),
                        e -> keyMapper.apply(e.getKey()),
                        mergeFunction
                ));
    }

    /**
     * 快速创建可变Map（参数必须为偶数，key1, value1, key2, value2...）
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> of(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) return new HashMap<>();
        if (keyValues.length % 2 != 0) throw new IllegalArgumentException("参数必须为偶数");
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            K key = (K) keyValues[i];
            V value = (V) keyValues[i + 1];
            if (key == null) throw new NullPointerException("key 不能为 null");
            if (map.containsKey(key)) throw new IllegalArgumentException("key 重复: " + key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 快速创建不可变Map（参数必须为偶数，key1, value1, key2, value2...）
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> ofImmutable(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) return Collections.unmodifiableMap(new HashMap<>());
        if (keyValues.length % 2 != 0) throw new IllegalArgumentException("参数必须为偶数");
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            K key = (K) keyValues[i];
            V value = (V) keyValues[i + 1];
            if (key == null) throw new NullPointerException("key 不能为 null");
            if (map.containsKey(key)) throw new IllegalArgumentException("key 重复: " + key);
            map.put(key, value);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获取并强制类型转换
     */
    @SuppressWarnings("unchecked")
    public static <K, V> V getAs(Map<K, ?> map, K key, Class<V> clazz) {
        if (map == null) return null;
        Object value = map.get(key);
        if (value == null) return null;
        return (V) value;
    }

    /**
     * 获取并强制类型转换，带默认值
     */
    @SuppressWarnings("unchecked")
    public static <K, V> V getAsOrDefault(Map<K, ?> map, K key, Class<V> clazz, V defaultValue) {
        if (map == null) return defaultValue;
        Object value = map.get(key);
        if (value == null) return defaultValue;
        return (V) value;
    }

    /**
     * put
     */
    public static <K, V> V put(Map<K, V> map, K key, V value) {
        if (map == null) return null;
        return map.put(key, value);
    }

    /**
     * putIfAbsent
     */
    public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        if (map == null) return null;
        return map.putIfAbsent(key, value);
    }

    /**
     * remove
     */
    public static <K, V> V remove(Map<K, V> map, K key) {
        if (map == null) return null;
        return map.remove(key);
    }

    /**
     * removeIf
     */
    public static <K, V> boolean removeIf(Map<K, V> map, K key, V value) {
        if (map == null) return false;
        return map.remove(key, value);
    }

    /**
     * replace
     */
    public static <K, V> V replace(Map<K, V> map, K key, V value) {
        if (map == null) return null;
        return map.replace(key, value);
    }

    /**
     * replaceIf
     */
    public static <K, V> boolean replaceIf(Map<K, V> map, K key, V oldValue, V newValue) {
        if (map == null) return false;
        return map.replace(key, oldValue, newValue);
    }

    /**
     * 转换键
     */
    public static <K, V, R> Map<R, V> transformKeys(Map<K, V> map, Function<? super K, ? extends R> keyMapper) {
        if (map == null || keyMapper == null) throw new NullPointerException();
        Map<R, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            R newKey = keyMapper.apply(entry.getKey());
            if (result.containsKey(newKey)) throw new IllegalArgumentException("key 重复: " + newKey);
            result.put(newKey, entry.getValue());
        }
        return result;
    }

    /**
     * 转换值
     */
    public static <K, V, R> Map<K, R> transformValues(Map<K, V> map, Function<? super V, ? extends R> valueMapper) {
        if (map == null || valueMapper == null) throw new NullPointerException();
        Map<K, R> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.put(entry.getKey(), valueMapper.apply(entry.getValue()));
        }
        return result;
    }

    /**
     * 转换键值对
     */
    public static <K, V, RK, RV> Map<RK, RV> transformEntries(Map<K, V> map, Function<? super K, ? extends RK> keyMapper, Function<? super V, ? extends RV> valueMapper) {
        if (map == null || keyMapper == null || valueMapper == null) throw new NullPointerException();
        Map<RK, RV> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            RK newKey = keyMapper.apply(entry.getKey());
            if (result.containsKey(newKey)) throw new IllegalArgumentException("key 重复: " + newKey);
            result.put(newKey, valueMapper.apply(entry.getValue()));
        }
        return result;
    }

    /**
     * 转换键值对到目标Map
     */
    public static <K, V, RK, RV> void transformEntriesTo(Map<K, V> map, Map<RK, RV> target, Function<? super K, ? extends RK> keyMapper, Function<? super V, ? extends RV> valueMapper) {
        if (map == null || target == null || keyMapper == null || valueMapper == null) throw new NullPointerException();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            RK newKey = keyMapper.apply(entry.getKey());
            if (target.containsKey(newKey)) throw new IllegalArgumentException("key 重复: " + newKey);
            target.put(newKey, valueMapper.apply(entry.getValue()));
        }
    }

    /**
     * 过滤键值对
     */
    public static <K, V> Map<K, V> filterEntries(Map<K, V> map, BiFunction<? super K, ? super V, Boolean> predicate) {
        if (map == null || predicate == null) throw new NullPointerException();
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.apply(entry.getKey(), entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 过滤键值对到目标Map
     */
    public static <K, V> void filterEntriesTo(Map<K, V> map, Map<K, V> target, BiFunction<? super K, ? super V, Boolean> predicate) {
        if (map == null || target == null || predicate == null) throw new NullPointerException();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.apply(entry.getKey(), entry.getValue())) {
                target.put(entry.getKey(), entry.getValue());
            }
        }
    }
} 