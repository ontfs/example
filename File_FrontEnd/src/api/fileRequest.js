import axios from 'axios'
import { Message } from 'element-ui'

const fileServer = axios.create({
  // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 10000 // request timeout
})

// request interceptor
fileServer.interceptors.request.use(
  config => {
    // do something before request is sent
    config.headers = {
      'Content-Type': 'multipart/form-data'
    }
    return config
    //   if (store.getters.token) {
    // let each request carry token
    // ['X-Token'] is a custom headers key
    // please modify it according to the actual situation
    // config.headers['X-Token'] = getToken()
    //   }
    //   return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
fileServer.interceptors.response.use(
  response => {
    const res = response.data
    return res
  },
  error => {
    const rest = error.response
    // console.log(rest)
    if (rest) {
      if (rest.status === 400) {
        // 提示错误
        Message({ message: rest.data.desc, type: 'error' })
      }
    } else {
      Message({ message: error, type: 'error' })
    }
    console.error('err' + error) // for debug
    return Promise.reject(error)
  }
)

export default fileServer
