package com.indigo.iam.service;

import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.repository.service.ISystemService;
import com.indigo.iam.sdk.dto.resource.AddOrModifySystemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.indigo.iam.sdk.enums.IamError.SYSTEM_EXIST;
import static com.indigo.iam.sdk.enums.IamError.SYSTEM_NOT_EXIST;

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
        iSystemService.removeById(id);
        return true;
    }
}