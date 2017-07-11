<template>
  <v-card class="conta-card">
    <v-card-text>
      <router-link :to="{ name: 'detalhe-conta', params: { id: conta.id } }">
        {{ conta.nome }}
      </router-link>
      {{ conta.saldoInicial | currency}}
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: [ 'conta' ]

}
</script>

<style>
.conta-card {
  margin-top: 5px;
}
</style>
