<template>
  <form @submit.prevent="salvar">
    <date-picker placeholder="Data" v-model="form.data" />
    <v-text-field placeholder="Descrição" v-model="form.descricao" />
    <v-text-field placeholder="Valor" type="number" v-model="form.valor" />
    <v-switch v-model="form.efetivada" label="Efetivada" />
    <v-btn type="submit">Salvar</v-btn>
  </form>
</template>

<script>
import moment from 'moment'

export default {
  props: ['idConta'],
  data() {
    return {
      form: {
        data: moment(),
        descricao: '',
        valor: 0,
        efetivada: false
      }
    }
  },
  methods: {
    salvar() {
      const parsed = {
        ...this.form,
        valor: parseInt(this.form.valor),
        idConta: this.idConta
      }

      this.$store.dispatch('lancamentoCriar', parsed)
      
      this.$emit('salvo')
    }
  }
}
</script>

<style>

</style>
