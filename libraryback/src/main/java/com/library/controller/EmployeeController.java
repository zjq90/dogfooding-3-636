package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Employee;
import com.library.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/page")
    public Result<PageResult<Employee>> getEmployeePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看人员列表");
        }
        PageResult<Employee> result = employeeService.getEmployeePage(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看人员信息");
        }
        Employee employee = employeeService.getEmployeeWithPermissions(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("人员不存在");
    }

    @PostMapping
    public Result<Void> addEmployee(@RequestBody Employee employee, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限添加人员");
        }

        log.info("新增人员: {}", employee.getName());

        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (employee.getEmployeeNo() == null || employee.getEmployeeNo().trim().isEmpty()) {
            return Result.error("员工编号不能为空");
        }
        if (employee.getDepartmentId() == null) {
            return Result.error("所属部门不能为空");
        }

        boolean success = employeeService.save(employee);
        if (success) {
            log.info("人员添加成功: {}", employee.getName());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateEmployee(@PathVariable Long id, @RequestBody Employee employee, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限修改人员");
        }

        log.info("更新人员信息: {}", id);
        employee.setId(id);
        boolean success = employeeService.updateById(employee);
        if (success) {
            log.info("人员更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限删除人员");
        }

        log.info("删除人员: {}", id);
        boolean success = employeeService.removeById(id);
        if (success) {
            log.info("人员删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @PostMapping("/{id}/permissions")
    public Result<Void> setEmployeePermissions(@PathVariable Long id, @RequestBody List<Long> menuIds, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限设置权限");
        }

        log.info("设置人员权限: {}", id);
        employeeService.setEmployeePermissions(id, menuIds);
        return Result.success("权限设置成功");
    }
}
