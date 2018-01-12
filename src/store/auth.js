import axios from 'axios';
import firebase from 'firebase';
import {bus, events} from '../EventBus';
import MIGRATIONS from './migrations';

if (process.env.NODE_ENV == 'development') {
  firebase.initializeApp({
    apiKey: "AIzaSyDNId3qWk7ivtZG4CxPVvc_ZHfl4-dH8Jw",
    authDomain: "eb-minhas-financas-dev.firebaseapp.com",
    databaseURL: "https://eb-minhas-financas-dev.firebaseio.com",
    projectId: "eb-minhas-financas-dev",
    storageBucket: "",
    messagingSenderId: "1040340906347"
  });
} else {
  firebase.initializeApp({
    apiKey: "AIzaSyB5mLWc-ry7gdmx8H8UeBIfmlWfqNRkBa4",
    authDomain: "eb-minhas-financas.firebaseapp.com",
    databaseURL: "https://eb-minhas-financas.firebaseio.com",
    projectId: "eb-minhas-financas",
    storageBucket: "eb-minhas-financas.appspot.com",
    messagingSenderId: "761623811227"
  });
}


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
    }
  },
  getters: {
    uid(state) {
      return state.usuario.uid;
    }
  },
  actions: {
    [d.LOGIN]({commit, dispatch}) {
      var provider = new firebase.auth.GoogleAuthProvider();
      provider.addScope('https://www.googleapis.com/auth/userinfo.email');

      return new Promise((resolve, reject) => {
        firebase.auth().signInWithPopup(provider).then((res) => {

        }).catch((err) => {
          dispatch(d.LOGOUT);
          reject();
        })}
      );
    },
    [d.LOGOUT]() {
      firebase.auth().signOut();
    },
    [d.INITIALIZE]({commit, dispatch}) {
      firebase.auth().onAuthStateChanged(user => {
        commit(m.LOGGED_IN, user);
        console.info('logou ' + user)
        dispatch(MIGRATIONS.d.MIGRATE).then(() => {
          bus.$emit(user != null ? events.SIGN_IN : events.SIGN_OUT, res.user);
        });
      })
    }
  }
}

export const AUTH = {
  store, m, d
};

export default AUTH;