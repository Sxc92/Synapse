//package com.indigo.core.config.serialization;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.util.TimeZone;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JacksonConfigTest {
//
//    private JacksonConfig jacksonConfig;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        jacksonConfig = new JacksonConfig();
//        // 设置测试时区
//        ReflectionTestUtils.setField(jacksonConfig, "timezone", "Asia/Shanghai");
//        objectMapper = jacksonConfig.objectMapper();
//    }
//
//    @Test
//    void testTimezoneConfiguration() {
//        TimeZone timeZone = objectMapper.getSerializationConfig().getTimeZone();
//        assertEquals(TimeZone.getTimeZone("Asia/Shanghai"), timeZone);
//        assertEquals(ZoneId.of("Asia/Shanghai"), timeZone.toZoneId());
//    }
//
//    @Test
//    void testLocalDateTimeSerialization() throws Exception {
//        // 测试序列化
//        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 21, 14, 30, 0);
//        String json = objectMapper.writeValueAsString(dateTime);
//        assertEquals("\"2024-03-21 14:30:00\"", json);
//
//        // 测试反序列化
//        LocalDateTime deserialized = objectMapper.readValue(json, LocalDateTime.class);
//        assertEquals(dateTime, deserialized);
//    }
//
//    @Test
//    void testLocalDateSerialization() throws Exception {
//        // 测试序列化
//        LocalDate date = LocalDate.of(2024, 3, 21);
//        String json = objectMapper.writeValueAsString(date);
//        assertEquals("\"2024-03-21\"", json);
//
//        // 测试反序列化
//        LocalDate deserialized = objectMapper.readValue(json, LocalDate.class);
//        assertEquals(date, deserialized);
//    }
//
//    @Test
//    void testLocalTimeSerialization() throws Exception {
//        // 测试序列化
//        LocalTime time = LocalTime.of(14, 30, 0);
//        String json = objectMapper.writeValueAsString(time);
//        assertEquals("\"14:30:00\"", json);
//
//        // 测试反序列化
//        LocalTime deserialized = objectMapper.readValue(json, LocalTime.class);
//        assertEquals(time, deserialized);
//    }
//
//    @Test
//    void testComplexObjectSerialization() throws Exception {
//        // 创建一个包含所有时间类型的测试对象
//        TestTimeObject testObject = new TestTimeObject(
//            LocalDateTime.of(2024, 3, 21, 14, 30, 0),
//            LocalDate.of(2024, 3, 21),
//            LocalTime.of(14, 30, 0)
//        );
//
//        // 测试序列化
//        String json = objectMapper.writeValueAsString(testObject);
//        assertTrue(json.contains("\"dateTime\":\"2024-03-21 14:30:00\""));
//        assertTrue(json.contains("\"date\":\"2024-03-21\""));
//        assertTrue(json.contains("\"time\":\"14:30:00\""));
//
//        // 测试反序列化
//        TestTimeObject deserialized = objectMapper.readValue(json, TestTimeObject.class);
//        assertEquals(testObject, deserialized);
//    }
//
//    // 用于测试的简单对象类
//    private static class TestTimeObject {
//        private LocalDateTime dateTime;
//        private LocalDate date;
//        private LocalTime time;
//
//        public TestTimeObject() {
//        }
//
//        public TestTimeObject(LocalDateTime dateTime, LocalDate date, LocalTime time) {
//            this.dateTime = dateTime;
//            this.date = date;
//            this.time = time;
//        }
//
//        // Getters and setters
//        public LocalDateTime getDateTime() {
//            return dateTime;
//        }
//
//        public void setDateTime(LocalDateTime dateTime) {
//            this.dateTime = dateTime;
//        }
//
//        public LocalDate getDate() {
//            return date;
//        }
//
//        public void setDate(LocalDate date) {
//            this.date = date;
//        }
//
//        public LocalTime getTime() {
//            return time;
//        }
//
//        public void setTime(LocalTime time) {
//            this.time = time;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            TestTimeObject that = (TestTimeObject) o;
//            return dateTime.equals(that.dateTime) &&
//                   date.equals(that.date) &&
//                   time.equals(that.time);
//        }
//
//        @Override
//        public int hashCode() {
//            int result = dateTime.hashCode();
//            result = 31 * result + date.hashCode();
//            result = 31 * result + time.hashCode();
//            return result;
//        }
//    }
//}