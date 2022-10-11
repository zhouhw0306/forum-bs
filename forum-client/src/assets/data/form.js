// 匹配规则
const rules = {
  username: [
    { required: true, trigger: 'blur', message: '请输入账号'}
  ],
  password: [
    { required: true, trigger: 'blur', message: '请输入密码' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phoneNum: [
    { message: '请选择日期', trigger: 'blur' }
  ],
  email: [
    { message: '请输入邮箱地址', trigger: 'blur' },
    { required: true,type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  birth: [
    { type: 'date', message: '请选择日期', trigger: 'change' }
  ],
  introduction: [
    { message: '请输入介绍', trigger: 'blur' }
  ],
  location: [
    { message: '请输入地区', trigger: 'change' }
  ],
  dept: [
    { required: true, message: '请输入系部', trigger: 'change' }
  ],
  checkCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}
// 系部选择
const depts = [{
  value: '信息工程系',
  label: '信息工程系'
}, {
  value: '化学化工系',
  label: '化学化工系'
},{
  value: '数学系',
  label: '数学系'
}, {
  value: '物理系',
  label: '物理系'
}, {
  value: '电子科学与工程系',
  label: '电子科学与工程系'
}, {
  value: '管理系',
  label: '管理系'
}, {
  value: '法律系',
  label: '法律系'
}, {
  value: '新闻传播系',
  label: '新闻传播系'
},{
  value: '汉语言文学系',
  label: '汉语言文学系'
}, {
  value: '哲学系',
  label: '哲学系'
}, {
  value: '生命科学系',
  label: '生命科学系'
}, {
  value: '医学系',
  label: '医学系'
},]
// 地区选择
const cities = [{
  value: '北京',
  label: '北京'
}, {
  value: '天津',
  label: '天津'
}, {
  value: '河北',
  label: '河北'
}, {
  value: '山西',
  label: '山西'
}, {
  value: '内蒙古',
  label: '内蒙古'
}, {
  value: '辽宁',
  label: '辽宁'
}, {
  value: '吉林',
  label: '吉林'
}, {
  value: '黑龙江',
  label: '黑龙江'
}, {
  value: '上海',
  label: '上海'
}, {
  value: '江苏',
  label: '江苏'
}, {
  value: '浙江',
  label: '浙江'
}, {
  value: '安徽',
  label: '安徽'
}, {
  value: '福建',
  label: '福建'
}, {
  value: '江西',
  label: '江西'
}, {
  value: '山东',
  label: '山东'
}, {
  value: '河南',
  label: '河南'
}, {
  value: '湖北',
  label: '湖北'
}, {
  value: '湖南',
  label: '湖南'
}, {
  value: '广东',
  label: '广东'
}, {
  value: '广西',
  label: '广西'
}, {
  value: '海南',
  label: '海南'
}, {
  value: '重庆',
  label: '重庆'
}, {
  value: '四川',
  label: '四川'
}, {
  value: '贵州',
  label: '贵州'
}, {
  value: '云南',
  label: '云南'
}, {
  value: '西藏',
  label: '西藏'
}, {
  value: '陕西',
  label: '陕西'
}, {
  value: '甘肃',
  label: '甘肃'
}, {
  value: '青海',
  label: '青海'
}, {
  value: '宁夏',
  label: '宁夏'
}, {
  value: '新疆',
  label: '新疆'
}, {
  value: '香港',
  label: '香港'
}, {
  value: '澳门',
  label: '澳门'
}, {
  value: '台湾',
  label: '台湾'
}]

export {
  rules,
  cities,
  depts
}
