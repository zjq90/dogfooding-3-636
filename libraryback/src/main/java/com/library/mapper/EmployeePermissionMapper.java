package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.EmployeePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface EmployeePermissionMapper extends BaseMapper<EmployeePermission> {

    @Delete("DELETE FROM sys_employee_permission WHERE employee_id = #{employeeId}")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);

    @Select("SELECT menu_id FROM sys_employee_permission WHERE employee_id = #{employeeId}")
    List<Long> selectMenuIdsByEmployeeId(@Param("employeeId") Long employeeId);
}
