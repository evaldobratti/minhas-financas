import firebase from 'firebase'
import moment from 'moment'

function normalize(object) {
  const val = Object.keys(object).reduce((ant, atual) => {
    let value = object[atual]
    if (moment.isMoment(value))
      value = value.toISOString()

    return {
      ...ant,
      [atual]: value
    }
  }, {})

  return JSON.parse(JSON.stringify(val))
}


export function hookAdded(path, cb) {
  firebase.database().ref(firebase.auth().currentUser.uid + path).on('child_added', (snapshot) => {
    const val = snapshot.val()
    const key = snapshot.key
    cb({
      ...val, id: key
    })  
  })
}

function hookRemoved(path, cb) {
  firebase.database().ref(firebase.auth().currentUser.uid + path).on('child_removed', (snapshot) => {
    const val = snapshot.val()
    const key = snapshot.key
    cb({
      ...val, id: key
    })
  })
}

export function add(path, value) {
  firebase.database().ref(firebase.auth().currentUser.uid + path).push(normalize(value))
}

function remove(path, id) {
  firebase.database().ref(firebase.auth().currentUser.uid + path + '/' + id).remove()
}

function update(updates) {
  const normalized = Object.keys(updates).reduce((ant, atual) => (
    {
      ...ant, 
      [firebase.auth().currentUser.uid + atual]: updates[atual]
    }), {}
  )
  
  firebase.database().ref().update(normalize(normalized))
}

export default {
  hookAdded, hookRemoved, add, remove, update
}