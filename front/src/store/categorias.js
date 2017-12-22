import { SNACKS } from './snacks';
import firebase from 'firebase';
import eventBus from '../EventBus';

export const d = {
  LOAD_CATEGORIAS: 'loadCategorias',
  SAVE_CATEGORIA: 'saveCategoria',
  INITIALIZE: 'categoriaInitialize'
}

export const m = {
  LOADED_CATEGORIAS: 'loadedCategorias',
  ADD_CATEGORIA: 'categoriaAdd',
  UPDATE_CATEGORIA: 'categoriaUpdate'
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
    },
    [m.ADD_CATEGORIA](state, categoria) {
      state.list = [...state.list, categoria];
    },
    [m.UPDATE_CATEGORIA](state, categoria) {
      const ix = state.list.findIndex(c => c.id == categoria.id);
      state.list.splice(ix, 1, categoria);
    }
  },
  getters: {
    getCategoria(state) {
      return id => {
        return state.list.find(c => c.id == id);
      }
    },
    getCategoriasRaizes(state) {
      return state.list.filter(c => c.pai == null);
    },
    getCategoriasFilhas(state) {
      return idCategoria => state.list.filter(c => c.pai == idCategoria);
    }
  },
  actions: {
    [d.INITIALIZE]({commit, getters}) {
      eventBus.bus.$on(eventBus.events.SIGN_IN, () => {
        firebase.database().ref(getters.uid + '/categorias').on('child_added', (snap) => {
          const categoria = snap.val();
          categoria.id = snap.key;
          commit(m.ADD_CATEGORIA, categoria);
        });
        firebase.database().ref(getters.uid + '/categorias').on('child_changed', (snap) => {
          commit(m.UPDATE_CATEGORIA, snap.val());
        });
      })
    },
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
    [d.SAVE_CATEGORIA]({dispatch, commit, getters}, categoria) {
      let future;
      let msg;
      if (categoria.id) {
        future = firebase.database().ref(getters.uid + '/categorias/' + categoria.id).set(categoria);
        msg = 'Categoria atualizada com sucesso!';
      } else {
        future = firebase.database().ref(getters.uid + '/categorias').push(categoria);
        msg = 'Categoria cadastrada com sucesso!';
      }

      return future.then(() => {
        commit(SNACKS.m.UPDATE_SUCESSO, msg);
      }).catch(err => {
        commit(SNACKS.m.UPDATE_ERRO, err);
        throw err;
      });
    }
  }
}

export const CATEGORIAS = {
  m, d, store
}