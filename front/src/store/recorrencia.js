import axios from 'axios';

export const m = {
  UPDATE_FORM: 'recorrenciaUpdateForm'
}

export const d = {
  SUBMIT_FORM: 'recorrenciaSubmitForm'
}

export const RECORRENCIA = {
  m, d
}

export default {
  state: {
    form: {
      tipoFrequencia: 'MES',//
      aCada: 1,//
      valor: null,//
      dia: null,
      partirDe: null,//
      conta: null,
      categoria: null,
      local: null
    }
  },
  mutations: {
    [m.UPDATE_FORM](state, form) {
      state.form = Object.assign(state.form, form);
    }
  },
  actions: {
    [d.SUBMIT_FORM]({commit, state}) {
      return new Promise((resolve, reject) => {
        axios.post('/api/recorrencias', state.form).then(() => {
          console.info('sucesso');
          resolve();
        }).catch(err => {
          console.info('crew', err);
          reject(err);
        })
      })
    }
  }
}