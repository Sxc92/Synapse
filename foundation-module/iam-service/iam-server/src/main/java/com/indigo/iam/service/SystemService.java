package com.indigo.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.repository.service.ISystemService;
import com.indigo.iam.sdk.dto.opera.AddOrModifySystemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.indigo.iam.sdk.enums.IamError.*;

/**
 * @author 史偕成
 * @date 2025/11/10 09:30
 **/
public interface SystemService {
    /**
     * 添加或修改系统
     *
     * @param param
     * @return
     */
    Boolean addOrModifySystem(AddOrModifySystemDTO param);

    /**
     * 删除系统
     *
     * @param id
     * @return
     */
    Boolean deleteSystem(String id);
}

@Slf4j
@Service
@RequiredArgsConstructor
class SystemServiceImpl implements SystemService {
    private final ISystemService iSystemService;

    private final IMenuService iMenuService;

    @Override
    public Boolean addOrModifySystem(AddOrModifySystemDTO param) {
        if (iSystemService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(SYSTEM_EXIST);
        }
        return iSystemService.saveOrUpdateFromDTO(param, IamSystem.class);
    }

    @Override
    public Boolean deleteSystem(String id) {
        IamSystem system = iSystemService.getById(id);
        if (system == null) {
            Ex.throwEx(SYSTEM_NOT_EXIST);
        }
        // 检查是否存在关联的菜单，如果存在则不允许删除
        if (iMenuService.exists(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getSystemId, id))) {
            Ex.throwEx(SYSTEM_BIND_MENU);
        }
        iSystemService.removeById(id);
        return true;
    }
}