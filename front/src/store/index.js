import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import authentication from './authentication'

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
      authentication
    }
});

store.dispatch('getInitialData');

export default store;

