import axios from 'axios';
import { SNACKS } from './snacks';
import { lancamentos, Lancamento } from './lancamento';
import firebase from 'firebase';
import eventBus from '../EventBus';
import moment from 'moment';

export const m = {
 ADD_RECORRENCIA: 'recorrenciaAdd'
}

export const d = {
  SUBMIT_FORM: 'recorrenciaSubmitForm',
  INITIALIZE: 'recorrenciaInitialize'
}
export const RECORRENCIA = {
  m, d
}

export default {
  state: {
    list: []
  },
  mutations: {
    [m.ADD_RECORRENCIA](state, recorrencia) {
      recorrencia.partirDe = moment(recorrencia.partirDe);
      state.list.push(recorrencia);
    }
  },
  getters: {
    recorrenciaOriginadora(state) {
      return idLancamento => {
        for (const recorrencia of state.list) {
          for (const gerou of recorrencia.lancamentos) {
            if (gerou.idLancamnento == idLancamento)
              return recorrencia;
          }
        }
        return null;
      }
    },
    projecoesAte(state, getters) {
      return (idsContas, ate) => {
        const lancamentos = [];
        const recorrencias = state.list.filter(r => idsContas.indexOf(r.idConta) >= 0 && r.partirDe.isSameOrBefore(ate));
        recorrencias.forEach(r => {
          let data = r.partirDe;
          
          while (data.isSameOrBefore(ate)) {
            data = data.clone().add(1, 'month');
            if (r.lancamentos.find(gerado => moment(gerado.data).isSame(data)))
              continue;

            const lancamento = {
              data,
              idCategoria: r.idCategoria,
              idConta: r.idConta,
              local: r.local,
              valor: r.valor,
              creationCallback: (id, lancamento) => {
                r.lancamentos.push({
                  data: lancamento.data,
                  idLancamento: id
                })
              }
            };
            firebase.database().ref(getters.uid + '/recorrencias/' + r.id).set(JSON.parse(JSON.stringify(r)));
            
            lancamentos.push(lancamento);
            
          }

        });
        return lancamentos;

      }
    }
  },
  actions: {
    [d.INITIALIZE]({commit, getters}) {
      eventBus.bus.$on(eventBus.events.SIGN_IN, () => {
        firebase.database().ref(getters.uid + '/recorrencias').on('child_added', (snap) => {
          const recorrencia = snap.val();
          recorrencia.id = snap.key;
          commit(m.ADD_RECORRENCIA, recorrencia);
        });
      })
    },
    [d.SUBMIT_FORM]({commit, state, getters, dispatch}, recorrencia) {
      const normalized = Object.assign({}, recorrencia);
      normalized.partirDe = recorrencia.partirDe.toISOString();

      
      normalized.lancamentos = []
      normalized.lancamentos.push({
        data: normalized.partirDe,
        idLancamento: normalized.idLancamentoInicial
      })
      delete normalized.idLancamentoInicial
      
      return firebase.database().ref(getters.uid + '/recorrencias/').push(normalized, (err) => {
        if (err) {
          commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
          return;
        }

        commit(SNACKS.m.UPDATE_SUCESSO, 'Recorrência criada com sucesso!');
      });
    }
  }
}