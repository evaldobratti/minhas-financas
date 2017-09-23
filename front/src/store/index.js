import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import router from '../router'
import authentication from './authentication';
import * as conta from './conta';
import categorias from './categorias';
import * as lancamento from './lancamento';
import recorrencia from './recorrencia';
import parcelamentos from './parcelamentos';
import snacks, {SNACKS} from './snacks';
import { LOCAIS } from './locais';

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
      authentication,
      conta: conta.store,
      categorias,
      lancamentos: lancamento.store,
      recorrencias: recorrencia,
      parcelamentos,
      snacks,
      locais: LOCAIS.store
    }
});

store.dispatch('getInitialData');
store.dispatch(conta.LOAD_CONTAS)
  .then(() => store.dispatch(lancamento.lancamentos.d.LANCAMENTO_LOAD));

export default store;

