package com.indigo.iam.sdk.vo.resource;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/21 09:11
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SystemMenuTreeVO extends BaseVO<String> {

    /**
     * 系统名称
     */
    private String name;

    /**
     * 系统图标
     */
    private String logo;

    /**
     * 子菜单
     */
    private List<MenuTreeVO> menus;
}
