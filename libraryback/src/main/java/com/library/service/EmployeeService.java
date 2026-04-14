package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {

    PageResult<Employee> getEmployeePage(Integer pageNum, Integer pageSize);

    Employee getEmployeeWithPermissions(Long id);

    void setEmployeePermissions(Long employeeId, List<Long> menuIds);
}
