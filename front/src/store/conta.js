import Vuex from 'vuex';
import Vue from 'vue';
import moment from 'moment';
import eventBus from '../EventBus';
import firebase from 'firebase'
import { SNACKS } from './snacks';

export const d = {
  INITIALIZE: 'contaInitialize',
  CONTA_SUBMIT: 'contaSubmit'
}

export const m = {
  ADD_CONTA: 'contaAdiciona'
}

function atualizaSaldoDiario(saldoInicio, lancamentos) {
  let ultimaData = null;
  let ultimoSaldo = saldoInicio;
  for (let l of lancamentos) {
    ultimoSaldo = ultimoSaldo + l.valor;

    if (l.data.valueOf() !== ultimaData || l == lancamentos[lancamentos.length - 1]) {
      ultimaData = l.data.valueOf();
      Vue.set(l, 'saldoDiario', ultimoSaldo);
    }
  }
}

export const store =  {
  state: {
    asMap: {},
    asList: []
  },
  mutations: {
    [m.ADD_CONTA](state, conta) {
      Vue.set(state.asMap, conta.id, conta);
      state.asList.push(conta);
    }
  },
  getters: {
    getContas(state) {
      return state.asList;
    },
    getConta(state) {
      return (id) => {
        return state.asMap[id];
      }
    }
  },
  actions: {
    contaSubmit({commit, getters}, conta) {
      firebase.database().ref(getters.uid + '/contas').push(conta);
    },
    [d.INITIALIZE]({commit, getters}){
      eventBus.bus.$on(eventBus.events.SIGN_IN, () => {
        firebase.database().ref(getters.uid + '/contas').on('child_added', (snap) => {
          const conta = snap.val();
          conta.id = snap.key;
          commit(m.ADD_CONTA, conta);
        });
      })
    }
  }
};

export const CONTA = {
  store, m, d
};

export default CONTA;