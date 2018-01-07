import store from './';

export default function(map) {
  return Object.keys(map).reduce((cp, key) => {
    const [getter, action] = map[key]
    cp[key] = {
      get() { return getter.split('.').reduce((o,i) => o == null ? null : o[i], store.state) },
      set(payload) { store.commit(action, payload) }
    }

    return cp
  }, {});
}