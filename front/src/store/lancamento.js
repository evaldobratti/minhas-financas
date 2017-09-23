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

function put({ dispatch, commit, state, getters}, lancamento) {
  new Promise((resolve, reject) => {
    axios.put('/api/lancamentos', lancamento).then(res => {
      commit(m.LIMPA_FORMULARIO);
      commit(LOCAIS.m.MAYBE_NOVO_LOCAL, res.data.local);
      commit(m.SET_LANCAMENTOS, {
        lancamentos: [...state.list, res.data],
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
        console.info('calculando');
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
          ...lancamentosDoMes, 
          {
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
          .filter(l => l.data.isSameOrBefore(data));
          /*.map(l => l.valor)
          .reduce((x, y) => x+ y, 0);*/
        if (saldoAcumulado.length)
          return saldoAcumulado[saldoAcumulado.length - 1].saldoDiario;
        else 
          return conta.saldoInicial;
        //return conta.saldoInicial + saldoAcumulado;
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
    [m.SET_LANCAMENTOS](state, {lancamentos, getters}) {
      state.list = lancamentos;
      
      state.list.forEach(l => {
        if (typeof l.data === 'string')
          l.data = moment(l.data);
        l.local = getters.getLocal(l.local.id);
      });

      state.list.sort((a, b) => {
        return a.data.valueOf() - b.data.valueOf();
      });
      
      const saldos = {};
      state.list.forEach(l => {
        if (saldos[l.conta.id] == null) {
          saldos[l.conta.id] = getters.getConta(l.conta.id).saldoInicial;
        }
        saldos[l.conta.id] += l.valor;
        Vue.set(l, 'saldoDiario', saldos[l.conta.id]);
      });
    },
  },
  actions: {
    [d.LANCAMENTO_SUBMIT](context) {
      const state = context.state;
      return put(context, {
        data: state.form.data,
        conta: state.form.conta,
        valor: state.form.valor,
        categoria: state.form.categoria,
        local: state.form.local,
        efetivada: state.form.efetuada
      })
    },
    [d.LANCAMENTO_EDICAO_SUBMIT](context)  {
      return put(context, context.state.edicao)
    },
    [d.LANCAMENTO_LOAD]({dispatch, commit, getters}) {
      return new Promise((resolve, reject) => {
        axios.get('/api/lancamentos').then(res => {
          res.data.forEach(l => l.conta = getters.getConta(l.conta.id))
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