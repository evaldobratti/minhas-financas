import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import authentication from './authentication';
import conta from './conta';
import categorias from './categorias';
Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
      authentication,
      conta,
      categorias
    }
});

store.dispatch('getInitialData');

export default store;

