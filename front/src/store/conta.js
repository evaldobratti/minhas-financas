import Vuex from 'vuex';
import axios from 'axios';
import Vue from 'vue';

export const CONTA_SET_NOME = 'contaSetNome';
export const CONTA_SET_SALDO_INICIAL = 'contaSetSaldoInicial';
export const CONTA_SET_ATUAL = 'contaSetAtual';
export const CONTAS_CARREGADAS = 'contasCarregadas';
export const CONTA_CARREGADA = 'contaCarregada';
export const CARREGA_CONTA = 'carregaConta';
export const CARREGA_CONTA_LANCAMENTOS = 'carregaContaLancamentos';
export const LOAD_CONTAS = 'loadContas';

const m = {
  CONTA_SET_LANCAMENTOS: 'contaSetLancamentos'
};

export const contas = {
  m
};

export default {
  state: {
    form: {
      nome: '',
      saldoInicial: 0
    },
    list: [],
    conta: {},
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
    [CARREGA_CONTA_LANCAMENTOS]({commit, state}, {contaId, mes, ano}) {
      axios.get('/api/contas/' + contaId + "/lancamentos?mes=" + mes + '&ano=' + ano).then(res =>{
        commit(m.CONTA_SET_LANCAMENTOS, res.data);
      }).catch(err => {
        console.error(err);
      })
    }
  }
};