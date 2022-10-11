import axios from 'axios';
import { get, post } from './http'
// =======================> 用户 API
// 登录
export const loginIn = (params) => post(`api/login/status`, params)
// 注册
export const SignUp = (params) => post(`api/user/add`, params)
// 发送注册邮箱验证码
export const ApiSendCode = (params) => post(`api/sigIn/code`, params)
// 根据id获取User对象
export const getAuthor = (params) => get(`api/getById`,params)
// 根据积分排序
export const getByScore = () => get(`api/getByScore`)
// 更新用户信息
export const updateUser = (params) => post(`api/updateUser`,params)

// =======================> 文章 API
//获得热帖
export const getHot = () => get(`articles/getHot`)
//根据文章id获取标签
export const getTags = (params) => get(`tag/getByArticleId`,params)
//获得所有分类
export const getAllCategorys = () =>get(`category/getCatAll`)
//获得所有标签
export const getAllTags = () =>get(`tag/getTagAll`)
//根据id获得文章
export const getArticle = (id) => get(`articles/${id}`)
//获得所有文章
export function getArticles(query, page) {
    return axios({
        url: 'articles/getAll',
        method: 'get',
        params: {
            pageNumber: page.pageNumber,
            pageSize: page.pageSize,
            sort: page.sort,
            isCareMe: query.isCareMe
            // year: query.year,
            // month: query.month,
            // tagId: query.tagId,
            // categoryId: query.categoryId
        }
    })
}

export const upload = (formData) => axios({
    headers: {'Content-Type': 'multipart/form-data'},
    url: '/upload',
    method: 'post',
    data: formData
})

export function getArticleById(id) {
    return axios({
        url: `/articles/${id}`,
        method: 'get'
    })
}

export function publishArticle(article) {
    return axios({
        url: '/articles/publish',
        method: 'post',
        data: article
    })
}

//根据id获取分类名称
export const getTypeById = (params) => get(`category/getName`,params)

//获得文章评论
export const getCommentsByArticle = (params) => get(`comment/getCommentsByArticle`,params)

//发布评论
export const pushComment = (params) => post(`comment/pushComment`,params)

//添加文章阅览量
export const addViewCount = (id) => get(`articles/addViewCount/${id}`)

// =======================> 公告 API
//获得所有公告
export const getAllNotify = () => get(`notify/getAll`)

// =======================> 关注关系 API
//判断用户是否关注某人
export const isFollow = (params) => post(`subScribe/isFollowAuthor`,params)
//关注
export const addFollow = (params) => post(`subScribe/addFollow`,params)
//移除关注
export const removeFollow = (params) => post(`subScribe/removeFollow`,params)

//获得个人数据getPersonal
export const getPersonal = () => get(`api/getPersonal`)

//获得背景图
export const initBg = () => get('api/initBg')