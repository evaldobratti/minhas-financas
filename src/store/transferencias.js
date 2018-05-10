import eventBus from '../EventBus';
import firebase from 'firebase'
import { lancamentos } from './lancamento';

const d = {
  INITIALIZE: 'transferenciasInitialize',
  SUBMIT: 'transferenciaSubmit',
  ATUALIZA: 'transferenciaAtualiza'
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
    [d.INITIALIZE]({getters, commit, dispatch}) {
      eventBus.bus.$on(eventBus.events.SIGN_IN, () => {
        firebase.database().ref(getters.uid + '/transferencias').on('child_added', (snap) => {
          const transf = snap.val();
          transf.id = snap.key;
          commit(m.ADD_TRANSFERENCIA, transf);
        });   
      });

      eventBus.interessados.push({
        evento: eventBus.events.ATUALIZANDO_LANCAMENTO,
        cb: (lancamento) => {
          return dispatch(d.ATUALIZA, lancamento);
        }
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
    },
    [d.ATUALIZA]({getters, state, dispatch}, lancamento) {
      let transferenciaParticipante = state.list.find(transferencia => transferencia.de == lancamento.id || transferencia.para == lancamento.id);

      if (transferenciaParticipante == null)
        return null;

      let idContraPartida = transferenciaParticipante.de == lancamento.id ? transferenciaParticipante.para : transferenciaParticipante.de;

      let contraPartida = getters.getLancamento(idContraPartida);

      contraPartida.data = lancamento.data;
      contraPartida.local = lancamento.local;
      contraPartida.valor = -lancamento.valor;
      contraPartida.efetivada = lancamento.efetivada;

      let updatePartida = lancamentos.utils.newLancamento({getters}, lancamento);
      let updateContraPartida = lancamentos.utils.newLancamento({getters}, contraPartida);

      var update = {}
      update[updatePartida.location] = updatePartida.value;
      update[updateContraPartida.location] = updateContraPartida.value;

      return new Promise((resolve, reject) => {
        firebase.database().ref().update(update) .then((l) => {
          resolve('Transferência atualizada com sucesso!');
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