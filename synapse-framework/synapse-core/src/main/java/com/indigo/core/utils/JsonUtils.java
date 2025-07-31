package com.indigo.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.indigo.core.exception.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

/**
 * JSON 工具类，基于 Jackson
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Slf4j
@Component
public class JsonUtils {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    public JsonUtils(@Qualifier("synapseObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // 配置 ObjectMapper
        configureObjectMapper();
    }
    
    private void configureObjectMapper() {
        // 注册 JavaTimeModule，以支持 Java 8 日期时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(javaTimeModule);
        
        // 设置日期格式化
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略空值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许空对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 美化输出
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }
    
    /**
     * 获取 ObjectMapper 实例
     *
     * @return ObjectMapper 实例
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 要转换的对象
     * @return JSON 字符串
     */
    public String toJson(Object obj) {
        if (obj == null) {
            throw new JsonException("json.error.object.null");
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Convert object to JSON string failed", e);
            throw new JsonException("json.error.serialize", e);
        }
    }
    
    /**
     * 将对象转换为格式化的 JSON 字符串
     *
     * @param obj 要转换的对象
     * @return 格式化的 JSON 字符串
     */
    public String toPrettyJson(Object obj) {
        if (obj == null) {
            throw new JsonException("json.error.object.null");
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Convert object to pretty JSON string failed", e);
            throw new JsonException("json.error.serialize", e);
        }
    }
    
    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public <T> T fromJsonToObject(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            throw new JsonException("json.error.string.empty");
        }
        if (clazz == null) {
            throw new JsonException("json.error.class.null");
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Convert JSON string to object failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 将 JSON 字符串转换为泛型对象
     *
     * @param json JSON 字符串
     * @param typeReference 类型引用
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public <T> T fromJsonToGeneric(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasText(json)) {
            throw new JsonException("json.error.string.empty");
        }
        if (typeReference == null) {
            throw new JsonException("json.error.type.reference.null");
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Convert JSON string to object with type reference failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 将 JSON 字符串转换为 List
     *
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的 List
     */
    public <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            throw new JsonException("json.error.string.empty");
        }
        if (clazz == null) {
            throw new JsonException("json.error.class.null");
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("Convert JSON string to list failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 将 JSON 字符串转换为 Map
     *
     * @param json JSON 字符串
     * @param keyClass 键类型
     * @param valueClass 值类型
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 转换后的 Map
     */
    public <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (!StringUtils.hasText(json)) {
            throw new JsonException("json.error.string.empty");
        }
        if (keyClass == null) {
            throw new JsonException("json.error.key.class.null");
        }
        if (valueClass == null) {
            throw new JsonException("json.error.value.class.null");
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("Convert JSON string to map failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 将输入流转换为对象
     *
     * @param inputStream 输入流
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public <T> T fromJsonStream(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            throw new JsonException("json.error.input.stream.null");
        }
        if (clazz == null) {
            throw new JsonException("json.error.class.null");
        }
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            log.error("Convert input stream to object failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 将对象转换为字节数组
     *
     * @param object 要转换的对象
     * @return 字节数组
     */
    public byte[] toBytes(Object object) {
        if (object == null) {
            throw new JsonException("json.error.object.null");
        }
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("Convert object to bytes error", e);
            throw new JsonException("json.error.serialize", e);
        }
    }
    
    /**
     * 将字节数组转换为对象
     *
     * @param bytes 字节数组
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public <T> T fromJsonBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new JsonException("json.error.bytes.null");
        }
        if (clazz == null) {
            throw new JsonException("json.error.class.null");
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("Convert bytes to object failed", e);
            throw new JsonException("json.error.deserialize", e);
        }
    }
    
    /**
     * 判断字符串是否为有效的 JSON
     *
     * @param json JSON 字符串
     * @return 是否为有效的 JSON
     */
    public boolean isValidJson(String json) {
        if (!StringUtils.hasText(json)) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    /**
     * 将对象转换为 Map
     *
     * @param object 要转换的对象
     * @return 转换后的 Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap(Object object) {
        if (object == null) {
            throw new JsonException("json.error.object.null");
        }
        return objectMapper.convertValue(object, Map.class);
    }
    
    /**
     * 将 Map 转换为对象
     *
     * @param map Map
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            throw new JsonException("json.error.map.null");
        }
        if (clazz == null) {
            throw new JsonException("json.error.class.null");
        }
        return objectMapper.convertValue(map, clazz);
    }
    
    /**
     * 将 JSON 字符串转换为对象（返回 Optional）
     *
     * @param json JSON 字符串
     * @param typeReference 类型引用
     * @param <T> 目标类型
     * @return Optional 对象
     */
    public <T> Optional<T> fromJsonOptionalGeneric(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasText(json)) {
            return Optional.empty();
        }
        if (typeReference == null) {
            throw new JsonException("json.error.type.reference.null");
        }
        try {
            return Optional.ofNullable(objectMapper.readValue(json, typeReference));
        } catch (JsonProcessingException e) {
            log.error("Convert JSON string to optional object failed", e);
            return Optional.empty();
        }
    }
    
    /**
     * 将时间戳转换为 LocalDateTime
     * Convert timestamp to LocalDateTime
     *
     * @param timestamp 时间戳 / Timestamp
     * @return LocalDateTime 对象 / LocalDateTime object
     */
    public LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 将 LocalDateTime 转换为时间戳
     * Convert LocalDateTime to timestamp
     *
     * @param dateTime LocalDateTime 对象 / LocalDateTime object
     * @return 时间戳 / Timestamp
     */
    public long localDateTimeToTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将 UUID 转换为字符串
     * Convert UUID to string
     *
     * @param uuid UUID 对象 / UUID object
     * @return 字符串 / String
     */
    public String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    /**
     * 将字符串转换为 UUID
     * Convert string to UUID
     *
     * @param uuidString UUID 字符串 / UUID string
     * @return UUID 对象 / UUID object
     */
    public UUID stringToUuid(String uuidString) {
        return uuidString != null ? UUID.fromString(uuidString) : null;
    }

    // ===============================
    // 静态方法 - 用于无法注入Bean的场景
    // Static methods - for scenarios where Bean injection is not possible
    // ===============================
    
    /**
     * 静态的 ObjectMapper 实例
     * 用于静态方法调用
     */
    private static final ObjectMapper STATIC_MAPPER;
    
    static {
        STATIC_MAPPER = new ObjectMapper();
        // 配置静态 ObjectMapper
        configureStaticObjectMapper();
    }
    
    /**
     * 配置静态 ObjectMapper
     */
    private static void configureStaticObjectMapper() {
        // 注册 JavaTimeModule，以支持 Java 8 日期时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        STATIC_MAPPER.registerModule(javaTimeModule);
        
        // 设置日期格式化
        STATIC_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略空值
        STATIC_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 忽略未知属性
        STATIC_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许空对象
        STATIC_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 美化输出
        STATIC_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
    }
    
    /**
     * 静态方法：将对象转换为 JSON 字符串
     * 用于无法注入Bean的场景（如静态方法、过滤器等）
     *
     * @param obj 要转换的对象
     * @return JSON 字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return STATIC_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Static convert object to JSON string failed", e);
            return "{\"error\":\"JSON序列化失败\"}";
        }
    }
    
    /**
     * 静态方法：将 JSON 字符串转换为对象
     * 用于无法注入Bean的场景（如静态方法、过滤器等）
     *
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        try {
            return STATIC_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Static convert JSON string to object failed", e);
            return null;
        }
    }
    
    /**
     * 静态方法：判断字符串是否为有效的 JSON
     * 用于无法注入Bean的场景（如静态方法、过滤器等）
     *
     * @param json JSON 字符串
     * @return 是否为有效的 JSON
     */
    public static boolean isValidJsonStatic(String json) {
        if (!StringUtils.hasText(json)) {
            return false;
        }
        try {
            STATIC_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
} 