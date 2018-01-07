import store from './';

export default {
  trataErro(err) {
    store.commit(SNACKS.m.UPDATE_SNACK, {
      context: 'error',
      text: 'Erro: ' + err.response && err.response.data && err.response.data.message
    });
  }
}

