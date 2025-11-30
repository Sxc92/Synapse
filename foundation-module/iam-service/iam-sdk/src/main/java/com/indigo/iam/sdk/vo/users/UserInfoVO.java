package com.indigo.iam.sdk.vo.users;

import com.indigo.core.entity.vo.BaseVO;
import com.indigo.iam.sdk.vo.resource.SystemMenuTreeVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 用户信息VO
 * 包含用户基本信息和权限相关数据（从缓存中获取）
 *
 * @author 史偕成
 * @date 2025/11/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserInfoVO extends BaseVO<String> {

    /**
     * 用户名
     */
    private String account;

    /**
     * 用户昵称
     */
    private String realName;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 权限列表（从缓存中获取）
     */
    private List<String> permissions;

    /**
     * 系统菜单树列表（从缓存中获取）
     */
    private List<SystemMenuTreeVO> systemMenuTree;
}

