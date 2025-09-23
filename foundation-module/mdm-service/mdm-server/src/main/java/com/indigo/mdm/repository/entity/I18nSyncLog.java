package com.indigo.mdm.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.indigo.databases.entity.AuditEntity;
import com.indigo.databases.entity.BaseEntity;
import com.indigo.databases.entity.CreatedEntity;
import com.indigo.mdm.sdk.enums.SyncType;
import com.indigo.mdm.sdk.enums.SyncStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 国际化缓存同步日志实体
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("sys_i18n_sync_log")
public class I18nSyncLog extends CreatedEntity<String> {

    /**
     * 同步类型：FULL,INCREMENTAL,ERROR_CODE,LANGUAGE
     */
    private SyncType syncType;

    /**
     * 同步状态：SUCCESS,FAILED,PARTIAL
     */
    private SyncStatus syncStatus;

    /**
     * 同步数量
     */
    private Integer syncCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failedCount;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 同步开始时间
     */
    private LocalDateTime syncStartTime;

    /**
     * 同步结束时间
     */
    private LocalDateTime syncEndTime;

    /**
     * 同步耗时（毫秒）
     */
    private Long syncDuration;

}
