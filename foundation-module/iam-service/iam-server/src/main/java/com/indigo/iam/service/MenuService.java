package com.indigo.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.entity.result.PageResult;
import com.indigo.core.exception.Ex;
import com.indigo.core.exception.SynapseException;
import com.indigo.databases.utils.VoMapper;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.entity.RoleMenu;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.repository.service.IRoleMenuService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyMenuDTO;
import com.indigo.iam.sdk.dto.query.MenuDTO;
import com.indigo.iam.sdk.vo.resource.MenuTreeVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 获取菜单树结构列表
     *
     * @param queryDTO 查询条件
     * @return 菜单树结构列表
     */
    List<MenuTreeVO> listTree(MenuDTO queryDTO);

    /**
     * 获取菜单树结构分页
     *
     * @param pageDTO 分页查询条件
     * @return 菜单树结构分页结果
     */
    PageResult<MenuTreeVO> pageTree(MenuDTO pageDTO);

    /**
     * 获取菜单树结构详情（包含子节点）
     *
     * @param queryDTO 查询条件
     * @return 菜单树结构详情
     */
    MenuTreeVO detailTree(MenuDTO queryDTO);
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
        // 检查是否存在关联的角色菜单，如果存在则不允许删除
        if (iRoleMenuService.exists(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, id))) {
            Ex.throwEx(ROLE_BIND_MENU);
        }
        // 检查是否存在关联的资源，如果存在则不允许删除
        if (iResourceService.exists(new LambdaQueryWrapper<IamResource>()
                .eq(IamResource::getMenuId, id))) {
            Ex.throwEx(RESOURCE_BIND_MENU);
        }
        return iMenuService.removeById(id);
    }

    @Override
    public List<MenuTreeVO> listTree(MenuDTO queryDTO) {
        // 1. 查询所有菜单（平铺结构）
        List<MenuVO> menuList = iMenuService.listWithDTO(queryDTO, MenuVO.class);
        
        // 2. 转换为树结构
        return buildMenuTree(menuList);
    }

    @Override
    public PageResult<MenuTreeVO> pageTree(MenuDTO pageDTO) {
        // 1. 构建根节点查询条件（parentId 为空或 null）
        // 注意：由于框架限制，这里先查询所有数据，然后过滤根节点
        // 如果数据量大，建议在 Mapper 中添加自定义 SQL 查询根节点
        MenuDTO allNodesQuery = MenuDTO.builder()
                .systemId(pageDTO.getSystemId())
                .code(pageDTO.getCode())
                .name(pageDTO.getName())
                .build();
        
        // 2. 查询所有菜单（带过滤条件，减少数据量）
        List<MenuVO> allMenus = iMenuService.listWithDTO(allNodesQuery, MenuVO.class);
        
        // 3. 构建完整的树结构
        List<MenuTreeVO> allTreeList = buildMenuTree(allMenus);
        
        // 4. 过滤出根节点（parentId 为空或 null）
        List<MenuTreeVO> rootNodes = allTreeList.stream()
                .filter(menu -> !StringUtils.hasText(menu.getParentId()))
                .collect(Collectors.toList());
        
        // 5. 计算分页信息
        int total = rootNodes.size();
        long pageNo = pageDTO.getPageNo();
        long pageSize = pageDTO.getPageSize();
        int start = (int) ((pageNo - 1) * pageSize);
        int end = Math.min(start + (int) pageSize, total);
        
        // 6. 对根节点进行内存分页
        // 说明：树结构分页的复杂性在于一个根节点可能包含多个子节点，
        // SQL 层面很难直接实现树结构的分页，因此采用内存分页的方式
        // 如果数据量特别大，可以考虑：
        // 1. 在 Mapper 中添加自定义 SQL，先查询根节点并分页（WHERE parent_id IS NULL）
        // 2. 然后查询这些根节点的所有子节点（使用递归查询或批量查询）
        // 3. 在内存中构建树结构
        List<MenuTreeVO> pagedRootNodes = (start < total) 
                ? rootNodes.subList(start, end) 
                : new ArrayList<>();
        
        // 7. 构建分页结果（使用 PageResult.of() 静态工厂方法）
        return PageResult.of(pagedRootNodes, (long) total, pageNo, pageSize);
    }

    @Override
    public MenuTreeVO detailTree(MenuDTO queryDTO) {
        // 1. 查询菜单详情
        MenuVO menuVO = iMenuService.getOneWithDTO(queryDTO, MenuVO.class);
        if (menuVO == null) {
            Ex.throwEx(MENU_NOT_EXIST);
        }
        
        // 2. 查询所有子菜单
        MenuDTO childrenQuery = MenuDTO.builder()
                .parentId(menuVO.getId())
                .build();
        List<MenuVO> children = iMenuService.listWithDTO(childrenQuery, MenuVO.class);
        
        // 3. 构建树结构（以当前菜单为根节点）
        MenuTreeVO treeVO = VoMapper.mapToVo(menuVO, MenuTreeVO.class);
        
        // 4. 如果有子菜单，构建子菜单树结构
        if (children != null && !children.isEmpty()) {
            List<MenuTreeVO> childrenTree = buildMenuTree(children);
            treeVO.setChildren(childrenTree);
        }
        
        return treeVO;
    }

    /**
     * 构建菜单树结构
     * 
     * @param menuList 平铺的菜单列表
     * @return 树结构的菜单列表
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuVO> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 转换为 MenuTreeVO
        List<MenuTreeVO> treeList = menuList.stream()
                .map(menu -> VoMapper.mapToVo(menu, MenuTreeVO.class))
                .toList();
        
        // 2. 构建 ID 映射
        Map<String, MenuTreeVO> menuMap = treeList.stream()
                .collect(Collectors.toMap(MenuTreeVO::getId, menu -> menu));
        
        // 3. 构建树结构
        List<MenuTreeVO> rootNodes = new ArrayList<>();
        for (MenuTreeVO menu : treeList) {
            String parentId = menu.getParentId();
            if (StringUtils.hasText(parentId) && menuMap.containsKey(parentId)) {
                // 有父节点，添加到父节点的 children
                menuMap.get(parentId).addChild(menu);
            } else {
                // 没有父节点或父节点不存在，作为根节点
                rootNodes.add(menu);
            }
        }
        
        return rootNodes;
    }
}

