const configure = {
  state: {
    HOST: 'http://localhost:8888',
    searchword: '', // 搜索关键词
    index: 0, // 列表中的序号
    isActive: false
  },
  getters: {
    isActive: state => {
      let isActive = state.isActive
      if (!isActive) {
        isActive = JSON.parse(window.sessionStorage.getItem('isActive') || null)
      }
      return isActive
    },
    index: state => {
      let index = state.index
      if (!index) {
        index = JSON.parse(window.sessionStorage.getItem('index') || null)
      }
      return index
    },
    searchword: state => state.searchword
  },
  mutations: {
    setIsActive: (state, isActive) => {
      state.isActive = isActive
      window.sessionStorage.setItem('isActive', JSON.stringify(isActive))
    },

    setIndex: (state, index) => { state.index = index },
    setSearchword: (state, searchword) => { state.searchword = searchword }
  },
  actions: {}
}

export default configure
