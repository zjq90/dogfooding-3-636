package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Menu;
import com.library.mapper.MenuMapper;
import com.library.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> getAllMenus() {
        return baseMapper.selectAll();
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = getAllMenus();

        List<Menu> rootMenus = allMenus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .collect(Collectors.toList());

        for (Menu root : rootMenus) {
            buildMenuTree(root, allMenus);
        }

        log.debug("构建菜单树完成，根节点数量: {}", rootMenus.size());
        return rootMenus;
    }

    @Override
    public List<Menu> getMenusByEmployeeId(Long employeeId) {
        return baseMapper.selectByEmployeeId(employeeId);
    }

    private void buildMenuTree(Menu parent, List<Menu> allMenus) {
        List<Menu> children = allMenus.stream()
                .filter(m -> parent.getId() != null && parent.getId().equals(m.getParentId()))
                .collect(Collectors.toList());

        parent.setChildren(children);

        for (Menu child : children) {
            buildMenuTree(child, allMenus);
        }
    }
}
