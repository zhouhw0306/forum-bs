import axios from 'axios';
import { get, post } from './http'
// =======================> 用户 API
// 登录
export const loginIn = (params) => post(`api/login/status`, params)
// 注册
export const SignUp = (params) => post(`api/user/add`, params)
// 发送注册邮箱验证码
export const ApiSendCode = (params) => post(`api/sigIn/code`, params)
// 获取登录User对象
export const getAuthor = () => get(`api/getUser`)
// 获取指定id用户的部分信息
export const getAuthorById =(id) => get(`api/getUserById/${id}`)
// 根据积分排序
export const getByScore = () => get(`api/getByScore`)
// 更新用户信息
export const updateUser = (params) => post(`api/updateUser`,params)

// =======================> 文章 API
//获取帖子数量
export const getAllNoPage = () => get(`articles/getAllNOPage`)
//模糊查询
export const searchData = (word) => get(`articles/by/${word}`)
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
export function getArticles(isCareMe, index, page) {
    return axios({
        url: 'articles/getAll',
        method: 'get',
        params: {
            pageNumber: page.pageNumber,
            pageSize: page.pageSize,
            sort: page.sort,
            index: index,
            isCareMe: isCareMe
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

// 判断是否收藏文章
export const isFavour = (id) => get(`hasArticle/isFavour/${id}`)

// 更新(用户&文章)收藏关系
export const articleFavour = (params) => post(`hasArticle/favour`,params)

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

//条件筛选获取资源
export const getTableData = (params) => post(`source/vo`,params)

//点赞
export const thumb = (params) => post('source/thumb',params)

//收藏
export const favour = (params) => post('source/favour',params)

//根据id获取资源
export const getSourceById = (id) => get(`source/${id}`)

//添加资源
export const sourceShare = (source) => post(`source/insert`,source)

//获取收藏资源
export const getHasFavour = () => get(`source/getHasFavour`)

// =======================> 管理员 API
//获取所有用户
export const getAllUser = () => post(`api/getAllUser`)

//获取所有资源
export const getSourceAll = (state) => post(`source/getAll/${state}`)

//获取所有评论
export const getCommentAll = () => post(`comment/getCommentAll`)

//lockOrUnlock用户
export const lockOrUnlock = (id) => post(`api/lockOrUnlock/${id}`)

//删除文章
export const deleteArticle = (id) => post(`articles/deleteById/${id}`)

//删除评论
export const deleteComment = (params) => post(`comment/delete`,params)

//删除资源
export const deleteSource = (id) => post(`source/delete/${id}`)

//审核资源
export const sourcePass = (param) => post(`source/pass`,param)
