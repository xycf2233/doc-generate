import axios from 'axios'

import {Message} from 'element-ui'

import store from '@/store'


// create an axios instance axios

// baseURL : 以后用service 发出的任何请求 都会加上http://localhost:8090 一旦服务器的地址改变了 我们只要改一个地方就行

// 创建axios 用这个axios 发出的任何请求 都带有前缀

const service = axios.create({

    baseURL: 'http://localhost:7000', // url = base url + request url8090

    // withCredentials: true, // send cookies when cross-domain requests

    timeout: 20000 // request timeout

})


// 以后用service 发出的请求都会被拦截

service.interceptors.request.use(
    config => {

        // do something before request is sent

        // 我们每往服务器发一个request 都会往请求头里面添加一个 token(令牌)

        if (store.state.user.token) {

            config.headers['X-Token'] = store.state.user.token

        }

        // config.headers['X-Token'] = '123'

        // 拦截请求 在请求头加一个键值对

        // config.headers['X-Token'] = '123'

        return config

    },

    error => {

        // do something with request error

        console.log(error) // for debug

        return Promise.reject(error)

    }
)


// 用service 发的请求 响应也会被拦截

service.interceptors.response.use(
    // 正常会来 status200

    response => {

        const res = response.data


        // if the custom code is not 20000, it is judged as an error.

        if (res.code !== '200') {

            // elemet ui mssage插件

            Message({

                message: res.message || 'Error',

                type: 'error',

                duration: 5 * 1000

            })

        }

        return res

    },

    // 400 前端 // 500 服务器

    // status 不是200 403 404 405

    error => {

        console.log('err' + error) // for debug

        Message({

            message: error.message,

            type: 'error',

            duration: 5 * 1000

        })

        return Promise.reject(error)

    }
)


export default service