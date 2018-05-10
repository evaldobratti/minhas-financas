import Vue from 'vue';

export const events = {
    SIGN_IN: 'signIn',
    SIGN_OUT: 'signOut',
    ATUALIZANDO_LANCAMENTO: 'atualizandoLancamento'
}

export const bus = new Vue();

const interessados = [];

function notificaInteressados(evento, value) {
  for (var i = 0; i < interessados.length; i++) {
    var interessado = interessados[i];
    if (interessado.evento == evento) {
      var maybeFuture = interessado.cb(value);
      if (maybeFuture != null)
        return maybeFuture;
    }
  }
  return null;
}

export default {
    events, bus, interessados, notificaInteressados
}