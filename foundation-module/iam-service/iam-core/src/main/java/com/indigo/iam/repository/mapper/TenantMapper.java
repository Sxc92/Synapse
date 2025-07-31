package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.iam.api.model.pojo.IamTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/07/28 06:12
 **/
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
}
