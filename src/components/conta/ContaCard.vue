<template>
  <v-card class="conta-card">
    <v-card-text>
      <router-link :to="{ name: 'detalhe-conta', params: { id: conta.id } }">
        {{ conta.nome }}
      </router-link>
      <span style="float: right">Saldo atual: {{ saldoAtual | currency}}</span>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: [ 'conta' ],
  computed: {
    saldoAtual() {
      return this.conta.saldoInicial;
    }
  }

}
</script>

<style>
.conta-card {
  margin-top: 5px;
}
</style>
