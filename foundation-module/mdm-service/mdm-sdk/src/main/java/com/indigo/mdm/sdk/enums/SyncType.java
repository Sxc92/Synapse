package com.indigo.mdm.sdk.enums;

/**
 * 同步类型枚举
 *
 * @author 史偕成
 * @date 2025/01/27
 */
public enum SyncType {
    
    /**
     * 全量同步
     */
    FULL("FULL", "全量同步"),
    
    /**
     * 增量同步
     */
    INCREMENTAL("INCREMENTAL", "增量同步"),
    
    /**
     * 按错误码同步
     */
    ERROR_CODE("ERROR_CODE", "按错误码同步"),
    
    /**
     * 按语言同步
     */
    LANGUAGE("LANGUAGE", "按语言同步");
    
    private final String code;
    private final String description;
    
    SyncType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static SyncType fromCode(String code) {
        for (SyncType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sync type code: " + code);
    }
}
