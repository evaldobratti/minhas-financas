import firebase from 'firebase';

const d = {
  MIGRATE: 'migrate'
}

function colocaOrdemNosLancamentos(uid, migrations) {
  if (!migrations.lancamentosOrdenados) {
    let ix = 0;
    firebase.database().ref(uid + '/lancamentos').once('value').then(ref => {
      ref.forEach((idContaRef) => {
        idContaRef.forEach((lancamentoRef) => {
          lancamentoRef.ref.update({
            ordem: ix
          });
          ix++;
        })
      });
    });
    migrations.lancamentosOrdenados = true;
  }
}

const store = {
  actions: {
    [d.MIGRATE]({getters}) {
      return new Promise((resolve, reject) => {
        firebase.database().ref(getters.uid + '/migrations').once('value').then(ref => {
          const appliedMigrations = ref.val() || {};
          colocaOrdemNosLancamentos(getters.uid, appliedMigrations);
          firebase.database().ref(getters.uid + '/migrations').set(appliedMigrations);
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