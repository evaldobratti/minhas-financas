import { contas } from './conta';
import { LOCAIS } from './locais';
import moment from 'moment';
import Vue from 'vue';
import { SNACKS } from './snacks';
import firebase from 'firebase';

const m = {
  LANCAMENTO_SET_DATA: 'lancamentoSetData',
  LANCAMENTO_SET_LOCAL: 'lancamentoSetLocal',
  LANCAMENTO_SET_CONTA: 'lancamentoSetConta',
  LANCAMENTO_SET_CATEGORIA: 'lancamentoSetCategoria',
  LANCAMENTO_SET_VALOR: 'lancamentoSetValor',
  LANCAMENTO_SET_EFETUADA: 'lancamentoSetEfetuada',
  LIMPA_FORMULARIO: 'lancamentoLimpaFormulario',
  SET_BACKEND_ERRORS: 'lancamentoBackendErrors',
  SET_EDICAO: 'lancamentoSetEdicao',
  EDICAO_SET_LOCAL: 'lancamentoEdicaoSetLocal',
  SET_LANCAMENTOS: 'lancamentoSetList',
  LANCAMENTO_UPDATE_SALDO: 'lancamentoUpdateSaldo', 
  REMOVE_LANCAMENTO_ID: 'lancamentoRemoveId',
  CONTA_CARREGADA: 'lancamentoContaCarregada'
}

const d = {
  LANCAMENTO_SUBMIT: 'lancamentoFormSubmit',
  LANCAMENTO_EDICAO_SUBMIT: 'lancamentoEdicaoSubmit',
  LANCAMENTO_LOAD: 'lancamentoLoad',
  UPDATE_SALDOS: 'lancamentoUpdateSaldos',
  REMOVE_LANCAMENTO: 'lancamentoRemove'
}

export const lancamentos = {
  m,
  d
}

export class Lancamento {
  constructor(snapshot) {
    if (snapshot) {
      const firebaseObject = snapshot.val();
      this.id = snapshot.key;
      this.data = moment(firebaseObject.data);
      this.idConta = firebaseObject.idConta;
      this.idCategoria = firebaseObject.idCategoria;
      this.local = firebaseObject.local;
      this.valor = firebaseObject.valor;
      this.efetivada = firebaseObject.efetivada;
      return;
    }
    this.id = null;
    this.idConta = null;
    this.data = moment();
    this.local = null;
    this.valor = null;
    this.efetivada = false;
    this.idCategoria = null;
  }

  toFirebaseObject() {
    return {
      idConta: this.idConta,
      data: this.data.toISOString(),
      local: this.local,
      valor: this.valor,
      idCategoria: this.idCategoria
    }
  }

}

export function normalizeLancamentos(lancamentos, getters) {
  lancamentos.forEach(l => {
    if (typeof l.data === 'string')
      l.data = moment(l.data);
    
    const categoria = l.categoria ? getters.getCategoria(l.categoria.id) : null;
    if (categoria != null)
      l.categoria = categoria;

    if (l.motivo != null)  {
      if (l.motivo['@class'].endsWith("RecorrenciaLancamentoGerado")) {
        l.motivo.data = moment(l.motivo.data);
        l.motivo.recorrencia.partirDe = moment(l.motivo.recorrencia.partirDe);
        l.motivo.recorrencia.dataFim = l.motivo.recorrencia.dataFim != null ?
          moment(l.motivo.recorrencia.dataFim) :
          null;
      }
    }
  });

  lancamentos.sort((a, b) => {
    const porData = a.data.valueOf() - b.data.valueOf();
    if (porData != 0)
      return porData;
    
    return a.id - b.id;
  });
}

function put({ dispatch, commit, state, getters}, lancamento) {
  if (lancamento.id)
    return firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).set(lancamento.toFirebaseObject());

  return firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta).push(lancamento.toFirebaseObject());
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
        
        const lancamentosDaConta = state.list.filter(l => {
          return contasIds.indexOf(l.idConta) >= 0;
        });
        const lancamentosDoMes = lancamentosDaConta.filter(l => {
          return l.data.month() == mes && l.data.year() == ano;
        });
        
        const dataBase = moment(ano + '-' + (mes + 1) + '-' + 1, 'YYYY-MM-DD');
        const saldoDataInicial = dataBase.clone().add(-1, 'days');
        const saldoDataFinal = dataBase.clone().add(1, 'month').add(-1, 'days');

        let saldoInicial = 0;
        contasIds.forEach(contaId => {
          saldoInicial += getters.saldoEm(getters.getConta(contaId), saldoDataInicial);
        });

        let saldoFinal = 0;
        contasIds.forEach(contaId => {
          saldoFinal += getters.saldoEm(getters.getConta(contaId), saldoDataFinal);
        });

        lancamentosDoMes.reduce((x, y) => {
          const saldo = x + y.valor;
          Vue.set(y, 'saldoDiario', saldo);
          return saldo;
        }, saldoInicial);

        const resultado = [ {
            id: -1,
            data: saldoDataInicial,
            conta: null,
            local: 'Saldo inicial',
            categoria: { id: -1},
            saldoDiario: saldoInicial,
            efetuada: false
          }, 
          ...lancamentosDoMes, 
          {
            id: -2,
            data: saldoDataFinal,
            conta: null,
            local: 'Saldo final',
            categoria: { id: -2 },
            saldoDiario: saldoFinal,
            efetuada: false
          }
        ];
        return resultado;
      }
    },
    saldoEm(state) {
      return (conta, data) => {
        const lancamentosDaConta = state.list.filter(l => l.idConta == conta.id);
        
        const saldoAcumulado = lancamentosDaConta
          .filter(l => l.data.isSameOrBefore(data));

        return conta.saldoInicial + saldoAcumulado.map(l => l.valor).reduce((x, y) => x+y, 0);
      }
    }
  },
  mutations: {
    [m.CONTA_CARREGADA](state, idConta) {
      state.contasCarregadas.push(idConta);
    },
    [m.SET_LANCAMENTOS](state, {lancamentos, getters}) {
      state.list = lancamentos;
      
      normalizeLancamentos(state.list, getters);
      
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
      return put(context, lancamento).then(() => {
        let msg = '';
        if (lancamento.id)
          msg = 'Lançamento atualizado com sucesso!';
        else
          msg = 'Lançamento criado com sucesso!';
        context.commit(SNACKS.m.UPDATE_SUCESSO, msg);
      }).catch((err) => {
        context.commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
        throw err;
      });
    },
    [d.LANCAMENTO_EDICAO_SUBMIT](context, lancamento)  {
      return put(context, lancamento)
    },
    [d.LANCAMENTO_LOAD]({state, dispatch, commit, getters}, contaId) {
      if (state.contasCarregadas.indexOf(contaId) >= 0) 
        return;

      commit(m.CONTA_CARREGADA, contaId)

      firebase.database().ref(getters.uid + '/lancamentos/' + contaId).on('child_added', (snap) => {
        const lancamento = new Lancamento(snap);
        commit(LOCAIS.m.ADD_LOCAL, lancamento.local);
        commit(m.SET_LANCAMENTOS, {
          lancamentos: [...state.list, lancamento],
          getters
        });
      }, (err) => {
        context.commit(SNACKS.m.UPDATE_ERRO, 'Erro ao carregar lançamentos! ' + err);
      });

      firebase.database().ref(getters.uid + '/lancamentos/' + contaId).on('child_removed', (snap) => {
        commit(m.REMOVE_LANCAMENTO_ID, snap.key);
      }, (err) => {
        commit(SNACKS.m.UPDATE_ERRO, 'Erro ao remover lançamentos! ' + err);
      });
    },
    [d.REMOVE_LANCAMENTO]({state, dispatch, commit, getters}, lancamento) {
      
      firebase.database().ref(getters.uid + '/lancamentos/' + lancamento.idConta + '/' + lancamento.id).remove().then(() => {
        commit(SNACKS.m.UPDATE_SUCESSO, 'Lançamento removido com sucesso!');
      }).catch((err) => {
        context.commit(SNACKS.m.UPDATE_ERRO, 'Ocorreram erros!');
        throw err;
      });
    }
  }
}