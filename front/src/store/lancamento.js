import axios from 'axios';
import { contas } from './conta';
import { LOCAIS } from './locais';
import moment from 'moment';
import Vue from 'vue';

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

function put(dispatch, commit, lancamento) {
  new Promise((resolve, reject) => {
    axios.put('/api/lancamentos', lancamento).then(res => {
      commit(m.LIMPA_FORMULARIO);
      dispatch(LOCAIS.d.LOAD_LOCAIS);
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
    list: [],
    form: {
      data: new Date(),
      conta: null,
      local: null,
      categoria: null,
      valor: null,
      efetuada: false
    },
    formErrors: {
      data: [],
      conta: [],
      local: [],
      categoria: [],
      valor: [],
      efetuada: []
    },
    edicao: {
      data: new Date(),
      conta: null,
      local: null,
      categoria: null,
      valor: null,
      efetuada: false
    },
  },
  getters: {
    lancamentoEdicao(state) {
      return state.edicao;
    },
    lancamentosDe(state, getters) {
      return (contaId, mes, ano) => {
        const conta = getters.getConta(contaId);
        if (conta == null)
          return [];
        
        const lancamentosDaConta = state.list.filter(l => l.conta.id = contaId);
        
        
        const lancamentosDoMes = lancamentosDaConta.filter(l => {
          return l.data.month() + 1 == mes && l.data.year() == ano;
        });
        
        const dataBase = moment(ano + '-' + mes + '-' + 1, 'YYYY-MM-DD');
        
        const saldoDataInicial = dataBase.clone().add(-1, 'days');
        const saldoDataFinal = dataBase.clone().add(1, 'month').add(-1, 'days');
        
        return [ {
            data: saldoDataInicial,
            conta: conta,
            local: { nome: 'Saldo inicial'},
            categoria: {},
            saldoDiario: getters.saldoEm(conta, saldoDataInicial),
            efetuada: false
          }, 
          ...lancamentosDoMes
          , {
            data: saldoDataFinal,
            conta: conta,
            local: { nome: 'Saldo final'},
            categoria: {},
            saldoDiario: getters.saldoEm(conta, saldoDataFinal),
            efetuada: false
          }
        ]
      }
    },
    saldoEm(state) {
      return (conta, data) => {
        const lancamentosDaConta = state.list.filter(l => l.conta.id = conta.id);
        
        const saldoAcumulado = lancamentosDaConta
          .filter(l => l.data.isSameOrBefore(data))
          .map(l => l.valor)
          .reduce((x, y) => x+ y, 0);

        return conta.saldoInicial + saldoAcumulado;
      }
    }
  },
  mutations: {
    [m.LANCAMENTO_SET_DATA](state, data) {
      state.form.data = data;
    },
    [m.LANCAMENTO_SET_LOCAL](state, local) {
      state.formErrors.local = [];
      state.form.local = local;
    },
    [m.LANCAMENTO_SET_CATEGORIA](state, categoria) {
      state.formErrors.categoria = [];
      state.form.categoria = categoria;
    },
    [m.LANCAMENTO_SET_VALOR](state, valor) {
      state.form.valor = valor;
    },
    [m.LANCAMENTO_SET_EFETUADA](state, efetuada) {
      state.form.efetuada = efetuada;
    },
    [m.LANCAMENTO_SET_CONTA](state, conta) {
      state.form.conta = conta;
    },
    [m.LIMPA_FORMULARIO](state) {
      state.form.local= null;
      state.form.categoria= null;
      state.form.valor= null;
      state.form.efetuada= false;
    },
    [m.SET_BACKEND_ERRORS](state, errors) {
      function getError(field) {
        const error = errors.find(e => e.field === field);
        return error ? [ error.message ] : [];
      }

      state.formErrors.local = getError('local.nome');
      state.formErrors.categoria = getError('categoria');
    },
    [m.SET_EDICAO](state, lancamento) {
      state.edicao = lancamento;
    },
    [m.EDICAO_SET_LOCAL](state, local) {
      state.edicao.local = local;
    },
    [m.SET_LANCAMENTOS](state, lancamentos) {
      state.list = lancamentos;
      state.list.sort((a, b) => {
        return a.data.valueOf() - b.data.valueOf();
      });
      state.list.forEach(l => {
        l.data = moment(l.data);
      })
    },
    [m.LANCAMENTO_UPDATE_SALDO](state, payload) {
      Vue.set(payload.lancamento, 'saldoDiario', payload.saldo);
    }
  },
  actions: {
    [d.LANCAMENTO_SUBMIT]({state, dispatch, commit}) {
      return put(dispatch, commit, {
        data: state.form.data,
        conta: state.form.conta,
        valor: state.form.valor,
        categoria: state.form.categoria,
        local: state.form.local,
        efetivada: state.form.efetuada
      })
    },
    [d.LANCAMENTO_EDICAO_SUBMIT]({state, dispatch, commit})  {
      return put(dispatch, commit, state.edicao)
    },
    [d.LANCAMENTO_LOAD]({commit, getters}) {
      return new Promise((resolve, reject) => {
        axios.get('/api/lancamentos').then(res => {
          res.data.forEach(l => l.conta = getters.getConta(l.conta.id))
          commit(m.SET_LANCAMENTOS, res.data);
          resolve();
        })
      });
    },
    [d.UPDATE_SALDOS]({state, commit, getters}) {
      const saldos = {};
      state.list.forEach(l => {
        if (saldos[l.conta.id] == null) {
          saldos[l.conta.id] = getters.getConta(l.conta.id).saldoInicial;
        }
        saldos[l.conta.id] += l.valor;
        commit(m.LANCAMENTO_UPDATE_SALDO, {
          lancamento: l, saldo: saldos[l.conta.id]
        })
      });
    },
    [d.REMOVE_LANCAMENTO]({state, dispatch, commit}, lancamento) {
      axios.delete('/api/lancamentos', {
        data: lancamento
      }).then(() => {
        commit(m.SET_LANCAMENTOS, state.list.splice(state.list.indexOf(lancamento), 1));
        dispatch(d.UPDATE_SALDOS);
      }).catch(err => {
        commit(SNACKS.m.TRATA_ERRO, err);
      });
    }
  }
}