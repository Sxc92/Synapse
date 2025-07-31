package com.indigo.databases.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页查询DTO
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
public class PageDTO {
    
    /**
     * 当前页码，从1开始
     */
    private Integer pageNo = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 排序字段列表
     */
    private List<OrderBy> orderByList;
    
    /**
     * 排序字段
     */
    @Data
    public static class OrderBy {
        /**
         * 字段名
         */
        private String field;
        
        /**
         * 排序方向：ASC, DESC
         */
        private String direction = "ASC";
        
        public OrderBy() {}
        
        public OrderBy(String field) {
            this.field = field;
        }
        
        public OrderBy(String field, String direction) {
            this.field = field;
            this.direction = direction;
        }
    }
} 