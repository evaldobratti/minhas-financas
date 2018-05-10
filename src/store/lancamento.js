import { contas } from './conta';
import { LOCAIS } from './locais';
import moment from 'moment';
import Vue from 'vue';
import { SNACKS } from './snacks';
import firebase from 'firebase';
import EventBus from '../EventBus';
import { createNoSubstitutionTemplateLiteral } from 'typescript';

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

function newLancamento({ getters}, lancamento) {
  let location = '';
  let id = '';
  if (lancamento.ordem == null) {
    const doDia = getters.lancamentosDia(lancamento.idConta, lancamento.data);
    if (doDia.length > 0) {
      lancamento.ordem =  doDia[doDia.length - 1].ordem + 1;
    } else {
      lancamento.ordem = 0;
    }
  }

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

export const lancamentos = {
  m,
  d,
  utils: {
    newLancamento
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
      return (contasIds, inicio, fim) => {
        if (getters.getContas.length == 0)
          return [];
        
        const lancamentosDaConta = state.list.filter(l => {
          return contasIds.indexOf(l.idConta) >= 0;
        });
        
        let lancamentosDoMes = lancamentosDaConta.filter(l => {
          return l.data.isSameOrAfter(inicio) && l.data.isSameOrBefore(fim);
        });
        
        const projecoes = getters.projecoesAte(contasIds, fim);
        
        projecoes.filter(l => {
          return l.data.isSameOrAfter(inicio) && l.data.isSameOrBefore(fim);
        }).forEach(l => {
          lancamentosDoMes.push(l);
        })
        
        return ordena(lancamentosDoMes);
      }
    },
    saldoEm(state, getters) {
      return (conta, data) => {
        const lancamentosDaConta = [
          ...state.list.filter(l => l.idConta == conta.id),
          ...getters.projecoesAte([conta.id], data)
        ];

        const saldoAcumulado = lancamentosDaConta.filter(l => l.data.isSameOrBefore(data));

        return conta.saldoInicial + saldoAcumulado.map(l => l.valor).reduce((x, y) => x+y, 0);
      }
    },
    lancamentosDia(state) {
      return (idConta, data) => {
        return ordena(state.list.filter(l => l.idConta == idConta && l.data.isSame(data)));
      }
    },
    getLancamento(state) {
      return (idLancamento) => {
        return state.list.find(lancamento => lancamento.id == idLancamento);
      }
    }
  },
  mutations: {
    [m.CONTA_CARREGADA](state, idConta) {
      state.contasCarregadas.push(idConta);
    },
    [m.SET_LANCAMENTOS](state, {lancamentos}) {
      state.list = lancamentos;
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

      let futureInteressado = EventBus.notificaInteressados(EventBus.events.ATUALIZANDO_LANCAMENTO, lancamento);
      if (futureInteressado != null)
        return futureInteressado;
      
      var actionSubmit = newLancamento(context, lancamento);
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

      return new Promise((resolve, reject) => {
        firebase.database().ref().update(update) .then((l) => {
          resolve(lancamento.id ? 'Lançamento atualizado com sucesso!' : 'Lançamento criado com sucesso!');
        }).catch((err) => {
          reject(err);
        });
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
      
      return new Promise((resolve, reject) => {
        firebase.database().ref().update(update).then(() => {  
          resolve('Lançamento excluído com sucesso!');
        }).catch((err) => {
          reject('Ocorreram erros!');
        });
      });
      
    },
    [d.TROCA_CONTA]({dispatch, getters, commit}, { lancamento, idNovaConta }) {
      firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).remove().then(() => {
        lancamento.idConta = idNovaConta;
        return new Promise((resolve, reject) => {
          firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).set(JSON.parse(JSON.stringify(lancamento))).then(() => {
            resolve('Troca de conta efetuada com sucesso!');
          }).catch((err) => {
            commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
            throw err;
          })
        });
      });
    },
    [d.SOBE_LANCAMENTO]({getters, dispatch}, lancamento) {
      return new Promise((resolve, reject) => {
        const lancamentos = getters.lancamentosDia(lancamento.idConta, lancamento.data);
        const ix = lancamentos.indexOf(lancamento);
        if (ix > 0) {
          const anterior = lancamentos[ix - 1];
          
          const ordemAnterior = anterior.ordem;
          anterior.ordem = lancamento.ordem;
          lancamento.ordem = ordemAnterior;

          const updateAtual = newLancamento({getters}, lancamento);
          const updateAnterior = newLancamento({getters}, anterior);

          const update = {};
          update[updateAtual.location] = updateAtual.value;
          update[updateAnterior.location] = updateAnterior.value;

          firebase.database().ref().update(update).then(() => {
            resolve('Lançamentos reordenados!');
          }).catch(() => {
            reject('Erro ao reordenar lançamentos');
          });
        } else {
          resolve();
        }
      })
    },
    [d.DESCE_LANCAMENTO]({getters, dispatch}, lancamento) {
      return new Promise((resolve, reject) => {
        const lancamentos = getters.lancamentosDia(lancamento.idConta, lancamento.data);
        const ix = lancamentos.indexOf(lancamento);
        if (ix < lancamentos.length - 1) {
          const posterior = lancamentos[ix + 1];
          
          const ordemPosterior = posterior.ordem;
          posterior.ordem = lancamento.ordem;
          lancamento.ordem = ordemPosterior;

          const updateAtual = newLancamento({getters}, lancamento);
          const updatePosterior = newLancamento({getters}, posterior);

          const update = {};
          update[updateAtual.location] = updateAtual.value;
          update[updatePosterior.location] = updatePosterior.value;

          firebase.database().ref().update(update).then().then(() => {
            resolve('Lançamentos reordenados!');
          }).catch(() => {
            reject('Erro ao reordenar lançamentos');
          });
        } else {
          resolve();
        }
      });
    }
  }
}