import axios from 'axios';
import { contas } from './conta';
import { LOCAIS } from './locais';
import moment from 'moment';
import Vue from 'vue';
import { SNACKS } from './snacks';

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
  LANCAMENTO_UPDATE_SALDO: 'lancamentoUpdateSaldo'
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

export function normalizeLancamentos(lancamentos, getters) {
  lancamentos.forEach(l => {
    if (typeof l.data === 'string')
      l.data = moment(l.data);
    l.conta = getters.getConta(l.conta.id);
    
    const local = getters.getLocal(l.local.id)
    if (local != null)
      l.local = local;

    const categoria = l.categoria ? getters.getCategoria(l.categoria.id) : null;
    if (categoria != null)
      l.categoria = categoria;
  });

  lancamentos.sort((a, b) => {
    const porData = a.data.valueOf() - b.data.valueOf();
    if (porData != 0)
      return porData;
    
    return a.id - b.id;
  });
}

function put({ dispatch, commit, state, getters}, lancamento) {
  new Promise((resolve, reject) => {
    axios.put('/api/lancamentos', lancamento).then(res => {
      commit(LOCAIS.m.MAYBE_NOVO_LOCAL, res.data.local);
      const lancamentos = state.list;
      if (lancamento.id == null) {
        lancamentos.push(res.data);
      }
      
      commit(m.SET_LANCAMENTOS, {
        lancamentos: lancamentos,
        getters
      });
      
      resolve();
    }).catch(err => {
      if (err.response.status === 400) {
        commit(m.SET_BACKEND_ERRORS, err.response.data.fieldErrors);
      }
      reject();
    });
  });
}

export const store = {
  state: {
    list: []
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
          return contasIds.indexOf(l.conta.id) >= 0;
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
            local: { id: -1, nome: 'Saldo inicial'},
            categoria: { id: -1},
            saldoDiario: saldoInicial,
            efetuada: false
          }, 
          ...lancamentosDoMes, 
          {
            id: -2,
            data: saldoDataFinal,
            conta: null,
            local: { id: -2, nome: 'Saldo final'},
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
        const lancamentosDaConta = state.list.filter(l => l.conta.id == conta.id);
        
        const saldoAcumulado = lancamentosDaConta
          .filter(l => l.data.isSameOrBefore(data));

        return conta.saldoInicial + saldoAcumulado.map(l => l.valor).reduce((x, y) => x+y, 0);
      }
    }
  },
  mutations: {
    [m.SET_LANCAMENTOS](state, {lancamentos, getters}) {
      state.list = lancamentos;
      
      normalizeLancamentos(state.list, getters);
      
      /*const saldos = {};
      state.list.forEach(l => {
        if (saldos[l.conta.id] == null) {
          saldos[l.conta.id] = getters.getConta(l.conta.id).saldoInicial;
        }
        saldos[l.conta.id] += l.valor;
        Vue.set(l, 'saldoDiario', saldos[l.conta.id]);
      });*/
    },
  },
  actions: {
    [d.LANCAMENTO_SUBMIT](context, lancamento) {
      const state = context.state;
      return put(context, lancamento)
    },
    [d.LANCAMENTO_EDICAO_SUBMIT](context, lancamento)  {
      return put(context, lancamento)
    },
    [d.LANCAMENTO_LOAD]({dispatch, commit, getters}) {
      return new Promise((resolve, reject) => {
        axios.get('/api/lancamentos').then(res => {
          res.data.forEach(l => {
            return l.conta = getters.getConta(l.conta.id)
          });
          commit(m.SET_LANCAMENTOS, {
            lancamentos: res.data,
            getters
          });
          resolve();
        })
      });
    },
    [d.REMOVE_LANCAMENTO]({state, dispatch, commit, getters}, lancamento) {
      axios.delete('/api/lancamentos', {
        data: lancamento
      }).then(() => {
        const lancamentosAtuais = [...state.list];
        const ix = state.list.indexOf(lancamento);
        lancamentosAtuais.splice(ix, 1);
        commit(m.SET_LANCAMENTOS, {
          lancamentos: lancamentosAtuais,
          getters
        });
      }).catch(err => {
        commit(SNACKS.m.TRATA_ERRO, err);
      });
    }
  }
}