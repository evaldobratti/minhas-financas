
const m = {
  UPDATE_SNACK: 'snackUpdateSnack',
  UPDATE_SHOWING: 'snackUpdateShowing',
  TRATA_ERRO: 'snackTrataErro',
  UPDATE_SUCESSO: 'snackSucesso',
  UPDATE_ERRO: 'snackErro',
}

const DURACAO = 2500;

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
    ultimoErroMostrado: null,
    notifications: []
  },
  mutations: {
    [m.UPDATE_SUCESSO](state, text) {
      state.notification = {
        timeout: DURACAO,
        text,
        context: 'success'
      };
      state.showing = true;
    },
    [m.UPDATE_ERRO](state, text) {
      state.notification = {
        timeout: 0,
        text,
        context: 'error'
      };
      state.showing = true;
    },
    [m.UPDATE_SNACK](state, snack) {
      if (typeof snack == 'string') {
        state.notification = {
          timeout: DURACAO,
          text: snack,
          context: 'success'
        }
      } else {
        state.notification = Object.assign({}, {
          timeout: 0, 
          context: 'success'
        }, snack);
      }
      state.showing = true;
    },
    [m.UPDATE_SHOWING](state, showing) {
      state.showing = showing;
    },
    [m.TRATA_ERRO](state, err) {
      let msg = "Mensagem de erro não definida"
      
      if (err && err.response && err.response.data && err.response.data.message) {
        msg = err.response.data.message;
      }

      state.notification = {
        timeout: 0, 
        context: 'error',
        text: 'Erro: ' + err.response.status + ' ' + msg
      };
      state.showing = true;
      state.ultimoErroMostrado = err;
    }
  }
}