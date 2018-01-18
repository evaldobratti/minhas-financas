import axios from 'axios';
import { SNACKS } from './snacks';
import { lancamentos, Lancamento } from './lancamento';
import firebase from 'firebase';
import eventBus from '../EventBus';
import moment from 'moment';
import uuid from 'uuid'

export const m = {
 ADD_RECORRENCIA: 'recorrenciaAdd',
 UPDATE_RECORRENCIA: 'recorrenciaUpdate',
 ADD_RECORRENCIA_GERADA: 'recorrenciaGeradaAdd'
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
    geradas: []
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
    },
    [m.ADD_RECORRENCIA_GERADA](state, gerada) {
      lancamentosTransientes = [];
      state.geradas.push(gerada);
    }
  },
  getters: {
    getRecorrencia(state) {
      return idRecorrencia => {
        return state.list.find(r => r.id == idRecorrencia)
      }
    },
    recorrenciaOriginadora(state, getters) {
      return lancamento => {
        let gerada = state.geradas.find(g => g.idLancamento == lancamento.id);

        if (!gerada)  {
          const transiente = lancamentosTransientes.find(l => l.lancamento == lancamento);
          if (transiente)
            gerada = transiente.gerado;
        }

        if (gerada) 
          return getters.getRecorrencia(gerada.idRecorrencia);

        return null;
      }
    },
    isRecorrente(state, getters) {
      return lancamento => {
        return getters.recorrenciaOriginadora(lancamento) != null;
      }
    },
    getComplemento(state, getters) {
      return lancamento => {
        let gerada = state.geradas.find(g => g.idLancamento == lancamento.id);
        if (!gerada) {
          const transiente = lancamentosTransientes.find(l => l.lancamento == lancamento);
          if (transiente)
            gerada = transiente.gerado;
        }

        if (gerada) {
          const recorrencia = getters.getRecorrencia(gerada.idRecorrencia);
          if (recorrencia.parcelaInicio) {
            return ' (' + gerada.parcelaNumero + '/' + recorrencia.parcelaQuantidade + ')';
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
          let ixParcela = r.parcelaInicio || 1;  
          while (data.isSameOrBefore(ate)) {
            data = data.clone().add(r.aCada, 'month');
            ixParcela += 1;

            if (r.dataFim && data.isAfter(r.dataFim))
              break;
            
            if (state.geradas.find(g => g.idRecorrencia == r.id && moment(g.data).isSame(data)))
              continue;

            if (r.parcelaInicio != null) {
              if (ixParcela > r.parcelaQuantidade)
                continue;

              if (state.geradas.find(g => g.idRecorrencia == r.id && g.parcelaNumero == ixParcela))
                continue;
            }

            const lancamento = {
              data,
              idCategoria: r.idCategoria,
              idConta: r.idConta,
              local: r.local,
              valor: r.valor,
              efetivada: false,
              tempId: uuid.v4()
            };

            lancamentosTransientes.push({
              lancamento,
              gerado: {
                idRecorrencia: r.id,
                data: lancamento.data,
                parcelaNumero: ixParcela
              }
            });

            lancamento.creationCallback = (id, lancamento) => {
              const gerado = lancamentosTransientes.find(l => l.lancamento == lancamento).gerado;
              gerado.idLancamento = id;
              const ref = firebase.database().ref(getters.uid + '/recorrenciasGeradas/').push();
              return {
                location: getters.uid + '/recorrenciasGeradas/' + ref.key,
                value: JSON.parse(JSON.stringify(gerado))
              }
            }
             
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

        firebase.database().ref(getters.uid + '/recorrencias').on('child_changed', (snap) => {
          commit(m.UPDATE_RECORRENCIA, snap.val());
        });

        firebase.database().ref(getters.uid + '/recorrenciasGeradas').on('child_added', (snap) => {
          commit(m.ADD_RECORRENCIA_GERADA, snap.val());
        });
      });
      
      
    },
    [d.SUBMIT_FORM]({commit, state, getters, dispatch}, { recorrencia, gerada }) {
      let normalized = JSON.parse(JSON.stringify(recorrencia));
      let future;
      let msg;
      if (recorrencia.id) {
        future = firebase.database().ref(getters.uid + '/recorrencias/' + recorrencia.id).set(normalized);
        msg = 'Recorrência atualizada com sucesso!';
      } else {
        future = firebase.database().ref(getters.uid + '/recorrencias/').push(normalized);
        gerada.idRecorrencia = future.key;
        firebase.database().ref(getters.uid + '/recorrenciasGeradas').push(JSON.parse(JSON.stringify(gerada)));
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