import axios from 'axios';
import { contas } from './conta';

const m = {
  LANCAMENTO_SET_DATA: 'lancamentoSetData',
  LANCAMENTO_SET_LOCAL: 'lancamentoSetLocal',
  LANCAMENTO_SET_CONTA: 'lancamentoSetConta',
  LANCAMENTO_SET_CATEGORIA: 'lancamentoSetCategoria',
  LANCAMENTO_SET_VALOR: 'lancamentoSetValor',
  LANCAMENTO_SET_EFETUADA: 'lancamentoSetEfetuada',
  LIMPA_FORMULARIO: 'lancamentoLimpaFormulario'
}

const d = {
  LANCAMENTO_SUBMIT: 'lancamentoFormSubmit'
}

export const lancamentos = {
  m,
  d
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
    },
    [m.LIMPA_FORMULARIO](state) {
      state.form.conta= null;
      state.form.local= null;
      state.form.categoria= null;
      state.form.valor= null;
      state.form.efetuada= false;
    }
  },
  actions: {
    [d.LANCAMENTO_SUBMIT]({state, dispatch, commit}) {
      return new Promise((resolve, reject) => {
        axios.post('/api/lancamentos', {
          data: state.form.data,
          conta: state.form.conta,
          valor: state.form.valor,
          categoria: state.form.categoria,
          local: {
            nome: state.form.local
          },
          efetivada: state.form.efetuada
        }).then(res => {
          commit(m.LIMPA_FORMULARIO);
          dispatch(contas.d.CARREGA_CONTA_LANCAMENTOS);
          resolve();
        }).catch(err => {
          console.error(err);
          reject();
        });
      });
    }
  }
}