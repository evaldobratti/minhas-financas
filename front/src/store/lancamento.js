
const m = {
  LANCAMENTO_SET_DATA: 'lancamentoSetData',
  LANCAMENTO_SET_LOCAL: 'lancamentoSetLocal',
  LANCAMENTO_SET_CONTA: 'lancamentoSetConta',
  LANCAMENTO_SET_CATEGORIA: 'lancamentoSetCategoria',
  LANCAMENTO_SET_VALOR: 'lancamentoSetValor',
  LANCAMENTO_SET_EFETUADA: 'lancamentoSetEfetuada'
}

export const lancamentos = {
  m
}

export default {
  state: {
    form: {
      data: new Date(),
      conta: null,
      local: null,
      categoria: null,
      valor: null,
      efetuada: false
    }
  },
  mutations: {
    [m.LANCAMENTO_SET_DATA](state, data) {
      state.form.data = data;
    },
    [m.LANCAMENTO_SET_LOCAL](state, local) {
      state.form.local = local;
    },
    [m.LANCAMENTO_SET_CATEGORIA](state, categoria) {
      state.form.categoria = categoria;
    },
    [m.LANCAMENTO_SET_VALOR](state, valor) {
      state.form.valor = valor;
    },
    [m.LANCAMENTO_SET_EFETUADA](state, efetuada) {
      state.form.efetuada = efetuada;
    },
    [m.LANCAMENTO_SET_CONTA](state, conta) {
      state.form.conta = conta;
    }
  }
}