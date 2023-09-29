import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'font-awesome/css/font-awesome.min.css';
import VCharts from 'v-charts'
Vue._watchers = Vue.prototype._watchers = [] //消除v-charts引入任意图表的时候报错 ：map undefined #935
Vue.use(ElementUI)
Vue.use(VCharts)
Vue.config.productionTip = false

router.beforeEach((to,from,next) => {
  if (to.meta.requireLogin === true){
    let loginIn = window.localStorage.getItem('loginIn')
    let role = window.localStorage.getItem('role')
    if (loginIn === 'true'){
      if (role === '"ADMIN"') {
        next()
        return
      }
      if (to.meta.admin === true) {
        next({
          path: '/',
        })
      }
      next()
    } else {
      next({
        path: '/login',
        query:{
          redirect: to.fullPath
        }
      })
    }
  } else {
    next()
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

Vue.directive('title',  function (el, binding) {
  document.title = el.dataset.title
})

import hljs from 'highlight.js';
// import 'highlight.js/styles/stackoverflow-dark.css'; 和mavon-editor有冲突 无法解决
Vue.directive('hljs', el => {
  let blocks = el.querySelectorAll('pre code');
  Array.prototype.forEach.call(blocks, hljs.highlightBlock);
})
