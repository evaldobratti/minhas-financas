import Vue from 'vue'
import Vuex from 'vuex'
import lancamentos from './lancamentos'
import auth from './auth'
import contas from './contas'
import transferencias from './transferencias'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    auth,
    lancamentos,
    contas,
    transferencias,
  },
  mutations: {
    resetAll(state) {
      Object.assign(state, initialState)
    }
  }
})

const initialState = JSON.parse(JSON.stringify(store.state))

export default store