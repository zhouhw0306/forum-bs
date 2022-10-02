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

// =======================> 文章 API
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