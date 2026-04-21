package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Department;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT * FROM sys_department WHERE deleted = 0 ORDER BY sort_order, create_time")
    List<Department> selectAllDepartments();

    @Select("SELECT * FROM sys_department WHERE parent_id = #{parentId} AND deleted = 0 AND status = 1 ORDER BY sort_order")
    List<Department> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_department WHERE dept_code = #{deptCode} AND deleted = 0 LIMIT 1")
    Department selectByDeptCode(@Param("deptCode") String deptCode);

    @Select("SELECT COUNT(*) FROM sys_staff WHERE department_id = #{deptId} AND deleted = 0 AND status = 1")
    int countStaffByDeptId(@Param("deptId") Long deptId);
}
