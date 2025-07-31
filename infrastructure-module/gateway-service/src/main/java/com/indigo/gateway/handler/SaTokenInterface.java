package com.indigo.gateway.handler;

import cn.dev33.satoken.stp.StpInterface;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/05/09 20:53
 **/
public class SaTokenInterface implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
