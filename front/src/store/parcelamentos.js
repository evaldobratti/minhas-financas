import axios from 'axios';
import { contas } from './conta';
import { SNACKS } from './snacks';
export const m = {
  UPDATE_FORM_INICIO_PARCELAS: 'parcelamentoFormUpdateInicioParcelas',
  UPDATE_FORM_QUANTIDADE_PARCELAS: 'parcelamentoFormUpdateQuantidadeParcelas',
  UPDATE_FORM_LANCAMENTO_INICIAL: 'parelamentoFormUpdateLancamentoInicial'
}

export const d = {
  SUBMIT_FORM: 'parcelamentoSubmitForm'
}

export const PARCELAMENTOS = {
  m, d
}

export default {
  state: {
    form: {
      inicioParcelas: 1,
      quantidadeParcelas: null,
      lancamentoInicial: null
    }
  },
  mutations: {
    [m.UPDATE_FORM_INICIO_PARCELAS](state, inicioParcelas) {
      state.form.inicioParcelas = inicioParcelas;
    },
    [m.UPDATE_FORM_QUANTIDADE_PARCELAS](state, quantidadeParcelas) {
      state.form.quantidadeParcelas = quantidadeParcelas;
    },
    [m.UPDATE_FORM_LANCAMENTO_INICIAL](state, lancamentoInicial) {
      state.form.lancamentoInicial = lancamentoInicial;
    }
  },
  actions: {
    [d.SUBMIT_FORM]({state, commit, dispatch}) {
      return new Promise((resolve, reject) => {
        axios.post('/api/parcelamentos', state.form).then(res => {
          dispatch(contas.d.CARREGA_CONTA_LANCAMENTOS);
          commit(SNACKS.m.UPDATE_SNACK, {
            text: 'Recorrência cadastrada!',
            timeout: 1500,
            context: 'success'
          });
          resolve();
        }).catch(err => {
          commit(SNACKS.m.TRATA_ERRO, err);
        });  
      })
      
    }
  }
}