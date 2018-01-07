import firebase from 'firebase';
import eventBus from '../EventBus';

const m = {
  ADD_LOCAL: 'localAdd'
}

const d = {
  INITIALIZE: 'localInitialize'
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
    [m.ADD_LOCAL](state, local) {
      if (state.list.indexOf(local) < 0)
        state.list = [ ...state.list, local];
    },
  },
  actions: {
    [d.INITIALIZE]({commit, getters}) {
      
    }
  }
}

export const LOCAIS = {
  m, d, store
}

