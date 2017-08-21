

export const m = {
  UPDATE_FORM_INICIO_PARCELAS: 'parcelamentoFormUpdateInicioParcelas',
  UPDATE_FORM_QUANTIDADE_PARCELAS: 'parcelamentoFormUpdateQuantidadeParcelas'
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
      quantidadeParcelas: null
    }
  },
  mutations: {
    [m.UPDATE_FORM_INICIO_PARCELAS](state, inicioParcelas) {
      state.form.inicioParcelas = inicioParcelas;
    },
    [m.UPDATE_FORM_QUANTIDADE_PARCELAS](state, quantidadeParcelas) {
      state.form.quantidadeParcelas = quantidadeParcelas;
    }
  },
  actions: {
    [d.SUBMIT_FORM]({commit}) {
      console.info('créu');
    }
  }
}