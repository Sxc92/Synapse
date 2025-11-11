package com.indigo.iam.repository.mapper;

import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.UsersRole;
import com.indigo.iam.sdk.vo.users.UserRoleVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/11/06 16:52
 **/
@Mapper
public interface UsersRoleMapper extends EnhancedVoMapper<UsersRole, UserRoleVO> {
}
