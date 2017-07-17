import axios from 'axios';
import router from '../router';
const LOGGED_IN = 'loggedIn';
const SET_USER_DATA = 'setUserData';
const LOGIN_FORM_RESET = 'loginFormReset';

export default {
  state: {
    form: {
      usuario: '',
      senha: '',
      erros: {
        form: ''
      }
    },
    isAuthenticated: false,
    token: '',
    usuario: {}
  },
  mutations: {
    [LOGGED_IN]: (state, token) => {
      state.isAuthenticated = true;
      state.token = token;
      localStorage.setItem('token', token);
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
    },
    [SET_USER_DATA]: (state, payload) => {
      state.usuario = payload;
    },
    loggedFail: (state, payload) => {
      state.form.erros.form = payload;
    },
    loggedOut: state => {
      state.isAuthenticated = false;
      localStorage.removeItem('token');
    },
    setFormUsuario: (state, usuario) => {
      state.form.usuario = usuario;
    },
    setFormSenha: (state, senha) => {
      state.form.senha = senha;
    },
    [LOGIN_FORM_RESET]: (state, senha) => {
      state.form.usuario = '';
      state.form.senha = '';
    }
  },
  actions: {
    getInitialData({commit, dispatch}) {
      if (localStorage.getItem('token')) {
        commit(LOGGED_IN, localStorage.getItem('token'));
        dispatch('getUserData');
      }
    },
    login({commit, state, dispatch, rootState}) {
      axios.post('api/authenticate', {
        username: state.form.usuario,
        password: state.form.senha
      }).then(res => {
        commit(LOGGED_IN, res.data.id_token);
        if (rootState.route.query.next){
            router.push(rootState.route.query.next);}
        else
            router.push('dashboard');
        dispatch('getUserData');
        commit(LOGIN_FORM_RESET);
      }).catch(err => {
        commit('loggedFail', err);
      });
    },
    getUserData({commit}) {
      axios.get('api/account').then(res => commit(SET_USER_DATA, res.data));
    }
  }
}