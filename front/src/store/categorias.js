import axios from 'axios';


export const d = {
  LOAD_CATEGORIAS: 'loadCategorias',
  SAVE_CATEGORIA: 'saveCategoria'
}

export const m = {
  LOADED_CATEGORIAS: 'loadedCategorias'
}


export default {
  state: {
    list: []
  },
  mutations: {
    [m.LOADED_CATEGORIAS](state, categorias) {
      state.list = categorias;
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
            commit(m.LOADED_CATEGORIAS, res.data.filter(c => c.pai == null));
            resolve(state.list);
          }).catch(err => {
            console.error(err);
            reject();
          });
        });
      } else {
        return Promise.resolve(state.list);
      }
    },
    [d.SAVE_CATEGORIA]({dispatch}, categoria) {
      axios.post('/api/categorias', categoria).then(res => {
        dispatch(d.LOAD_CATEGORIAS, true);
      }).catch(err => {
        console.error(err)
      });
    }
  }
}