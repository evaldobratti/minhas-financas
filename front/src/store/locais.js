import axios from 'axios';
import { SNACKS } from './snacks';

const m = {
  SET_LOCAIS: 'locaisSet'
}

const d = {
  LOAD_LOCAIS: 'locaisLoad'
}

const store = {
  state: {
    list: []
  },
  getters: {
    locais(state) {
      return state.list;
    }
  },
  mutations: {
    [m.SET_LOCAIS](state, locais) {
      state.list = locais;
    }
  },
  actions: {
    [d.LOAD_LOCAIS]({commit}) {
      return new Promise((resolve, reject) => {
        axios.get('/api/locais').then(res => {
          commit(m.SET_LOCAIS, res.data);
          resolve(res.data);
        }).catch(err => {
          commit(SNACKS.m.TRATA_ERRO, err);
          reject();
        })
      });
    }
  }
}

export const LOCAIS = {
  m, d, store
}

