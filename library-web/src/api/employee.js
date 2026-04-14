import request from '@/utils/request'

export function getEmployeeList(pageNum, pageSize) {
  return request({
    url: '/employee/page',
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

export function getEmployeeDetail(id) {
  return request({
    url: `/employee/${id}`,
    method: 'get'
  })
}

export function addEmployee(data) {
  return request({
    url: '/employee',
    method: 'post',
    data
  })
}

export function updateEmployee(id, data) {
  return request({
    url: `/employee/${id}`,
    method: 'put',
    data
  })
}

export function deleteEmployee(id) {
  return request({
    url: `/employee/${id}`,
    method: 'delete'
  })
}

export function setEmployeePermissions(id, menuIds) {
  return request({
    url: `/employee/${id}/permissions`,
    method: 'post',
    data: menuIds
  })
}

export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}
