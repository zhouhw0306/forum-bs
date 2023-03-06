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
        path: '/source',
        component: resolve => require(['../views/Source'], resolve),
      },
      {
        path: '/details/:id',
        component: resolve => require(['../components/source/Details'], resolve)
      },
      {
        path: '/center',
        component: resolve => require(['../views/Center'], resolve)
      },
      {
        path: '/searchPage',
        component: resolve => require(['../views/SearchPage'], resolve)
      },
      {
        path: '/admin',
        component: resolve => require(['../views/Admin'], resolve),
        children: [
          {
            path: '/userManage',
            component: resolve => require(['../components/admin/UserManage'], resolve)
          },
          {
            path: '/articleManage',
            component: resolve => require(['../components/admin/ArticleManage'], resolve)
          },
          {
            path: '/commentManage',
            component: resolve => require(['../components/admin/CommentManage'], resolve)
          },
          {
            path: '/sourceManage',
            component: resolve => require(['../components/admin/SourceManage'], resolve)
          }
        ]
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
