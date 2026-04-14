package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Department;
import com.library.entity.DeptPermission;
import com.library.mapper.DepartmentMapper;
import com.library.mapper.DeptPermissionMapper;
import com.library.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private DeptPermissionMapper deptPermissionMapper;

    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepts = baseMapper.selectAllDepartments();
        return buildTree(allDepts, 0L);
    }

    private List<Department> buildTree(List<Department> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .peek(dept -> dept.setChildren(buildTree(depts, dept.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<Department> getDepartmentPage(Integer page, Integer size, String keyword) {
        Page<Department> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Department::getDeptName, keyword)
                    .or()
                    .like(Department::getDeptCode, keyword));
        }

        wrapper.orderByAsc(Department::getSortOrder)
                .orderByDesc(Department::getCreateTime);

        Page<Department> deptPage = this.page(pageParam, wrapper);

        List<Department> records = deptPage.getRecords();
        for (Department dept : records) {
            if (dept.getParentId() != null && dept.getParentId() > 0) {
                Department parent = this.getById(dept.getParentId());
                if (parent != null) {
                    dept.setParentName(parent.getDeptName());
                }
            }
        }

        return new PageResult<>(deptPage.getTotal(), records,
                deptPage.getCurrent(), deptPage.getSize());
    }

    @Override
    public Department getDepartmentByCode(String deptCode) {
        return baseMapper.selectByDeptCode(deptCode);
    }

    @Override
    public boolean checkDeptCodeExists(String deptCode) {
        return getDepartmentByCode(deptCode) != null;
    }

    @Override
    @Transactional
    public boolean saveDepartment(Department department) {
        if (checkDeptCodeExists(department.getDeptCode())) {
            throw new BusinessException("部门编号已存在");
        }

        if (department.getParentId() == null) {
            department.setParentId(0L);
        }

        if (department.getStatus() == null) {
            department.setStatus(1);
        }

        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }

        return this.save(department);
    }

    @Override
    @Transactional
    public boolean updateDepartment(Department department) {
        Department existDept = getDepartmentByCode(department.getDeptCode());
        if (existDept != null && !existDept.getId().equals(department.getId())) {
            throw new BusinessException("部门编号已存在");
        }

        if (department.getParentId() != null && department.getParentId().equals(department.getId())) {
            throw new BusinessException("上级部门不能是自己");
        }

        return this.updateById(department);
    }

    @Override
    @Transactional
    public boolean deleteDepartment(Long id) {
        long childCount = this.count(new LambdaQueryWrapper<Department>()
                .eq(Department::getParentId, id)
                .eq(Department::getDeleted, 0));

        if (childCount > 0) {
            throw new BusinessException("该部门下存在子部门，无法删除");
        }

        int staffCount = baseMapper.countStaffByDeptId(id);
        if (staffCount > 0) {
            throw new BusinessException("该部门下存在人员，无法删除");
        }

        deptPermissionMapper.deleteByDepartmentId(id);

        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        Department department = new Department();
        department.setId(id);
        department.setStatus(status);
        return this.updateById(department);
    }

    @Override
    @Transactional
    public Result<Void> setPermissions(Long departmentId, List<Long> permissionIds) {
        if (departmentId == null) {
            return Result.error("部门ID不能为空");
        }

        Department department = this.getById(departmentId);
        if (department == null) {
            return Result.error("部门不存在");
        }

        deptPermissionMapper.deleteByDepartmentId(departmentId);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            deptPermissionMapper.batchInsert(departmentId, permissionIds);
        }

        return Result.success("权限设置成功");
    }

    @Override
    public List<Long> getPermissionIdsByDeptId(Long departmentId) {
        return deptPermissionMapper.selectPermissionIdsByDeptId(departmentId);
    }
}
