import Vue from 'vue'
import Vuex from 'vuex'
import lancamentos from './lancamentos'
import auth from './auth'
import contas from './contas'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    auth,
    lancamentos,
    contas,
  },
  mutations: {
    resetAll(state) {
      Object.assign(state, initialState)
    }
  }
})

const initialState = JSON.parse(JSON.stringify(store.state))

export default store