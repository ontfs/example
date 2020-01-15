import axios from 'axios'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: process.env.VUE_APP_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 30000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    config.data = JSON.stringify(config.data)
    config.headers = {
      'Content-Type': 'application/json'
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    return res
  },
  error => {
    const rest = error.response
    console.error('err' + error) // for debug
    if (rest) {
      if (rest.status === 400) {
        Message({ message: rest.data.desc, type: 'error' })
      }
    } else {
      Message({ message: 'Api Error, Please Try Again!', type: 'error' })
    }
    return Promise.reject(error)
  }
)

export default service
