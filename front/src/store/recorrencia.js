import axios from 'axios';
import { SNACKS } from './snacks';
import { contas } from './conta';
export const m = {
  UPDATE_FORM: 'recorrenciaUpdateForm',
  UPDATE_FORM_TIPO_FREQUENCIA: 'recorrenciaUpdateFormTipoFrequencia',
  UPDATE_FORM_VALOR: 'recorrenciaUpdateFormValor',
  UPDATE_FORM_A_CADA: 'recorrenciaUpdateFormACada',
  UPDATE_FORM_DIA: 'recorrenciaUpdateFormDia',
  UPDATE_FORM_PARTIR_DE: 'recorrenciaUpdateFormPartirDe',
  UPDATE_FORM_CONTA: 'recorrenciaUpdateFormConta',
  UPDATE_FORM_CATEGORIA: 'recorrenciaUpdateFormCategoria',
  UPDATE_FORM_LOCAL: 'recorrenciaUpdateFormLocal',
  UPDATE_FORM_LANCAMENTO_INICIAL: 'recorrenciaUpdateFormLancamentoInicial',
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
      local: null, 
      lancamentoInicial: null
    }
  },
  mutations: {
    [m.UPDATE_FORM_TIPO_FREQUENCIA](state, tipoFrequencia) {
      state.form.tipoFrequencia = tipoFrequencia;
    },
    [m.UPDATE_FORM_VALOR](state, valor) {
      state.form.valor = valor;
    },
    [m.UPDATE_FORM_A_CADA](state, aCada) {
      state.form.aCada = aCada;
    },
    [m.UPDATE_FORM_DIA](state, dia) {
      state.form.dia = dia;
    },
    [m.UPDATE_FORM_PARTIR_DE](state, partirDe) {
      state.form.partirDe = partirDe;
    },
    [m.UPDATE_FORM_CONTA](state, conta) {
      state.form.conta = conta;
    },
    [m.UPDATE_FORM_CATEGORIA](state, categoria) {
      state.form.categoria = categoria;
    },
    [m.UPDATE_FORM_LOCAL](state, local) {
      state.form.local = local;
    },
    [m.UPDATE_FORM_LANCAMENTO_INICIAL](state, lancamentoInicial) {
      state.form.lancamentoInicial = lancamentoInicial;
    },
  },
  actions: {
    [d.SUBMIT_FORM]({commit, state, dispatch}) {
      return new Promise((resolve, reject) => {
        axios.post('/api/recorrencias', state.form).then(() => {
          dispatch(contas.d.CARREGA_CONTA_LANCAMENTOS);
          commit(SNACKS.m.UPDATE_SNACK, {
            text: 'Recorrência cadastrada!',
            timeout: 1500,
            context: 'success'
          });
          resolve();
        }).catch(err => {
          commit(SNACKS.m.TRATA_ERRO, err);
        })
      })
    }
  }
}