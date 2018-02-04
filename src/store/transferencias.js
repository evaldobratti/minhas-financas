import eventBus from '../EventBus';
import firebase from 'firebase'
import { lancamentos } from './lancamento';

const d = {
  INITIALIZE: 'transferenciasInitialize',
  SUBMIT: 'transferenciaSubmit'
}

const m = {
  ADD_TRANSFERENCIA: 'transferenciaAdd'
}

const store = {
  state: {
    list: []
  },
  mutations: {
    [m.ADD_TRANSFERENCIA](state, transf) {
      state.list.push(transf);
    }
  },
  actions: {
    [d.INITIALIZE]({getters, commit}) {
      eventBus.bus.$on(eventBus.events.SIGN_IN, () => {
        firebase.database().ref(getters.uid + '/transferencias').on('child_added', (snap) => {
          const transf = snap.val();
          transf.id = snap.key;
          commit(m.ADD_TRANSFERENCIA, transf);
        });   
      });
    },
    [d.SUBMIT]({getters, dispatch}, { lancamento, idContaDestino }) {
      const update = {};
      const idTransferencia = firebase.database().ref(getters.uid + '/transferencias').push().key;

      const contraPartida = Object.assign({}, lancamento);
      contraPartida.id = null;
      contraPartida.idConta = idContaDestino;
      contraPartida.valor = -lancamento.valor;
      contraPartida.creationCallback = (id) => {
        return {
          location: getters.uid + '/transferencias/' + idTransferencia,
          value: {
            de: lancamento.id,
            para: id
          }
        }
      }

      dispatch(lancamentos.d.LANCAMENTO_SUBMIT, contraPartida);
    }
  }
}

export const TRANSFERENCIAS = {
    store, m, d
  };
  
export default TRANSFERENCIAS;