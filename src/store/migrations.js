import firebase from 'firebase';

const d = {
  MIGRATE: 'migrate'
}

const store = {
  actions: {
    [d.MIGRATE]({getters}) {
      return new Promise((resolve, reject) => {
        firebase.database().ref(getters.uuid + '/migrations').once('value').then(ref => {
          console.info(ref);
          resolve();
        });
      });
    }
  }
}

export const MIGRATIONS = {
  d, store
}

export default MIGRATIONS;