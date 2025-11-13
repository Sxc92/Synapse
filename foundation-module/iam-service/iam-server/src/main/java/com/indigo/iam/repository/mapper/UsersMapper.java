package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.sdk.vo.users.UserVO;

/**
 * @author 史偕成
 * @date 2025/07/22 16:15
 **/
public interface UsersMapper extends EnhancedVoMapper<Users, UserVO> {

}
