import Vue from 'vue'
import VueRouter from 'vue-router'

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/write/:id?',
    component: resolve => require(['../views/BlogWrite'], resolve),
    meta: {requireLogin: true}
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
        component: resolve => require(['../views/Setting'], resolve),
        meta: {requireLogin: true}
      },
      {
        path: '/carePost',
        component: resolve => require(['../views/SecondPage'], resolve),
        meta: {requireLogin: true}
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
        component: resolve => require(['../views/Center'], resolve),
        meta: {requireLogin: true}
      },
      {
        path: '/searchPage',
        component: resolve => require(['../views/SearchPage'], resolve)
      },
      {
        path: '/admin',
        component: resolve => require(['../views/Admin'], resolve),
        meta: {requireLogin: true},
        children: [
          {
            path: '/info',
            component: resolve => require(['../components/admin/Info'], resolve),
            meta: {requireLogin: true,admin:true}
          },
          {
            path: '/userManage',
            component: resolve => require(['../components/admin/UserManage'], resolve),
            meta: {requireLogin: true,admin:true}
          },
          {
            path: '/articleManage',
            component: resolve => require(['../components/admin/ArticleManage'], resolve),
            meta: {requireLogin: true,admin:true}
          },
          {
            path: '/commentManage',
            component: resolve => require(['../components/admin/CommentManage'], resolve),
            meta: {requireLogin: true,admin:true}
          },
          {
            path: '/sourceManage',
            component: resolve => require(['../components/admin/SourceManage'], resolve),
            meta: {requireLogin: true,admin:true}
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
