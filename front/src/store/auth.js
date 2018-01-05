import axios from 'axios';
import firebase from 'firebase';
import {bus, events} from '../EventBus';

firebase.initializeApp({
  apiKey: "AIzaSyB5mLWc-ry7gdmx8H8UeBIfmlWfqNRkBa4",
  authDomain: "eb-minhas-financas.firebaseapp.com",
  databaseURL: "https://eb-minhas-financas.firebaseio.com",
  projectId: "eb-minhas-financas",
  storageBucket: "eb-minhas-financas.appspot.com",
  messagingSenderId: "761623811227"
});

export const d = {
  LOGIN: 'login',
  INITIALIZE: 'authInitialize',
  LOGOUT: 'logout'
}

export const m = {
  LOGGED_IN: 'authLogin'
}

export const store = {
  state: {
    isAuthenticated: null,
    usuario: {}
  },
  mutations: {
    [m.LOGGED_IN]: (state, usuario) => {
      state.isAuthenticated = usuario != null;
      state.usuario = usuario || {};
      bus.$emit(state.isAuthenticated ? events.SIGN_IN : events.SIGN_IN, usuario);
    }
  },
  getters: {
    uid(state) {
      return state.usuario.uid;
    }
  },
  actions: {
    [d.LOGIN]({commit}) {
      var provider = new firebase.auth.GoogleAuthProvider();
      provider.addScope('https://www.googleapis.com/auth/userinfo.email');

      return new Promise((resolve, reject) => {
        firebase.auth().signInWithPopup(provider).then((res) => {
          commit(m.LOGGED_IN, res.user);
          resolve();
        }).catch(() => {
          commit(m.LOGGED_OUT);
          reject();
        })}
      );
    },
    [d.LOGOUT]() {
      firebase.auth().signOut();
    },
    [d.INITIALIZE]({commit}) {
      firebase.auth().onAuthStateChanged(user => {
        commit(m.LOGGED_IN, user);
      })
    }
  }
}

export const AUTH = {
  store, m, d
};

export default AUTH;