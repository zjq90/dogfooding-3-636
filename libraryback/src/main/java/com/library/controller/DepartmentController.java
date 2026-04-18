package com.library.controller;

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

    @GetMapping("/list")
    public Result<List<Department>> getAllDepartments(@RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看部门列表");
        }
        List<Department> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    @GetMapping("/tree")
    public Result<List<Department>> getDepartmentTree(@RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看部门树");
        }
        List<Department> departments = departmentService.getDepartmentTree();
        return Result.success(departments);
    }

    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看部门信息");
        }
        Department department = departmentService.getById(id);
        if (department != null) {
            return Result.success(department);
        }
        return Result.error("部门不存在");
    }

    @PostMapping
    public Result<Void> addDepartment(@RequestBody Department department, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限添加部门");
        }

        log.info("新增部门: {}", department.getName());

        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return Result.error("部门名称不能为空");
        }
        if (department.getCode() == null || department.getCode().trim().isEmpty()) {
            return Result.error("部门编号不能为空");
        }
        if (!department.getCode().matches("^B-\\d+$")) {
            return Result.error("部门编号格式错误，格式应为：B-数字");
        }

        boolean success = departmentService.save(department);
        if (success) {
            log.info("部门添加成功: {}", department.getName());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateDepartment(@PathVariable Long id, @RequestBody Department department, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限修改部门");
        }

        log.info("更新部门信息: {}", id);
        department.setId(id);
        boolean success = departmentService.updateById(department);
        if (success) {
            log.info("部门更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限删除部门");
        }

        log.info("删除部门: {}", id);
        boolean success = departmentService.removeById(id);
        if (success) {
            log.info("部门删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
