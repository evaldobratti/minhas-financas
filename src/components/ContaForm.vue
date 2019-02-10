<template>
  <form @submit.prevent="salvar">
      <v-text-field placeholder="Nome" v-model="nome"></v-text-field>
      <v-text-field placeholder="Saldo inicial" v-model="saldoInicial"></v-text-field>
      <v-btn type="submit">Salvar</v-btn>
  </form>
</template>

<script>

const initialForm = {
  nome: '',
  saldoInicial: 0
}

export default {
  data() {
    return Object.assign({}, initialForm)
  },
  methods: {
    salvar() {
      const {nome, saldoInicial} = this

      this.$store.dispatch('contaCriar', {
        nome, saldoInicial
      })

      this.$emit("salvo")
      Object.assign(this, initialForm)
    }
  }

}
</script>

<style>

</style>
