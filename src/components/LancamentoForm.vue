<template>
  <form @submit.prevent="salvar">
    <date-picker label="Data" v-model="form.data" />
    <v-text-field label="Descrição" v-model="form.descricao" />
    <v-text-field label="Valor" v-model="form.valor" />
    <v-switch v-model="form.efetivada" label="Efetivada" />
    <v-switch v-model="continuarCriando" label="Continuar criando" />
    <v-btn type="submit">Salvar</v-btn>
  </form>
</template>

<script>
import moment from 'moment'

const formOriginal = {
  data: moment(),
  descricao: '',
  valor: null,
  efetivada: false
}

export default {
  props: ['idConta'],
  data() {
    return {
      form: Object.assign({}, formOriginal),
      continuarCriando: false
    }
  },
  methods: {
    salvar() {
      const parsed = {
        ...this.form,
        idConta: this.idConta
      }

      this.$store.dispatch('lancamentoCriar', parsed)
      
      Object.assign(this.form, formOriginal)

      if (!this.continuarCriando) {
        this.$emit('salvo')
      }
    }
  },
  watch: {
    'form.valor'() {
      this.form.valor = Number(this.form.valor)
    }
  }
}
</script>

<style>

</style>
