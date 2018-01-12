import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import { CATEGORIAS } from './categorias';
import * as lancamento from './lancamento';
import recorrencia, { RECORRENCIA } from './recorrencia';
import parcelamentos from './parcelamentos';
import snacks, {SNACKS} from './snacks';
import { LOCAIS } from './locais';
//import * as bla from './affe';
import AUTH from './auth';
import CONTA from './conta';
import MIGRATIONS from './migrations';


Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    auth: AUTH.store,
    conta: CONTA.store,
    categorias: CATEGORIAS.store,
    lancamentos: lancamento.store,
    recorrencias: recorrencia,
    parcelamentos,
    snacks,
    locais: LOCAIS.store,
    migrations: MIGRATIONS.store
  }
});

store.dispatch(AUTH.d.INITIALIZE);
store.dispatch(CONTA.d.INITIALIZE);
store.dispatch(LOCAIS.d.INITIALIZE);
store.dispatch(CATEGORIAS.d.INITIALIZE);
store.dispatch(RECORRENCIA.d.INITIALIZE);


export default store;

