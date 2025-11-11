package com.indigo.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.core.exception.SynapseException;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.entity.RoleMenu;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.repository.service.IRoleMenuService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyMenuDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import static com.indigo.iam.sdk.enums.IamError.*;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
public interface MenuService {
    /**
     * 添加或修改菜单
     *
     * @param param 菜单参数
     * @return 操作结果
     */
    Boolean addOrModifyMenu(AddOrModifyMenuDTO param);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    Boolean deleteMenu(String id);
}

@Slf4j
@Service
@RequiredArgsConstructor
class MenuServiceImpl implements MenuService {
    private final IMenuService iMenuService;

    private final IRoleMenuService iRoleMenuService;

    private final IResourceService iResourceService;

    @Override
    public Boolean addOrModifyMenu(AddOrModifyMenuDTO param) {
        if (iMenuService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(MENU_EXIST);
        }
        return iMenuService.saveOrUpdateFromDTO(param, Menu.class);
    }

    @Override
    @GlobalTransactional(rollbackFor = {SynapseException.class, Exception.class})
    public Boolean deleteMenu(String id) {
        Menu menu = iMenuService.getById(id);
        if (menu == null) {
            Ex.throwEx(MENU_NOT_EXIST);
        }
        if (!iRoleMenuService.exists(new LambdaUpdateWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, id))) {
            Ex.throwEx(ROLE_BIND_MENU);
        }
        if (!iResourceService.exists(new LambdaQueryWrapper<IamResource>()
                .eq(IamResource::getMenuId, id))) {
            Ex.throwEx(RESOURCE_BIND_MENU);
        }
        return iMenuService.removeById(id);
    }
}

