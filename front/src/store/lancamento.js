import axios from 'axios';
import { contas } from './conta';
import { LOCAIS } from './locais';

const m = {
  LANCAMENTO_SET_DATA: 'lancamentoSetData',
  LANCAMENTO_SET_LOCAL: 'lancamentoSetLocal',
  LANCAMENTO_SET_CONTA: 'lancamentoSetConta',
  LANCAMENTO_SET_CATEGORIA: 'lancamentoSetCategoria',
  LANCAMENTO_SET_VALOR: 'lancamentoSetValor',
  LANCAMENTO_SET_EFETUADA: 'lancamentoSetEfetuada',
  LIMPA_FORMULARIO: 'lancamentoLimpaFormulario',
  SET_BACKEND_ERRORS: 'lancamentoBackendErrors'
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
    },
    formErrors: {
      data: [],
      conta: [],
      local: [],
      categoria: [],
      valor: [],
      efetuada: []
    }
  },
  mutations: {
    [m.LANCAMENTO_SET_DATA](state, data) {
      state.form.data = data;
    },
    [m.LANCAMENTO_SET_LOCAL](state, local) {
      state.formErrors.local = [];
      state.form.local = local;
    },
    [m.LANCAMENTO_SET_CATEGORIA](state, categoria) {
      state.formErrors.categoria = [];
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
      state.form.local= null;
      state.form.categoria= null;
      state.form.valor= null;
      state.form.efetuada= false;
    },
    [m.SET_BACKEND_ERRORS](state, errors) {
      function getError(field) {
        const error = errors.find(e => e.field === field);
        return error ? [ error.message ] : [];
      }

      state.formErrors.local = getError('local.nome');
      state.formErrors.categoria = getError('categoria');
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
          local: state.form.local,
          efetivada: state.form.efetuada
        }).then(res => {
          commit(m.LIMPA_FORMULARIO);
          dispatch(contas.d.CARREGA_CONTA_LANCAMENTOS);
          dispatch(LOCAIS.d.LOAD_LOCAIS);
          resolve();
        }).catch(err => {
          if (err.response.status === 400) {
            commit(m.SET_BACKEND_ERRORS, err.response.data.fieldErrors);
          }
          reject();
        });
      });
    }
  }
}