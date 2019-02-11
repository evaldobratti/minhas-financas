import repo from './repo'
import Vue from 'vue'
import moment from 'moment'
import _ from 'underscore'

const state = {
  byId: {

  },
  byConta: {

  }
}

const getters = {
  lancamentos: (state) => (contas, de, ate) => {
    let lancamentosDasContas = contas.map(conta => state.byConta[conta.id] || []).reduce((ant, atual) => [...ant, ...atual], [])
    let lancamentosDoPeriodo = lancamentosDasContas.filter(l => l.data.isBetween(de, ate))

    const saldoInicialContas = contas
      .map(c => c.saldoInicial)
      .reduce((ant, atual) => ant + atual, 0) 

    const saldoAnterior = lancamentosDasContas
      .filter(l => l.data.isBefore(de))
      .map(l => l.valor)
      .reduce((ant, atual) => ant + atual, 0)

    const saldoInicial = saldoInicialContas + saldoAnterior

    const saldos = lancamentosDoPeriodo 
      .map(l => l.valor)
      .reduce((ant, atual) => [...ant, _.last(ant) + atual], [saldoInicial])
    
    return [lancamentosDoPeriodo, saldos]
  }
}

const mutations = {
  addLancamento(state, lancamento) {
    if (!state.byConta[lancamento.idConta]) {
      Vue.set(state.byConta, lancamento.idConta, [])
    }

    var depoisDe = state.byConta[lancamento.idConta].findIndex(l => l.id === lancamento.lancamentoAnterior)
    if (depoisDe >= 0) {
      state.byConta[lancamento.idConta].splice(depoisDe + 1, 0, lancamento)
    } else {
      state.byConta[lancamento.idConta].push(lancamento)
    }

    Vue.set(state.byId, lancamento.id, lancamento)
  },
  removeLancamento(state, lancamento) {
    Vue.delete(state.byId, lancamento.id)
    state.byConta[lancamento.idConta].splice(state.byConta[lancamento.idConta].findIndex(l => l.id === lancamento.id), 1)
  }
}

const actions = {
  carregaLancamentosConta({commit}, contaId) {
    repo.hookAdded('/lancamentos/' + contaId, lancamento => {
      lancamento.data = moment(lancamento.data)
      commit('addLancamento', lancamento)
    })

    repo.hookRemoved('/lancamentos/' + contaId, lancamento => {
      commit('removeLancamento', lancamento)
    })
      
  },
  lancamentoCriar({state}, lancamento) {
    lancamento.data = lancamento.data.toISOString()
    if (!lancamento.lancamentoAnterior && state.byConta[lancamento.idConta] && state.byConta[lancamento.idConta].length > 0) {
      lancamento.lancamentoAnterior = _.last(state.byConta[lancamento.idConta]).id
    }
    repo.add('/lancamentos/' + lancamento.idConta, lancamento)
  },
  lancamentoExcluir({state}, lancamento) {
    repo.remove('/lancamentos/' + lancamento.idConta, lancamento.id)
    const lancamentoReferenciava = lancamento.lancamentoAnterior
    
    const lancamentoMeReferencia = state.byConta[lancamento.idConta].find(l => l.lancamentoAnterior === lancamento.id)

    if (lancamentoMeReferencia) {
      lancamentoMeReferencia.lancamentoAnterior = lancamentoReferenciava
      
      repo.update({
        ['/lancamentos/' + lancamento.idConta + '/' + lancamentoMeReferencia.id]: lancamentoMeReferencia
      })
    }
  },
  lancamentoAtualizar(_, lancamento) {
    repo.update({
      ['/lancamentos/' + lancamento.idConta + '/' + lancamento.id]: lancamento
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}