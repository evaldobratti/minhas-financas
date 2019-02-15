<template>
  <v-form ref="form" @submit.prevent="salvar" v-model="isValido">
    <v-text-field 
      label="Nome" 
      v-model="form.nome"
      :rules="[v => !!v || 'Campo obrigatório']"
    />
    <v-text-field label="Saldo inicial" v-model="form.saldoInicial"></v-text-field>
    <v-btn type="submit">Salvar</v-btn>
  </v-form>
</template>

<script>

const initialForm = {
  nome: '',
  saldoInicial: 0
}

export default {
  data() {
    return {
      form: Object.assign({}, initialForm),
      isValido: false
    }
  },
  methods: {
    salvar() {
      this.$refs.form.validate()
      if (!this.isValido)
        return

      const {nome, saldoInicial} = this.form

      this.$store.dispatch('contaCriar', {
        nome, saldoInicial
      })

      this.$emit("salvo")
      Object.assign(this.form, initialForm)
      this.$refs.form.resetValidation()
    }
  }, 
  watch: {
    saldoInicial() {
      this.saldoInicial = Number(this.saldoInicial)
    }
  }

}
</script>

<style>

</style>
