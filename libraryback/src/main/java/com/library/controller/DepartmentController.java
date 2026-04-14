package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Department;
import com.library.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/tree")
    public Result<List<Department>> getDepartmentTree() {
        List<Department> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }

    @GetMapping("/page")
    public Result<PageResult<Department>> getDepartmentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        PageResult<Department> result = departmentService.getDepartmentPage(page, size, keyword);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<Department>> getDepartmentList() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        if (department != null) {
            if (department.getParentId() != null && department.getParentId() > 0) {
                Department parent = departmentService.getById(department.getParentId());
                if (parent != null) {
                    department.setParentName(parent.getDeptName());
                }
            }
            List<Long> permissionIds = departmentService.getPermissionIdsByDeptId(id);
            department.setPermissionIds(permissionIds);
            return Result.success(department);
        }
        return Result.error("部门不存在");
    }

    @PostMapping
    public Result<Void> addDepartment(@RequestBody Department department) {
        log.info("新增部门: {}", department.getDeptName());

        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            return Result.error("部门名称不能为空");
        }
        if (department.getDeptCode() == null || department.getDeptCode().trim().isEmpty()) {
            return Result.error("部门编号不能为空");
        }
        if (!department.getDeptCode().matches("B-\\d+")) {
            return Result.error("部门编号格式不正确，应为B-数字格式");
        }

        boolean success = departmentService.saveDepartment(department);
        if (success) {
            log.info("部门添加成功: {}", department.getDeptName());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        log.info("更新部门信息: {}", id);

        department.setId(id);

        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            return Result.error("部门名称不能为空");
        }
        if (department.getDeptCode() == null || department.getDeptCode().trim().isEmpty()) {
            return Result.error("部门编号不能为空");
        }
        if (!department.getDeptCode().matches("B-\\d+")) {
            return Result.error("部门编号格式不正确，应为B-数字格式");
        }

        boolean success = departmentService.updateDepartment(department);
        if (success) {
            log.info("部门更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        log.info("删除部门: {}", id);

        boolean success = departmentService.deleteDepartment(id);
        if (success) {
            log.info("部门删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新部门状态: {}, status: {}", id, status);

        boolean success = departmentService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    @PostMapping("/{id}/permissions")
    public Result<Void> setPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        log.info("设置部门权限: {}, permissions: {}", id, permissionIds);
        return departmentService.setPermissions(id, permissionIds);
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        List<Long> permissionIds = departmentService.getPermissionIdsByDeptId(id);
        return Result.success(permissionIds);
    }
}
