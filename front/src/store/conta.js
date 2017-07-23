import Vuex from 'vuex';
import axios from 'axios';
import Vue from 'vue';
import moment from 'moment';

export const CONTA_SET_NOME = 'contaSetNome';
export const CONTA_SET_SALDO_INICIAL = 'contaSetSaldoInicial';
export const CONTA_SET_ATUAL = 'contaSetAtual';
export const CONTAS_CARREGADAS = 'contasCarregadas';
export const CONTA_CARREGADA = 'contaCarregada';
export const CARREGA_CONTA = 'carregaConta';
export const LOAD_CONTAS = 'loadContas';

const m = {
  CONTA_SET_LANCAMENTOS: 'contaSetLancamentos',
  CONTA_SET_SALDO_INICIO: 'contaSetSaldoInicio',
  CONTA_SET_SALDO_FIM: 'contaSetSaldoFim',
  CONTA_SET_PARAMS: 'contaSetParams'
};

const d = {
  CONTA_CARREGA_SALDOS: 'contaCarregaSaldoAnterior',
  CARREGA_CONTA_LANCAMENTOS: 'carregaContaLancamentos',
  REMOVE_LANCAMENTO: 'contaRemoveLancamento'
}

export const contas = {
  m, d
};

function sortLancamentos(lancamentos) {
  lancamentos.sort((a, b) => {
    return a.data.valueOf() - b.data.valueOf();
  });
} 

function atualizaSaldoDiario(saldoInicio, lancamentos) {
  let ultimaData = null;
  let ultimoSaldo = saldoInicio;
  for (let l of lancamentos) {
    ultimoSaldo = ultimoSaldo + l.valor;

    if (l.data.valueOf() !== ultimaData || l == lancamentos[lancamentos.length - 1]) {
      ultimaData = l.data.valueOf();
      Vue.set(l, 'saldoDiario', ultimoSaldo);
    }
  }
}

export default {
  state: {
    form: {
      nome: '',
      saldoInicial: 0
    },
    params: {
      contaId: null,
      mes: null,
      ano: null
    },
    list: [],
    conta: {},
    saldoInicio: {},
    saldoFim: {},
    lancamentos: []
  },
  mutations: {
    [CONTA_SET_NOME](state, nome) {
      state.form.nome = nome;
    },
    [CONTA_SET_SALDO_INICIAL](state, saldoInicial) {
      state.form.saldoInicial = saldoInicial;
    },
    [CONTAS_CARREGADAS](state, contas) {
      state.list = contas;
    },
    [CONTA_SET_ATUAL](state, conta) {
      state.conta = conta;
    },
    [m.CONTA_SET_LANCAMENTOS](state, lancamentos) {
      sortLancamentos(lancamentos);
      state.lancamentos = lancamentos
    },
    [m.CONTA_SET_SALDO_INICIO](state, saldo) {
      state.saldoInicio = saldo;
      atualizaSaldoDiario(state.saldoInicio.saldo, state.lancamentos);
      
      state.lancamentos = [{
        data: moment(saldo.data),
        local: {nome: 'Saldo inicial'},
        categoria: {},
        saldoDiario: saldo.saldo
      }, ...state.lancamentos];
    },
    [m.CONTA_SET_SALDO_FIM](state, saldo) {
      state.saldoFim = saldo;
      state.lancamentos.push({
        data: moment(saldo.data),
        local: {nome: 'Saldo final'},
        categoria: {},
        saldoDiario: saldo.saldo
      });
    },
    [m.CONTA_SET_PARAMS](state, params) {
      state.params = params;
    }
  },
  actions: {
    contaSubmit({commit, state, rootState}) {
      axios.post('/api/contas', {
        nome: state.form.nome,
        saldoInicial: state.form.saldoInicial,
        usuario: rootState.authentication.usuario.id
      }).then(res => {
        console.info(res);
      }).catch(err => {
        console.info(err);
      })
    },
    [LOAD_CONTAS]({commit, state}) {
      axios.get('/api/contas').then(res => {
        commit(CONTAS_CARREGADAS, res.data);
      }).catch(err => {
        console.error(err);
      })
    },
    [CARREGA_CONTA]({commit, state}, contaId){
      axios.get('/api/contas/' + contaId).then(res =>{
        commit(CONTA_SET_ATUAL, res.data);
      }).catch(err => {
        console.error(err);
      })
    },
    [d.CARREGA_CONTA_LANCAMENTOS]({dispatch, commit, state}) {
      axios.get('/api/contas/' + state.params.contaId + "/lancamentos?mes=" + state.params.mes + '&ano=' + state.params.ano).then(res =>{
        res.data.forEach(l => {
          l.data = moment(l.data);
        });

        commit(m.CONTA_SET_LANCAMENTOS, res.data);
        dispatch(d.CONTA_CARREGA_SALDOS);
      }).catch(err => {
        console.error(err);
      })
    },
    [d.CONTA_CARREGA_SALDOS]({commit, dispatch, state}) {
      const mesAtual = moment(state.params.ano + '-' + state.params.mes + '-1', 'YYYY-MM-DD')
      
      const fimMesAnterior = mesAtual.add(-1, 'day').format('YYYY-MM-DD');
      const fimMesAtual = mesAtual.add(1, 'month').format('YYYY-MM-DD');
      
      axios.all([
        axios.get('/api/contas/' + state.params.contaId + "/saldo?data=" + fimMesAnterior),
        axios.get('/api/contas/' + state.params.contaId + "/saldo?data=" + fimMesAtual)
      ]).then(axios.spread((resSaldoInicio, resSaldoFim) => {
        commit(m.CONTA_SET_SALDO_INICIO, resSaldoInicio.data);
        commit(m.CONTA_SET_SALDO_FIM, resSaldoFim.data);
      }));
    },
    [d.REMOVE_LANCAMENTO]({dispatch}, lancamento) {
      axios.delete('/api/lancamentos/' + lancamento.id).then(() => {
        dispatch(d.CARREGA_CONTA_LANCAMENTOS);
      }).catch(err => {
        console.info(err);
      });
    }
  }
};