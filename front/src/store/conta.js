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
export const CARREGA_CONTA_LANCAMENTOS = 'carregaContaLancamentos';
export const LOAD_CONTAS = 'loadContas';

const m = {
  CONTA_SET_LANCAMENTOS: 'contaSetLancamentos',
  LANCAMENTO_CRIADO: 'lancamentoCriado',
  CONTA_SET_SALDO_INICIO: 'contaSetSaldoInicio',
  CONTA_SET_SALDO_FIM: 'contaSetSaldoFim',
  CONTA_SET_PARAMS: 'contaSetParams'
};

const d = {
  CONTA_CARREGA_SALDOS: 'contaCarregaSaldoAnterior'
}

export const contas = {
  m, d
};

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
    [CONTA_SET_NOME]: (state, nome) => {
      state.form.nome = nome;
    },
    [CONTA_SET_SALDO_INICIAL]: (state, saldoInicial) => {
      state.form.saldoInicial = saldoInicial;
    },
    [CONTAS_CARREGADAS](state, contas) {
      state.list = contas;
    },
    [CONTA_SET_ATUAL](state, conta) {
      state.conta = conta;
    },
    [m.CONTA_SET_LANCAMENTOS](state, lancamentos) {
      state.lancamentos = lancamentos
    },
    [m.LANCAMENTO_CRIADO](state, lancamento) {
      state.lancamentos.push(lancamento);
    },
    [m.CONTA_SET_SALDO_INICIO](state, saldo) {
      state.saldoInicio = saldo.saldo;
    },
    [m.CONTA_SET_SALDO_FIM](state, saldo) {
      state.saldoFim = saldo.saldo;
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
    [CARREGA_CONTA_LANCAMENTOS]({dispatch, commit, state}) {
      dispatch(d.CONTA_CARREGA_SALDOS);
      axios.get('/api/contas/' + state.params.contaId + "/lancamentos?mes=" + state.params.mes + '&ano=' + state.params.ano).then(res =>{
        commit(m.CONTA_SET_LANCAMENTOS, res.data);
      }).catch(err => {
        console.error(err);
      })
    },
    [d.CONTA_CARREGA_SALDOS]({commit, state}) {
      const mesAtual = moment(state.params.ano + '-' + state.params.mes + '-1', 'YYYY-MM-DD')
      
      const fimMesAnterior = mesAtual.add(-1, 'day').format('YYYY-MM-DD');
      axios.get('/api/contas/' + state.params.contaId + "/saldo?data=" + fimMesAnterior).then(res =>{
        commit(m.CONTA_SET_SALDO_INICIO, res.data);
      }).catch(err => {
        console.error(err);
      });

      const fimMesAtual = mesAtual.add(1, 'month').add(-1, 'day').format('YYYY-MM-DD');
      axios.get('/api/contas/' + state.params.contaId + "/saldo?data=" + fimMesAtual).then(res =>{
        commit(m.CONTA_SET_SALDO_FIM, res.data);
      }).catch(err => {
        console.error(err);
      });
    }
  }
};