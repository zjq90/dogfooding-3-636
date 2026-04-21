import request from '@/utils/request'

export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

export function getAllMenus() {
  return request({
    url: '/permission/menus',
    method: 'get'
  })
}

export function getButtonPermissions() {
  return request({
    url: '/permission/buttons',
    method: 'get'
  })
}

export function getPermissionList() {
  return request({
    url: '/permission/list',
    method: 'get'
  })
}

export function getPermissionsByDeptId(deptId) {
  return request({
    url: `/permission/dept/${deptId}`,
    method: 'get'
  })
}

export function getAllPermissionsWithDeptStatus(departmentId) {
  return request({
    url: '/permission/all-with-dept',
    method: 'get',
    params: { departmentId }
  })
}
