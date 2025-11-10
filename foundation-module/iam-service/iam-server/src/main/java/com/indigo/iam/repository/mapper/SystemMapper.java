package com.indigo.iam.repository.mapper;


import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/11/08 15:40
 **/
@Mapper
public interface SystemMapper extends EnhancedVoMapper<IamSystem, SystemVO> {
}
