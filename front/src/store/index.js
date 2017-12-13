import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'

import * as conta from './conta';
import { CATEGORIAS } from './categorias';
import * as lancamento from './lancamento';
import recorrencia from './recorrencia';
import parcelamentos from './parcelamentos';
import snacks, {SNACKS} from './snacks';
import { LOCAIS } from './locais';
//import * as bla from './affe';
import AUTH from './auth';

console.info(AUTH);
Vue.use(Vuex);
const store = new Vuex.Store({
  modules: {
    auth: AUTH.store,
    conta: conta.store,
    categorias: CATEGORIAS.store,
    lancamentos: lancamento.store,
    recorrencias: recorrencia,
    parcelamentos,
    snacks,
    locais: LOCAIS.store
  }
});

store.dispatch(AUTH.d.INITIALIZE);


export default store;

