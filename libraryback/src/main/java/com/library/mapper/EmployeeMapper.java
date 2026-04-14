package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    @Select("SELECT e.*, d.name as department_name FROM sys_employee e " +
            "LEFT JOIN sys_department d ON e.department_id = d.id " +
            "WHERE e.deleted = 0 " +
            "ORDER BY e.create_time DESC")
    Page<Employee> selectEmployeePage(Page<Employee> page);

    @Select("SELECT e.*, d.name as department_name FROM sys_employee e " +
            "LEFT JOIN sys_department d ON e.department_id = d.id " +
            "WHERE e.deleted = 0 AND e.id = #{id}")
    Employee selectEmployeeById(@Param("id") Long id);
}
