
const m = {
  UPDATE_SNACK: 'snackUpdateSnack',
  UPDATE_SHOWING: 'snackUpdateShowing',
  TRATA_ERRO: 'snackTrataErro'
}

export const SNACKS = {
  m
}

export default {
  state: {
    showing: false,
    notification: {
      context: null,
      text: '',
      timeout: 3000
    },
    notifications: []
  },
  mutations: {
    [m.UPDATE_SNACK](state, snack) {
      state.notification = Object.assign({}, {
        timeout: 0, 
        context: 'primary'
      }, snack);
      state.showing = true;
    },
    [m.UPDATE_SHOWING](state, showing) {
      state.showing = showing;
    },
    [m.TRATA_ERRO](state, err) {
      state.notification = {
        timeout: 0, 
        context: 'error',
        text: 'Erro: ' + err.response && err.response.data && err.response.data.message
      };
      state.showing = true;
    }
  }
}