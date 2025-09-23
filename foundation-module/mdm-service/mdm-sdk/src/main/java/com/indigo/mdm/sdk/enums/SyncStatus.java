package com.indigo.mdm.sdk.enums;

/**
 * 同步状态枚举
 *
 * @author 史偕成
 * @date 2025/01/27
 */
public enum SyncStatus {
    
    /**
     * 同步成功
     */
    SUCCESS("SUCCESS", "同步成功"),
    
    /**
     * 同步失败
     */
    FAILED("FAILED", "同步失败"),
    
    /**
     * 部分成功
     */
    PARTIAL("PARTIAL", "部分成功");
    
    private final String code;
    private final String description;
    
    SyncStatus(String code, String description) {
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
    public static SyncStatus fromCode(String code) {
        for (SyncStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown sync status code: " + code);
    }
}
