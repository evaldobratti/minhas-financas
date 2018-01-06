import axios from 'axios';
import { SNACKS } from './snacks';
import { lancamentos, Lancamento } from './lancamento';
import firebase from 'firebase';
import eventBus from '../EventBus';
import moment from 'moment';

export const m = {
 ADD_RECORRENCIA: 'recorrenciaAdd',
 UPDATE_RECORRENCIA: 'recorrenciaUpdate'
}

export const d = {
  SUBMIT_FORM: 'recorrenciaSubmitForm',
  INITIALIZE: 'recorrenciaInitialize'
}
export const RECORRENCIA = {
  m, d
}

let lancamentosTransientes = [];

export default {
  state: {
    list: [],
    
  },
  mutations: {
    [m.ADD_RECORRENCIA](state, recorrencia) {
      recorrencia.partirDe = moment(recorrencia.partirDe);
      lancamentosTransientes = [];
      state.list.push(recorrencia);
    },
    [m.UPDATE_RECORRENCIA](state, recorrencia) {
      recorrencia.partirDe = moment(recorrencia.partirDe);
      lancamentosTransientes = [];

      const ix = state.list.findIndex(r => r.id == recorrencia.id);
      state.list.splice(ix, 1, recorrencia);
    }
  },
  getters: {
    getRecorrencia(state) {
      return idRecorrencia => {
        return state.list.find(r => r.id == idRecorrencia)
      }
    },
    recorrenciaOriginadora(state) {
      return idLancamento => {
        for (const recorrencia of state.list) {
          for (const gerou of recorrencia.lancamentos) {
            if (gerou.idLancamento == idLancamento)
              return recorrencia;
          }
        }
        return null;
      }
    },
    getComplemento(state) {
      return lancamento => {
        for (const recorrencia of state.list) {
          for (const gerou of recorrencia.lancamentos) {
            if (gerou.idLancamento == lancamento.id)
              return ' BIRL';
            
            if (lancamentosTransientes.find(l => l.lancamento == lancamento)) {
              return ' RÁ';
            }
          }
        }
        return '';
      }
    },
    projecoesAte(state, getters) {
      return (idsContas, ate) => {
        const lancamentos = [];
        const recorrencias = state.list.filter(r => idsContas.indexOf(r.idConta) >= 0 && r.partirDe.isSameOrBefore(ate));
        
        recorrencias.forEach(r => {
          let data = r.partirDe;
          let ixParcela = r.parcelaInicio;  
          while (data.isSameOrBefore(ate)) {
            data = data.clone().add(r.aCada, 'month');
            ixParcela += 1;

            if (r.dataFim && data.isAfter(r.dataFim))
              break;
            
            for (const gerado of r.lancamentos) {

              
            }
            if (r.lancamentos.find(gerado => moment(gerado.data).isSame(data)))
              continue;

            if (r.parcelaInicio != null && r.lancamentos.find(gerado => gerado.parcelaNumero == ixParcela))
              continue;

            const lancamento = {
              data,
              idCategoria: r.idCategoria,
              idConta: r.idConta,
              local: r.local,
              valor: r.valor,
              idRecorrencia: r.id
            };

            lancamentosTransientes.push({
              lancamento,
              gerado: {
                data: lancamento.data,
                parcelaNumero: ixParcela
              }
            });

            lancamento.creationCallback = (id, lancamento) => {
              const gerado = lancamentosTransientes.find(l => l.lancamento == lancamento).gerado;
              gerado.idLancamento = id;
              r.lancamentos.push(gerado);
              firebase.database().ref(getters.uid + '/recorrencias/' + r.id).set(JSON.parse(JSON.stringify(r)));
            }
            
            
            lancamentos.push(lancamento);
            
          }

        });
        console.info(lancamentosTransientes);
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

        firebase.database().ref(getters.uid + '/recorrencias').on('child_changed', (snap) => {
          commit(m.UPDATE_RECORRENCIA, snap.val());
        });
      });
      
      
    },
    [d.SUBMIT_FORM]({commit, state, getters, dispatch}, recorrencia) {
      let normalized = JSON.parse(JSON.stringify(recorrencia));
      let future;
      let msg;
      if (recorrencia.id) {
        future = firebase.database().ref(getters.uid + '/recorrencias/' + recorrencia.id).set(normalized);
        msg = 'Recorrência atualizada com sucesso!';
      } else {
        future = firebase.database().ref(getters.uid + '/recorrencias/').push(normalized);
        msg = 'Recorrência criada com sucesso!';
      }
      return future.then(() => {
        commit(SNACKS.m.UPDATE_SUCESSO, msg);
      }).catch(err => {
        commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
      });
    }
  }
}