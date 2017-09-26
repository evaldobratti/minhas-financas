import axios from 'axios';
import { SNACKS } from './snacks';

export const d = {
  LOAD_CATEGORIAS: 'loadCategorias',
  SAVE_CATEGORIA: 'saveCategoria'
}

export const m = {
  LOADED_CATEGORIAS: 'loadedCategorias'
}


export const store = {
  state: {
    list: [],
    flat: []
  },
  mutations: {
    [m.LOADED_CATEGORIAS](state, {categorias, categoriasFlat}) {
      state.list = categorias;
      state.flat = categoriasFlat;
    }
  },
  getters: {
    getCategoria(state) {
      return id => {
        return state.flat.find(c => c.id == id);
      }
    }
  },
  actions: {
    [d.LOAD_CATEGORIAS]({commit, state}, forceLoad) {
      if (forceLoad || state.list.length === 0) {
        return new Promise((resolve, reject) => {
          axios.get('/api/categorias').then(res => {
            res.data.forEach(c => {
              c.filhas = res.data.filter(f => f.pai && f.pai.id == c.id);
            });
            commit(m.LOADED_CATEGORIAS, {
               categorias: res.data.filter(c => c.pai == null), 
              categoriasFlat: res.data
            });
            resolve(state.list);
          }).catch(err => {
            commit(SNACKS.m.TRATA_ERRO, err);
            reject();
          });
        });
      } else {
        return Promise.resolve(state.list);
      }
    },
    [d.SAVE_CATEGORIA]({dispatch, commit}, categoria) {
      return new Promise((resolve, reject) => {
        axios.post('/api/categorias', categoria).then(res => {
          dispatch(d.LOAD_CATEGORIAS, true);
          resolve();
          commit(SNACKS.m.UPDATE_SNACK, {
            text: 'Categoria cadastrada!',
            timeout: 1500,
            context: 'success'
          });
        }).catch(err => {
          commit(SNACKS.m.TRATA_ERRO, err);
          reject();
        });
      });
    }
  }
}

export const CATEGORIAS = {
  m, d, store
}