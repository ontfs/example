import request from './request'

export function Register(data) {
  return request({
    url: '/api/v1/account/register',
    method: 'post',
    data
  })
}

export function Login(data) {
  return request({
    url: '/api/v1/account/login',
    method: 'POST',
    data
  })
}

export function getFileList(ontId) {
  return request({
    url: `/api/v1/data/${ontId}`,
    method: 'GET'
  })
}

export function downloadFile(data) {
  return request({
    url: `/api/v1/data/download-url?password=${data.password}&fileHash=${data.fileHash}`,
    method: 'GET'
  })
}
