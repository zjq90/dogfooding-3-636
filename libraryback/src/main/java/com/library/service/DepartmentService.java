package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {

    List<Department> getDepartmentTree();

    PageResult<Department> getDepartmentPage(Integer page, Integer size, String keyword);

    Department getDepartmentByCode(String deptCode);

    boolean checkDeptCodeExists(String deptCode);

    boolean saveDepartment(Department department);

    boolean updateDepartment(Department department);

    boolean deleteDepartment(Long id);

    boolean updateStatus(Long id, Integer status);

    Result<Void> setPermissions(Long departmentId, List<Long> permissionIds);

    List<Long> getPermissionIdsByDeptId(Long departmentId);
}
