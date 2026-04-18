package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.PageResult;
import com.library.entity.Employee;
import com.library.entity.EmployeePermission;
import com.library.mapper.EmployeeMapper;
import com.library.mapper.EmployeePermissionMapper;
import com.library.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeePermissionMapper employeePermissionMapper;

    @Override
    public PageResult<Employee> getEmployeePage(Integer pageNum, Integer pageSize) {
        Page<Employee> pageParam = new Page<>(pageNum, pageSize);
        Page<Employee> employeePage = baseMapper.selectEmployeePage(pageParam);
        return new PageResult<>(employeePage.getTotal(), employeePage.getRecords(),
                employeePage.getCurrent(), employeePage.getSize());
    }

    @Override
    public Employee getEmployeeWithPermissions(Long id) {
        Employee employee = baseMapper.selectEmployeeById(id);
        if (employee != null) {
            List<Long> permissionIds = employeePermissionMapper.selectMenuIdsByEmployeeId(id);
            employee.setPermissionIds(permissionIds);
        }
        return employee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEmployeePermissions(Long employeeId, List<Long> menuIds) {
        employeePermissionMapper.deleteByEmployeeId(employeeId);
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                EmployeePermission permission = new EmployeePermission();
                permission.setEmployeeId(employeeId);
                permission.setMenuId(menuId);
                employeePermissionMapper.insert(permission);
            }
        }
        log.info("员工权限设置成功，员工ID: {}, 权限数量: {}", employeeId, menuIds == null ? 0 : menuIds.size());
    }
}
