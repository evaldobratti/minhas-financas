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
    [d.SUBMIT]({getters, dispatch}, {data, idContaOrigem, idContaDestino, valor }) {
      var updateOrigem = lancamentos.utils.newLancamento({getters}, {
        idConta: idContaOrigem,
        data: data,
        local: 'Transferência',
        valor: valor
      })

      var updateDestino = lancamentos.utils.newLancamento({getters}, {
        idConta: idContaDestino,
        data: data,
        local: 'Transferência',
        valor: -valor
      })

      const idTransferencia = firebase.database().ref(getters.uid + '/transferencias').push().key;
      var updateTransferencia = {
        location: getters.uid + '/transferencias/' + idTransferencia,
        value: {
          de: updateOrigem.id,
          para: updateDestino.id
        }
      }
      
      var update = {}
      update[updateOrigem.location] = updateOrigem.value;
      update[updateDestino.location] = updateDestino.value;
      update[updateTransferencia.location] = updateTransferencia.value;
      
      return new Promise((resolve, reject) => {
        firebase.database().ref().update(update) .then((l) => {
          resolve('Transferência criada com sucesso!');
        }).catch((err) => {
          reject(err);
        });
      });
    }
  }
}

export const TRANSFERENCIAS = {
    store, m, d
  };
  
export default TRANSFERENCIAS;