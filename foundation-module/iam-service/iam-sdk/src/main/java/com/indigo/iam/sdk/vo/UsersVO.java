package com.indigo.iam.sdk.vo;

import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-22 17:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UsersVO extends BaseVO<String> {
    private String account;
}
