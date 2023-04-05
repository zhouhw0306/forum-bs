import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import 'font-awesome/css/font-awesome.min.css';
import VCharts from 'v-charts'
Vue._watchers = Vue.prototype._watchers = [] //消除v-charts引入任意图表的时候报错 ：map undefined #935
Vue.use(mavonEditor)
Vue.use(ElementUI)
Vue.use(VCharts)
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
