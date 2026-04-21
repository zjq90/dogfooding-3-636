package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Staff;
import com.library.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/page")
    public Result<PageResult<Staff>> getStaffPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId) {

        PageResult<Staff> result = staffService.getStaffPage(page, size, keyword, departmentId);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<Staff>> getStaffList(@RequestParam(required = false) Long departmentId) {
        List<Staff> list = staffService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Staff> getStaffById(@PathVariable Long id) {
        Staff staff = staffService.getStaffWithDeptById(id);
        if (staff != null) {
            staff.setPassword(null);
            List<String> permissions = staffService.getPermissionsByStaffId(id);
            staff.setPermissions(permissions);
            return Result.success(staff);
        }
        return Result.error("人员不存在");
    }

    @GetMapping("/{id}/permissions")
    public Result<List<String>> getStaffPermissions(@PathVariable Long id) {
        List<String> permissions = staffService.getPermissionsByStaffId(id);
        return Result.success(permissions);
    }

    @PostMapping
    public Result<Void> addStaff(@RequestBody Staff staff) {
        log.info("新增内部人员: {}", staff.getStaffName());

        if (staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (staff.getStaffCode() == null || staff.getStaffCode().trim().isEmpty()) {
            return Result.error("人员编号不能为空");
        }
        if (staff.getUsername() == null || staff.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (staff.getDepartmentId() == null) {
            return Result.error("所属部门不能为空");
        }

        boolean success = staffService.saveStaff(staff);
        if (success) {
            log.info("人员添加成功: {}", staff.getStaffName());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        log.info("更新内部人员信息: {}", id);

        staff.setId(id);

        if (staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (staff.getStaffCode() == null || staff.getStaffCode().trim().isEmpty()) {
            return Result.error("人员编号不能为空");
        }
        if (staff.getUsername() == null || staff.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (staff.getDepartmentId() == null) {
            return Result.error("所属部门不能为空");
        }

        boolean success = staffService.updateStaff(staff);
        if (success) {
            log.info("人员更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteStaff(@PathVariable Long id) {
        log.info("删除内部人员: {}", id);

        boolean success = staffService.deleteStaff(id);
        if (success) {
            log.info("人员删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新人员状态: {}, status: {}", id, status);

        boolean success = staffService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable Long id,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        log.info("修改密码: {}", id);

        boolean success = staffService.updatePassword(id, oldPassword, newPassword);
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.error("密码修改失败");
    }
}
