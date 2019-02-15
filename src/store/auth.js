//import firebase from 'firebase/firebase-app'
import firebase from 'firebase/app'
import 'firebase/auth'
import 'firebase/database'

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

const state = {
  isAutenticado: null,
  autenticacao: {}
}

const mutations = {
  login: (_, autenticacao) => {
    state.isAutenticado = true
    state.autenticacao = autenticacao
  },
  logout: (state) => {
    state.isAutenticado = false
    state.autenticacao = {}
  }
}

const actions = {
  inicializa: ({commit, dispatch}) => {
    firebase.auth().onAuthStateChanged(user => {
      if (user) {
        commit('login', user)
        dispatch('posLogin')
      }
      else
        commit('logout')
    })
  },
  login: ({commit}) => {
    const provider = new firebase.auth.GoogleAuthProvider();
    provider.addScope('https://www.googleapis.com/auth/userinfo.email');

    return new Promise((resolve, reject) => {
      firebase.auth().signInWithPopup(provider).then(() => {
        resolve()
      }).catch(() => {
        commit('logout');
        reject();
      })}
    );
  },
  logout: ({commit, dispatch}) => {
    firebase.auth().signOut().then(() => {
      commit('resetAll'); 
      commit('logout');
    })
  }
}

export default {
  state,
  mutations,
  actions
}