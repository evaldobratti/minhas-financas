import firebase from 'firebase/app'
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

export function save(path, value) {
  if (value.id) {
    firebase.database().ref(firebase.auth().currentUser.uid + path + '/' + value.id).set(normalize(value))
  } else {
    firebase.database().ref(firebase.auth().currentUser.uid + path).push(normalize(value))
  }
}

function remove(path, id) {
  firebase.database().ref(firebase.auth().currentUser.uid + path + '/' + id).remove()
}

function load(path) {
  return new Promise((acc, rej) => {
    firebase.database().ref(firebase.auth().currentUser.uid + path).on('value', (snap) => {
      const val = snap.val()
      if (val == null)
        rej()
      else
        acc({
          ...val,
          id: snap.key
        })
    })
  })
}

export default {
  hookAdded, hookRemoved, save, remove, load
}