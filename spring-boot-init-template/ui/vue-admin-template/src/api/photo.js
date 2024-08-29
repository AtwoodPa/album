import request from '@/utils/request'

export function getPhotos(data) {
  return request({
    url: '/admin/photo/page',
    method: 'get',
    params: data
  })
}
export function addPhotoWithTags(photo, selectedTagIds) {
  return request({
    url: '/photo/addPhotoWithTags',
    method: 'post',
    data: {
      photo: photo,
      selectedTagIds: selectedTagIds
    }
  })
}
export function updatePhotoWithTags(photo, selectedTagIds) {
  return request({
    url: '/admin/photo/update',
    method: 'put',
    data: {
      photo: photo,
      selectedTagIds: selectedTagIds
    }
  })
}
export function addPhoto(data) {
  return request({
    url: '/admin/photo/add',
    method: 'post',
    data
  })
}

export function deletePhoto(data) {
  return request({
    url: '/admin/photo/delete/' + data,
    method: 'delete'
  })
}

export function updatePhoto(data) {
  return request({
    url: '/admin/photo/update',
    method: 'put',
    data
  })
}

export function uploadPhoto(data) {
  return request({
    url: '/admin/photo/upload/photo',
    method: 'put',
    data
  })
}
