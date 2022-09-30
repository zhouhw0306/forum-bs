import Vue from 'vue'
import Vuex from 'vuex'
import configure from './configure'
import user from './user'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    configure,
    user
  }
})
