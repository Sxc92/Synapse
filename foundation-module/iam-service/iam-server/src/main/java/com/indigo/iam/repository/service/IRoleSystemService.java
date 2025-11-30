package com.indigo.iam.repository.service;

/**
 * 角色系统关联 Repository
 *
 * <p><b>注意：</b>此功能已暂时注释，待业务完整后扩展
 * 暂时取消 iam_role_system 表，通过菜单推导系统关系
 *
 * @author 史偕成
 * @date 2025/11/20
 */

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.RoleSystem;
import com.indigo.iam.repository.mapper.RoleSystemMapper;

@AutoRepository
@IdeFriendlyRepository("iRoleSystemService")
public interface IRoleSystemService extends BaseRepository<RoleSystem, RoleSystemMapper> {
}
