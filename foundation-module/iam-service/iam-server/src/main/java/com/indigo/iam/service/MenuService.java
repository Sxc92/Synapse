package com.indigo.iam.service;

import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.sdk.dto.resource.AddOrModifyMenuDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.indigo.iam.sdk.enums.IamError.MENU_EXIST;
import static com.indigo.iam.sdk.enums.IamError.MENU_NOT_EXIST;

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

    @Override
    public Boolean addOrModifyMenu(AddOrModifyMenuDTO param) {
        if (iMenuService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(MENU_EXIST);
        }
        return iMenuService.saveOrUpdateFromDTO(param, Menu.class);
    }

    @Override
    public Boolean deleteMenu(String id) {
        Menu menu = iMenuService.getById(id);
        if (menu == null) {
            Ex.throwEx(MENU_NOT_EXIST);
        }
        iMenuService.removeById(id);
        return true;
    }
}

