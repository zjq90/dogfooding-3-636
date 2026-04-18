package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT * FROM sys_department WHERE deleted = 0 ORDER BY sort_order, id")
    List<Department> selectAll();
}
