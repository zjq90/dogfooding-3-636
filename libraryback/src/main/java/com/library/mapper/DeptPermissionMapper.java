package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.DeptPermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeptPermissionMapper extends BaseMapper<DeptPermission> {

    @Delete("DELETE FROM sys_dept_permission WHERE department_id = #{departmentId}")
    void deleteByDepartmentId(@Param("departmentId") Long departmentId);

    @Select("SELECT permission_id FROM sys_dept_permission WHERE department_id = #{departmentId}")
    List<Long> selectPermissionIdsByDeptId(@Param("departmentId") Long departmentId);

    void batchInsert(@Param("departmentId") Long departmentId, @Param("permissionIds") List<Long> permissionIds);
}
