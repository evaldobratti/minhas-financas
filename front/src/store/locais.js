import axios from 'axios';
import { SNACKS } from './snacks';

const m = {
  SET_LOCAIS: 'locaisSet',
  MAYBE_NOVO_LOCAL: 'localMaybeNovo'
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
    },
    getLocal(state) {
      return id => {
        return state.list.find(l => l.id === id);
      }
    }
  },
  mutations: {
    [m.SET_LOCAIS](state, locais) {
      state.list = locais;
    },
    [m.MAYBE_NOVO_LOCAL](state, local) {
      if (!state.list.find(l => l.id == local.id)) {
        console.info("novo local ", local);
        state.list = [...state.list, local]
      }
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

