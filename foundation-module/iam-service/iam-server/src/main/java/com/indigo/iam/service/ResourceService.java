package com.indigo.iam.service;

import cn.hutool.core.util.StrUtil;
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
 * @date 2025/11/08 15:52
 **/
public interface ResourceService {
    /**
     * 删除菜单
     *
     * @param param
     * @return
     */
    Boolean addOrModifyMenu(AddOrModifyMenuDTO param);

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    Boolean deleteMenu(String id);
}

@Slf4j
@Service
@RequiredArgsConstructor
class ResourceServiceImpl implements ResourceService {

    private final IMenuService iMenuService;

    @Override
    public Boolean addOrModifyMenu(AddOrModifyMenuDTO param) {
        if (iMenuService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(MENU_EXIST);
        }
        
        if (StrUtil.isBlank(param.getId())) {
            // 新增场景：使用 EntityMapper 自动映射
            Menu menu = Menu.builder().build();
            return iMenuService.saveFromDTO(param, menu);
        } else {
            // 更新场景：使用 updateFromDTO 自动映射
            try {
                return iMenuService.updateFromDTO(param);
            } catch (IllegalArgumentException e) {
                // 实体不存在时抛出业务异常
                Ex.throwEx(MENU_NOT_EXIST);
                return false;
            }
        }
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