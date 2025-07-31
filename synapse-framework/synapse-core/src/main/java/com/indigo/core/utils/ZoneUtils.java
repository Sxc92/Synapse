package com.indigo.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Timezone utility class
 * Provides common operations for timezone handling
 * 
 * @author 史偕成
 * @date 2025/04/24 22:30
 **/
@Slf4j
public class ZoneUtils {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter ZONE_FORMATTER = DateTimeFormatter.ofPattern("XXX");

    private ZoneUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取系统默认时区
     */
    public static ZoneId getDefaultZone() {
        return DEFAULT_ZONE;
    }

    /**
     * 获取所有可用的时区ID
     */
    public static Set<String> getAvailableZoneIds() {
        return ZoneId.getAvailableZoneIds();
    }

    /**
     * 获取所有可用的时区
     */
    public static List<ZoneId> getAvailableZones() {
        return ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .collect(Collectors.toList());
    }

    /**
     * 根据时区ID获取时区
     */
    public static ZoneId of(String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (Exception e) {
            log.error("Invalid zone id: {}", zoneId, e);
            return DEFAULT_ZONE;
        }
    }

    /**
     * 获取指定时区的当前时间
     */
    public static LocalDateTime now(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    /**
     * 获取指定时区的当前日期
     */
    public static LocalDate today(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    /**
     * 获取指定时区的当前时间戳
     */
    public static long currentTimestamp(ZoneId zoneId) {
        return Instant.now().atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 转换时区
     */
    public static LocalDateTime convertZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.atZone(fromZone)
                        .withZoneSameInstant(toZone)
                        .toLocalDateTime())
                .orElse(null);
    }

    /**
     * 转换时区（带格式化）
     */
    public static String convertZoneAndFormat(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone, String pattern) {
        return Optional.ofNullable(dateTime)
                .map(dt -> convertZone(dt, fromZone, toZone))
                .map(dt -> dt.format(DateTimeFormatter.ofPattern(pattern)))
                .orElse(null);
    }

    /**
     * 获取时区偏移量
     */
    public static String getZoneOffset(ZoneId zoneId) {
        return ZONE_FORMATTER.format(ZonedDateTime.now(zoneId));
    }

    /**
     * 获取时区规则
     */
    public static ZoneRules getZoneRules(ZoneId zoneId) {
        return zoneId.getRules();
    }

    /**
     * 判断时区是否支持夏令时
     */
    public static boolean isDaylightSavings(ZoneId zoneId) {
        return getZoneRules(zoneId).isDaylightSavings(Instant.now());
    }

    /**
     * 获取时区的夏令时偏移量
     */
    public static Duration getDaylightSavingsOffset(ZoneId zoneId) {
        return getZoneRules(zoneId).getDaylightSavings(Instant.now());
    }

    /**
     * 获取时区的标准偏移量
     */
    public static ZoneOffset getStandardOffset(ZoneId zoneId) {
        return getZoneRules(zoneId).getStandardOffset(Instant.now());
    }

    /**
     * 获取时区的下一个夏令时转换时间
     */
    public static ZonedDateTime getNextDaylightSavingsTransition(ZoneId zoneId) {
        ZoneOffsetTransition transition = getZoneRules(zoneId).nextTransition(Instant.now());
        return transition != null ? transition.getDateTimeAfter().atZone(zoneId) : null;
    }

    /**
     * 获取时区的上一个夏令时转换时间
     */
    public static ZonedDateTime getPreviousDaylightSavingsTransition(ZoneId zoneId) {
        ZoneOffsetTransition transition = getZoneRules(zoneId).previousTransition(Instant.now());
        return transition != null ? transition.getDateTimeBefore().atZone(zoneId) : null;
    }

    /**
     * 获取指定时间在指定时区的夏令时状态
     */
    public static boolean isDaylightSavingsAt(ZoneId zoneId, Instant instant) {
        return getZoneRules(zoneId).isDaylightSavings(instant);
    }

    /**
     * 获取指定时间在指定时区的夏令时偏移量
     */
    public static Duration getDaylightSavingsOffsetAt(ZoneId zoneId, Instant instant) {
        return getZoneRules(zoneId).getDaylightSavings(instant);
    }

    /**
     * 获取指定时间在指定时区的标准偏移量
     */
    public static ZoneOffset getStandardOffsetAt(ZoneId zoneId, Instant instant) {
        return getZoneRules(zoneId).getStandardOffset(instant);
    }

    /**
     * 获取时区信息（包含ID、偏移量、是否夏令时等）
     */
    public static Map<String, Object> getZoneInfo(ZoneId zoneId) {
        Map<String, Object> info = new HashMap<>();
        Instant now = Instant.now();
        ZoneRules rules = getZoneRules(zoneId);

        info.put("zoneId", zoneId.getId());
        info.put("offset", getZoneOffset(zoneId));
        info.put("standardOffset", rules.getStandardOffset(now).toString());
        info.put("isDaylightSavings", rules.isDaylightSavings(now));
        info.put("daylightSavingsOffset", rules.getDaylightSavings(now).toString());
        
        ZoneOffsetTransition nextTransition = rules.nextTransition(now);
        if (nextTransition != null) {
            info.put("nextTransition", nextTransition.getDateTimeAfter().toString());
        }
        
        ZoneOffsetTransition previousTransition = rules.previousTransition(now);
        if (previousTransition != null) {
            info.put("previousTransition", previousTransition.getDateTimeBefore().toString());
        }

        return info;
    }

    /**
     * 获取所有时区信息
     */
    public static List<Map<String, Object>> getAllZoneInfo() {
        return getAvailableZones().stream()
                .map(ZoneUtils::getZoneInfo)
                .collect(Collectors.toList());
    }

    /**
     * 根据偏移量获取时区列表
     */
    public static List<ZoneId> getZonesByOffset(String offset) {
        return getAvailableZones().stream()
                .filter(zoneId -> getZoneOffset(zoneId).equals(offset))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定时区的当前时间（带格式化）
     */
    public static String getCurrentTimeFormatted(ZoneId zoneId, String pattern) {
        return now(zoneId).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取指定时区的当前日期（带格式化）
     */
    public static String getCurrentDateFormatted(ZoneId zoneId, String pattern) {
        return today(zoneId).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 判断两个时区是否相同
     */
    public static boolean isSameZone(ZoneId zone1, ZoneId zone2) {
        return zone1.equals(zone2);
    }

    /**
     * 判断两个时区是否在同一时区组（具有相同的标准偏移量）
     */
    public static boolean isSameZoneGroup(ZoneId zone1, ZoneId zone2) {
        return getStandardOffset(zone1).equals(getStandardOffset(zone2));
    }

    /**
     * 获取时区组（具有相同标准偏移量的时区集合）
     */
    public static Map<ZoneOffset, List<ZoneId>> getZoneGroups() {
        return getAvailableZones().stream()
                .collect(Collectors.groupingBy(ZoneUtils::getStandardOffset));
    }
} 