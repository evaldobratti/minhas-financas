import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import authentication from './authentication';
import conta from './conta';
import categorias from './categorias';
import lancamento from './lancamento';
import recorrencia from './recorrencia';
import parcelamentos from './parcelamentos';
Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
      authentication,
      conta,
      categorias,
      lancamentos: lancamento,
      recorrencias: recorrencia,
      parcelamentos
    }
});

store.dispatch('getInitialData');

export default store;

