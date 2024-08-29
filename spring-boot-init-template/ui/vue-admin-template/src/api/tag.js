import request from '@/utils/request'

export function getAllTag() {
  return request({
    url: '/tag/allTags',
    method: 'get'
  })
}
export function getTags(data) {
  return request({
    url: '/admin/tag/page',
    method: 'get',
    params: data
  })
}

export function addTag(data) {
  return request({
    url: '/admin/tag/add',
    method: 'post',
    data
  })
}

export function deleteTag(data) {
  return request({
    url: '/admin/tag/delete/' + data,
    method: 'delete'
  })
}
