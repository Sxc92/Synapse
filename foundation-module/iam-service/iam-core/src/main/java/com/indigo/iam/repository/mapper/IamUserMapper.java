package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.iam.api.model.pojo.IamUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/07/22 16:15
 **/
@Mapper
public interface IamUserMapper extends BaseMapper<IamUser> {
}
