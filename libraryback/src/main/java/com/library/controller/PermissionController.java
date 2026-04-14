package com.library.controller;

import com.library.common.Result;
import com.library.entity.Permission;
import com.library.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> tree = permissionService.getMenuTree();
        return Result.success(tree);
    }

    @GetMapping("/menus")
    public Result<List<Permission>> getAllMenus() {
        List<Permission> menus = permissionService.getAllMenus();
        return Result.success(menus);
    }

    @GetMapping("/buttons")
    public Result<List<Permission>> getButtonPermissions() {
        List<Permission> buttons = permissionService.getButtonPermissions();
        return Result.success(buttons);
    }

    @GetMapping("/list")
    public Result<List<Permission>> getPermissionList() {
        List<Permission> list = permissionService.list();
        return Result.success(list);
    }

    @GetMapping("/dept/{deptId}")
    public Result<List<Permission>> getPermissionsByDeptId(@PathVariable Long deptId) {
        List<Permission> permissions = permissionService.getPermissionsByDepartmentId(deptId);
        return Result.success(permissions);
    }

    @GetMapping("/all-with-dept")
    public Result<List<Permission>> getAllPermissionsWithDeptStatus(
            @RequestParam(required = false) Long departmentId) {
        List<Permission> permissions = permissionService.getAllPermissionsWithDeptStatus(departmentId);
        return Result.success(permissions);
    }
}
