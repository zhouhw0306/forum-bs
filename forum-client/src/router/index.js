import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

const routes = [
  {
    path: '/write/:id?',
    component: resolve => require(['../views/BlogWrite'], resolve),
    meta: {
      requireLogin: true
    },
  },
  {
    path: '',
    component: resolve => require(['../views/Home'], resolve),
    meta: { title: '编程论坛' },
    children: [
      {
        path: '/',
        component: resolve => require(['../views/MainPage'], resolve)
      },
      {
        path: '/view/:id',
        component: resolve => require(['../views/BlogView'], resolve)
      },
      {
        path: '/login',
        component: resolve => require(['../views/LoginIn'], resolve)
      },
      {
        path: '/register',
        component: resolve => require(['../views/SignUp'], resolve)
      },
      {
        path: '/setting',
        component: resolve => require(['../views/Setting'], resolve)
      },
      {
        path: '/carePost',
        component: resolve => require(['../views/SecondPage'], resolve)
      },
      {
        path: '/nav',
        component: resolve => require(['../views/Nav'], resolve)
      },
      {
        path: '/source',
        component: resolve => require(['../views/Source'], resolve),
        children: [
          {
            path: '/tools/:type',
            component: resolve => require(['../components/source/Tools'], resolve)
          },
          {
            path: '/details/:id',
            component: resolve => require(['../components/source/Details'], resolve)
          }
        ]
      },
      {
        path: '/world',
        component: resolve => require(['../views/World'], resolve)
      }
    ]
  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
