package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.Staff;

import java.util.List;

public interface StaffService extends IService<Staff> {

    Staff login(String username, String password);

    Staff getByUsername(String username);

    Staff getStaffWithDeptById(Long id);

    PageResult<Staff> getStaffPage(Integer page, Integer size, String keyword, Long departmentId);

    boolean checkUsernameExists(String username);

    boolean checkStaffCodeExists(String staffCode);

    boolean saveStaff(Staff staff);

    boolean updateStaff(Staff staff);

    boolean deleteStaff(Long id);

    boolean updateStatus(Long id, Integer status);

    boolean updatePassword(Long staffId, String oldPassword, String newPassword);

    List<String> getPermissionsByStaffId(Long staffId);

    boolean hasPermission(Long staffId, String permissionCode);
}
