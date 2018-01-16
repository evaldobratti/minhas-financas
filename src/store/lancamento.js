import { contas } from './conta';
import { LOCAIS } from './locais';
import moment from 'moment';
import Vue from 'vue';
import { SNACKS } from './snacks';
import firebase from 'firebase';

const m = {
  SET_LANCAMENTOS: 'lancamentoSetList',
  REMOVE_LANCAMENTO_ID: 'lancamentoRemoveId',
  CONTA_CARREGADA: 'lancamentoContaCarregada'
}

const d = {
  LANCAMENTO_SUBMIT: 'lancamentoFormSubmit',
  LANCAMENTO_LOAD: 'lancamentoLoad',
  REMOVE_LANCAMENTO: 'lancamentoRemove',
  TROCA_CONTA: 'lancamentoTrocaConta',
  LANCAMENTOS_EM: 'lancamentosEm',
  SOBE_LANCAMENTO: 'lancamentoSobe',
  DESCE_LANCAMENTO: 'lancamentoDesce'
}

export const lancamentos = {
  m,
  d
}

function put({ dispatch, commit, state, getters}, lancamento) {
  let location = '';
  let id = '';
  const normalized = JSON.parse(JSON.stringify(lancamento));
  if (normalized.id) {
    id = normalized.id;
    location = getters.uid + '/lancamentos/' + normalized.idConta + '/' + normalized.id;
  }
  else {
    const ref = firebase.database().ref(getters.uid + '/lancamentos/' + normalized.idConta).push()
    id = ref.key;
    location = getters.uid + '/lancamentos/' + normalized.idConta + '/' + ref.key;
  }
  return {
    id,
    location,
    value: normalized
  }
}

function ordena(lancamentos) {
  const copia = [...lancamentos];
  copia.sort((a, b) => {
    const porData = a.data.valueOf() - b.data.valueOf();
    if (porData != 0)
      return porData;
    
    return a.ordem - b.ordem;
  });
  return copia;
}

export const store = {
  state: {
    list: [],
    contasCarregadas: []
  },
  getters: {
    lancamentoEdicao(state) {
      return state.edicao;
    },
    lancamentosDe(state, getters) {
      return (contasIds, mes, ano) => {
        if (getters.getContas.length == 0)
          return [];
        
        const dataBase = moment(ano + '-' + (mes + 1) + '-' + 1, 'YYYY-MM-DD');
        const saldoDataInicial = dataBase.clone().add(-1, 'days');
        const saldoDataFinal = dataBase.clone().add(1, 'month').add(-1, 'days');

        const lancamentosDaConta = state.list.filter(l => {
          return contasIds.indexOf(l.idConta) >= 0;
        });
        let lancamentosDoMes = lancamentosDaConta.filter(l => {
          return l.data.month() == mes && l.data.year() == ano;
        });
        const projecoes = getters.projecoesAte(contasIds, saldoDataFinal);
        projecoes.filter(l => {
          return l.data.month() == mes && l.data.year() == ano;
        }).forEach(l => {
          lancamentosDoMes.push(l);
        })
        
        let saldoInicial = 0;
        contasIds.forEach(contaId => {
          saldoInicial += getters.saldoEm(getters.getConta(contaId), saldoDataInicial);
        });

        let saldoFinal = 0;
        contasIds.forEach(contaId => {
          saldoFinal += getters.saldoEm(getters.getConta(contaId), saldoDataFinal);
        });


        lancamentosDoMes = ordena(lancamentosDoMes);

        lancamentosDoMes.reduce((x, y) => {
          const saldo = x + y.valor;
          Vue.set(y, 'saldoDiario', saldo);
          return saldo;
        }, saldoInicial);

        const resultado = [ {
            data: saldoDataInicial,
            conta: null,
            local: 'Saldo inicial',
            categoria: { id: -1},
            saldoDiario: saldoInicial,
            isSaldo: true
          }, 
          ...lancamentosDoMes, 
          {
            data: saldoDataFinal,
            conta: null,
            local: 'Saldo final',
            categoria: { id: -2 },
            saldoDiario: saldoFinal,
            isSaldo: true
          }
        ];
        
        return resultado;
      }
    },
    saldoEm(state, getters) {
      return (conta, data) => {
        const lancamentosDaConta = [
          ...state.list.filter(l => l.idConta == conta.id),
          ...getters.projecoesAte([conta.id], data)
        ];

        const saldoAcumulado = lancamentosDaConta
          .filter(l => l.data.isSameOrBefore(data));

        return conta.saldoInicial + saldoAcumulado.map(l => l.valor).reduce((x, y) => x+y, 0);
      }
    },
    lancamentosDia(state) {
      return (idConta, data) => {
        return ordena(state.list.filter(l => l.idConta == idConta && l.data.isSame(data)));
      }
    }
  },
  mutations: {
    [m.CONTA_CARREGADA](state, idConta) {
      state.contasCarregadas.push(idConta);
    },
    [m.SET_LANCAMENTOS](state, {lancamentos, getters}) {
      state.list = lancamentos;
      
      const saldos = {};
      state.list.forEach(l => {
        if (saldos[l.idConta] == null) {
          saldos[l.idConta] = getters.getConta(l.idConta).saldoInicial;
        }
        saldos[l.idConta] += l.valor;
        Vue.set(l, 'saldoDiario', saldos[l.idConta]);
      });
    },
    [m.REMOVE_LANCAMENTO_ID](state, id) {
      const lancamento = state.list.find(l => l.id === id);
      const ix = state.list.indexOf(lancamento);
      state.list.splice(ix, 1);
    }
  },
  actions: {
    [d.LANCAMENTO_SUBMIT](context, lancamento) {
      const state = context.state;
      const doDia = context.getters.lancamentosDia(lancamento.idConta, lancamento.data);
      if (doDia.length > 0) {
        lancamento.ordem =  doDia[doDia.length - 1].ordem + 1;
      } else {
        lancamento.ordem = 0;
      }

      var actionSubmit = put(context, lancamento);
      var actions = [];
      actions.push({
        location: actionSubmit.location,
        value: actionSubmit.value
      });

      if (lancamento.creationCallback)
        actions.push(lancamento.creationCallback(actionSubmit.id, lancamento));

      var update = {};
      for (const action of actions) {
        update[action.location] = action.value;
      }

      return  firebase.database().ref().update(update) .then((l) => {
        let msg = '';
        if (lancamento.id) {
          msg = 'Lançamento atualizado com sucesso!';
        }
        else {
          msg = 'Lançamento criado com sucesso!';
        }
        context.commit(SNACKS.m.UPDATE_SUCESSO, msg);
      }).catch((err) => {
        context.commit(SNACKS.m.UPDATE_ERRO, err);
        throw err;
      });
    },
    [d.LANCAMENTO_LOAD]({state, dispatch, commit, getters}, contaId) {
      if (!getters.uid)
        return;

      if (state.contasCarregadas.indexOf(contaId) >= 0) 
        return;

      commit(m.CONTA_CARREGADA, contaId)

      firebase.database().ref(getters.uid + '/lancamentos/' + contaId).on('child_added', (snap) => {
        const lancamento = snap.val();
        lancamento.id = snap.key;
        lancamento.data = moment(lancamento.data);

        commit(LOCAIS.m.ADD_LOCAL, lancamento.local);
        commit(m.SET_LANCAMENTOS, {
          lancamentos: [...state.list, lancamento],
          getters
        });
      }, (err) => {
        commit(SNACKS.m.UPDATE_ERRO, 'Erro ao carregar lançamentos! ' + err);
      });

      firebase.database().ref(getters.uid + '/lancamentos/' + contaId).on('child_removed', (snap) => {
        commit(m.REMOVE_LANCAMENTO_ID, snap.key);
      }, (err) => {
        commit(SNACKS.m.UPDATE_ERRO, 'Erro ao remover lançamentos! ' + err);
      });
    },
    [d.REMOVE_LANCAMENTO]({state, dispatch, commit, getters}, lancamento) {
      var update = {};
      update[getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id] = null;
      if (lancamento.creationCallback) {
        const recorrencia = lancamento.creationCallback('REMOVIDO', lancamento);
        update[recorrencia.location] = recorrencia.value;
      }
      
      firebase.database().ref().update(update).then(() => {  
        commit(SNACKS.m.UPDATE_SUCESSO, 'Lançamento excluído com sucesso!');
      }).catch((err) => {
        context.commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
        throw err;
      });
    },
    [d.TROCA_CONTA]({dispatch, getters, commit}, { lancamento, idNovaConta }) {
      firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).remove().then(() => {
        lancamento.idConta = idNovaConta;
        firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).set(lancamento.toFirebaseObject()).then(() => {
          commit(SNACKS.m.UPDATE_SUCESSO, 'Troca de conta efetuada com sucesso!');
        }).catch((err) => {
          commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
          throw err;
        })
      });
    },
    [d.SOBE_LANCAMENTO]({getters, dispatch}, lancamento) {
      const lancamentos = getters.lancamentosDia(lancamento.idConta, lancamento.data);
      const ix = lancamentos.indexOf(lancamento);
      if (ix > 0) {
        const anterior = lancamentos[ix - 1];
        console.info('subindo ' + lancamento.ordem + ' com ' + anterior.ordem)
        const ordemAnterior = anterior.ordem;
        anterior.ordem = lancamento.ordem;
        lancamento.ordem = ordemAnterior;
      }
    },
    [d.DESCE_LANCAMENTO]({getters, dispatch}, lancamento) {
      
      const lancamentos = getters.lancamentosDia(lancamento.idConta, lancamento.data);
      const ix = lancamentos.indexOf(lancamento);
      console.info(ix)
      if (ix < lancamentos.length - 1) {
        const posterior = lancamentos[ix + 1];
        console.info('descendo ' + lancamento.ordem + ' com ' + posterior.ordem)
        const ordemPosterior = posterior.ordem;
        posterior.ordem = lancamento.ordem;
        lancamento.ordem = ordemPosterior;
      }
    }
  }
}