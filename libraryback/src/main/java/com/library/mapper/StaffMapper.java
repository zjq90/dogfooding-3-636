package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Staff;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StaffMapper extends BaseMapper<Staff> {

    @Select("SELECT s.*, d.dept_name as department_name, d.dept_code FROM sys_staff s " +
            "LEFT JOIN sys_department d ON s.department_id = d.id " +
            "WHERE s.id = #{id} AND s.deleted = 0")
    Staff selectStaffWithDeptById(@Param("id") Long id);

    @Select("SELECT s.*, d.dept_name as department_name FROM sys_staff s " +
            "LEFT JOIN sys_department d ON s.department_id = d.id " +
            "WHERE s.username = #{username} AND s.deleted = 0")
    Staff selectByUsername(@Param("username") String username);

    Page<Staff> selectStaffPage(Page<Staff> page, @Param("keyword") String keyword,
                                 @Param("departmentId") Long departmentId);

    @Select("SELECT permission_code FROM sys_permission p " +
            "INNER JOIN sys_dept_permission dp ON p.id = dp.permission_id " +
            "INNER JOIN sys_staff s ON s.department_id = dp.department_id " +
            "WHERE s.id = #{staffId} AND p.status = 1 AND p.deleted = 0")
    List<String> selectPermissionsByStaffId(@Param("staffId") Long staffId);
}
