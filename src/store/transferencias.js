import repo from './repo'

const state = {
  byId: {

  }
}

const getters = {
  idContraPartida: (state) => (idLancamento) => {
    const transferencia = Object.values(state.byId).find(t => t.idPartida === idLancamento || t.idContraPartida === idLancamento)
    return transferencia.idPartida == idLancamento ? transferencia.idContraPartida : transferencia.idPartida
  }

}

const mutations = {
  addTransferencia(state, transferencia) {
    state.byId[transferencia.id] = transferencia
  }
}

const actions = {
  posLogin({commit}) {
    repo.hookAdded('/transferencias', transferencia => {
      commit('addTransferencia', transferencia)
    })
  },
  transferenciaSalvar({dispatch, getters}, lancamento) {
    if (lancamento.id) {
      const idContraPartida = getters.idContraPartida(lancamento.id)
      const lancamentoContraPartida = {...getters.lancamentoId(idContraPartida)}
      
      dispatch('lancamentoSalvar', lancamento).then(() => {
        lancamentoContraPartida.valor = -lancamento.valor
        lancamentoContraPartida.data = lancamento.data
        lancamentoContraPartida.efetivada = lancamento.efetivada
        dispatch('lancamentoSalvar', lancamentoContraPartida)
      })

    } else {
      dispatch('lancamentoSalvar', {...lancamento}).then(idPartida => {
        const contraPartida = {
          ...lancamento,
          idConta: lancamento.idContaDestino,
          idContaDestino: lancamento.idConta,
          valor: -lancamento.valor
        }
        
        dispatch('lancamentoSalvar', contraPartida).then(idContraPartida => {
          repo.save('/transferencias', {
            idPartida, 
            idContraPartida
          })
        })
      })
    }
  },
  transferenciaExcluir({dispatch, getters}, lancamento) {
    dispatch('lancamentoExcluir', lancamento)
    const idContraPartida = getters.idContraPartida(lancamento.id)
    const lancamentoContraPartida = getters.lancamentoId(idContraPartida)
    dispatch('lancamentoExcluir', lancamentoContraPartida)
  }

}

export default {
  state,
  getters,
  mutations,
  actions
}