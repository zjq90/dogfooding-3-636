package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    List<Permission> getMenuTree();

    List<Permission> getAllMenus();

    List<Permission> getButtonPermissions();

    List<Permission> getPermissionsByDepartmentId(Long departmentId);

    List<Permission> getAllPermissionsWithDeptStatus(Long departmentId);
}
