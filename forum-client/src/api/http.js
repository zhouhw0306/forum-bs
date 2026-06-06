/* eslint-disable */
import axios from 'axios';
import router from "@/router";

axios.defaults.timeout = 100000;  //超时时间设置
axios.defaults.withCredentials = true;  //true允许跨域
//Content-Type 响应头
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

axios.defaults.baseURL = 'http://localhost:8888';

//请求拦截器
axios.interceptors.request.use(config => {
      //config.headers['Content-type'] = "application/json;charset=utf-8"
      let token = localStorage.getItem('token') ? JSON.parse(localStorage.getItem('token')) : null
      if (token){
        config.headers['token'] = token; // 设置请求头
      }
      return config
  },error => {
      return Promise.reject(error)
  }
)
import ElementUI from 'element-ui'
//响应拦截器
axios.interceptors.response.use(
  response => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    if (response.status === 200) {
      return Promise.resolve(response);
    } else {
      return Promise.reject(response);
    }
  },
  // 服务器状态码不是2开头的的情况
  error => {
    if (error.response.status) {
      switch (error.response.status) {
        // 401: 未登录
        case 401:
          window.localStorage.clear()
          router.replace({
            path: '/login',
            query: {
              redirect: router.currentRoute.fullPath
            }
          });
          break;
        case 403:
          // console.log('管理员权限已修改请重新登录')
          // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
          setTimeout(() => {
            router.replace({
              path: '/',
              query: {
                redirect: router.currentRoute.fullPath
              }
            });
          }, 1000);
          break;

        // 404请求不存在
        case 404:
          // console.log('请求页面飞到火星去了')
          break;
        default:
          ElementUI.Notification(error.response.data.msg);
      }
      return Promise.reject(error.response);
    }
  });



/**
   * 封装get方法
   * @param url
   * @param data
   * @returns {Promise}
   */

export function get(url, params = {}) {
  return new Promise((resolve, reject) => {
    axios.get(url, {
      params: params
    })
      .then(response => {
        resolve(response.data);
      })
      .catch(err => {
        reject(err)
      })
  })
}


/**
   * 封装post请求
   * @param url
   * @param data
   * @returns {Promise}l
   */

export function post(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data)
      .then(response => {
        resolve(response.data);
      }, err => {
        reject(err)
      })
  })
}

/**
   * 封装delete请求
   * @param url
   * @param data
   * @returns {Promise}
   */

export function deletes(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.delete(url, data)
      .then(response => {
        resolve(response.data);
      }, err => {
        reject(err)
      })
  })
}

/**
   * 封装put请求
   * @param url
   * @param data
   * @returns {Promise}
   */

export function put(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data)
      .then(response => {
        resolve(response.data)
      }, err => {
        reject(err)
      })
  })
}
