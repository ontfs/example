import fileServer from './fileRequest'

export function uploadfile(data) {
  return fileServer({
    url: 'http://192.168.1.129:10665/api/v1/data/upload',
    method: 'post',
    data
  })
}
