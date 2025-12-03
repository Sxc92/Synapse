package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.entity.result.PageResult;
import com.indigo.core.exception.Ex;
import com.indigo.core.exception.SynapseException;
import com.indigo.databases.utils.VoMapper;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.entity.RoleMenu;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.repository.service.IRoleMenuService;
import com.indigo.iam.repository.service.ISystemService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyMenuDTO;
import com.indigo.iam.sdk.dto.query.MenuDTO;
import com.indigo.iam.sdk.vo.resource.MenuTreeVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import com.indigo.iam.sdk.vo.resource.SystemMenuTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 获取系统菜单树结构列表
     *
     * @return
     */
    List<SystemMenuTreeVO> listSystemMenuTree(List<String> systemIds);

    /**
     * 修改状态和可见性
     *
     * @param param
     * @return
     */
    boolean modify(AddOrModifyMenuDTO param);
}

@Slf4j
@Service
@RequiredArgsConstructor
class MenuServiceImpl implements MenuService {
    private final IMenuService iMenuService;

    private final IRoleMenuService iRoleMenuService;

    private final IResourceService iResourceService;

    private final ISystemService iSystemService;

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
        // 方案1：全量查询 + 内存分页（简单可靠）
        // 1. 查询所有菜单（带过滤条件，减少数据量）
        List<MenuVO> allMenus = iMenuService.listWithDTO(pageDTO, MenuVO.class);

        // 2. 构建完整的树结构
        // 注意：buildMenuTree 方法已经正确处理了以下情况：
        // - 如果子节点的父节点在查询结果中，会被添加到父节点的 children 中
        // - 如果子节点的父节点不在查询结果中，会被当作根节点处理（作为虚拟根节点）
        // - 支持多棵树的情况：多个根节点会生成多棵树，每个根节点及其子节点构成一棵独立的树
        // 这样可以确保查询结果中的所有数据都能被正确显示
        List<MenuTreeVO> rootNodes = buildMenuTree(allMenus);

        // 3. 计算分页信息
        int total = rootNodes.size();
        long pageNo = pageDTO.getPageNo();
        long pageSize = pageDTO.getPageSize();
        int start = (int) ((pageNo - 1) * pageSize);
        int end = Math.min(start + (int) pageSize, total);

        // 4. 对根节点进行内存分页
        // 说明：树结构分页的复杂性在于一个根节点可能包含多个子节点，
        // SQL 层面很难直接实现树结构的分页，因此采用内存分页的方式
        List<MenuTreeVO> pagedRootNodes = (start < total)
                ? rootNodes.subList(start, end)
                : new ArrayList<>();

        // 5. 构建分页结果
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

    @Override
    public List<SystemMenuTreeVO> listSystemMenuTree(List<String> systemIds) {
        List<IamSystem> list = iSystemService.list(new LambdaQueryWrapper<IamSystem>()
                .in(CollUtil.isNotEmpty(systemIds), IamSystem::getId, systemIds));
        List<SystemMenuTreeVO> systemMenuTreeVO = VoMapper.mapToVoList(list, SystemMenuTreeVO.class);
        if (CollUtil.isEmpty(systemMenuTreeVO)) {
            return List.of();
        }
        List<MenuTreeVO> menuTreeVOS = listTree(MenuDTO.builder()
                .status(true).build());
        for (SystemMenuTreeVO systemMenuTreeVO1 : systemMenuTreeVO) {
            List<MenuTreeVO> list1 = menuTreeVOS.stream().filter(item -> item.getSystemId().equals(systemMenuTreeVO1.getId()))
                    .toList();
            systemMenuTreeVO1.setMenus(list1);
        }
        return systemMenuTreeVO;
    }

    @Override
    public boolean modify(AddOrModifyMenuDTO param) {
        Menu menu = iMenuService.getById(param);
        if (menu == null) {
            Ex.throwEx(MENU_NOT_EXIST);
        }
        if (param.getStatus() != null) {
            menu.setStatus(!menu.getStatus());
        }
        if (param.getVisible() != null) {
            menu.setVisible(!menu.getVisible());
        }
        return iMenuService.updateById(menu);
    }

    /**
     * 构建菜单树结构
     * 支持多棵树的情况：多个根节点会生成多棵树，每个根节点及其子节点构成一棵独立的树
     *
     * @param menuList 平铺的菜单列表
     * @return 树结构的菜单列表（可能包含多棵树，每个根节点是一棵树的根）
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuVO> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 转换为 MenuTreeVO 并构建 ID 映射（一次性完成，避免多次遍历）
        var menuMap = menuList.stream()
                .map(menu -> VoMapper.mapToVo(menu, MenuTreeVO.class))
                .collect(Collectors.toMap(MenuTreeVO::getId, menu -> menu));

        // 2. 构建树结构并分离根节点
        var rootNodes = new ArrayList<MenuTreeVO>();
        menuMap.values().forEach(menu -> {
            var parentId = menu.getParentId();
            // 使用 Map.get() 替代 containsKey() + get()，更符合 JDK 17 风格
            var parent = StringUtils.hasText(parentId) ? menuMap.get(parentId) : null;
            if (parent != null) {
                // 有父节点，添加到父节点的 children
                parent.addChild(menu);
            } else {
                // 没有父节点或父节点不存在，作为根节点
                rootNodes.add(menu);
            }
        });

        return rootNodes;
    }
}

