import request from '@/utils/request'

export function getStaffPage(params) {
  return request({
    url: '/staff/page',
    method: 'get',
    params
  })
}

export function getStaffList(params) {
  return request({
    url: '/staff/list',
    method: 'get',
    params
  })
}

export function getStaffDetail(id) {
  return request({
    url: `/staff/${id}`,
    method: 'get'
  })
}

export function addStaff(data) {
  return request({
    url: '/staff',
    method: 'post',
    data
  })
}

export function updateStaff(id, data) {
  return request({
    url: `/staff/${id}`,
    method: 'put',
    data
  })
}

export function deleteStaff(id) {
  return request({
    url: `/staff/${id}`,
    method: 'delete'
  })
}

export function updateStaffStatus(id, status) {
  return request({
    url: `/staff/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function updateStaffPassword(id, oldPassword, newPassword) {
  return request({
    url: `/staff/${id}/password`,
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

export function getStaffPermissions(id) {
  return request({
    url: `/staff/${id}/permissions`,
    method: 'get'
  })
}
