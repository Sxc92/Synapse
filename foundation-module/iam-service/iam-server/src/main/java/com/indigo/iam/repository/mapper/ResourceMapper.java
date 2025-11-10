package com.indigo.iam.repository.mapper;

import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
@Mapper
public interface ResourceMapper extends EnhancedVoMapper<IamResource, ResourceVO> {
}

