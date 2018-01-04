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
    projecoesAte(state) {
      return (idsContas, ate) => {
        const lancamentos = [];
        const recorrencias = state.list.filter(r => idsContas.indexOf(r.idConta) >= 0 && r.partirDe.isSameOrBefore(ate));
        recorrencias.forEach(r => {
          let data = r.partirDe;
          
          while (data.isSameOrBefore(ate)) {
            data = data.clone().add(1, 'month');
            if (r.lancamentos.find(gerado => moment(gerado.data).isSame(data)))
              continue;

            const lancamento = new Lancamento();
            lancamento.data = data;
            lancamento.idCategoria = r.idCategoria;
            lancamento.idConta = r.idConta;
            lancamento.local = r.local;
            lancamento.valor = r.valor;
            
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