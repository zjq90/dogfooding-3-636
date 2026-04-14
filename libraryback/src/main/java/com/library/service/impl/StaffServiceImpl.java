package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Staff;
import com.library.mapper.StaffMapper;
import com.library.service.StaffService;
import com.library.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Staff login(String username, String password) {
        Staff staff = baseMapper.selectByUsername(username);
        if (staff == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (staff.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!PasswordUtil.matches(password, staff.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String cacheKey = "staff:" + staff.getId();
        redisTemplate.opsForValue().set(cacheKey, staff, 30, TimeUnit.MINUTES);

        return staff;
    }

    @Override
    public Staff getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public Staff getStaffWithDeptById(Long id) {
        return baseMapper.selectStaffWithDeptById(id);
    }

    @Override
    public PageResult<Staff> getStaffPage(Integer page, Integer size, String keyword, Long departmentId) {
        Page<Staff> pageParam = new Page<>(page, size);

        Page<Staff> staffPage = baseMapper.selectStaffPage(pageParam, keyword, departmentId);

        return new PageResult<>(staffPage.getTotal(), staffPage.getRecords(),
                staffPage.getCurrent(), staffPage.getSize());
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return getByUsername(username) != null;
    }

    @Override
    public boolean checkStaffCodeExists(String staffCode) {
        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Staff::getStaffCode, staffCode);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean saveStaff(Staff staff) {
        if (checkUsernameExists(staff.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (checkStaffCodeExists(staff.getStaffCode())) {
            throw new BusinessException("人员编号已存在");
        }

        if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
            staff.setPassword(PasswordUtil.encode(staff.getPassword()));
        } else {
            staff.setPassword(PasswordUtil.encode("123456"));
        }

        if (staff.getStatus() == null) {
            staff.setStatus(1);
        }

        return this.save(staff);
    }

    @Override
    @Transactional
    public boolean updateStaff(Staff staff) {
        Staff existStaff = getByUsername(staff.getUsername());
        if (existStaff != null && !existStaff.getId().equals(staff.getId())) {
            throw new BusinessException("用户名已存在");
        }

        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Staff::getStaffCode, staff.getStaffCode());
        Staff existByCode = this.getOne(wrapper);
        if (existByCode != null && !existByCode.getId().equals(staff.getId())) {
            throw new BusinessException("人员编号已存在");
        }

        if (StringUtils.hasText(staff.getPassword())) {
            staff.setPassword(PasswordUtil.encode(staff.getPassword()));
        } else {
            staff.setPassword(null);
        }

        return this.updateById(staff);
    }

    @Override
    @Transactional
    public boolean deleteStaff(Long id) {
        redisTemplate.delete("staff:" + id);
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        Staff staff = new Staff();
        staff.setId(id);
        staff.setStatus(status);
        return this.updateById(staff);
    }

    @Override
    @Transactional
    public boolean updatePassword(Long staffId, String oldPassword, String newPassword) {
        Staff staff = this.getById(staffId);
        if (staff == null) {
            throw new BusinessException("用户不存在");
        }

        if (!PasswordUtil.matches(oldPassword, staff.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        staff.setPassword(PasswordUtil.encode(newPassword));
        return this.updateById(staff);
    }

    @Override
    public List<String> getPermissionsByStaffId(Long staffId) {
        return baseMapper.selectPermissionsByStaffId(staffId);
    }

    @Override
    public boolean hasPermission(Long staffId, String permissionCode) {
        List<String> permissions = getPermissionsByStaffId(staffId);
        return permissions.contains(permissionCode);
    }
}
