package com.indigo.mdm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.mdm.repository.entity.I18nMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国际化消息Repository
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@Mapper
public interface I18nMessageRepository extends BaseMapper<I18nMessage> {
}
