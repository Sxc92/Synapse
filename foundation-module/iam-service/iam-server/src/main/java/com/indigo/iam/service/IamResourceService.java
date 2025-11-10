package com.indigo.iam.service;

import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.sdk.dto.resource.AddOrModifyResourceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.indigo.iam.sdk.enums.IamError.RESOURCE_EXIST;
import static com.indigo.iam.sdk.enums.IamError.RESOURCE_NOT_EXIST;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
public interface IamResourceService {
    /**
     * 添加或修改资源
     *
     * @param param 资源参数
     * @return 操作结果
     */
    Boolean addOrModifyResource(AddOrModifyResourceDTO param);

    /**
     * 删除资源
     *
     * @param id 资源ID
     * @return 删除结果
     */
    Boolean deleteResource(String id);
}

@Slf4j
@Service
@RequiredArgsConstructor
class IamResourceServiceImpl implements IamResourceService {
    private final IResourceService iResourceService;

    @Override
    public Boolean addOrModifyResource(AddOrModifyResourceDTO param) {
        if (iResourceService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(RESOURCE_EXIST);
        }
        return iResourceService.saveOrUpdateFromDTO(param, IamResource.class);
    }

    @Override
    public Boolean deleteResource(String id) {
        IamResource resource = iResourceService.getById(id);
        if (resource == null) {
            Ex.throwEx(RESOURCE_NOT_EXIST);
        }
        iResourceService.removeById(id);
        return true;
    }
}

