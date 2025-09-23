package com.indigo.mdm.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.mdm.repository.entity.ErrorCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错误码Repository
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@Mapper
public interface ErrorCodeRepository extends BaseMapper<ErrorCode> {
}
