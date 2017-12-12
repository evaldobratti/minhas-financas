import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import authentication from './authentication';
import * as conta from './conta';
import { CATEGORIAS } from './categorias';
import * as lancamento from './lancamento';
import recorrencia from './recorrencia';
import parcelamentos from './parcelamentos';
import snacks, {SNACKS} from './snacks';
import { LOCAIS } from './locais';
import { initializeApp } from 'firebase';

Vue.use(Vuex);

initializeApp({
  apiKey: "AIzaSyB5mLWc-ry7gdmx8H8UeBIfmlWfqNRkBa4",
  authDomain: "eb-minhas-financas.firebaseapp.com",
  databaseURL: "https://eb-minhas-financas.firebaseio.com",
  projectId: "eb-minhas-financas",
  storageBucket: "eb-minhas-financas.appspot.com",
  messagingSenderId: "761623811227"
});

const store = new Vuex.Store({
    modules: {
      authentication,
      conta: conta.store,
      categorias: CATEGORIAS.store,
      lancamentos: lancamento.store,
      recorrencias: recorrencia,
      parcelamentos,
      snacks,
      locais: LOCAIS.store
    }
});

store.dispatch('getInitialData');


export default store;

