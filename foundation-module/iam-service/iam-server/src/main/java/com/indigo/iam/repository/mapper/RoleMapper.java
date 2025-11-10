package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.Roles;
import com.indigo.iam.sdk.vo.users.RoleVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper
 * 实现EnhancedVoMapper以支持VO映射和继承的父类属性
 * 
 * @author 史偕成
 * @date 2025/11/06 16:36
 **/
@Mapper
public interface RoleMapper extends EnhancedVoMapper<Roles, RoleVO> {
}
