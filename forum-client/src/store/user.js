const user = {
  state: {
    userId: '',
    username: '',
    avatar: '',
    token:'',
    role:'',
    loginIn: false // 用户是否登录
  },
  getters: {
    userId: state => {
      let userId = state.userId
      if (!userId) {
        userId = JSON.parse(window.localStorage.getItem('userId') || null)
      }
      return userId
    },
    username: state => {
      let username = state.username
      if (!username) {
        username = JSON.parse(window.localStorage.getItem('username') || null)
      }
      return username
    },
    avatar: state => {
      let avatar = state.avatar
      if (!avatar) {
        avatar = JSON.parse(window.localStorage.getItem('avatar') || null)
      }
      return avatar
    },
    token: state => {
      let token = state.token
      if (!token){
        token = JSON.parse(window.localStorage.getItem('token') || null)
      }
      return token;
    },
    role: state => {
      let role = state.role
      if (!role){
        role = JSON.parse(window.localStorage.getItem('role') || null)
      }
      return role;
    },
    loginIn: state => {
      let loginIn = state.loginIn
      if (!loginIn) {
        loginIn = JSON.parse(window.localStorage.getItem('loginIn') || null)
      }
      return loginIn
    }
  },
  mutations: {
    setUserId: (state, userId) => {
      state.userId = userId
      userId === '' ? window.localStorage.removeItem('userId') : window.localStorage.setItem('userId', JSON.stringify(userId))
    },
    setUsername: (state, username) => {
      state.username = username
      username === '' ? window.localStorage.removeItem('username') : window.localStorage.setItem('username', JSON.stringify(username))
    },
    setAvatar: (state, avatar) => {
      state.avatar = avatar
      avatar ==='' ? window.localStorage.removeItem('avatar') : window.localStorage.setItem('avatar', JSON.stringify(avatar));
    },
    setToken : (state,token) => {
      state.token = token
      token ==='' ? window.localStorage.removeItem('token') : window.localStorage.setItem('token', JSON.stringify(token));
    },
    setRole : (state,role) => {
      state.role = role
      role ==='' ? window.localStorage.removeItem('role') : window.localStorage.setItem('role', JSON.stringify(role));
    },
    setLoginIn: (state, loginIn) => {
      state.loginIn = loginIn
      window.localStorage.setItem('loginIn', JSON.stringify(loginIn))
    }
  },
  actions: {}
}

export default user
