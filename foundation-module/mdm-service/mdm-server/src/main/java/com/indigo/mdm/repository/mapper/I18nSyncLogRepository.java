package com.indigo.mdm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.mdm.repository.entity.I18nSyncLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国际化缓存同步日志Repository
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@Mapper
public interface I18nSyncLogRepository extends BaseMapper<I18nSyncLog> {
}
