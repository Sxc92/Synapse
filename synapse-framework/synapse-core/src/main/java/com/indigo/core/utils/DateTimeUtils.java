package com.indigo.core.utils;

import com.indigo.core.exception.DateTimeException;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Date;
import java.util.Optional;

/**
 * Date and time utility class
 * Provides common operations for LocalDateTime, LocalDate, Date and Timestamp
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@Slf4j
public class DateTimeUtils {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATETIME_PATTERN_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATETIME_PATTERN_WITH_ZONE = "yyyy-MM-dd'T'HH:mm:ssXXX";

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long currentTimestampSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 日期时间格式化
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.format(DateTimeFormatter.ofPattern(pattern)))
                .orElse(null);
    }

    /**
     * 日期格式化
     */
    public static String format(LocalDate date, String pattern) {
        return Optional.ofNullable(date)
                .map(d -> d.format(DateTimeFormatter.ofPattern(pattern)))
                .orElse(null);
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("Failed to parse date time: {}", dateTimeStr, e);
            throw new DateTimeException("Failed to parse date time: " + dateTimeStr, e);
        }
    }

    /**
     * 解析日期字符串为LocalDate
     */
    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("Failed to parse date: {}", dateStr, e);
            throw new DateTimeException("Failed to parse date: " + dateStr, e);
        }
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return Optional.ofNullable(date)
                .map(d -> LocalDateTime.ofInstant(d.toInstant(), DEFAULT_ZONE))
                .orElseThrow(() -> new DateTimeException("Date cannot be null"));
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(dt -> Date.from(dt.atZone(DEFAULT_ZONE).toInstant()))
                .orElseThrow(() -> new DateTimeException("LocalDateTime cannot be null"));
    }

    /**
     * 获取一天的开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return Optional.ofNullable(date)
                .map(d -> d.atStartOfDay())
                .orElseThrow(() -> new DateTimeException("Date cannot be null"));
    }

    /**
     * 获取一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return Optional.ofNullable(date)
                .map(d -> d.atTime(23, 59, 59, 999999999))
                .orElseThrow(() -> new DateTimeException("Date cannot be null"));
    }

    /**
     * 获取本周的开始日期
     */
    public static LocalDate startOfWeek() {
        return today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取本周的结束日期
     */
    public static LocalDate endOfWeek() {
        return today().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取本月的开始日期
     */
    public static LocalDate startOfMonth() {
        return today().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月的结束日期
     */
    public static LocalDate endOfMonth() {
        return today().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本年的开始日期
     */
    public static LocalDate startOfYear() {
        return today().with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取本年的结束日期
     */
    public static LocalDate endOfYear() {
        return today().with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> ChronoUnit.DAYS.between(s, e))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 计算两个日期时间之间的小时数
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> ChronoUnit.HOURS.between(s, e))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 计算两个日期时间之间的分钟数
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> ChronoUnit.MINUTES.between(s, e))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 日期加减天数
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return Optional.ofNullable(date)
                .map(d -> d.plusDays(days))
                .orElse(null);
    }

    /**
     * 日期时间加减小时
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.plusHours(hours))
                .orElse(null);
    }

    /**
     * 日期时间加减分钟
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.plusMinutes(minutes))
                .orElse(null);
    }

    /**
     * 判断日期是否在指定范围内
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        return Optional.ofNullable(date)
                .map(d -> Optional.ofNullable(start)
                        .map(s -> Optional.ofNullable(end)
                                .map(e -> !d.isBefore(s) && !d.isAfter(e))
                                .orElse(false))
                        .orElse(false))
                .orElse(false);
    }

    /**
     * 判断日期时间是否在指定范围内
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(dateTime)
                .map(dt -> Optional.ofNullable(start)
                        .map(s -> Optional.ofNullable(end)
                                .map(e -> !dt.isBefore(s) && !dt.isAfter(e))
                                .orElse(false))
                        .orElse(false))
                .orElse(false);
    }

    /**
     * 获取指定日期的年龄
     */
    public static int getAge(LocalDate birthDate) {
        return Optional.ofNullable(birthDate)
                .map(birth -> Period.between(birth, today()).getYears())
                .orElse(0);
    }

    /**
     * 获取指定日期是星期几
     */
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return Optional.ofNullable(date)
                .map(LocalDate::getDayOfWeek)
                .orElse(null);
    }

    /**
     * 获取指定日期是当年的第几天
     */
    public static int getDayOfYear(LocalDate date) {
        return Optional.ofNullable(date)
                .map(LocalDate::getDayOfYear)
                .orElse(0);
    }

    /**
     * 获取指定日期是当月的第几天
     */
    public static int getDayOfMonth(LocalDate date) {
        return Optional.ofNullable(date)
                .map(LocalDate::getDayOfMonth)
                .orElse(0);
    }

    /**
     * 获取指定日期时间的时区
     */
    public static ZoneId getZoneId(LocalDateTime dateTime) {
        return DEFAULT_ZONE;
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
     * 获取当前Date
     */
    public static Date nowDate() {
        return new Date();
    }

    /**
     * 获取指定时间戳的Date
     */
    public static Date fromTimestamp(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 获取指定时间戳（秒）的Date
     */
    public static Date fromTimestampSeconds(long timestampSeconds) {
        return new Date(timestampSeconds * 1000);
    }

    /**
     * Date格式化
     */
    public static String formatDate(Date date, String pattern) {
        return Optional.ofNullable(date)
                .map(d -> format(toLocalDateTime(d), pattern))
                .orElse(null);
    }

    /**
     * 解析日期字符串为Date
     * 支持日期格式（如yyyy-MM-dd）和日期时间格式（如yyyy-MM-dd HH:mm:ss）
     */
    public static Date parseDate(String dateStr, String pattern) {
        try {
            // 尝试先解析为LocalDate
            LocalDate localDate = parseLocalDate(dateStr, pattern);
            if (localDate != null) {
                return toDate(localDate.atStartOfDay());
            }
            
            // 如果解析为LocalDate失败，尝试解析为LocalDateTime
            LocalDateTime dateTime = parseDateTime(dateStr, pattern);
            return Optional.ofNullable(dateTime)
                    .map(DateTimeUtils::toDate)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to parse date: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 获取Date的开始时间
     */
    public static Date startOfDay(Date date) {
        return Optional.ofNullable(date)
                .map(d -> toDate(startOfDay(toLocalDateTime(d).toLocalDate())))
                .orElse(null);
    }

    /**
     * 获取Date的结束时间
     */
    public static Date endOfDay(Date date) {
        return Optional.ofNullable(date)
                .map(d -> toDate(endOfDay(toLocalDateTime(d).toLocalDate())))
                .orElse(null);
    }

    /**
     * Date加减天数
     */
    public static Date plusDays(Date date, long days) {
        return Optional.ofNullable(date)
                .map(d -> toDate(plusDays(toLocalDateTime(d).toLocalDate(), days).atStartOfDay()))
                .orElse(null);
    }

    /**
     * Date加减小时
     */
    public static Date plusHours(Date date, long hours) {
        return Optional.ofNullable(date)
                .map(d -> toDate(plusHours(toLocalDateTime(d), hours)))
                .orElse(null);
    }

    /**
     * Date加减分钟
     */
    public static Date plusMinutes(Date date, long minutes) {
        return Optional.ofNullable(date)
                .map(d -> toDate(plusMinutes(toLocalDateTime(d), minutes)))
                .orElse(null);
    }

    /**
     * 计算两个Date之间的天数
     */
    public static long daysBetween(Date start, Date end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> daysBetween(
                                toLocalDateTime(s).toLocalDate(),
                                toLocalDateTime(e).toLocalDate()))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 计算两个Date之间的小时数
     */
    public static long hoursBetween(Date start, Date end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> hoursBetween(
                                toLocalDateTime(s),
                                toLocalDateTime(e)))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 计算两个Date之间的分钟数
     */
    public static long minutesBetween(Date start, Date end) {
        return Optional.ofNullable(start)
                .map(s -> Optional.ofNullable(end)
                        .map(e -> minutesBetween(
                                toLocalDateTime(s),
                                toLocalDateTime(e)))
                        .orElse(0L))
                .orElse(0L);
    }

    /**
     * 判断Date是否在指定范围内
     */
    public static boolean isBetween(Date date, Date start, Date end) {
        return Optional.ofNullable(date)
                .map(d -> Optional.ofNullable(start)
                        .map(s -> Optional.ofNullable(end)
                                .map(e -> !d.before(s) && !d.after(e))
                                .orElse(false))
                        .orElse(false))
                .orElse(false);
    }

    /**
     * 获取指定Date的年龄
     */
    public static int getAge(Date birthDate) {
        return Optional.ofNullable(birthDate)
                .map(birth -> getAge(toLocalDateTime(birth).toLocalDate()))
                .orElse(0);
    }

    /**
     * 获取指定Date是星期几
     */
    public static DayOfWeek getDayOfWeek(Date date) {
        return Optional.ofNullable(date)
                .map(d -> getDayOfWeek(toLocalDateTime(d).toLocalDate()))
                .orElse(null);
    }

    /**
     * 获取指定Date是当年的第几天
     */
    public static int getDayOfYear(Date date) {
        return Optional.ofNullable(date)
                .map(d -> getDayOfYear(toLocalDateTime(d).toLocalDate()))
                .orElse(0);
    }

    /**
     * 获取指定Date是当月的第几天
     */
    public static int getDayOfMonth(Date date) {
        return Optional.ofNullable(date)
                .map(d -> getDayOfMonth(toLocalDateTime(d).toLocalDate()))
                .orElse(0);
    }

    /**
     * 时间戳转LocalDateTime
     */
    public static LocalDateTime fromTimestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE);
    }

    /**
     * 时间戳（秒）转LocalDateTime
     */
    public static LocalDateTime fromTimestampSecondsToLocalDateTime(long timestampSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestampSeconds), DEFAULT_ZONE);
    }

    /**
     * LocalDateTime转时间戳
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.atZone(DEFAULT_ZONE).toInstant().toEpochMilli())
                .orElse(0L);
    }

    /**
     * LocalDateTime转时间戳（秒）
     */
    public static long toTimestampSeconds(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.atZone(DEFAULT_ZONE).toEpochSecond())
                .orElse(0L);
    }

    /**
     * Date转时间戳
     */
    public static long toTimestamp(Date date) {
        return Optional.ofNullable(date)
                .map(Date::getTime)
                .orElse(0L);
    }

    /**
     * Date转时间戳（秒）
     */
    public static long toTimestampSeconds(Date date) {
        return Optional.ofNullable(date)
                .map(d -> d.getTime() / 1000)
                .orElse(0L);
    }

    /**
     * 时间戳格式化
     */
    public static String formatTimestamp(long timestamp, String pattern) {
        return format(fromTimestampToLocalDateTime(timestamp), pattern);
    }

    /**
     * 时间戳（秒）格式化
     */
    public static String formatTimestampSeconds(long timestampSeconds, String pattern) {
        return format(fromTimestampSecondsToLocalDateTime(timestampSeconds), pattern);
    }

    /**
     * 获取时间戳的开始时间
     */
    public static long startOfDayTimestamp(long timestamp) {
        return toTimestamp(startOfDay(fromTimestampToLocalDateTime(timestamp).toLocalDate()));
    }

    /**
     * 获取时间戳的结束时间
     */
    public static long endOfDayTimestamp(long timestamp) {
        return toTimestamp(endOfDay(fromTimestampToLocalDateTime(timestamp).toLocalDate()));
    }

    /**
     * 时间戳加减天数
     */
    public static long plusDaysTimestamp(long timestamp, long days) {
        return toTimestamp(plusDays(fromTimestampToLocalDateTime(timestamp).toLocalDate(), days).atStartOfDay());
    }

    /**
     * 时间戳加减小时
     */
    public static long plusHoursTimestamp(long timestamp, long hours) {
        return toTimestamp(plusHours(fromTimestampToLocalDateTime(timestamp), hours));
    }

    /**
     * 时间戳加减分钟
     */
    public static long plusMinutesTimestamp(long timestamp, long minutes) {
        return toTimestamp(plusMinutes(fromTimestampToLocalDateTime(timestamp), minutes));
    }

    /**
     * 计算两个时间戳之间的天数
     */
    public static long daysBetweenTimestamp(long start, long end) {
        return daysBetween(
                fromTimestampToLocalDateTime(start).toLocalDate(),
                fromTimestampToLocalDateTime(end).toLocalDate()
        );
    }

    /**
     * 计算两个时间戳之间的小时数
     */
    public static long hoursBetweenTimestamp(long start, long end) {
        return hoursBetween(
                fromTimestampToLocalDateTime(start),
                fromTimestampToLocalDateTime(end)
        );
    }

    /**
     * 计算两个时间戳之间的分钟数
     */
    public static long minutesBetweenTimestamp(long start, long end) {
        return minutesBetween(
                fromTimestampToLocalDateTime(start),
                fromTimestampToLocalDateTime(end)
        );
    }

    /**
     * 判断时间戳是否在指定范围内
     */
    public static boolean isBetweenTimestamp(long timestamp, long start, long end) {
        return isBetween(
                fromTimestampToLocalDateTime(timestamp),
                fromTimestampToLocalDateTime(start),
                fromTimestampToLocalDateTime(end)
        );
    }
} 