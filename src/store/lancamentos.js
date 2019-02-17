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
  lancamentos: (state, getters) => (contas, de, ate) => {
    let lancamentosDasContas = contas.map(conta => getters.lancamentosDaConta(conta.id, de, ate)).reduce((ant, atual) => [...ant, ...atual], [])
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
  },
  lancamentosDaConta: (state, getters) => (idConta, de, ate) => {
    const data = de.clone()
    let lancamentos = []
    while (!data.isSame(ate, 'date')) {
      lancamentos = [...lancamentos, ...getters.lancamentosDaContaDoDia(idConta, data)]
      data.add(1, 'day')
    }
    lancamentos = [...lancamentos, ...getters.lancamentosDaContaDoDia(idConta, data)]
    
    return lancamentos
  },
  lancamentosDaContaDoDia: (state) => (idConta, dia) => {
    const lancamentos = (state.byConta[idConta] || []).filter(l => l.data.isSame(dia, 'date'))

    const inicioCorrente = lancamentos.find(l => l.lancamentoAnterior == null || l.lancamentoAnterior == undefined)
    if (!inicioCorrente)
      return []

    const ordenados = [inicioCorrente]

    for (let i = 1; i < lancamentos.length; i++) {
      const continuacao = lancamentos.find(l => l.lancamentoAnterior === _.last(ordenados).id)
      if (!continuacao)
        return
      ordenados.push(continuacao)
    }
    
    return ordenados
  },
  lancamentoPosterior: (state) => (lancamento) => {
    return state.byConta[lancamento.idConta].find(l => l.lancamentoAnterior === lancamento.id)
  },
  lancamentoId: (state) => (id) => {
    return state.byId[id]
  }
}

const mutations = {
  addLancamento(state, lancamento) {
    if (!state.byConta[lancamento.idConta]) {
      Vue.set(state.byConta, lancamento.idConta, [])
    }
    
    state.byConta[lancamento.idConta].push(lancamento)
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
  lancamentoSalvar({getters, dispatch}, lancamento) {
    let lancamentoTrocouData = false
    if (lancamento.id) {
      let valorSalvo = getters.lancamentoId(lancamento.id)
      
      if (!valorSalvo.data.isSame(lancamento.data, 'date')) {
          lancamentoTrocouData = true
          dispatch('lancamentoExcluir', valorSalvo)
      }
    }

    if (lancamentoTrocouData || (!lancamento.id && !lancamento.lancamentoAnterior)) {
      const doDia = getters.lancamentosDaContaDoDia(lancamento.idConta, lancamento.data)
      if (doDia.length == 0) {
        lancamento.lancamentoAnterior = null
      } else {
        lancamento.lancamentoAnterior = _.last(doDia).id
      }
    }
    
    repo.save('/lancamentos/' + lancamento.idConta, lancamento)

    if (lancamento.id)
      Object.assign(getters.lancamentoId(lancamento.id), lancamento)
  },
  lancamentoExcluir({getters, dispatch}, lancamento) {
    repo.remove('/lancamentos/' + lancamento.idConta, lancamento.id)
    const lancamentoReferenciava = lancamento.lancamentoAnterior
    
    const lancamentoMeReferencia = getters.lancamentoPosterior(lancamento)

    if (lancamentoMeReferencia) {
      lancamentoMeReferencia.lancamentoAnterior = lancamentoReferenciava
      
      dispatch('lancamentoSalvar', lancamentoMeReferencia)
    }
  },
  lancamentoSubir({state, getters, dispatch, commit}, lancamento) {
    const lancamentoAcima = state.byId[lancamento.lancamentoAnterior]
    const lancamentoAbaixo = getters.lancamentoPosterior(lancamento)
    const lancamentoAtual = getters.lancamentoId(lancamento.id)

    lancamentoAtual.lancamentoAnterior = lancamentoAcima ? lancamentoAcima.lancamentoAnterior : null
    if (lancamentoAcima){
      lancamentoAcima.lancamentoAnterior = lancamentoAtual.id
      lancamentoAbaixo && (lancamentoAbaixo.lancamentoAnterior = lancamentoAcima.id)
    }
  
    dispatch('lancamentoSalvar', lancamentoAtual)
    lancamentoAcima && dispatch('lancamentoSalvar', lancamentoAcima)
    lancamentoAbaixo && dispatch('lancamentoSalvar', lancamentoAbaixo)
  },
  lancamentoDescer({dispatch, getters}, lancamento) {
    const lancamentoAbaixo = getters.lancamentoPosterior(lancamento)
    if (lancamentoAbaixo)
      dispatch('lancamentoSubir', lancamentoAbaixo)
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}