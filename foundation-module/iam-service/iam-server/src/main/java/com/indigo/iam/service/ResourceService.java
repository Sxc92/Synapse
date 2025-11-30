package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.indigo.core.entity.dto.QueryDTO;
import com.indigo.core.entity.result.PageResult;
import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyResourceDTO;
import com.indigo.iam.sdk.dto.query.ResourceDTO;
import com.indigo.iam.sdk.vo.resource.ResourceDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.indigo.iam.sdk.enums.IamError.RESOURCE_EXIST;
import static com.indigo.iam.sdk.enums.IamError.RESOURCE_NOT_EXIST;

/**
 * @author 史偕成
 * @date 2025/11/08 15:52
 **/
public interface ResourceService {
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

    /**
     * 资源详情分页查询（使用手写 SQL 多表联查）
     * 使用 MyBatis-Plus 的 ew 参数进行 SQL 拼接
     *
     * @param queryDTO 查询条件
     * @return 资源详情分页结果
     */
    PageResult<ResourceDetailVO> pageResourceDetail(ResourceDTO queryDTO);
}

@Slf4j
@Service
@RequiredArgsConstructor
class ResourceServiceImpl implements ResourceService {

    private final IResourceService iResourceService;
    private final IMenuService iMenuService;

    @Override
    public Boolean addOrModifyResource(AddOrModifyResourceDTO param) {
        // 验证资源必须关联菜单
        if (StringUtils.hasText(param.getMenuId())) {
            // 验证菜单是否存在
            Menu menu = iMenuService.getById(param.getMenuId());
            if (menu == null) {
                Ex.throwEx(com.indigo.iam.sdk.enums.IamError.MENU_NOT_EXIST);
            }
        } else {
            Ex.throwEx(com.indigo.iam.sdk.enums.IamError.MENU_NOT_EXIST, "资源必须关联菜单");
        }

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
        return iResourceService.removeById(id);
    }

    @Override
    public PageResult<ResourceDetailVO> pageResourceDetail(ResourceDTO queryDTO) {
        // 创建分页对象
        Page<ResourceDetailVO> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());

        // 使用 QueryWrapper 构建查询条件（使用表别名）
        QueryWrapper<IamResource> wrapper = new QueryWrapper<>();

        // 固定查询条件：逻辑删除
        wrapper.eq("res.deleted", 1);

        // 资源表字段查询条件（使用表别名 res）
        // 使用 MyBatis-Plus 的条件判断方法，第一个参数判断是否需要拼接
        wrapper.like(StringUtils.hasText(queryDTO.getCode()), "res.code", queryDTO.getCode())
                .like(StringUtils.hasText(queryDTO.getName()), "res.name", queryDTO.getName())
                .eq(StringUtils.hasText(queryDTO.getSystemId()), "sys.id", queryDTO.getSystemId())
                .eq(StringUtils.hasText(queryDTO.getMenuId()), "res.menu_id", queryDTO.getMenuId())
                .eq(StringUtils.hasText(queryDTO.getType()), "res.type", queryDTO.getType())
                .like(StringUtils.hasText(queryDTO.getPermissions()), "res.permissions", queryDTO.getPermissions());

        // 排序：使用 DTO 中的 orderByList，如果为空则使用默认排序
        if (CollUtil.isNotEmpty(queryDTO.getOrderByList())) {
            // 遍历排序字段列表
            for (QueryDTO.OrderBy orderBy : queryDTO.getOrderByList()) {
                if (StringUtils.hasText(orderBy.getField())) {
                    // 字段名加上表别名（如果字段名不包含点号，则添加 res. 前缀）
                    String field = orderBy.getField();
                    if (!field.contains(".")) {
                        field = "res." + field;
                    }

                    // 根据排序方向进行排序
                    String direction = orderBy.getDirection();
                    if ("DESC".equalsIgnoreCase(direction)) {
                        wrapper.orderByDesc(field);
                    } else {
                        wrapper.orderByAsc(field);
                    }
                }
            }
        } else {
            // 默认排序：按创建时间倒序
            wrapper.orderByDesc("res.create_time");
        }

        // 执行查询（使用 ew 参数进行 SQL 拼接）
        Page<ResourceDetailVO> result = iResourceService.getMapper().selectResourceDetailPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

}