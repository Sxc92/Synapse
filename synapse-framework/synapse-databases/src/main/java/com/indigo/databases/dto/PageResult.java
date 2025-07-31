package com.indigo.databases.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页结果DTO
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
        this.hasNext = current < pages;
        this.hasPrevious = current > 1;
    }
    
    /**
     * 创建空的分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0L, 1L, 10L);
    }
    
    /**
     * 从MyBatis-Plus的IPage创建PageResult
     */
    public static <T> PageResult<T> fromIPage(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(
            page.getRecords(),
            page.getTotal(),
            page.getCurrent(),
            page.getSize()
        );
    }
} 