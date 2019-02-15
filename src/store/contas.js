import repo from './repo'
import Vue from 'vue'

const state = {
  byId: {
  },
  list: []
}

const getters = {
}

const mutations = {
  addConta(state, conta) {
    Vue.set(state.byId, conta.id, conta)
    state.list.push(conta)
  },
  removeConta(state, conta) {
    delete state.byId[conta.id]
    const ix = state.list.findIndex(c => c.id === conta.id)
    state.list.splice(ix, 1)
  }

}

const actions = {
  posLogin({commit, dispatch}) {
    repo.hookAdded('/contas', conta => {
      commit('addConta', conta)
      dispatch('carregaLancamentosConta', conta.id)
    })
    repo.hookRemoved('/contas', conta => {
      commit('removeConta', conta)
    })
  },
  contaCriar(_, conta) {
    repo.save('/contas', conta)
  },
  contaExcluir(_, idConta) {
    repo.remove('/contas', idConta)
    repo.remove('/lancamentos/', idConta)
  },
  contaCarrega(_, idConta) {
    return repo.load("/contas/" + idConta)
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}