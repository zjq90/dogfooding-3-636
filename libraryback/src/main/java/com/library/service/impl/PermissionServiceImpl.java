package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Permission;
import com.library.mapper.PermissionMapper;
import com.library.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getMenuTree() {
        List<Permission> allMenus = permissionMapper.selectMenuPermissions();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<Permission> getAllMenus() {
        return permissionMapper.selectAllMenus();
    }

    @Override
    public List<Permission> getButtonPermissions() {
        return permissionMapper.selectButtonPermissions();
    }

    @Override
    public List<Permission> getPermissionsByDepartmentId(Long departmentId) {
        return permissionMapper.selectByDepartmentId(departmentId);
    }

    @Override
    public List<Permission> getAllPermissionsWithDeptStatus(Long departmentId) {
        List<Permission> allPermissions = this.list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder));

        if (departmentId != null) {
            List<Permission> deptPermissions = getPermissionsByDepartmentId(departmentId);
            Set<Long> deptPermissionIds = deptPermissions.stream()
                    .map(Permission::getId)
                    .collect(Collectors.toSet());

            for (Permission permission : allPermissions) {
                permission.setHasPermission(deptPermissionIds.contains(permission.getId()));
            }
        }

        return allPermissions;
    }

    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        return permissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .peek(p -> p.setChildren(buildTree(permissions, p.getId())))
                .collect(Collectors.toList());
    }
}
